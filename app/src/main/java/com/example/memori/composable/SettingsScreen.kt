package com.example.memori.composable

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.memori.auth.GoogleAuthClient
import com.example.memori.auth.SignInState
import com.example.memori.auth.SignInViewModel
import com.example.memori.auth.UserData
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.database.NoteDatabase
import com.example.memori.database.note_data.NoteViewModel
import com.example.memori.database.note_data.NoteViewModelFactory
import com.example.memori.database.note_data.NotesRepository
import com.example.memori.preference.ThemePreferences
import com.example.memori.preference.UserPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    signInViewModel: SignInViewModel,
    viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            repository = NotesRepository(
                NoteDatabase.getDatabase(context).noteDao()
            )
        )
    ),
    folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
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


    val isSyncEnabled = UserPreferences.isSyncEnabled(context).collectAsState(initial = false).value


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { res ->
            if(res.resultCode == RESULT_OK) {
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

    LaunchedEffect(state.isSignInSuccessful) {
        Log.e("Dentro al launchedeffect valore di isSignIn", state.isSignInSuccessful.toString())
        if(state.isSignInSuccessful){
            delay(1500)
            Toast.makeText(
                context,
                "Login successful",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("Dentro al launchedeffect valore di isSignIn", state.isSignInSuccessful.toString())
        }

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


            if(isLoading){
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
                    signInViewModel.onSignInStart()
                    launcher.launch(googleAuthClient.getSignInIntent())
                },
                state = state
            )

            SettingCard(
                title = "Theme",
                icon = when(iconForTheme) {
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
                    if(state.isSignInSuccessful){

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

            if(showDialog){

                GenericAlertDialog(
                    onDismissRequest = { showDialog = false },
                    onConfirmation = {
                        val userID = Firebase.auth.currentUser?.uid ?: return@GenericAlertDialog
                        folderViewModel.syncAllFolders(userID, context)
                        viewModel.syncAllNotes(userID)
                        showDialog = false

                    },
                    dialogTitle = "Synchronization",
                    dialogText = if (isSyncEnabled) {
                        "Are you sure you want to disable synchronization?"
                    } else {
                        "Do you want to enable synchronization?"
                    },
                    icon = Icons.Default.CloudSync

                )
            }
        }
    }
}

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

@Composable
fun SettingCard(
    title: String,
    icon: ImageVector,
    content: String,
    onClick: () -> Unit
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
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = content, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
            }
            TextButton(onClick = onClick) {
                Text("Edit", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
fun ProfileScreen(
    account: UserData?,
    onSignOut: () -> Unit,
    onSignIn: () -> Unit,
    state: SignInState
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
            if(account?.profilePicture != null){
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
            if(account?.username != null){
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
            ){

                Text(
                    text = if(state.isSignInSuccessful) "Logout" else "Login",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp

                )
            }
        }
    }
}
