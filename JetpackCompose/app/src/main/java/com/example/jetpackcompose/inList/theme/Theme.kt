package com.example.jetpackcompose.inList.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    tertiary = BlackGreen,
    background = BlackGreen,
    surface = BlackGreen,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,

    //цвета экрана
    primary = DarkThemeDarkGreen,
    onPrimary = White,

    //кнопка активна
    primaryContainer = DarkThemeBrightGreen,
    onPrimaryContainer = White,

    //кнопка неактивна
    secondaryContainer = DarkThemeLightGreen,
    onSecondaryContainer = White
)

private val LightColorScheme = lightColorScheme(

    tertiary = White,
    background = White,
    surface = White,

    onSecondary = BlackGreen,
    onTertiary = BlackGreen,
    onBackground = BlackGreen,
    onSurface = BlackGreen,

    //цвета экрана
    primary = White,
    onPrimary = BlackGreen,

    //кнопка активна
    primaryContainer = LightThemeBrightGreen,
    onPrimaryContainer = White,

    //кнопка неактивна
    secondaryContainer = LightThemeLightGreen,
    onSecondaryContainer = BlackGreen
    )

@Composable
fun JetpackComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Динамические цвета доступны начиная с Android 12
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}