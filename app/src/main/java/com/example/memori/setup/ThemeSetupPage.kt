package com.example.memori.setup

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.animation.AnimBackground
import com.example.memori.theme.MyPalette
import com.example.memori.theme.MyPaletteDark

/**
 * Represents the available theme options for the application.
 *
 * - [SYSTEM]: Follows the system-wide theme setting.
 * - [LIGHT]: Forces the application to use a light theme.
 * - [DARK]: Forces the application to use a dark theme.
 */
enum class ThemeType {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * A composable wrapper for the theme setup page that manages the selected theme state
 * and provides animated transitions between theme selections.
 *
 * @param initialTheme The initial theme to be selected when the page is first displayed. Defaults to [ThemeType.SYSTEM].
 * @param onThemeSelected Callback invoked when the user selects a new theme.
 * @param onContinue Callback invoked when the user confirms their theme selection and chooses to continue.
 */
@Composable
fun ThemeSetupPageWrapper(
    initialTheme: ThemeType = ThemeType.SYSTEM,
    onThemeSelected: (ThemeType) -> Unit,
    onContinue: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(initialTheme) }


    Crossfade(
        targetState = selectedTheme,
        animationSpec = tween(durationMillis = 300)
    ) { theme ->
        ThemeSetupPage(
            currentTheme = theme,
            onThemeChange = { newTheme ->
                selectedTheme = newTheme
                onThemeSelected(newTheme)
            },
            onThemeConfirmed = {
                onContinue()
            }
        )
    }
}


/**
 * Composable function that displays the theme selection setup page.
 *
 * @param currentTheme The currently selected [ThemeType].
 * @param onThemeChange Callback invoked when the user selects a different theme.
 * @param onThemeConfirmed Callback invoked when the user confirms their theme selection.
 *
 * This page presents three theme options ("System", "Light", "Dark") for the user to choose from,
 * highlights the currently selected theme, and provides a "Continue" button to confirm the selection.
 * It also displays an animated background and uses Material Design components for styling.
 */
@Composable
fun ThemeSetupPage(
    currentTheme: ThemeType,
    onThemeChange: (ThemeType) -> Unit,
    onThemeConfirmed: () -> Unit,
) {


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select your theme",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeOptionBox("System", Color.Gray, currentTheme == ThemeType.SYSTEM) {
                    onThemeChange(ThemeType.SYSTEM)
                    Log.e("Theme", onThemeChange(ThemeType.SYSTEM).toString())
                }
                ThemeOptionBox("Light", Color.White, currentTheme == ThemeType.LIGHT) {
                    onThemeChange(ThemeType.LIGHT)
                    Log.e("Theme", onThemeChange(ThemeType.LIGHT).toString())
                }
                ThemeOptionBox("Dark", Color.DarkGray, currentTheme == ThemeType.DARK) {
                    onThemeChange(ThemeType.DARK)
                    Log.e("Theme", onThemeChange(ThemeType.DARK).toString())
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    onThemeConfirmed()
                } ,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(0.8f),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * A composable function that displays a selectable theme option box with a color preview and label.
 *
 * @param label The text label describing the theme option.
 * @param color The color to display as the theme preview.
 * @param selected Whether this option is currently selected.
 * @param onClick Callback invoked when the box is clicked.
 *
 * The box animates its border color and scale when selected, and shows a check icon in the corner.
 */
@Composable
fun ThemeOptionBox(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {

    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300)
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .size(110.dp)
            .padding(4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onClick() },
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            shadowElevation = if (selected) 8.dp else 2.dp,
            border = BorderStroke(2.dp, borderColor),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    color = color,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp
                ) {}

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Check icon when selected
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
            )
        }
    }
}