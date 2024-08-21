package es.rgmf.riegalgoandroid.ui.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.rgmf.riegalgoandroid.R
import es.rgmf.riegalgoandroid.ui.PreferencesViewModel

@Composable
fun MediaScreen(
    id: Int,
    modifier: Modifier = Modifier
) {
    val prefsViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val token by prefsViewModel.tokenFlow.collectAsState(initial = "")

    if (token.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest
                .Builder(context = LocalContext.current)
                .data("https://rieapi.rgmf.es/medias/${id}/data/")
                .addHeader("Authorization", "Bearer ${token}")
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.media_data_content_description),
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth()
        )
    } else {
        Image(
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading),
            modifier = modifier.fillMaxWidth()
        )
    }
}