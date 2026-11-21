# Elderly Support

Elderly Support è un progetto pensato per aiutare le persone anziane a riconoscere i farmaci dalla confezione, ricordare gli orari di assunzione e contattare rapidamente una persona di fiducia in caso di emergenza.

Oggi su `main` si può provare un’app Android che salva i farmaci inseriti a mano o confermati dopo la scansione e invia una notifica di prova. La checklist segue il documento *Elderly Support — Requisiti minimi, consegne e milestone*. Spuntiamo una funzione quando è integrata su `main` e si può verificare dall’app.

## Checklist del progetto

### Farmaci e riconoscimento della confezione

- [x] Inserire a mano il nome del farmaco e, facoltativamente, la scadenza.
- [x] Salvare i farmaci sul dispositivo, mostrarli in elenco ed eliminarli.
- [x] Acquisire la foto della confezione tramite fotocamera o caricamento di un’immagine.
- [x] Proporre nome e scadenza con OCR e consentire la correzione prima del salvataggio.
- [ ] Misurare su almeno 20 confezioni diverse quanto spesso entrambi i campi vengono letti correttamente, con percentuali separate.
- [x] Durante la revisione della scansione, segnalare scadenze superate o vicine (entro due mesi di calendario).

### Promemoria di assunzione

- [x] Inviare una notifica locale immediata da **Promemoria farmaci**, con un testo di esempio.
- [x] Gestire il permesso di notifica, aprire l’app toccando il promemoria e rimuovere la notifica di prova.
- [ ] Associare a un farmaco giorni e orari di assunzione modificabili dall’utente.
- [ ] Attivare il promemoria all’orario impostato e leggere ad alta voce il nome del farmaco.

### Contatti di emergenza

- [ ] Permettere all’utente di aggiungere, modificare ed eliminare i contatti di emergenza.
- [ ] Mostrare un pulsante **SOS** ben visibile e verificare che il messaggio arrivi realmente ai destinatari tramite il canale scelto.

## Avvio e prova

1. Clonare la repository privata con un account autorizzato e aprire la cartella principale `elderlysupport` in Android Studio.
2. Installare dal gestore SDK la piattaforma **Android API 37** e usare **JDK 25** per Gradle. Attendere la sincronizzazione del progetto.
3. Selezionare il modulo **app** e un emulatore o telefono con **Android 8.0 (API 26)** o successivo, quindi premere **Run ▶**.
4. Nella schermata iniziale inserire un nome di esempio, aggiungere una scadenza e premere **Salva farmaco**. Riaprire l’app per verificare che il farmaco sia ancora in elenco.
5. Premere **Scansiona farmaco**, scattare una foto o sceglierla dalla galleria. Controllare nome e scadenza sulla confezione, correggere eventuali errori e premere **Conferma farmaco** per aggiungerlo all’elenco.
6. Aprire **Promemoria farmaci → Invia notifica di prova**. Su Android 13 o successivo, concedere il permesso quando richiesto. Aprire il pannello notifiche e toccare **Promemoria di prova** per tornare all’app.

Per compilare l’APK di debug ed eseguire i test unitari dalla cartella principale:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

Sul prototipo Android sono stati verificati la compilazione, i test unitari esistenti e, su emulatore API 37, l’invio, l’apertura e la rimozione della notifica. Lo scanner è stato provato con uno scatto virtuale e con un’immagine sintetica da galleria, verificando data completa, avviso di scadenza e salvataggio dopo il riavvio. I test di accuratezza su confezioni reali restano da svolgere.

## Architettura

