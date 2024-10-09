package es.rgmf.riegalgoandroid.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.rgmf.riegalgoandroid.model.Media
import es.rgmf.riegalgoandroid.ui.media.MediasScreen

@Composable
fun MediasApp(id: Int, medias: List<Media>) {
    Scaffold{ innerPadding ->
        MediasScreen(
            startId = id,
            medias = medias,
            modifier = Modifier.padding(innerPadding)
        )
    }
}