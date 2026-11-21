# Elderly Support — progetto Android da zero

Questa cartella contiene il codice completo di una prima app **Kotlin + Jetpack Compose** per riconoscere nome e scadenza dei farmaci con ML Kit. L'app è un prototipo didattico: dati da verificare manualmente.

## Creazione con Android Studio (consigliato)

1. Apri Android Studio, seleziona **New Project → Empty Activity**.
2. Imposta:
   - Name: `ElderlySupport`
   - Package name: `com.example.elderlysupport`
   - Language: **Kotlin**
   - Minimum SDK: **API 26** o superiore (API 26 supporta `java.time` senza desugaring).
   - Usa Jetpack Compose (il template Empty Activity moderno lo prevede).
3. Aspetta che termini la sincronizzazione Gradle.
4. Sostituisci `app/src/main/java/com/example/elderlysupport/MainActivity.kt` con il file `app/src/main/java/com/example/elderlysupport/MainActivity.kt` di questo pacchetto.
5. Copia tutta la cartella `app/src/main/java/com/example/elderlysupport/medicine` nel percorso corrispondente del progetto.
6. Copia `app/src/main/res/xml/medicine_file_paths.xml` nel percorso corrispondente (crea la cartella `xml` se non esiste).
7. Nel `app/build.gradle.kts` generato da Android Studio, dentro `dependencies`, aggiungi:

   ```kotlin
   implementation("com.google.mlkit:text-recognition:16.0.1")
   ```

   Non rimuovere le altre dipendenze del progetto.
8. Dentro `<application>` di `app/src/main/AndroidManifest.xml`, aggiungi il provider mostrato in `MANIFEST_PROVIDER.xml.txt`.
9. Esegui **Sync Project with Gradle Files**, poi **Run** su un telefono Android oppure un emulatore dotato di app Fotocamera.

Il codice usa `ActivityResultContracts.TakePicture` per la fotocamera e `GetContent` per le immagini. Non serve implementare CameraX per questa prima versione; la foto viene acquisita dalla fotocamera di sistema.

## Flusso dell'app

Home → Scansiona farmaco → Scatta foto / Galleria → OCR → Proposta nome e scadenza → Correzione manuale → Conferma → Riepilogo.

Il riepilogo è solo in memoria. Nella versione del team potrete sostituire la callback di conferma con una chiamata al vostro repository/database.

## Controlli importanti

- Testa con **20 confezioni reali diverse** e annota i due tassi di riconoscimento indipendentemente.
- La lista di nomi è **dimostrativa**, non un catalogo completo. Se il nome manca, inseriscilo manualmente.
- La scadenza richiede una dicitura `SCAD`, `SCADENZA`, `EXP` o simile per evitare di confonderla con la data di produzione.
- Se nome e scadenza sono su lati diversi, scansiona il lato della scadenza e compila o correggi il nome, oppure amplia successivamente il modulo per combinare più foto.
- In questo starter **non sono ancora implementati** utenti, notifiche, database o lettura DataMatrix: restano di competenza degli altri moduli o di una fase successiva.

## Limite di verifica

Il pacchetto è stato controllato strutturalmente, ma non compilato qui perché in questo ambiente non sono disponibili Android SDK e Gradle. Android Studio scaricherà/risolverà le dipendenze del progetto generato; in caso di errori incolla i messaggi Build Output.
