package com.example.listify

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.listify.presentation.navigation.NavGraph
import com.example.listify.ui.theme.ListifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListifyTheme {
                val view = LocalView.current
                val primaryBarColor = MaterialTheme.colorScheme.primary.toArgb()
                val darkTheme = isSystemInDarkTheme()

                SideEffect {
                    val window = (view.context as Activity).window

                    // Enable Edge-to-Edge (Modern way)
                    enableEdgeToEdge(
                        statusBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(scrim = primaryBarColor)
                        } else {
                            SystemBarStyle.light(scrim = primaryBarColor, darkScrim = primaryBarColor)
                        },
                        navigationBarStyle = if (darkTheme) {
                            SystemBarStyle.dark(scrim = primaryBarColor)
                        } else {
                            SystemBarStyle.light(scrim = primaryBarColor, darkScrim = primaryBarColor)
                        }
                    )

                    // Optional: Force status bar color if needed
                    window.statusBarColor = primaryBarColor

                    // Control light/dark icons
                    val controller = WindowInsetsControllerCompat(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !darkTheme
                    controller.isAppearanceLightNavigationBars = !darkTheme
                }
                NavGraph()
            }
        }
    }
}