# Progetto di Sistemi Informativi su Web per la gestione di tornei di calcio amatoriali
Guardare `analisi_sperimentale_prestazioni.md` per l'analisi prestazionale e dell'accesso ai dati.

## Istruzioni
- Clonare il repository
- Separare la cartella `passionecalcio-react` dal resto del sorgente
- Modificare eventualmente `src/main/resources/application.properties` da ***create*** a ***update*** per non caricare le entità di test e mantenere le modifiche a ogni riavvio
- Eseguire il contenuto di `passionecalcio-react` con Vite e il resto come progetto Eclipse/Spring Boot

## Organizzazione del progetto Eclipse/Spring Boot
- In `src/main/java` si trovano le classi del backend in Java
- In `src/main/resources` si trovano i template del frontend in Thymeleaf
- In `src/test/java` si trovano i test di unità usati per l'analisi prestazionale

## Altre informazioni utili
Ho lasciato alcuni template inutilizzati, riconoscibili dalla dicitura `_old`, corrispondenti a vecchie versioni del frontend, utilizzabili in caso di backup.
