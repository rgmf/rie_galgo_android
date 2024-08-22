package es.rgmf.riegalgoandroid.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.rgmf.riegalgoandroid.ui.media.MediasScreen

@Composable
fun MediasApp(id: Int, allIds: List<Int>) {
    Scaffold{ innerPadding ->
        MediasScreen(
            startId = id,
            ids = allIds,
            modifier = Modifier.padding(innerPadding)
        )
    }
}