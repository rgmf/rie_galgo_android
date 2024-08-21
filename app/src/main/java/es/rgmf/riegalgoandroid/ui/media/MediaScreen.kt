package es.rgmf.riegalgoandroid.ui.media

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
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

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var boxSize by remember { mutableStateOf(Size.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                boxSize = coordinates.size.toSize()
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceAtLeast(1f)

                    val maxFactorX = ((scale * boxSize.width) - boxSize.width) / 2f
                    val maxFactorY = ((scale * boxSize.height) - boxSize.height) / 2f

                    Log.d("AAAAAA", "Min factor:   " + (-maxFactorX))
                    Log.d("AAAAAA", "Max factor:   " + maxFactorX)
                    Log.d("AAAAAA", "Min factor:   " + (-maxFactorY))
                    Log.d("AAAAAA", "Max factor:   " + maxFactorY)
                    Log.d("AAAAAA", "coerceIn X:    " + (offsetX + pan.x).coerceIn(-maxFactorX, maxFactorX))
                    Log.d("AAAAAA", "coerceIn X:    " + (offsetY + pan.y).coerceIn(-maxFactorY, maxFactorY))
                    offsetX = (offsetX + pan.x).coerceIn(-maxFactorX, maxFactorX)
                    offsetY = (offsetY + pan.y).coerceIn(-maxFactorY, maxFactorY)

                    Log.d("AAAAAA", "Scale:        " + scale)
                    Log.d("AAAAAA", "Offset X:     " + offsetX)
                    Log.d("AAAAAA", "Offset Y:     " + offsetY)
                    Log.d("AAAAAA", "Box Size:     " + boxSize.width + ", " + boxSize.height)
                    Log.d("AAAAAA", "Image Size:   " + imageSize.width + ", " + imageSize.height)
                    Log.d("AAAAAA---", "-----------------------------------")
                }
            }
    ) {
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
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size.toSize()
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            )
        } else {
            Image(
                painter = painterResource(R.drawable.loading_img),
                contentDescription = stringResource(R.string.loading),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}