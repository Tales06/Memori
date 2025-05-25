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


/**
 * Data class representing a custom color palette for the application theme.
 *
 * @property primary The primary color used throughout the app.
 * @property secondary The secondary color used for accents and highlights.
 * @property accent An additional accent color for emphasis.
 * @property primaryContainer The color used for primary containers, such as buttons.
 * @property background The background color for app screens.
 * @property surface The color used for surfaces like cards and sheets.
 * @property onPrimary The color used for content displayed on top of the primary color.
 * @property onBackground The color used for content displayed on top of the background color.
 */
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

/**
 * Represents a color palette for the dark theme of the application.
 *
 * @property primary The primary color used throughout the app.
 * @property secondary The secondary color for additional accents.
 * @property accent The accent color for highlighting elements.
 * @property primaryContainer The color used for primary containers, such as buttons.
 * @property background The background color for the app's main surfaces.
 * @property surface The color used for surfaces like cards and sheets.
 * @property onPrimary The color used for text and icons displayed on primary color.
 * @property onBackground The color used for text and icons displayed on background color.
 */
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

/**
 * Defines the custom color schemes for the application using the `MyPalette` and `MyPaletteDark` classes.
 *
 * - `MyLightColorScheme`: The color scheme used for light theme mode, utilizing colors from `MyPalette`.
 * - `MyDarkColorScheme`: The color scheme used for dark theme mode, utilizing colors from `MyPaletteDark`.
 *
 * Each color scheme specifies the following color roles:
 * - `primary`: Main color, typically used for the toolbar.
 * - `secondary`: Secondary color for accents and highlights.
 * - `tertiary`: Additional accent color.
 * - `primaryContainer`: Container color for primary elements.
 * - `background`: Background color for the app.
 * - `surface`: Surface color for UI elements.
 * - `onPrimary`: Color used for content on top of the primary color.
 * - `onBackground`: Color used for content on top of the background.
 */
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
/**
 * Applies the Memori app theme to its content, supporting light, dark, and dynamic color schemes.
 *
 * @param themeType The type of theme to use (light, dark, or system default).
 * @param dynamicColor Whether to use dynamic color schemes (available on Android 12+).
 * @param content The composable content to which the theme will be applied.
 *
 * This function determines the appropriate color scheme based on the provided [themeType],
 * the system's dark theme setting, and whether dynamic colors are enabled and supported.
 * It then applies the selected color scheme and typography to the [MaterialTheme].
 */
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
    /**
     * Determines the appropriate [ColorScheme] to use based on the current theme settings and device capabilities.
     *
     * - If `dynamicColor` is enabled and the device is running Android 12 (API level S) or higher,
     *   it uses dynamic color schemes generated from the user's wallpaper, choosing between dark or light
     *   variants based on `useDarkTheme`.
     * - If dynamic colors are not available or not enabled, it falls back to custom-defined color schemes:
     *   [MyDarkColorScheme] for dark theme, or [MyLightColorScheme] for light theme.
     *
     * @see dynamicDarkColorScheme
     * @see dynamicLightColorScheme
     * @see MyDarkColorScheme
     * @see MyLightColorScheme
     */
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
