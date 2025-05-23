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

enum class ThemeType {
    SYSTEM,
    LIGHT,
    DARK
}

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

    Surface(
        modifier = Modifier
            .size(110.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onClick() },
        shadowElevation = if (selected) 8.dp else 2.dp,
        border = if (selected) BorderStroke(2.dp, borderColor) else null,
        color = MaterialTheme.colorScheme.surface
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
}