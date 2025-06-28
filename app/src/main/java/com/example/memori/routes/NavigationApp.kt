/**
 * MainNavigation is the central composable function responsible for handling the navigation graph
 * of the Memori app. It defines all navigation routes, their arguments, and the corresponding
 * composable screens to display for each route.
 *
 * @param navController The NavHostController used to manage app navigation.
 * @param modifier Modifier for styling and layout adjustments.
 * @param showWelcome Boolean flag to determine if the setup flow should be shown as the start destination.
 * @param searchExpanded Boolean indicating if the search UI is expanded.
 * @param onThemeSelected Callback invoked when a theme is selected during setup or in settings.
 * @param onSearchExpanded Callback invoked when the search expansion state changes.
 * @param context The Android Context, required for various operations such as accessing preferences and databases.
 *
 * This function:
 * - Initializes required ViewModels and repositories for notes, folders, and themes.
 * - Handles Google authentication and sync logic with Firebase.
 * - Sets up system UI colors using Accompanist.
 * - Defines navigation routes for home, setup, authentication, folder/note details, settings, and more.
 * - Manages navigation transitions and state updates based on user actions and authentication results.
 *
 * All navigation destinations are defined within the NavHost, and each composable screen receives
 * the necessary dependencies and callbacks for proper operation.
 */
