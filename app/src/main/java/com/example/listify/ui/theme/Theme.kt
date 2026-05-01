package com.example.listify.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary           = TealDark,
    onPrimary         = Color.White,
    primaryContainer  = Color(0xFFB2DFDB),
    onPrimaryContainer= TealDark,
    background        = CreamBg,
    onBackground      = TextPrimary,
    surface           = CardWhite,
    onSurface         = TextPrimary,
    secondary         = TealLight,
    onSecondary       = Color.White,
    secondaryContainer  = Color(0xFFBEE0DC),
)

@Composable
fun ListifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = ListifyTypography,
        content     = content
    )
}