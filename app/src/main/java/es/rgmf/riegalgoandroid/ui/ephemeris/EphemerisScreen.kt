package es.rgmf.riegalgoandroid.ui.ephemeris

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.ui.components.MediasGridScreen

@Composable
fun EphemerisScreen(modifier: Modifier = Modifier) {
    val ephemerisViewModel: EphemerisViewModel = viewModel(factory = EphemerisViewModel.Factory)
    val uiState by ephemerisViewModel.uiState.collectAsState()

    if (uiState.authError) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = uiState.error!!),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.medias.isEmpty()) {
                LoadingIndicator(Modifier.fillMaxSize())
            } else {
                MediasGridScreen(
                    medias = uiState.medias,
                    modifier = modifier.weight(1f),
                    onLoadMore = {
                        if (!uiState.endReached && !uiState.isLoading) {
                            ephemerisViewModel.fetchEphemeris()
                        }
                    }
                )

                if (uiState.isLoading) {
                    LoadingIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
