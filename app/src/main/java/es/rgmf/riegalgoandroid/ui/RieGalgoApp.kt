package es.rgmf.riegalgoandroid.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.R

@Composable
fun RieGalgoApp() {
    Scaffold(
        topBar = {
            RieGalgoTopBar()
        }
    ) { innerPadding ->
        val rieGalgoViewModel: RieGalgoViewModel = viewModel(factory = RieGalgoViewModel.Factory)
        HomeScreen(
            rieGalgoUiState = rieGalgoViewModel.rieGalgoUiState,
            retryAction = rieGalgoViewModel::getEphemeris,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RieGalgoTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
    )
}