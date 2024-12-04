// JsonViewerComponent.kt

package com.byteflipper.jsoncomposer

import androidx.compose.ui.graphics.Color
import java.net.URL

sealed class JsonSource {
    data class Text(val jsonString: String) : JsonSource()
    data class File(val file: File) : JsonSource()
    data class Url(val url: URL) : JsonSource()
}

sealed class JsonParseResult {
    data class Success(val data: Any) : JsonParseResult()
    data class Error(val message: String, val exception: Throwable?) : JsonParseResult()
}

data class JsonViewerColors(
    val background: Color = JsonViewerTheme.Colors.darkBackground,
    val backgroundLight: Color = JsonViewerTheme.Colors.darkBackgroundLight,
    val objectColor: Color = JsonViewerTheme.Colors.objectColor,
    val arrayColor: Color = JsonViewerTheme.Colors.arrayColor,
    val stringColor: Color = JsonViewerTheme.Colors.stringColor,
    val numberColor: Color = JsonViewerTheme.Colors.numberColor,
    val booleanColor: Color = JsonViewerTheme.Colors.booleanColor,
    val nullColor: Color = JsonViewerTheme.Colors.nullColor,
    val keyColor: Color = JsonViewerTheme.Colors.keyColor,
    val textColor: Color = JsonViewerTheme.Colors.textColor
)