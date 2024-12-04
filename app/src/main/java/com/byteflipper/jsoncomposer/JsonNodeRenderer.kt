// JsonNodeRenderer.kt

package com.byteflipper.jsoncomposer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

class JsonNodeRenderer(
    private val colors: JsonViewerColors,
    private val icons: JsonNodeIcons? = null
) {
    @Composable
    fun Render(
        node: Any,
        modifier: Modifier = Modifier,
        depth: Int = 0,
        isRoot: Boolean = true
    ) {
        if (depth >= JsonViewerTheme.Styles.maxDepth) return

        var isExpanded by remember { mutableStateOf(isRoot) }
        val nodeType = node.getJsonNodeType()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = (depth * JsonViewerTheme.Styles.defaultDepthIndent).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.background)
        ) {
            NodeHeader(
                node = node,
                nodeType = nodeType,
                isExpanded = isExpanded,
                onToggle = {
                    if (nodeType in listOf(JsonNodeType.OBJECT, JsonNodeType.ARRAY)) {
                        isExpanded = !isExpanded
                    }
                },
                colors = colors,
                icon = icons?.getIconForNodeType(nodeType)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(animationSpec = tween(JsonViewerTheme.Styles.animationDuration)),
                exit = shrinkVertically() + fadeOut(animationSpec = tween(JsonViewerTheme.Styles.animationDuration))
            ) {
                NodeContent(
                    node = node,
                    depth = depth,
                    colors = colors
                )
            }
        }
    }

    @Composable
    private fun NodeHeader(
        node: Any,
        nodeType: JsonNodeType,
        isExpanded: Boolean,
        onToggle: () -> Unit,
        colors: JsonViewerColors,
        icon: Painter? = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (nodeType in listOf(JsonNodeType.OBJECT, JsonNodeType.ARRAY)) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Expand/Collapse",
                    tint = nodeType.getColor(colors)
                )
            }

            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = "JSON Node Type",
                    tint = nodeType.getColor(colors),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Text(
                text = when (nodeType) {
                    JsonNodeType.OBJECT -> "{...}"
                    JsonNodeType.ARRAY -> "[...]"
                    JsonNodeType.STRING -> "\"$node\""
                    JsonNodeType.NUMBER -> "$node"
                    JsonNodeType.BOOLEAN -> "$node"
                    JsonNodeType.NULL -> "null"
                },
                color = nodeType.getColor(colors),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    @Composable
    private fun NodeContent(
        node: Any,
        depth: Int,
        colors: JsonViewerColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.backgroundLight)
                .padding(8.dp)
        ) {
            when (node) {
                is JSONObject -> RenderObject(node, depth, colors)
                is JSONArray -> RenderArray(node, depth, colors)
            }
        }
    }

    @Composable
    private fun RenderObject(jsonObject: JSONObject, depth: Int, colors: JsonViewerColors) {
        val keys = jsonObject.keys().asSequence().toList()
        keys.forEach { key ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "$key: ",
                    color = colors.keyColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Render(
                    node = jsonObject[key],
                    depth = depth + 1,
                    isRoot = false
                )
            }
        }
    }

    @Composable
    private fun RenderArray(jsonArray: JSONArray, depth: Int, colors: JsonViewerColors) {
        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.get(index)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "[$index]: ",
                    color = colors.nullColor,
                    fontSize = 14.sp
                )
                Render(
                    node = item,
                    depth = depth + 1,
                    isRoot = false
                )
            }
        }
    }
}