// Constants.kt
package com.byteflipper.jsoncomposer

import androidx.compose.ui.graphics.Color

object JsonViewerTheme {
    object Colors {
        val darkBackground = Color(0xFF121212)
        val darkBackgroundLight = Color(0xFF1E1E1E)
        val objectColor = Color(0xFF4DB6AC)
        val arrayColor = Color(0xFF81C784)
        val stringColor = Color(0xFFFF8A80)
        val numberColor = Color(0xFF9FA8DA)
        val booleanColor = Color(0xFF80DEEA)
        val nullColor = Color(0xFF90A4AE)
        val keyColor = Color(0xFFB0BEC5)
        val textColor = Color(0xFFE0E0E0)

        val accentColor = Color(0xFF5D50C6)
        val warningColor = Color(0xFFFFAB00)
        val errorColor = Color(0xFFFF5252)
    }

    object Styles {
        const val defaultDepthIndent = 16
        const val animationDuration = 300
        const val maxDepth = 10
    }
}