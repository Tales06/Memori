Memori
======

*Memori* è un'applicazione Android per la creazione e l'organizzazione di note personali, progettata con una moderna architettura e funzionalità avanzate. L'app fornisce un sistema per gestire note e cartelle (comprese cartelle protette da PIN), sincronizzazione opzionale sul cloud e persino un'integrazione con AI generativa per arricchire i contenuti.

Panoramica del Progetto
-----------------------

Contesto e Scopo: Memori nasce come blocco note digitale avanzato, pensato per utenti che vogliono organizzare appunti e promemoria in modo semplice ma sicuro. L'obiettivo del progetto è offrire un'esperienza completa di note-taking: le note vengono archiviate offline in un database locale sul dispositivo, con possibilità di organizzazione in cartelle, protezione di dati sensibili con PIN e sincronizzazione cloud opzionale. Il tutto è costruito seguendo i principi dell'architettura MVVM e utilizzando tecnologie moderne di Android (Compose, Kotlin, ecc.) per garantire un'interfaccia utente reattiva e piacevole.

Caratteristiche Generali: L'app offre funzionalità chiave che combinano produttività e sicurezza. In sintesi:

-   Note offline e promemoria: È possibile creare note testuali che vengono salvate localmente sfruttando un database *Room*. Le note possono includere titolo e contenuto testuale e sono accompagnate da campi come data di ultima modifica e indicatori di stato (ad esempio preferito, archiviato). Si possono organizzare in cartelle (ad es. per progetti, categoria "Preferiti", "Archivio", ecc.) e l'app supporta anche l'idea di *promemoria* (note pensate per ricordare attività, eventualmente estendibili con notifiche in futuro).

-   Cartelle protette da PIN: Si possono proteggere cartelle specifiche impostando un codice PIN, così da mantenere private le note al loro interno. Memori salva in modo sicuro un hash del PIN utilizzando *Jetpack DataStore*, richiedendo poi il PIN corretto per visualizzare o sbloccare le cartelle protette. Questa funzionalità garantisce un ulteriore livello di privacy per le note sensibili (il PIN viene memorizzato solo in forma crittografata, mai in chiaro).

-   Sincronizzazione cloud opzionale: L'utente può scegliere di autenticarsi con Google per abilitare il backup e la sincronizzazione delle note sul cloud. In caso di login, l'app utilizza Firebase Authentication per il sign-in con Google e memorizza note e cartelle su Cloud Firestore in tempo reale. La sincronizzazione è bidirezionale: all'avvio, le note locali vengono caricate sul cloud e quelle esistenti nel cloud vengono scaricate sul dispositivo, mantenendo entrambe le copie allineate. L'uso del cloud è facoltativo: l'app può funzionare completamente offline se l'utente preferisce non eseguire l'accesso (in tal caso, i dati restano solo in locale).

-   Esperienza utente personalizzabile: Al primo avvio, l'utente può scegliere il tema grafico preferito (chiaro, scuro oppure predefinito di sistema). L'interfaccia è realizzata interamente con Jetpack Compose (Material3), offrendo un design moderno e reattivo. L'app include schermate dedicate per gestire note speciali come il *cestino/archivio* (note eliminate o archiviate) e i *preferiti*, oltre a una procedura iniziale guidata (*onboarding*) che presenta le funzioni chiave.

-   Integrazione AI generativa: Una caratteristica avanzata di Memori è la possibilità di utilizzare l'intelligenza artificiale generativa per arricchire le note. Questa integrazione (basata sulle API generative di Google) consente, ad esempio, di ottenere suggerimenti di testo o contenuti aggiuntivi a partire dalle note dell'utente. *Nota:* per usufruire di questa funzione potrebbe essere necessario avere una chiave API valida configurata nell'app (la funzionalità è opzionale e inattiva se non configurata).

Struttura delle Cartelle e dei File
-----------------------------------

Il progetto adotta la classica struttura di un'app Android basata su Gradle, con una chiara suddivisione in pacchetti per responsabilità. Di seguito la struttura principale delle directory e il ruolo di ciascuna:

