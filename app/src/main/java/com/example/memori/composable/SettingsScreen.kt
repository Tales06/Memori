package com.example.memori.composable

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.memori.auth.GoogleAuthClient
import com.example.memori.auth.SignInState
import com.example.memori.auth.SignInViewModel
import com.example.memori.auth.UserData
import com.example.memori.database.NoteDatabase
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import com.example.memori.preference.ThemePreferences
import com.example.memori.preference.UserPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * Displays the Settings screen of the application, allowing users to manage their profile,
 * theme preferences, and synchronization settings.
 *
 * @param navController The [NavHostController] used for navigation between screens.
 * @param context The [Context] used for accessing resources and preferences.
 * @param signInViewModel The [SignInViewModel] managing Google sign-in state and actions.
 * @param viewModel The [NoteViewModel] for managing note data (default provided).
 * @param folderViewModel The [FolderViewModel] for managing folder data (default provided).
 *
 * This composable provides:
 * - User profile display and Google sign-in/out functionality.
 * - Theme selection and navigation to theme settings.
 * - Synchronization management for notes and folders with cloud support.
 * - Dialog prompts for enabling/disabling synchronization.
 * - Loading indicator during sign-in/out processes.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    signInViewModel: SignInViewModel,
    viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            context = context,
            repository = NotesRepository(
                NoteDatabase.getDatabase(context).noteDao()
            )
        )
    ),
    folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            context = context,
            repository = FolderRepository(
                NoteDatabase.getDatabase(context).folderDao()
            )
        )
    )


) {


    val state by signInViewModel.state.collectAsStateWithLifecycle()
    val googleAuthClient by remember { mutableStateOf(GoogleAuthClient(context)) }
    val isLoading by signInViewModel.isLoading.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    var showDialogForConnection by remember { mutableStateOf(false) }

    val isSyncEnabled = UserPreferences.isSyncEnabled(context).collectAsState(initial = false).value

    val cloudIsOnline by viewModel.isCloudOnline.collectAsState()


    val leadingIconForNetwork = if (cloudIsOnline && isSyncEnabled) {
        Icons.Outlined.CloudDone
    } else {
        Icons.Outlined.CloudOff
    }


    /**
     * Remembers a launcher for the Google Sign-In activity result.
     *
     * When the sign-in activity returns a result, this launcher checks if the result is successful (`RESULT_OK`).
     * If successful, it launches a coroutine to:
     * 1. Sign in with the returned intent data using `googleAuthClient`.
     * 2. Passes the sign-in result to the `signInViewModel`.
     * 3. Updates the account information in the `signInViewModel` with the currently signed-in user.
     *
     * This is typically used to handle Google authentication flow in a Jetpack Compose screen.
     */

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { res ->
            if (res.resultCode == RESULT_OK) {
                scope.launch {
                    val signInRes = googleAuthClient.signInWithIntent(
                        res.data ?: return@launch
                    )
                    signInViewModel.onSignInResult(signInRes)
                    signInViewModel.updateAccount(googleAuthClient.getSignedInUser())
                }
            }
        }
    )


    val iconForTheme = ThemePreferences.getTheme(context).collectAsState(initial = "SYSTEM").value


    if (state.isSignInSuccessful) {
        ToastOnce(context, "Login successful", key = "login_successful")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }


            if (isLoading) {
                LoadingIndicator()
            }

            ProfileScreen(

                onSignOut = {
                    scope.launch {
                        signInViewModel.onSignInStart()
                        googleAuthClient.signOut()
                        signInViewModel.onSignInEnd()
                        Toast.makeText(
                            context,
                            "Logout successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                account = googleAuthClient.getSignedInUser(),
                onSignIn = {
                    if (cloudIsOnline) {
                        signInViewModel.onSignInStart()
                        launcher.launch(googleAuthClient.getSignInIntent())
                    } else {
                        showDialogForConnection = true

                    }
                },
                state = state,
            )

            SettingCard(
                title = "Theme",
                icon = when (iconForTheme) {
                    "SYSTEM" -> if (isSystemInDarkTheme()) Icons.Default.DarkMode else Icons.Default.LightMode
                    "LIGHT" -> Icons.Default.LightMode
                    "DARK" -> Icons.Default.DarkMode
                    else -> Icons.Default.LightMode
                },
                content = "Change the theme of the app",
                onClick = {
                    scope.launch {
                        navController.navigate("set_theme_on_settings") {
                            popUpTo("settings") {
                                inclusive = true
                            }
                        }
                    }
                }
            )


            SettingCard(
                title = "Synchronization",
                icon = Icons.Default.CloudSync,
                content = "Manage your notes in the cloud",
                onClick = {
                    if (state.isSignInSuccessful) {

                        showDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            "You need to login to sync notes",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            if (state.isSignInSuccessful) {

                SettingCard(
                    title = "Status Network",
                    icon = leadingIconForNetwork,
                    content = if (cloudIsOnline && isSyncEnabled) "Cloud is online" else "Cloud is offline",
                )
            }
        }


        /**
         * Displays a confirmation dialog for enabling or disabling synchronization.
         *
         * When `showDialog` is true, this dialog prompts the user to confirm their action regarding synchronization.
         * - If the user confirms, it retrieves the current user's ID from Firebase Authentication.
         * - Calls `syncAllFolders` on `folderViewModel` and `syncAllNotes` on `viewModel` to perform synchronization actions.
         * - The dialog is dismissed after the action.
         *
         * @param showDialog Controls the visibility of the dialog.
         * @param onDismissRequest Callback to handle dialog dismissal.
         * @param onConfirmation Callback executed when the user confirms the action.
         * @param dialogTitle The title displayed at the top of the dialog.
         * @param dialogText The message shown in the dialog, which changes based on the synchronization state.
         * @param icon The icon displayed in the dialog, representing synchronization.
         */
        if (showDialog) {

            GenericAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    val userID = Firebase.auth.currentUser?.uid ?: return@GenericAlertDialog

                    if (isSyncEnabled) {
                        // Disables synchronization
                        scope.launch {

                            UserPreferences.setSyncEnabled(context, false)
                        }
                    } else {
                        // Enables synchronization
                        folderViewModel.syncAllFolders(userID, context)
                        viewModel.syncAllNotes(userID)
                        scope.launch {

                            UserPreferences.setSyncEnabled(context, true)
                        }
                    }
                    showDialog = false
                },
                dialogTitle = "Synchronization",
                dialogText = if (isSyncEnabled)
                    "Are you sure you want to disable synchronization?"
                else
                    "Do you want to enable synchronization?",
                icon = Icons.Default.CloudSync
            )
        }
        if (showDialogForConnection) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning Icon",
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onDismissRequest = { showDialogForConnection = false },
                title = { Text("Connection Error") },
                text = { Text("You need to be connected to the internet to perform this action.") },
                confirmButton = {
                    TextButton(onClick = { showDialogForConnection = false }) {
                        Text("OK")
                    }
                }
            )
        }

    }
}


