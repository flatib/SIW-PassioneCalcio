# Analisi sperimentale su prestazioni e accesso ai dati

## Confronto tra le strategie LAZY e JOIN FETCH

### Primo caso di studio: estrazione multipla di squadre e giocatori

Il database di test è stato popolato con un volume di dati rappresentativo, consistente in 50 squadre e 200 giocatori complessivi.

Utilizzando un caricamento di tipo **LAZY**, si verifica il problema N+1, dal momento che l’ORM ha generato una query principale per estrarre le squadre, seguita da 50 query sequenziali per inizializzare i proxy dei giocatori.

Il tempo di esecuzione registrato è stato pertanto di **342 ms**.

Per ottimizzare l’accesso ai dati, è stata utilizzata una query del tipo `LEFT JOIN FETCH t.players`, che costringe il database a caricare sia le squadre sia i giocatori correlati eseguendo una singola query SQL atomica.

In quest’altro caso, il tempo di esecuzione è stato di **302 ms**, consentendo un risparmio di 40 ms. Oltre al guadagno cronometrico, l’eliminazione di 50 query ridondanti rappresenta un miglioramento architetturale cruciale.

In un ambiente di produzione in cloud, dove è presente una forte latenza di rete, raggruppare i round-trip al database in una singola richiesta previene severi colli di bottiglia e degradi prestazionali.

### Secondo caso di studio: estrazione di una singola entità “torneo” e di più giocatori

Il database è stato configurato per interrogare un singolo torneo a cui sono state associate 30 squadre.

Utilizzando un’interrogazione di tipo **LAZY**, sono state generate due query: una per caricare i dati di base del torneo e una sulla tabella di join, al momento dell’invocazione della collezione delle squadre. Il tempo di esecuzione è stato di **16 ms**.

È stata poi tentata la strategia ottimizzata: usando il metodo `findByIdWithTeams` con `JOIN FETCH`, il sistema ha elaborato una singola query complessa, comprendente una `LEFT JOIN` tra tre tabelle, per recuperare istantaneamente tutti i dati.

Si è registrato un tempo di **20 ms**.

Come si può notare, in questo caso la strategia “ottimizzata” sembrerebbe essere quella meno efficiente. Nel caso di un’estrazione singola, infatti, non si manifesta un problema N+1, bensì un semplice comportamento “1+1”, poiché le query totali sono solo due.

I test in ambiente locale (localhost) rivelano che il metodo **LAZY** risulta impercettibilmente più veloce (16 ms contro 20 ms) poiché, mancando totalmente la latenza di rete, l’esecuzione di due query elementari risulta più rapida per il motore SQL rispetto al calcolo algoritmico di una join tra tre tabelle.

Ciononostante, per le ragioni di rete citate nel caso precedente, l’uso di **JOIN FETCH** resta la soluzione più scalabile e sicura in produzione.
