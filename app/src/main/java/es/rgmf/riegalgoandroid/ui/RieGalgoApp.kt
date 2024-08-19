package es.rgmf.riegalgoandroid.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rgmf.riegalgoandroid.R

@Composable
fun RieGalgoApp() {
    Scaffold(
        topBar = {
            RieGalgoTopBar()
        }
    ) { innerPadding ->
        val apiViewModel: ApiViewModel = viewModel(factory = ApiViewModel.Factory)
        val uiState = apiViewModel.apiUiState

        when (uiState) {
            is ApiUiState.LoadingToken -> LoadingScreen()
            is ApiUiState.Loading -> LoadingScreen()
            is ApiUiState.Success -> {
                EphemerisScreen(
                    medias = uiState.medias,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is ApiUiState.Error -> {
                ErrorScreen(
                    text = uiState.message,
                    retryAction = apiViewModel::getEphemeris,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is ApiUiState.ErrorAuth -> {
                AuthScreen(
                    text = uiState.message,
                    onLogin = { username, password ->
                        apiViewModel.login(username, password)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
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

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading),
            modifier = Modifier.size(200.dp).align(Alignment.Center)
        )
    }
}

@Composable
fun ErrorScreen(text: String, retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}