package com.example.memori.routes

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.memori.MainActivity
import com.example.memori.auth.SignInScreen
import com.example.memori.auth.GoogleAuthClient
import com.example.memori.auth.PrivacyInfo
import com.example.memori.auth.SignInViewModel
import com.example.memori.auth.SyncChoiceScreen
import com.example.memori.composable.ArchivePage
import com.example.memori.composable.DownloadContentFromCloudToDatabase
import com.example.memori.composable.FavoritesScreen
import com.example.memori.composable.FolderNotesScreen
import com.example.memori.composable.HomeScreen
import com.example.memori.composable.PinSetupScreen
import com.example.memori.composable.ProtectedFolderInfoScreen
import com.example.memori.composable.ScreenModifiedNotes
import com.example.memori.composable.ScreenNotes
import com.example.memori.composable.SettingsScreen
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import com.example.memori.database.theme_data.ThemeViewModel
import com.example.memori.database.theme_data.ThemeViewModelFactory
import com.example.memori.preference.ThemePreferences
import com.example.memori.preference.UserPreferences
import com.example.memori.preference.setHasSeenSetup
import com.example.memori.setup.SetupCompleteScreen
import com.example.memori.setup.SetupPage
import com.example.memori.setup.ThemeSetupPageWrapper
import com.example.memori.setup.ThemeType
import com.example.memori.sync.FirestoreNoteRepository
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@ExperimentalMaterial3Api
@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier,
    showWelcome: Boolean = false,
    searchExpanded: Boolean,
    onThemeSelected: (ThemeType) -> Unit = {},
    onSearchExpanded: (Boolean) -> Unit = {},
    context: Context,

) {
    /**
     * Sets up the main navigation graph of the application using Jetpack Compose's [NavHost].
     *
     * This function initializes and provides all necessary ViewModels and repositories, manages the theme and system UI colors,
     * and handles user authentication and synchronization with Firebase and Google Sign-In.
     *
     * Included navigation destinations:
     * - Home, Favorites, Settings, Archive, and Setup screens
     * - Theme selection and configuration
     * - Google Sign-In and sync configuration
     * - Folder and note management, including protected and edited notes
     * - Downloading notes from the cloud and setup completion
     *
     * Main features:
     * - Initializes ViewModels for authentication, notes, folders, and theme management
     * - Handles Google authentication and sync preferences
     * - Updates system bar and navigation bar colors based on the current theme
     * - Navigates between screens based on user actions and authentication state
     * - Supports passing arguments (IDs, names) between composable destinations
     * - Ensures correct navigation stack management with `popUpTo` and `inclusive` flags
     *
     * @param navController The [NavHostController] used for navigation between composable destinations.
     * @param context The current [Context] used for initializing ViewModels and repositories.
     * @param showWelcome Boolean flag to determine whether to show the welcome/setup flow.
     * @param searchExpanded Boolean flag indicating if the search UI is expanded.
     * @param onSearchExpanded Callback to handle changes in the search expansion state.
     * @param onThemeSelected Callback invoked when a theme is selected.
     */


    /**
     * Imposta il grafo di navigazione principale dell'applicazione utilizzando [NavHost] di Jetpack Compose.
     *
     * Questa funzione inizializza e fornisce tutte le ViewModel e repository necessari, gestisce il tema e i colori della UI di sistema,
     * e si occupa dell'autenticazione utente e della sincronizzazione con Firebase e Google Sign-In.
     *
     * Destinazioni di navigazione incluse:
     * - Schermate Home, Preferiti, Impostazioni, Archivio e Setup
     * - Selezione e configurazione del tema
     * - Google Sign-In e configurazione della sincronizzazione
     * - Gestione di cartelle e note, incluse note protette e modificate
     * - Download delle note dal cloud e completamento del setup
     *
     * Caratteristiche principali:
     * - Inizializza le ViewModel per autenticazione, note, cartelle e gestione del tema
     * - Gestisce autenticazione Google e preferenze di sincronizzazione
     * - Aggiorna i colori della barra di sistema e della barra di navigazione in base al tema corrente
     * - Naviga tra le schermate in base alle azioni dell'utente e allo stato di autenticazione
     * - Supporta il passaggio di argomenti (ID, nomi) tra le destinazioni composable
     * - Garantisce una corretta gestione dello stack di navigazione con `popUpTo` e flag `inclusive`
     *
     * @param navController Il [NavHostController] utilizzato per la navigazione tra le destinazioni composable.
     * @param context Il [Context] corrente utilizzato per l'inizializzazione di ViewModel e repository.
     * @param showWelcome Flag booleano per determinare se mostrare il flusso di benvenuto/setup.
     * @param searchExpanded Flag booleano che indica se la UI di ricerca è espansa.
     * @param onSearchExpanded Callback per gestire i cambiamenti dello stato di espansione della ricerca.
     * @param onThemeSelected Callback invocato quando viene selezionato un tema.
     */


    val scope = rememberCoroutineScope()
    val googleAuthClient by lazy {

        GoogleAuthClient(
            context = context,
        )

    }
    val viewModel: SignInViewModel = viewModel()

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            context = context,
            repository = NotesRepository(
                NoteDatabase.getDatabase(context).noteDao()
            )
        )
    )

    val folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            context = context,
            repository = FolderRepository(
                NoteDatabase.getDatabase(context).folderDao()
            )
        )
    )

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(
            app = context.applicationContext as Application
        )
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    val sysBarColorScheme = MaterialTheme.colorScheme

    val systemUiController = rememberSystemUiController()



    /**
     * Launches a side-effect that performs the following actions:
     * - Checks if the user is already signed in using the provided Google authentication client.
     * - Retrieves the user's sync preference from [UserPreferences].
     * - If sync is enabled and a user is signed in, triggers synchronization of all notes for the user via [noteViewModel].
     * - Logs the sync status ("Sync Enabled" or "Sync Disabled").
     * - Sets the system bars and navigation bar colors using the provided [sysBarColorScheme] and [systemUiController].
     *
     * @param sysBarColorScheme The color scheme to apply to system bars.
     * @param systemUiController Controller to manage system UI colors.
     * @param viewModel The ViewModel responsible for authentication checks.
     * @param googleAuthClient The Google authentication client.
     * @param context The current context, used to access user preferences.
     * @param noteViewModel The ViewModel responsible for note synchronization.
     */
    LaunchedEffect(Unit, sysBarColorScheme) {
        val user = Firebase.auth.currentUser

        viewModel.checkIfUserAlreadySignedIn(googleAuthClient)
        val isEnabled = UserPreferences.isSyncEnabled(context).first()

        if (isEnabled && user != null) {
            noteViewModel.syncAllNotes(user.uid)
            Log.e("Sync", "Sync Enabled")
        } else {
            Log.e("Sync", "Sync Disabled")
        }

        systemUiController.setSystemBarsColor(
            color = sysBarColorScheme.background
        )
        systemUiController.setNavigationBarColor(
            color = sysBarColorScheme.surface
        )
    }





    NavHost(
        navController = navController,
        startDestination = if (showWelcome) "setup" else "home",

        ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                searchExpanded = searchExpanded,
                onSearchExpanded = onSearchExpanded,
            )
        }

        composable("protected_info") {
            ProtectedFolderInfoScreen(
                onCancel = {
                    navController.navigate("home") {
                        popUpTo("protected_info") {
                            inclusive = true
                        }
                    }
                },
                onProceed = {
                    navController.navigate("pin_setup") {
                        popUpTo("protected_info") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("pin_setup") {
            PinSetupScreen(
                navController = navController
            )
        }

        composable(route = "choice_theme") {
            ThemeSetupPageWrapper(
                onThemeSelected = {
                    onThemeSelected(it)

                },
                onContinue = {
                    navController.navigate("setup_sync_choice") {
                        popUpTo("choice_theme") {
                            inclusive = true
                        }
                    }
                },
            )

        }

        composable(route = "set_theme_on_settings") {
            val currentTheme by themeViewModel.selectedTheme.collectAsState()

            ThemeSetupPageWrapper(
                initialTheme = currentTheme,
                onThemeSelected = { type ->
                    themeViewModel.setTheme(type)
                    onThemeSelected(type)
                },
                onContinue = {
                    navController.navigate("settings") {
                        popUpTo("set_theme_on_settings") {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable("setup") {
            SetupPage(
                onContinue = {
                    navController.navigate("choice_theme") {
                        popUpTo("setup") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                navController
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                context = context,
                signInViewModel = viewModel
            )
        }
        composable(route = "pageNotes") {
            ScreenNotes(navController)
        }
        composable(
            route = "modifiedNotes/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            if (id != -1) {
                ScreenModifiedNotes(id = id, navController = navController)
            } else {
                Log.e("Nav", "Id è -1")
            }
        }

        composable("archive") {
            ArchivePage(
                navController = navController
            )
        }
        composable("setup_sync_choice") {
            SyncChoiceScreen(
                onChooseSync = { navController.navigate("privacy_policy") },
                onSkipSync = { navController.navigate("setup_complete") },
                onBack = {
                    navController.navigate("choice_theme") {
                        popUpTo("setup_sync_choice") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /**
         * Composable route for handling Google Sign-In setup within the app's navigation.
         *
         * This route manages the Google authentication flow, user preferences for sync, and navigation
         * based on the sign-in result. It uses a launcher for the Google sign-in intent and processes
         * the result asynchronously. Upon successful sign-in, it updates the user account, enables sync,
         * and triggers synchronization of folders and notes with Firebase. Depending on whether the user
         * has existing notes in the cloud, it navigates to either the setup completion or note download screen.
         *
         * UI is provided by [SignInScreen], which allows the user to initiate sign-in, go back, or skip the process.
         *
         * Key responsibilities:
         * - Launches Google sign-in and handles the result.
         * - Updates user preferences and triggers data synchronization.
         * - Navigates to appropriate screens based on sign-in and cloud data state.
         * - Displays a toast message upon successful login.
         *
         * Dependencies:
         * - [googleAuthClient]: Handles Google authentication.
         * - [viewModel]: Manages sign-in state and user account.
         * - [folderViewModel], [noteViewModel]: Handle folder and note synchronization.
         * - [UserPreferences]: Manages sync preferences.
         * - [FirestoreNoteRepository]: Accesses notes in the cloud.
         * - [navController]: Controls navigation between screens.
         */
        composable("setup_google_login") {

            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { res ->
                    if (res.resultCode == RESULT_OK) {
                        scope.launch {
                            val signInRes = googleAuthClient.signInWithIntent(
                                res.data ?: return@launch
                            )

                            viewModel.onSignInResult(signInRes)
                            viewModel.updateAccount(googleAuthClient.getSignedInUser())

                            UserPreferences.setSyncEnabled(
                                context,
                                true
                            )

                            UserPreferences.isSyncEnabled(context).collect {
                                if (it) {
                                    val userID = Firebase.auth.currentUser?.uid ?: return@collect
                                    folderViewModel.syncAllFolders(userID, context)
                                    noteViewModel.syncAllNotes(userID)
                                }
                            }
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSignInSuccessful) {


                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        context,
                        "Login effected successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Valore di isSignIn launch nav", state.isSignInSuccessful.toString())
                    scope.launch {
                        val userId = Firebase.auth.currentUser?.uid
                        folderViewModel.syncAllFolders(userId.toString(), context)
                        noteViewModel.syncAllNotes(userId.toString())
                    }

                    val query = FirestoreNoteRepository()
                        .getAllNotesFromCloud(Firebase.auth.currentUser?.uid.toString())

                    if(query.isEmpty()) {

                        navController.navigate("setup_complete") {
                            popUpTo("setup_google_login") {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate("download_note") {
                            popUpTo("setup_google_login") {
                                inclusive = true
                            }
                        }
                    }

                }
            }

            SignInScreen(
                signInViewModel = viewModel,
                onLoggedIn = {
                    scope.launch {
                        Log.e("Valore di isSignIn", state.isSignInSuccessful.toString())
                        launcher.launch(googleAuthClient.getSignInIntent())
                        Log.e("Valore di isSignIn", state.isSignInSuccessful.toString())
                    }
                },
                onBack = {
                    navController.navigate("privacy_policy") {
                        popUpTo("setup_google_login") {
                            inclusive = true
                        }
                    }
                },
                onSkip = {
                    navController.navigate("setup_complete") {
                        popUpTo("setup_sync_choice") {
                            inclusive = true
                        }
                    }
                }
            )

        }

        composable("download_note") {
            DownloadContentFromCloudToDatabase(
                onDownloadComplete = {
                    scope.launch {
                        navController.navigate("setup_complete") {
                            popUpTo("download_note") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        composable("setup_complete") {
            SetupCompleteScreen(
                onContinue = {
                    scope.launch {

                        context.setHasSeenSetup(true)

                        navController.navigate("home") {
                            popUpTo("setup_complete") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(
            route = "folderNotes/{folderId}/{folderName}",
            arguments = listOf(
                navArgument("folderId") { type = NavType.IntType },
                navArgument("folderName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getInt("folderId") ?: -1
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""

            if(folderId != -1) {
                FolderNotesScreen(
                    folderId = folderId,
                    folderName = folderName,
                    navController = navController,

                )
            } else {
                Log.e("Nav", "Idfolder è -1")
            }
        }

        composable(
            route = "pageNotes/{folderId}/{folderName}",
            arguments = listOf(
                navArgument("folderId") { type = NavType.IntType },
                navArgument("folderName") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val folderId = backStackEntry.arguments?.getInt("folderId") ?: -1
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""

            if(folderId != -1) {
                ScreenNotes(
                    navController = navController,
                    folderId = folderId,
                    folderName = folderName
                )
            } else {
                Log.e("Nav", "Idfolder è -1")
            }
        }

        /**
         * Defines a composable navigation route for displaying or modifying notes within a specific folder.
         *
         * Route: "modifiedNotes/{noteId}/{folderId}/{folderName}"
         *
         * Arguments:
         * - noteId (Int): The unique identifier of the note to be modified.
         * - folderId (Int): The unique identifier of the folder containing the note.
         * - folderName (String): The name of the folder containing the note.
         *
         * Behavior:
         * - Extracts the noteId, folderId, and folderName from the navigation arguments.
         * - If a valid folderId is provided (not -1), it displays the [ScreenModifiedNotes] composable,
         *   passing the extracted noteId, folderId, folderName, and the navigation controller.
         */
        composable(
            route = "modifiedNotes/{noteId}/{folderId}/{folderName}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType },
                navArgument("folderId") { type = NavType.IntType },
                navArgument("folderName") { type = NavType.StringType }
            )
        /**
         * Lambda expression that receives a [NavBackStackEntry] as a parameter.
         * Typically used in Jetpack Compose navigation to define the content
         * that should be displayed for a particular navigation destination.
         *
         * @param backStackEntry The navigation back stack entry associated with the current destination.
         */
        ) { backStackEntry ->

            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            val folderId = backStackEntry.arguments?.getInt("folderId") ?: -1
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""

            if(folderId != -1) {
                ScreenModifiedNotes(
                    id = noteId,
                    navController = navController,
                    folderId = folderId,
                    folderName = folderName
                )
            }
        }

        composable(
            route = "privacy_policy",

        ) {
            PrivacyInfo(
                onContinueClicked = {
                    navController.navigate("setup_google_login") {
                        popUpTo("privacy_policy") {
                            inclusive = true
                        }
                    }
                }
            )
        }






    }
}

