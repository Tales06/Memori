package com.example.memori.routes

import android.app.Activity.RESULT_OK
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
    val scope = rememberCoroutineScope()
    val googleAuthClient by lazy {

        GoogleAuthClient(
            context = context,
        )

    }
    val viewModel: SignInViewModel = viewModel()

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(context).noteDao()
            )
        )
    )

    val folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            repository = FolderRepository(
                NoteDatabase.getDatabase(context).folderDao()
            )
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sysBarColorScheme = MaterialTheme.colorScheme

    val systemUiController = rememberSystemUiController()

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
            ThemeSetupPageWrapper(
                onThemeSelected = {
                    onThemeSelected(it)
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
                onChooseSync = { navController.navigate("setup_google_login") },
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
                    navController.navigate("setup_sync_choice") {
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

        composable(
            route = "modifiedNotes/{noteId}/{folderId}/{folderName}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType },
                navArgument("folderId") { type = NavType.IntType },
                navArgument("folderName") { type = NavType.StringType }
            )
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






    }
}

