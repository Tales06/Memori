/**
 * Displays the "Setup Complete" screen after the user has finished the setup process.
 *
 * This composable shows a congratulatory message, a brief note about changing settings later,
 * and a button to continue to the main part of the app. It also includes an animated background.
 *
 * @param onContinue Callback invoked when the user taps the "Go to the app" button.
 */
package com.example.memori.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.R
import com.example.memori.animation.AnimBackground

@Composable
fun SetupCompleteScreen(
    onContinue: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        AnimBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Setup Complete",
                style = TextStyle(
                    fontFamily = FontFamily(
                        Font(R.font.poppins_bold, FontWeight.Bold),
                    ),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(16.dp)

            )

            Text(
                text = "You can change these setting in the settings page.",
                style = TextStyle(
                    fontFamily = FontFamily(
                        Font(R.font.poppins_regular_400, FontWeight.Normal),
                    ),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(16.dp),

            )

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp,
                    hoveredElevation = 8.dp,
                    focusedElevation = 8.dp
                )
            ) {
                Text(
                    text = "Go to the app",
                    style = TextStyle(
                        fontFamily = FontFamily(
                            Font(R.font.poppins_regular_400, FontWeight.Normal),
                        ),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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