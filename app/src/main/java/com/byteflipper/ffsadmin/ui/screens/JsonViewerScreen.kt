package com.byteflipper.ffsadmin.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.byteflipper.jsoncomposer.JsonSource
import com.byteflipper.jsoncomposer.JsonViewer
import java.net.URL

@Composable
fun JsonViewerScreen(jsonUrl: URL) {

    Box(modifier = Modifier.fillMaxSize()) {
        JsonViewer().Render(source = JsonSource.Url(jsonUrl))
    }
}
