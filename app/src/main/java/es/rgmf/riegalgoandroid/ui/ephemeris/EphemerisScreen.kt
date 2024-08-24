package es.rgmf.riegalgoandroid.ui.ephemeris

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.ui.components.MediasGridScreen

@Composable
fun EphemerisScreen(modifier: Modifier = Modifier) {
    val ephemerisViewModel: EphemerisViewModel = viewModel(factory = EphemerisViewModel.Factory)
    val uiState by ephemerisViewModel.uiState.collectAsState()

    MediasGridScreen(
        medias = uiState.medias,
        modifier = modifier,
        onLoadMore = {
            if (!uiState.endReached) {
                ephemerisViewModel.fetchEphemeris()
            }
        }
    )
}