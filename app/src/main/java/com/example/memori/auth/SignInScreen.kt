package com.example.memori.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memori.R
import com.example.memori.animation.AnimBackground

/**
 * Composable function that displays the Sign-In screen.
 * This screen allows the user to log in using Google, go back to the previous screen, or skip the login process.
 *
 * @param signInViewModel The ViewModel responsible for managing the sign-in state.
 * @param onLoggedIn Callback invoked when the user successfully logs in.
 * @param onBack Callback invoked when the user chooses to go back to the previous screen.
 * @param onSkip Callback invoked when the user decides to skip the login process.
 */
@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel,
    onLoggedIn: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    // Retrieve the current context
    val context = LocalContext.current

    // Collect the state from the ViewModel
    val state by signInViewModel.state.collectAsStateWithLifecycle()

    // Show a toast message if there is an error
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // Main container for the screen
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        AnimBackground()

        // Centered content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Card containing the login options
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.85f
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title text
                    Text(
                        text = "Login to sync your notes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google login button
                    Button(
                        onClick = onLoggedIn,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Login with Google")
                    }

                    // Back button
                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Go back")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skip login button
                    Button(
                        onClick = onSkip,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = "Skip the login",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}