-   `app/`: Contiene il codice sorgente dell'applicazione Android (module "app"). Al suo interno si trovano le risorse e il codice Kotlin:

    -   `app/src/main/java/com/example/memori/`: Radice del codice Kotlin. Include diversi package organizzati per funzionalità:

        -   `database/`: Gestione dei dati locali con Room. Suddiviso in:

            -   `note_data/`: definisce la classe entità NotesEntity (struttura di una nota: id, titolo, contenuto, preferito, immagine di sfondo, timestamp ultima modifica, flag eliminato/archiviato, cartella associata), la DAO NoteDao con metodi CRUD e query per preferiti/archiviati/ricerca, il NotesRepository che fornisce un livello di astrazione sopra la DAO per operazioni sulle note (inserimento, modifica, eliminazione, ricerca, gestione preferiti/archivio e spostamento tra cartelle). In questo package è presente anche NoteViewModel, il ViewModel MVVM che interagisce con NotesRepository e fornisce i dati delle note all'UI.

            -   `folder_data/`: definisce l'entità FolderEntity (struttura di una cartella: id, UUID univoco, nome, eventuale userId per l'utente proprietario, timestamp ultima modifica, flag di protezione e PIN cifrato), la DAO FoldersDao per operazioni sulle cartelle, e FolderRepository/FolderViewModel per la logica di gestione cartelle (creazione, rinomina, eliminazione e caricamento elenco cartelle, inclusa l'indicazione se una cartella è protetta da PIN).

            -   `NoteDatabase.kt`: definisce il database Room che combina le entità Note e Folder in un'unica base dati locale (inclusa la configurazione delle DAO).

        -   `sync/`: Contiene le classi per la sincronizzazione cloud con Firestore. In particolare FirestoreNoteRepository e FirestoreFolderRepository implementano le operazioni per caricare, recuperare, inserire o cancellare note e cartelle su Firestore (sotto il percorso `users/{userId}/notes` e `users/{userId}/folders` nel database cloud). Ad esempio, *FirestoreNoteRepository.uploadNote* carica tutte le note locali sul cloud per uno specifico utente, mentre *getAllNotesFromCloud* scarica tutte le note dal cloud. Analogamente, *FirestoreFolderRepository* gestisce le cartelle sul cloud, incluse operazioni di rinomina con aggiornamento del timestamp.

        -   `auth/`: Comprende la logica di autenticazione con Google. La classe principale è GoogleAuthClient, un helper per gestire il login tramite Firebase Auth (Google Sign-In) e ottenere l'ID utente Google corrente. Nel package ci sono anche composable UI come SignInScreen.kt (schermata di login) e data class come SignInRes.kt per rappresentare lo stato del login (successo, errore, caricamento).

        -   `composable/`: Include le schermate e componenti UI scritte in Jetpack Compose. Ad esempio:

            -   Home e Note list: schermate principali per visualizzare tutte le note non archiviate (home) e le note per singola cartella. Ogni nota è rappresentata visivamente da un *NoteCard* (componente composable per la scheda di una nota).

            -   FavoritesScreen.kt: schermata che mostra l'elenco delle note contrassegnate come preferite (usando `NotesRepository.getFavoritesNote()` sotto al cofano).

            -   ArchivePage.kt: schermata per le note archiviate (usa `getArchivedNotes()` dal repository) e probabilmente il *cestino* se implementato.

            -   SearchBar.kt: componente per la barra di ricerca delle note (filtra le note tramite `searchNotes()`).

            -   SettingsScreen.kt: schermata impostazioni (es. modifica tema, logout, ecc.).

            -   PinSetupScreen.kt: schermata per impostare il PIN di protezione quando si abilita una cartella protetta.

            -   ScreenModifiedNotes.kt: schermata di dettaglio/modifica di una singola nota. In questa view l'utente può visualizzare e modificare titolo e contenuto, contrassegnare o rimuovere il flag di preferito, cambiare l'immagine di sfondo (*wallpaper*) della nota, archiviare/riattivare la nota, spostarla in una cartella diversa o rimuoverla dalla cartella corrente, ed eliminare la nota. Ogni azione è supportata da apposite funzioni (es. conferma eliminazione con dialog, selezione immagine da galleria con bottom sheet, ecc.).

            -   NavigationApp.kt (routes/): definisce la navigazione dell'applicazione tramite NavController di Compose Navigation. Mappa le diverse schermate (home, login, dettagli nota, impostazioni, ecc.) in un grafo di navigazione, permettendo la transizione tra di esse.

        -   `preference/`: Contiene la gestione delle preferenze utente con DataStore. Le classi PinPreferences, UserPreferences e ThemePreferences definiscono funzioni estensione su Context per salvare/richiamare valori in DataStore. In particolare, *PinPreferences* gestisce il salvataggio sicuro dell'hash del PIN in un file di preferenze crittografato (`pin_preferences`), mentre *ThemePreferences* salva la scelta del tema (chiaro/scuro) e *UserPreferences* può memorizzare informazioni sull'utente autenticato (es. ID utente o preferenze varie).

        -   `ui/theme/` e `theme/`: Contiene definizioni di tema grafico personalizzato. Ad esempio Type.kt definisce tipografia/font, MyPalette.kt (nel package `theme`) definisce la palette di colori personalizzata utilizzata nell'app (inclusi colori per lo sfondo delle note, testi, ecc.), potenzialmente generata dinamicamente (ad es. uso di *Palette API* per estrarre colori dalle immagini). Queste classi vengono utilizzate per applicare il Material3 Theme a livello di Compose.

        -   `animation/`: (Opzionale) Questo package include effetti animati utilizzati nell'app. Ad esempio AnimBackground è una composable per uno sfondo animato mostrato nella pagina di benvenuto (setup), mentre i file Lottie JSON per animazioni (come "data.json") sono posti nella cartella assets.

    -   `app/src/main/res/`: Risorse dell'app Android:

        -   `drawable/`, `mipmap/`: icone e immagini statiche (tra cui l'icona dell'app).

        -   `values/`: definizioni di temi XML (ad esempio colori, temi Material3 se presenti come fallback per Compose), stringhe localizzate, e altre risorse di base.

        -   `assets/`: file risorsa generici. Qui è presente ad esempio `data.json` (animazione Lottie caricata nella schermata di setup iniziale.

        -   `AndroidManifest.xml`: file manifesto Android che dichiara componenti e permessi. In Memori include almeno la dichiarazione di MainActivity (attività principale che avvia Jetpack Compose) e l'uso di Internet (necessario per la sincronizzazione cloud e chiamate AI), oltre ai requisiti per Firebase/Google Sign-In.

        -   `google-services.json`: file di configurazione per Firebase (contiene gli ID e chiavi del progetto Firebase utilizzate per Auth e Firestore). Questo file è necessario per far funzionare i servizi Google e deve corrispondere a un progetto Firebase configurato; nella repository è presente un file placeholder di esempio (gli sviluppatori che clonano il progetto dovranno sostituirlo con il proprio file scaricato da Firebase console, associato al proprio package `com.example.app_memori`).

    -   File di build e configurazione:

        -   `build.gradle.kts` (root) e `app/build.gradle.kts`: script Gradle Kotlin DSL per la compilazione. Il file del modulo app definisce il namespace, versioni SDK e dipendenze. Ad esempio, Memori è impostato con *compileSdk = 35* (SDK Android 14) e richiede *minSdk = 34* (Android 14 come requisito minimo). Le dipendenze includono AndroidX Compose (BOM Material3, Navigation Compose), Room (database locale), DataStore (preferenze sicure), Firebase Auth e Firebase Firestore (cloud sync), librerie per autenticazione Google (Play Services Auth, Credential API), libreria Google Generative AI (per l'AI integrata) e altre utility come Coil (caricamento immagini), Lottie (animazioni), Palette (estrazione colori), Accompanist (controllo UI di sistema)
        -   `settings.gradle.kts`: include il progetto app (monomodulo, dato che c'è un solo modulo "app").

        -   Script Gradle wrapper (`gradlew`, `gradlew.bat`) e cartella `gradle/` con definizioni di versione (il file `libs.versions.toml` elenca tutte le librerie e plugin utilizzati con relative versioni per una gestione centralizzata).

Principali Funzionalità Implementate
------------------------------------

Memori implementa una serie di funzionalità principali volte a migliorare la produttività e la gestione sicura delle note:

-   CRUD Note: L'utente può creare nuove note, visualizzarle in elenco, modificarne il contenuto e cancellarle. Ogni nota è composta da un titolo e testo libero, e può essere corredata da un'immagine di sfondo personalizzata (scegliendo dalla galleria). Le operazioni di creazione, aggiornamento ed eliminazione sono gestite tramite il *NoteViewModel* che interagisce con il database locale e, se attivo, con il cloud. L'eliminazione di una nota può avvenire direttamente (rimozione dal database) oppure essere trattata come *cancellazione logica* spostandola in un cestino (funzionalità supportata a livello di modello tramite il flag `isDeleted`, anche se al momento l'interfaccia usa l'eliminazione definitiva).
-   Organizzazione in Cartelle: Le note possono essere organizzate in cartelle create dall'utente. Una nota può appartenere a una singola cartella (il campo `folderId` nella nota lega la nota alla cartella corrispondente. Tramite l'interfaccia è possibile spostare una nota in una cartella o rimuoverla dalla cartella (riportandola alla visualizzazione "Note generali"). Le cartelle vengono elencate in una sezione dedicata; selezionando una cartella si vedono solo le note al suo interno. Sono previste cartelle speciali come Archivio (note archiviated) e Preferiti, che di fatto sono viste filtrate delle note basate su attributi (flag `archivedNote` e `favorite`) invece che cartelle fisiche separate.

-   Contrassegno Preferiti e Archiviazione: Ogni nota dispone di un indicatore "preferito" (stella) per evidenziare le note più importanti. L'app offre una vista "Preferiti" che mostra solo le note con `favorite = true`. In modo analogo, le note possono essere archiviate: un'operazione di archiviazione imposta `archivedNote = 1` e le rimuove dalla lista principale; tali note sono visibili nella sezione "Archivio" e possono essere ripristinate (unarchive) in qualsiasi momento. Queste azioni sono accessibili sia dalla schermata principale (ad es. con pulsanti o swipe) sia dalla schermata di modifica della nota.

-   Ricerca Testuale: È disponibile una funzionalità di ricerca che filtra le note in base a una query. Digitando testo nella barra di ricerca, l'app interroga il database locale tramite il metodo `searchNotes(query)` della DAO, che cerca la stringa inserita nel titolo o nel contenuto di tutte le note. I risultati vengono aggiornati in tempo reale grazie all'uso di Flow (la UI osserva i dati della ricerca e si aggiorna automaticamente).

-   Protezione con PIN: Per mantenere private le informazioni sensibili, l'utente può creare cartelle protette da PIN. Quando una cartella è contrassegnata come protetta (`isProtected = true`), l'accesso alle note in essa contenute richiede l'inserimento di un codice PIN a 4 cifre. Durante la configurazione iniziale o quando si abilita la protezione su una cartella, l'app chiede di impostare un PIN e lo salva in modo sicuro (applica una funzione di hashing e memorizza solo l'hash cifrato tramite DataStore. Ogni volta che si tenta di accedere a quella cartella, l'app verifica il PIN inserito dall'utente confrontando l'hash. Se corretto, sblocca temporaneamente la visualizzazione della cartella; in caso contrario, nega l'accesso. *(Nota: assicurarsi di ricordare il PIN, poiché non esiste un metodo di recupero integrato se dimenticato, a meno di reimpostare manualmente il PIN perdendo l'accesso alle note protette.)*

-   Accesso e Sincronizzazione Cloud: L'app permette di autenticarsi tramite Google Account. Utilizzando Firebase Auth con il provider Google, l'utente può effettuare il login direttamente dall'app (viene mostrata una schermata di Google Sign-In). Dopo l'autenticazione, Memori abilita la sincronizzazione cloud in tempo reale: ogni nota e cartella viene salvata sia localmente sia su Firestore (nel database online privato dell'utente). Ad esempio, salvando una nuova nota mentre si è online, l'app la scrive sul database locale e invoca `FirestoreNoteRepository.uploadOneNote` per salvarla anche nel cloud[. All'avvio, se l'utente è loggato, l'app richiama `getAllNotesFromCloud` e `getAllFoldersFromCloud` per recuperare eventuali contenuti creati su altri dispositivi. La sincronizzazione è bidirezionale ma non conflittuale: ogni nota ha un identificatore unico e viene sovrascritta/aggiornata univocamente; in caso di modifica sia locale che remota durante l'offline, l'app cerca di unire i dati o può prevalere l'ultima modifica (dettagli implementativi dipendono dalla logica scelta nel ViewModel). L'utente può sempre scegliere di non effettuare il login: in tal caso, i dati restano solo in locale (e l'app funziona completamente offline senza richiedere internet).

-   Integrazione AI Generativa: Una funzionalità distintiva di Memori è l'integrazione di un modello di AI generativo per migliorare l'esperienza di scrittura delle note. Ad esempio, l'utente potrebbe utilizzare l'AI per generare il testo di una nota a partire da un prompt, ottenere suggerimenti di completamento per appunti brevi, oppure creare un riepilogo di una nota lunga. Tecnicamente, il progetto include la libreria *Google Generative AI* (nome in codice "Gemini") nelle dipendenze. Attraverso questa libreria, l'app invia il contenuto (o le istruzioni fornite dall'utente) a un servizio di intelligenza artificiale e ottiene una risposta generata automaticamente. *Esempio d'uso:* l'utente potrebbe scrivere "Bozza email per cliente X..." e poi chiedere all'AI di completare la frase o suggerire il corpo dell'email. I risultati possono essere inseriti direttamente nella nota. Questa integrazione è progettata per essere non invasiva: l'AI viene invocata solo su richiesta esplicita dell'utente (ad es. premendo un pulsante "AI Assist" in fase di modifica nota). Nota tecnica: per utilizzare la funzione AI è necessario configurare una chiave API valida per i servizi Google AI generativa. La chiave va inserita nei file di configurazione (ad esempio usando il plugin *Secrets Gradle* e un file `local.properties` non incluso nel repository). Se la chiave non è impostata, le funzionalità AI resteranno disabilitate di default.

Istruzioni per Installazione Locale
-----------------------------------

Per installare ed eseguire localmente il progetto Memori sul proprio ambiente, seguire questi passaggi:

1.  Prerequisiti: Assicurarsi di aver installato Android Studio (Arctic Fox/Flamingo o più recente) con il plugin Kotlin aggiornato. È necessario avere il SDK di Android 14 (API 34) e le relative *Google Play Services* installate, poiché il progetto è configurato per girare su Android 14+. Inoltre, verificare di avere almeno JDK 17 configurato nel proprio ambiente, in quanto il plugin Android Gradle 8.8.0 richiede Java 17.

2.  Clonazione del Repository: Clonare la repository GitHub 'Tales06/Memori' sul proprio computer:

    bash

    CopiaModifica

    `git clone https://github.com/Tales06/Memori.git `

    Entrare nella directory del progetto:

    bash

    CopiaModifica

    `cd Memori `

3.  Configurazione di Firebase (opzionale per cloud sync): Se si intende utilizzare le funzionalità di login Google e sincronizzazione cloud, è necessario configurare un progetto Firebase:

    -   Creare un nuovo progetto su Firebase Console e abilitare Authentication (metodo di accesso Google) e Firestore.

    -   Registrare un'app Android nel progetto Firebase usando l'ID applicazione `com.example.app_memori` (come da configurazione del progetto). Scaricare il file `google-services.json` generato da Firebase e sostituire quello presente nella cartella `app/` del progetto.

    -   (Opzionale) Nella sezione Authentication di Firebase, configurare l'OAuth consent screen per l'accesso Google se richiesto.

4.  Configurazione chiavi API (opzionale per AI): Per abilitare l'integrazione AI, ottenere una API Key per il servizio di Google Generative AI (ad es. tramite Google Cloud Platform o programma sviluppatori Google AI). Inserire la chiave API nel file di configurazione locale: aprire (o creare) un file `local.properties` nella root del progetto e aggiungere una linea del tipo:

    ini

    CopiaModifica

    `GENERATIVE_AI_API_KEY=YOUR_API_KEY_HERE `

    Grazie al plugin *Secrets Gradle*, questa chiave può essere letta dall'app (ad esempio potrebbe essere accessibile tramite `BuildConfig` se configurato, benché nel codice corrente non vi sia un riferimento diretto -- assicurarsi di gestirne l'utilizzo nel codice relativo all'AI).

5.  Apertura del Progetto: Aprire Android Studio e selezionare "Open an existing project", quindi scegliere la cartella radice `Memori`. Android Studio provvederà a sincronizzare il progetto Gradle. Assicurarsi che il processo di sync completi senza errori (tutte le dipendenze verranno scaricate automaticamente grazie ai wrapper Gradle e al file `gradle/libs.versions.toml` che contiene le versioni.

6.  Esecuzione dell'App: Collegare un dispositivo Android con Android 14 (o avviare un emulator API 34) e premere Run (o usare la configurazione di esecuzione predefinita "app"). In alternativa, si può compilare l'APK di debug via riga di comando con Gradle:

    bash

    CopiaModifica

    `./gradlew assembleDebug `

    L'APK generato si troverà in `app/build/outputs/apk/debug/`. Installare l'APK sul dispositivo/emulatore manualmente se non si è eseguito direttamente da Android Studio.

7.  Primo Avvio: All'avvio dell'app, verrà mostrata la schermata di benvenuto (setup) con un breve onboarding. Si potrà scegliere il tema preferito. Se si vuole utilizzare la sincronizzazione cloud, cliccare su "Accedi con Google" nella pagina di login; altrimenti, è possibile proseguire in modalità offline. È consigliabile impostare anche un PIN se si prevede di creare cartelle protette.

8.  Configurazione Debug (opzionale): Il progetto include configurazioni di debug standard. Per vedere i log, utilizzare *Logcat* in Android Studio. L'app potrebbe stampare log utili (es. success/fallimento di login, caricamento Lottie, etc.) su Logcat per facilitare il debug durante lo sviluppo.

Esecuzione e Test del Progetto
------------------------------

Esecuzione Interattiva: Una volta installata, l'app può essere utilizzata per creare note e cartelle. Si suggerisce di provare il seguente flusso per familiarizzare con le funzionalità:

1.  Creare una nuova nota dalla schermata Home (es. tramite il pulsante flottante "➕"). Inserire un titolo e contenuto semplice, quindi salvarla.

2.  Creare una cartella (nel menu o se presente un pulsante "Nuova Cartella"), ad esempio "Personale". Spostare la nota creata nella nuova cartella (tramite l'opzione *Muovi in cartella* disponibile sulla nota).

3.  Provare a impostare la cartella "Personale" come protetta da PIN dalle opzioni della cartella. Inserire un PIN e ricordarlo.

4.  Uscire dalla cartella e provare a rientrare: verrà richiesto il PIN per accedere. Inserire il PIN per confermare che l'accesso funzioni.

5.  Contrassegnare la nota come preferita (ad esempio aprendo la nota e toccando l'icona a forma di stella). Tornare alla schermata "Preferiti" per verificare che la nota compaia lì.

6.  Archiviare la nota (dall'interno della nota o con un'azione rapida). Controllare che la nota sparisca dalla lista principale e compaia nella sezione Archivio.

7.  Provare la funzione di ricerca: creare più note con parole chiave riconoscibili, quindi usare la barra di ricerca per filtrare.

8.  (Se loggati con Google) Creare un'altra nota e verificare sul [Firebase Console > Firestore] che compaia nella raccolta `users/{yourUid}/notes`. Eliminare una nota dall'app e controllare che venga rimossa dal Firestore (e viceversa, provare ad aggiungere un documento manualmente su Firestore e riaprire l'app per vedere se la nota viene scaricata).

Esecuzione dei Test Automatizzati: Il progetto include alcuni test di base auto-generati. In particolare, c'è un *ExampleUnitTest* (test locale JUnit) e un *ExampleInstrumentedTest* (test strumentato Android) di default. Per eseguire i test:

-   Dal terminale, lanciare `./gradlew test` per eseguire i test JUnit locali (questi girano sulla JVM senza necessità di dispositivo).

-   Lanciare `./gradlew connectedAndroidTest` per eseguire eventuali test strumentati sull'emulatore/dispositivo connesso.

Attualmente, i test forniti sono perlopiù placeholder; non coprono le funzionalità specifiche dell'app (ad esempio non ci sono test unitari sui ViewModel o repository né test strumentati delle UI Compose). È consigliato aggiungere test aggiuntivi per garantire la stabilità dell'app, ad esempio testando la logica dei ViewModel (magari usando *Robolectric* per testare componenti Android in JVM) e test UI con *Espresso* o *Compose UI Testing* framework.

Note Tecniche Aggiuntive
------------------------

Architettura e Scelte Progettuali: L'app segue il pattern MVVM (Model-View-ViewModel), dividendo le responsabilità in modo chiaro. Le schermate UI (Compose Composables) fungono da *View* e osservano lo stato esposto dai ViewModel (spesso tramite StateFlow o LiveData). I ViewModel (ad es. `NoteViewModel`, `FolderViewModel`, `SignInViewModel`) contengono la logica di business dell'app: interagiscono con i repository per recuperare o modificare i dati e preparano gli stati da esporre alla UI. La layer dei dati (Model/Repository) comprende sia il database locale Room (via DAO) sia fonti remote (Firestore tramite repository dedicati e autenticazione tramite GoogleAuthClient). In questo modo, i dettagli di persistenza e cloud sono incapsulati nei repository, mantenendo i ViewModel il più possibile separati dall'implementazione specifica. I servizi esterni integrati sono principalmente Firebase (Auth e Firestore) per la persistenza cloud e Google API per l'AI. Il flusso tipico vede: azioni dell'utente catturate nella UI → chiamate al ViewModel appropriato → il ViewModel aggiorna i dati nel database locale o chiama i servizi cloud/AI → lo stato aggiornato viene emesso e la UI reagisce automaticamente (Compose ricompone le schermate osservando lo state). Questo garantisce un'app reattiva: ad esempio, aggiungendo una nota, la lista si aggiorna subito; se arriva una modifica dal cloud, grazie ai Flow osservati, l'interfaccia riflette immediatamente il cambiamento.

Librerie e Strumenti Utilizzati: Oltre alle tecnologie già menzionate (Compose, Room, DataStore, Firebase, Google AI), il progetto utilizza vari componenti per migliorare l'esperienza:

-   *Material3 Compose*: per interfacce moderne seguendo le linee guida Material Design v3.

-   *Navigation Compose*: per la navigazione dichiarativa fra composable screens.

-   *Kotlin Coroutines e Flow*: per gestione asincrona e reattiva dei dati (ad es. osservare il flusso di note dal database).

-   *Kotlinx Serialization (JSON)*: inclusa come dipendenza, utile per serializzare/deserializzare oggetti (ad esempio potrebbero usarla per convertire note in JSON se necessario per l'AI o per backup).

-   *Coil*: libreria leggera per caricare immagini (utilizzata per gestire le immagini di sfondo delle note in modo efficiente).

-   *Lottie Compose*: per riprodurre animazioni vettoriali (usato nella schermata di onboarding, dove un'animazione JSON viene mostrata in loop.

-   *Accompanist (System UI Controller)*: per gestire colori della status bar e della navigational bar in base al tema (così da avere un'immersione grafica completa).

-   *Palette KTX*: per estrarre colori dominanti dalle immagini (potrebbe essere usato per adattare il colore del testo al wallpaper della nota, migliorando la leggibilità automaticamente).

Problemi Noti:

-   *Compatibilità Android:* Attualmente la minSdk è impostata a 34 (Android 14), il che significa che l'app non potrà essere installata su dispositivi con versioni precedenti di Android. Questo è un requisito piuttosto stringente e potrebbe essere innalzato per sfruttare API moderne, ma potrebbe limitare molto la base utenti. Valutare se abbassare la minSdk (es. a 21 o 23 per supportare più dispositivi) qualora non si utilizzino API esclusive di Android 14.

-   *Funzione Promemoria:* La descrizione iniziale menziona *reminders*, ma attualmente non sembra esserci un sistema di notifiche integrato per le note. Probabilmente l'idea è di implementare in futuro la possibilità di associare alle note una scadenza/orario e ricevere una notifica come promemoria. Questo sarebbe un utile miglioramento futuro (ad oggi, se si elimina una nota viene eliminata definitivamente, senza passare da un cestino temporizzato).

-   *Validazione Input:* Non sono state dettagliate qui, ma è buona prassi assicurarsi che titoli o contenuti vuoti siano gestiti (ad es. evitando note senza titolo o con solo spazi). Implementare controlli lato UI o ViewModel per migliorare la robustezza.

-   *Gestione Conflitti Cloud:* In scenari di modifica offline e successiva sincronizzazione potrebbero verificarsi conflitti di versione delle note. Una possibile estensione futura è implementare una risoluzione dei conflitti più sofisticata (attualmente, l'ultimo edit vince, ma si potrebbero ad esempio tenere entrambi i contenuti o notificare l'utente).

-   *Ottimizzazione Performance:* Per dataset di note molto ampi, verificare le performance di ricerca e caricamento (attualmente le query di ricerca usano wildcard su titolo e contenuto il che su migliaia di note potrebbe essere poco efficiente senza indici full-text).

Suggerimenti per Contribuire:\
Contributi al progetto sono benvenuti! Se desideri contribuire:

-   Segnalazione Bug/Feature: Apri una *Issue* su GitHub descrivendo il problema o la proposta di nuova funzionalità. Fornisci dettagli su come riprodurre il bug o casi d'uso per la feature.

-   Sviluppo: Sentiti libero di effettuare un fork del repository e creare una *Pull Request* con le tue modifiche. Prima di inviare la PR, assicurati di aver testato le modifiche sia in locale sia (se possibile) su un dispositivo reale. È importante mantenere lo stile di codice consistente con quello esistente (linguaggio Kotlin, usare gli Android KTX, pattern MVVM già in uso, ecc.). Ad esempio, continua a utilizzare i repository per accedere ai dati invece di chiamare direttamente DAO o servizi esterni nella UI.

-   Documentazione: Aggiorna il README (o la documentazione in-line nei commenti) se introduci cambiamenti significativi. Una documentazione chiara aiuta chiunque utilizzi il progetto a capire le nuove funzionalità.

-   Best Practice: Cerca di seguire le best practice Android (ad esempio gestione dei lifecycles in Compose, evitare *memory leak* non conservando riferimenti di Context in ViewModel, etc.). Se aggiungi nuove dipendenze, aggiornare il file `gradle/libs.versions.toml` assicurandoti di mantenere allineate le versioni.

-   Collaborazione: Discuti le idee complesse aprendo una issue o interagendo con i maintainer del progetto prima di implementarle, così da assicurarti che siano in linea con la vision di Memori.

In conclusione, Memori è un progetto completo e moderno che unisce le più recenti tecnologie Android per fornire un'app di note ricca di funzionalità. Che tu stia usando l'app per uso personale o contribuendo al suo sviluppo, speriamo che questa documentazione ti sia utile per comprendere il contesto, la struttura e il funzionamento interno del progetto. Buon lavoro con Memori!
### Interactive Diagram

```gitdiagram
# Nodes (Components & Relationships)
UI:JetpackComposeUI --|> ViewModel:NoteViewModel  
UI:JetpackComposeUI --> ViewModel:FolderViewModel  
UI:JetpackComposeUI --> ViewModel:SignInViewModel  
ViewModel:NoteViewModel --> LocalDB:RoomDatabase (NoteDatabase & DAO)  
ViewModel:NoteViewModel --> CloudDB:Firestore (NoteRepository)  
ViewModel:FolderViewModel --> LocalDB:RoomDatabase (NoteDatabase)  
ViewModel:FolderViewModel --> CloudDB:Firestore (FolderRepository)  
ViewModel:SignInViewModel --> Auth:GoogleAuthClient (Firebase Auth)  

# Flows (Example Flows)
User -> UI -> NoteViewModel -> NoteDatabase  
NoteDatabase -> Firestore (sync if online)  
User -> UI -> SignInViewModel -> GoogleAuthClient -> Firestore enabled  
