# Memori

*Memori* is an Android application for creating and organizing personal notes, designed with a modern architecture and advanced features. The app offers a system of notes and folders (including PIN-protected folders), optional cloud synchronization, and even generative AI integration to enrich content. Below you’ll find the main features, project architecture (with an interactive **gitdiagram**), installation and run instructions, and other useful information.

## Purpose and Features

- **Offline notes & reminders:**  
  Create text notes stored locally on the device via a *Room* database (with **Notes** and **Folders** entities). Organize notes into folders (e.g., favorites, archived, etc.).

- **PIN-protected folders:**  
  Lock specific folders with a PIN code to keep certain notes private. The app stores a hashed PIN using *Jetpack DataStore* and requires the PIN to view or unlock protected folders.

- **Optional cloud sync:**  
  Sign in with Google to enable backup and sync of notes to the cloud. Uses **Firebase Authentication** (Google sign-in) and **Cloud Firestore** for real-time storage. Sync is bidirectional: local notes upload, existing cloud notes download at startup. You can also skip login and use the app fully offline.

- **Generative AI integration:**  
  Experimentally generate text content from images and prompts via the **Google Generative AI** API (Gemini model). Send an image and descriptive prompt; receive auto-generated text (e.g., description or short story) inserted into the note.

- **Customizable experience:**  
  On first launch, choose theme (Light, Dark, or System default). UI built with **Jetpack Compose** (Material3) for a modern, responsive design. Includes trash/archive, favorites, and guided setup screens.

## Architecture & Logical Flow

Memori follows an **MVVM (Model-View-ViewModel)** pattern:

1. **UI (View):**  
   Jetpack Compose screens (Home, Notes, Folders, Settings, etc.) observe state from ViewModels via Flow/StateFlow.

2. **ViewModels:**  
   - `NoteViewModel`: CRUD operations for notes  
   - `FolderViewModel`: Folder management  
   - `SignInViewModel`: Login state  
   - `GenerativeViewModel`: Calls AI model  
   They interact with repositories and services, then expose state to the UI.

3. **Data Layer (Models/Repositories):**  
   - **Room Database** (`NoteDatabase` with DAOs for Note & Folder)  
   - **FirestoreNoteRepository** for cloud sync (`users/{userId}/notes`)  
   - **GoogleAuthClient** for Firebase/Google sign-in  
   - **DataStore** for encrypted PIN & user preferences

4. **External Services:**  
   - **Firebase Auth & Firestore** for cloud backend  
   - **Google Generative AI SDK** for text generation  

User actions → ViewModel → local DB or cloud/AI → updated state → UI reflects changes immediately.

### Interactive Diagram

```gitdiagram
# Nodes (Components & Relationships)
UI:JetpackComposeUI --|> ViewModel:NoteViewModel  
UI:JetpackComposeUI --> ViewModel:FolderViewModel  
UI:JetpackComposeUI --> ViewModel:SignInViewModel  
UI:JetpackComposeUI --> ViewModel:GenerativeViewModel  
ViewModel:NoteViewModel --> LocalDB:RoomDatabase (NoteDatabase & DAO)  
ViewModel:NoteViewModel --> CloudDB:Firestore (NoteRepository)  
ViewModel:FolderViewModel --> LocalDB:RoomDatabase (NoteDatabase)  
ViewModel:FolderViewModel --> CloudDB:Firestore (FolderRepository)  
ViewModel:SignInViewModel --> Auth:GoogleAuthClient (Firebase Auth)  
ViewModel:GenerativeViewModel --> Service:GoogleGenerativeAI (Gemini model)  

# Flows (Example Flows)
User -> UI -> NoteViewModel -> NoteDatabase  
NoteDatabase -> Firestore (sync if online)  
User -> UI -> SignInViewModel -> GoogleAuthClient -> Firestore enabled  
User -> UI -> GenerativeViewModel -> GoogleGenerativeAI -> GeneratedText
