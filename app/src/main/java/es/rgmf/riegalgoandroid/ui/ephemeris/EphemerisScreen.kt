package es.rgmf.riegalgoandroid.ui.ephemeris

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.ui.components.MediasGridScreen
import es.rgmf.riegalgoandroid.ui.auth.AuthScreen
import es.rgmf.riegalgoandroid.ui.components.ErrorScreen
import es.rgmf.riegalgoandroid.ui.components.LoadingScreen

@Composable
fun EphemerisScreen(modifier: Modifier = Modifier) {
    val ephemerisViewModel: EphemerisViewModel = viewModel(factory = EphemerisViewModel.Factory)
    val uiState = ephemerisViewModel.ephemerisUiState

    when (uiState) {
        is EphemerisUiState.LoadingToken -> LoadingScreen()
        is EphemerisUiState.Loading -> LoadingScreen()
        is EphemerisUiState.Success -> {
            MediasGridScreen(
                medias = uiState.data.medias,
                modifier = modifier,
                onLoadMore = {
                    if (!uiState.data.endReached) {
                        ephemerisViewModel.getEphemeris()
                    }
                }
            )
        }
        is EphemerisUiState.Error -> {
            ErrorScreen(
                text = uiState.message,
                retryAction = ephemerisViewModel::getEphemeris,
                modifier = modifier
            )
        }
        is EphemerisUiState.ErrorAuth -> {
            AuthScreen(
                text = uiState.message,
                onLogin = { username, password ->
                    ephemerisViewModel.login(username, password)
                },
                modifier = modifier
            )
        }
    }
}