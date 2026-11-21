# Demo notifiche locali

Questo progetto autonomo permette di provare le notifiche locali. Nell’app condivisa si accede allo stesso componente dalla schermata dei farmaci, premendo **Promemoria farmaci**.

## Aprire e provare

Aprire **questa cartella** (`samples/notifications-demo`) come progetto in Android Studio, attendere la sincronizzazione Gradle e avviare `app` su un emulatore o telefono Android.

1. Premere **Invia notifica di prova**.
2. Su Android 13 o successivo concedere il permesso quando richiesto. Se viene negato, la demo mostra un messaggio e non invia notifiche.
3. Aprire il pannello notifiche: compare **Promemoria di prova**.
4. Toccare il promemoria per tornare alla demo; la notifica viene rimossa automaticamente.
5. Per annullarla dall’app, premere **Rimuovi notifica di prova**.

**Impostazioni notifiche** apre le impostazioni dell’app; lo stato viene aggiornato al ritorno nella demo. Gli invii della demo usano l’id `1001` e aggiornano la stessa notifica.

## File del progetto dimostrativo

- `app/src/main/java/it/example/farmaci/demo/notifications/MedicineNotifications.kt`: creazione del canale, verifica permesso e abilitazione, invio e annullamento.
- `app/src/main/res/drawable/ic_notification.xml`: icona della notifica.
- `app/src/main/res/values/strings.xml`: testi del canale e versione pubblica della notifica; contiene anche i testi esclusivi della demo.
- `app/src/main/AndroidManifest.xml`: dichiarazione del permesso `POST_NOTIFICATIONS`.
- `MainActivity.kt`: esempio di richiesta del permesso, pulsanti di prova e `PendingIntent` per tornare all’app. Adattare questi comportamenti all’interfaccia condivisa.

La classe non dipende da Compose o `MainActivity`, ma usa AndroidX Core e risorse del modulo. Nell’integrazione adattare package/import di `R`, copiare le risorse necessarie e aggiungere il permesso al manifest esistente. Non sostituire manifest, Activity o configurazione Gradle dell’app con quelli della demo.

API: `MedicineNotifications(context)`, `availability()`, `showReminder(id, title, message, onTap)` e `cancel(id)`. `onTap` è un `PendingIntent` immutabile creato dall’app ospitante. Id uguali aggiornano, id diversi creano notifiche distinte. Gestire `SENT`, `PERMISSION_REQUIRED`, `APP_DISABLED` e `CHANNEL_DISABLED`. `SENT` conferma la richiesta di invio al sistema, non la lettura da parte dell’utente.

Il canale `medicine_reminders` usa importanza normale: il banner a comparsa non è garantito. Le preferenze impostate dall’utente vengono rispettate. La visibilità è privata, con una versione pubblica generica per la schermata di blocco, secondo le impostazioni del dispositivo.

## Ambiente e verifiche

- JDK 25, Gradle Wrapper 9.6.0, Android Gradle Plugin 9.5.1.
- SDK di compilazione e destinazione 37; versione minima Android 7.0 (API 24).
- Per il controllo locale: `./gradlew :app:assembleDebug :app:lintDebug` con JDK e SDK configurati.
- Compilazione e lint completati senza errori bloccanti il 14 novembre 2026. Rimangono avvisi sulle dipendenze del modello iniziale; alcuni controlli personalizzati di Lifecycle 2.6.1 sono incompatibili con il lint attuale e vengono saltati.
- Provati manualmente su emulatore ARM64 API 37: permesso negato/concesso, invio, visualizzazione, ritorno all’app e rimozione. Ripetuti invio e apertura prima di preparare questo branch.
- I file `ExampleUnitTest` e `ExampleInstrumentedTest` sono esempi generati da Android Studio, non test automatici delle notifiche.

Non sono ancora implementati orari automatici, persistenza dei promemoria o ripristino dopo il riavvio. Da verificare anche su telefono reale e sulle versioni Android supportate dal team. `it.example.farmaci.demo` è un package provvisorio per questo progetto di prova.

Il README principale della repository non viene modificato da questa demo.
