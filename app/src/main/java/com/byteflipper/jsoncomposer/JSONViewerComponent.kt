// JsonViewerComponent.kt
package com.byteflipper.jsoncomposer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class JsonViewer(
    private val customColors: JsonViewerColors? = null,
    private val icons: JsonNodeIcons? = null
) {
    @Composable
    fun Render(
        source: JsonSource,
        modifier: Modifier = Modifier
    ) {
        val parseResult = remember { mutableStateOf<JsonParseResult?>(null) }

        LaunchedEffect(source) {
            launch {
                parseResult.value = JsonParser.parseJson(source)
            }
        }

        when (val result = parseResult.value) {
            is JsonParseResult.Success -> {
                val colors = customColors ?: JsonViewerColors()

                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState()),
                    color = colors.background,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    JsonNodeRenderer(colors, icons).Render(
                        node = result.data,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            is JsonParseResult.Error -> {
                Text(
                    text = "JSON Parsing Error: ${result.message}",
                    color = JsonViewerTheme.Colors.errorColor
                )
            }
            null -> {
                CircularProgressIndicator()
            }
        }
    }
}

