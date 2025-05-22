package com.example.memori.theme
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.memori.setup.ThemeType
import com.example.memori.ui.theme.Typography


data class MyPalette (
    val primary: Color = Color(0xFF5B5F97),
    val secondary: Color = Color(0xFFA3A8D7),
    val accent: Color = Color(0xFF3C91E6),
    val primaryContainer: Color = Color(0xFF7C4DFF), //buttons
    val background: Color = Color(0xFFF4F4FB),
    val surface: Color = Color(0xFFE1E2F0),
    val onPrimary: Color = Color(0xFFFFFFFF),
    val onBackground: Color = Color(0xFF1C1C1E)
)

data class MyPaletteDark (
    val primary: Color = Color(0xFF8E8DFF),
    val secondary: Color = Color(0xFFA3A8D7),
    val accent: Color = Color(0xFF3C91E6),
    val primaryContainer: Color = Color(0xFFB388FF), //buttons
    val background: Color = Color(0xFF121212),
    val surface: Color = Color(0xFF1E1E1E),
    val onPrimary: Color = Color(0xFF000000),
    val onBackground: Color = Color(0xFFE0E0E0),
)

private val MyLightColorScheme = lightColorScheme(
    primary = MyPalette().primary, //toolbar
    secondary = MyPalette().secondary,
    tertiary = MyPalette().accent,
    primaryContainer = MyPalette().primaryContainer,
    background = MyPalette().background,
    surface = MyPalette().surface,
    onPrimary = MyPalette().onPrimary,
    onBackground = MyPalette().onBackground,

)

private val MyDarkColorScheme = darkColorScheme(
    primary = MyPaletteDark().primary, //toolbar
    secondary = MyPaletteDark().secondary,
    tertiary = MyPaletteDark().accent,
    primaryContainer = MyPaletteDark().primaryContainer,
    background = MyPaletteDark().background,
    surface = MyPaletteDark().surface,
    onPrimary = MyPaletteDark().onPrimary,
    onBackground = MyPaletteDark().onBackground
)
@Composable
fun MyMemoriTheme(
    themeType: ThemeType = ThemeType.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
){
    val sysDarkTheme = isSystemInDarkTheme()

    val useDarkTheme = when(themeType) {
        ThemeType.DARK -> true
        ThemeType.LIGHT -> false
        ThemeType.SYSTEM -> sysDarkTheme
    }
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> MyDarkColorScheme
        else -> MyLightColorScheme
    }



    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )


}
