# Progetto SIW 2025/2026 — Gestione Tornei di Calcio Amatoriale

## Descrizione del progetto

Il progetto realizza un sistema informativo web per la gestione di tornei di calcio amatoriale, sviluppato con architettura a livelli e con separazione tra persistenza, logica applicativa e gestione delle richieste HTTP, in coerenza con le specifiche della consegna . L’applicazione consente la gestione di tornei, squadre, giocatori e partite, includendo funzionalità pubbliche e funzionalità riservate agli amministratori, come richiesto dal testo del progetto .

La soluzione è stata implementata utilizzando Spring Boot per il backend, JPA/Hibernate per la persistenza, PostgreSQL come database relazionale, Thymeleaf per una parte del frontend e React per alcune viste dinamiche, in conformità con i requisiti minimi indicati nella consegna .

## Obiettivi funzionali

L’applicazione copre i principali casi d’uso previsti per il dominio dei tornei di calcio amatoriale . In particolare, il sistema permette la visualizzazione dell’elenco dei tornei, del dettaglio di un torneo, delle squadre partecipanti, del calendario delle partite e del dettaglio delle squadre con i relativi giocatori .

Per gli utenti amministratori sono state previste funzionalità di creazione e gestione delle entità principali, tra cui inserimento e modifica di tornei, squadre, giocatori e partite, oltre alla possibilità di eliminare squadre e partite, in linea con quanto richiesto dalla consegna . L’applicazione include inoltre autenticazione e autorizzazione basate sui ruoli USER e ADMIN, con protezione delle funzionalità amministrative .

## Architettura dell’applicazione

L’applicazione adotta una architettura a livelli composta da persistence layer, service layer e controller layer, come richiesto esplicitamente dal progetto . Il persistence layer utilizza repository JPA per l’accesso ai dati; il service layer contiene la logica di business e coordina le operazioni tra repository; il controller layer gestisce le richieste HTTP e delega l’esecuzione dei casi d’uso ai servizi applicativi .

I metodi del service layer che modificano lo stato del sistema sono progettati per essere gestiti transazionalmente, mentre le operazioni di sola lettura sono distinte dalle operazioni di aggiornamento, in coerenza con le indicazioni della consegna sulla gestione delle transazioni .

## Modello di dominio

Il modello di dominio include le entità principali richieste dalla traccia: Torneo, Squadra, Giocatore, Partita, Arbitro e Utente . Per ciascun torneo vengono gestite le squadre partecipanti, i giocatori associati alle squadre, il calendario delle partite, i risultati e lo stato degli incontri, rispecchiando lo scenario applicativo definito nel testo .

Le relazioni minime tra entità seguono la struttura indicata nella consegna: una squadra partecipa a uno o più tornei e possiede più giocatori; ogni giocatore appartiene a una sola squadra; una partita appartiene a un torneo, coinvolge due squadre ed è associata a un arbitro .

## Frontend

Il frontend è stato realizzato con approccio misto Thymeleaf + React, come consentito dalla consegna, che richiede almeno una parte dell’interfaccia sviluppata in React . Le viste React sono state utilizzate per migliorare usabilità e interattività in alcune sezioni dell’applicazione, mantenendo coerenza stilistica con le pagine server-side .

In particolare, sono state riorganizzate le viste di tornei e squadre in formato griglia, predisposte per l’integrazione di immagini, e sono state introdotte interazioni più moderne mediante card cliccabili e filtri lato client. Le viste di giocatori e partite sono invece state mantenute in formato elenco/tabella, in quanto più adatte alla consultazione di dati strutturati e amministrativi.

## Sicurezza

L’applicazione implementa autenticazione e autorizzazione, distinguendo tra utenti generici, utenti registrati e amministratori, come richiesto dalla consegna . Le funzionalità amministrative sono protette e accessibili esclusivamente agli utenti con ruolo ADMIN, in particolare per la creazione, modifica ed eliminazione delle entità gestionali .

Questa scelta consente di rispettare i requisiti minimi di sicurezza previsti, che includono login con username e password e protezione delle funzionalità riservate .

## Analisi prestazionale

Il progetto include una semplice analisi sperimentale sulle strategie di accesso ai dati, in conformità con la sezione della consegna dedicata a prestazioni e accesso efficiente ai dati . L’analisi è stata condotta sul caso d’uso di caricamento delle squadre con i relativi giocatori, confrontando una strategia non ottimizzata soggetta al problema N+1 con una strategia ottimizzata basata su `join fetch` .

Nel caso non ottimizzato, il sistema recupera l’elenco delle squadre e successivamente accede alla collezione dei giocatori per ogni squadra; tale accesso può generare query aggiuntive, evidenziando il problema N+1. Nel caso ottimizzato, invece, il recupero delle squadre e dei relativi giocatori viene eseguito tramite una query dedicata con fetch esplicito delle associazioni, riducendo il numero di query e migliorando l’efficienza dell’accesso ai dati .

Per la sperimentazione sono stati implementati due metodi distinti nel service layer:

- `unoptimizedFetch()`, che utilizza `findAll()` e poi accede alla collezione `players` di ciascuna squadra.
- `optimizedFetch()`, che utilizza un metodo repository dedicato, `findAllWithPlayers()`, basato su `join fetch`.

È stato inoltre predisposto un controller REST di supporto per l’esecuzione del test comparativo. Questa analisi soddisfa l’obiettivo della consegna di mostrare consapevolezza progettuale rispetto alle strategie di fetch, al problema N+1 query e alla misurazione dei tempi di esecuzione .

## Considerazioni finali

Il progetto è stato sviluppato con l’obiettivo di realizzare una applicazione completa, aderente ai requisiti funzionali e architetturali richiesti, con particolare attenzione alla qualità del codice, alla separazione delle responsabilità e alla chiarezza dell’interfaccia utente . La combinazione di Spring Boot, JPA/Hibernate, Thymeleaf e React ha permesso di integrare una struttura solida lato backend con una esperienza d’uso più moderna in alcune sezioni del frontend .

Tra gli aspetti di maggiore interesse progettuale rientrano la gestione dei ruoli applicativi, l’organizzazione a livelli, l’attenzione all’accesso efficiente ai dati e l’integrazione di componenti React all’interno di un’applicazione server-rendered. Nel complesso, il progetto intende rispondere in modo coerente e completo agli obiettivi didattici indicati dalla consegna .