| Parte | Dove si trova | Responsabilità |
| --- | --- | --- |
| Interfaccia Kotlin e Jetpack Compose | [MainActivity e SchermataProva](app/src/main/java/com/example/elderlysupport/) | Avvio, inserimento dei farmaci, elenco e accesso ai promemoria. |
| Persistenza locale con Room | [data](app/src/main/java/com/example/elderlysupport/data/) | Il database locale prevede farmaci, promemoria e contatti di emergenza. La schermata attuale usa i farmaci. |
| Scanner | [medicine](app/src/main/java/com/example/elderlysupport/medicine/) | ML Kit legge il testo sul dispositivo. Il parser propone nomi da un elenco dimostrativo di 10 farmaci e cerca una data associata a SCAD o EXP. |
| Notifiche Android | [notifications](app/src/main/java/com/example/elderlysupport/notifications/) | La schermata gestisce il permesso e le azioni di prova. Il componente crea il canale, invia e annulla la notifica. |
| Esempio autonomo | [samples/notifications-demo](samples/notifications-demo/) | Permette di provare le notifiche separatamente. Per avviare l’app condivisa si usa il progetto alla radice. |

## Decisioni da chiudere prima della consegna

| Scelta richiesta | Stato e criterio di decisione |
| --- | --- |
| Piattaforma | Il prototipo usa Android nativo, con accesso alle notifiche di sistema. La traccia indica web app o desktop. Confermare con il docente l’ammissibilità di Android e documentare il confronto in termini di leggibilità, facilità d’uso e disponibilità del dispositivo per una persona anziana. |
| Riconoscimento | Il prototipo usa OCR per leggere nome e scadenza stampati sulla confezione. Il catalogo dimostrativo copre dieci nomi. Il confronto con codice a barre, AIC o un approccio combinato resta aperto fino alle prove reali. |
| Canale SOS | Confrontare email, SMS e Telegram per immediatezza, affidabilità e requisiti per il destinatario. Provare la ricezione prima di segnare il requisito come completato. |
| Sintesi vocale | Scegliere la libreria o il servizio dopo una prova della voce italiana sul dispositivo previsto. Valutare comprensibilità e funzionamento senza rete. |

- [ ] Riportare qui e nella presentazione le scelte definitive, le alternative valutate e i motivi per cui sono state scartate.

## Test obbligatorio di accuratezza

- [ ] Raccogliere almeno **20 confezioni reali di farmaci diversi** e annotare per ciascuna il nome e la scadenza leggibili sulla confezione.
- [ ] Confrontare ogni risultato con i due valori annotati. Contare anche le mancate letture nel totale.
- [ ] Compilare la tabella e commentare nel README e nella presentazione i casi di errore osservati.

| Dato da riconoscere | Letture corrette | Confezioni provate | Accuratezza |
| --- | ---: | ---: | ---: |
| Nome del farmaco | — | — | — |
| Data di scadenza | — | — | — |

Per ogni riga, l’accuratezza è il numero di letture corrette diviso per il numero di confezioni provate, moltiplicato per 100. I trattini indicano misure ancora da raccogliere.

## Milestone e consegne

Le date sono quelle della traccia. Ogni milestone richiede una versione funzionante caricata su GitHub.

| Milestone | Scadenza | Evidenza richiesta |
| --- | --- | --- |
| M1 · Setup | 17 ottobre 2026 | Repository, motivazioni di piattaforma e riconoscimento, prima raccolta di immagini. |
| M2 · Riconoscimento | 31 ottobre 2026 | Lettura di nome e scadenza, primi test di accuratezza. |
| M3 · Promemoria ed emergenze | 14 novembre 2026 | Gestione farmaci, promemoria vocali, rubrica e SOS con invio reale. |
| M4 · Consegna finale | Da definire | App completa, risultati dei test e materiali di consegna. |

- [x] Repository GitHub con il codice del prototipo.
- [ ] Completare il README con le decisioni tecniche finali e i risultati misurati.
- [ ] Preparare la nota privacy su farmaci, orari e contatti: quali dati vengono raccolti, dove sono conservati, come sono protetti e quali dati vengono trasmessi per l’SOS.
- [ ] Registrare il video della demo in condizioni reali.
- [ ] Preparare la presentazione finale per la classe con demo, motivazioni tecniche e analisi degli errori.
