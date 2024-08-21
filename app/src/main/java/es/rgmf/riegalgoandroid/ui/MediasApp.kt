package es.rgmf.riegalgoandroid.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.rgmf.riegalgoandroid.ui.media.MediaScreen

@Composable
fun MediasApp(id: Int) {
    Scaffold{ innerPadding -> MediaScreen(id = id, modifier = Modifier.padding(innerPadding)) }
}