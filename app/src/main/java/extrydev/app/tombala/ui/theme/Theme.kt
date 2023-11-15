package extrydev.app.tombala.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val lightColorScheme = lightColorScheme(
    primary = Color(255, 142, 0, 255),
    secondary = Color(83, 152, 255, 255),
    tertiary = Color(255, 255, 255, 255),
    background = Color(0, 35, 88, 255),
    onSurface = Color(1,1,1)
)

private val darkColorScheme = darkColorScheme(
    primary = Color(255, 142, 0, 255),
    secondary = Color(83, 152, 255, 255),
    tertiary = Color(255, 255, 255, 255),
    background = Color(0, 35, 88, 255),
    onSurface = Color(1,1,1)
)

@Composable
fun TombalaTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if(darkTheme) darkColorScheme else lightColorScheme
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
