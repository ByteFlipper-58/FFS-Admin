//Extensions.kt

package com.byteflipper.jsoncomposer

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

fun Any.getJsonNodeType(): JsonNodeType = when (this) {
    is JSONObject -> JsonNodeType.OBJECT
    is JSONArray -> JsonNodeType.ARRAY
    is String -> JsonNodeType.STRING
    is Number -> JsonNodeType.NUMBER
    is Boolean -> JsonNodeType.BOOLEAN
    JSONObject.NULL -> JsonNodeType.NULL
    else -> throw IllegalArgumentException("Unsupported JSON type")
}

fun JsonNodeType.getColor(colors: JsonViewerColors): Color = when (this) {
    JsonNodeType.OBJECT -> colors.objectColor
    JsonNodeType.ARRAY -> colors.arrayColor
    JsonNodeType.STRING -> colors.stringColor
    JsonNodeType.NUMBER -> colors.numberColor
    JsonNodeType.BOOLEAN -> colors.booleanColor
    JsonNodeType.NULL -> colors.nullColor
}