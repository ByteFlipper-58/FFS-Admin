// JsonNodeIcons.kt

package com.byteflipper.jsoncomposer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.byteflipper.ffsadmin.R

data class JsonNodeIcons(
    val objectIcon: Painter?,
    val arrayIcon: Painter?,
    val stringIcon: Painter?,
    val numberIcon: Painter?,
    val booleanIcon: Painter?,
    val nullIcon: Painter?
) {
    fun getIconForNodeType(type: JsonNodeType): Painter? = when (type) {
        JsonNodeType.OBJECT -> objectIcon
        JsonNodeType.ARRAY -> arrayIcon
        JsonNodeType.STRING -> stringIcon
        JsonNodeType.NUMBER -> numberIcon
        JsonNodeType.BOOLEAN -> booleanIcon
        JsonNodeType.NULL -> nullIcon
    }
}

@Composable
fun provideJsonNodeIcons(): JsonNodeIcons {
    return JsonNodeIcons(
        objectIcon = painterResource(id = R.drawable.database_24px),
        arrayIcon = painterResource(id = R.drawable.database_24px),
        stringIcon = painterResource(id = R.drawable.database_24px),
        numberIcon = painterResource(id = R.drawable.database_24px),
        booleanIcon = painterResource(id = R.drawable.database_24px),
        nullIcon = painterResource(id = R.drawable.database_24px)
    )
}
