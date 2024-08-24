package es.rgmf.riegalgoandroid.ui.ephemeris

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.ui.components.MediasGridScreen

@Composable
fun EphemerisScreen(modifier: Modifier = Modifier) {
    val ephemerisViewModel: EphemerisViewModel = viewModel(factory = EphemerisViewModel.Factory)
    val uiState by ephemerisViewModel.uiState.collectAsState()

    val showErrorDialog = remember { mutableStateOf(uiState.error != null && uiState.error!!.isNotEmpty()) }

    if (showErrorDialog.value) {
        ErrorDialog(
            errorMessage = uiState.error ?: "",
            onDismiss = { showErrorDialog.value = false }
        )
    }

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
                LoadingIndicator(Modifier.fillMaxWidth().padding(16.dp))
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

@Composable
private fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Error")
        },
        text = {
            Text(text = errorMessage)
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
