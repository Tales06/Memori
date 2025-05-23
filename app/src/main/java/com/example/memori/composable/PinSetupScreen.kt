package com.example.memori.composable

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.memori.animation.AnimBackground
import com.example.memori.database.NoteDatabase
import com.example.memori.database.folder_data.FolderRepository
import com.example.memori.database.folder_data.FolderViewModel
import com.example.memori.database.folder_data.FolderViewModelFactory
import com.example.memori.preference.PinPreferences.savePinHash
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Composable
fun PinSetupScreen(
    navController: NavController,
    folderViewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(
            repository = FolderRepository(
                NoteDatabase.getDatabase(LocalContext.current).folderDao()
            )
        )
    )
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // Validation states
    val pinValid = pin.length in 4..6
    val pinsMatch = pin == confirmPin
    val canConfirm = pinValid && pinsMatch

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimBackground()
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(350.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set your PIN",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Use a PIN with 4-6 digits to protect your notes.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // PIN field
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Choose a PIN") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { pinVisible = !pinVisible }) {
                            Icon(
                                imageVector = if (pinVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (pinVisible) "Hide PIN" else "Show PIN"
                            )
                        }
                    },
                    isError = pin.isNotEmpty() && !pinValid,
                    modifier = Modifier.fillMaxWidth()
                )
                if (pin.isNotEmpty() && !pinValid) {
                    Text(
                        text = "The PIN must be 4-6 digits long",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                // Confirm PIN field
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 6) confirmPin = it.filter { ch -> ch.isDigit() }
                    },
                    label = { Text("Confirm PIN") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(
                                imageVector = if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (confirmVisible) "Hide PIN" else "Show PIN"
                            )
                        }
                    },
                    isError = confirmPin.isNotEmpty() && !pinsMatch,
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmPin.isNotEmpty() && !pinsMatch) {
                    Text(
                        text = "The PINs do not match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (!canConfirm) return@Button
                        scope.launch {

                            // Calcola hash SHA-256
                            val digest = MessageDigest.getInstance("SHA-256")
                            val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
                            val pinHash = hashBytes.joinToString("") { "%02x".format(it) }

                            // Salva in DataStore e crea cartella

                            context.savePinHash(pinHash)
                            folderViewModel.createFolder("Protected")
                            val protList = folderViewModel.allFolders
                                .filter { it.any { f -> f.folderName == "Protected" } }
                                .first()
                            val prot = protList.first { it.folderName == "Protected" }

                            // Naviga alla cartella protetta
                            navController.navigate("folderNotes/${prot.id}/${prot.folderName}") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },
                    enabled = canConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Confirm PIN")
                }
            }
        }
    }
}