/**
 * Displays a generic alert dialog with customizable title, text, icon, and actions.
 *
 * @param onDismissRequest Callback invoked when the dialog is dismissed.
 * @param onConfirmation Callback invoked when the confirm button is clicked.
 * @param dialogTitle The title text to display in the dialog.
 * @param dialogText The main message text to display in the dialog.
 * @param icon The icon to display at the top of the dialog.
 *
 * This composable shows an [AlertDialog] with a confirm and dismiss button.
 */
@Composable
fun GenericAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

/**
 * A composable function that displays a settings card with a title, icon, content, and an edit button.
 *
 * @param title The title text to display on the card.
 * @param icon The [ImageVector] icon to display at the start of the card.
 * @param content The content or description text to display below the title.
 * @param onClick The callback to be invoked when the "Edit" button is clicked.
 */
@Composable
fun SettingCard(
    title: String,
    icon: ImageVector,
    content: String,
    onClick: () -> Unit = {}
) {

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = content, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
            }
            if (title != "Status Network") {

                TextButton(onClick = onClick) {
                    Text("Edit", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}


/**
 * Composable function that displays the user's profile information in a card layout.
 *
 * @param account The [UserData] object containing the user's account information, or null if not signed in.
 * @param onSignOut Lambda function to be invoked when the user clicks the "Logout" button.
 * @param onSignIn Lambda function to be invoked when the user clicks the "Login" button.
 * @param state The [SignInState] object representing the current sign-in state.
 *
 * The card displays the user's profile picture (or a default icon if not available), username,
 * and a button that toggles between "Login" and "Logout" based on the sign-in state.
 */
@Composable
fun ProfileScreen(
    account: UserData?,
    onSignOut: () -> Unit,
    onSignIn: () -> Unit,
    state: SignInState,
) {

    Log.e("Dentro al profilescreen valore di isSignIn", state.isSignInSuccessful.toString())



    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (account?.profilePicture != null) {
                AsyncImage(
                    model = account.profilePicture,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(32.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(32.dp)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (account?.username != null) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = account.username, style = MaterialTheme.typography.titleMedium)
                }
            }
            Button(
                onClick = {

                    if (state.isSignInSuccessful) {
                        onSignOut()
                    } else {
                        onSignIn()
                    }
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {

                Text(
                    text = if (state.isSignInSuccessful) "Logout" else "Login",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp

                )
            }
        }
    }

}
