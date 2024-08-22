package es.rgmf.riegalgoandroid.ui.media

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.rgmf.riegalgoandroid.R
import es.rgmf.riegalgoandroid.ui.PreferencesViewModel
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(
    startId: Int,
    ids: List<Int>,
    modifier: Modifier = Modifier
) {
    val prefsViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val token by prefsViewModel.tokenFlow.collectAsState(initial = "")
    val pagerState = rememberPagerState(pageCount = { ids.size }, initialPage = ids.indexOf(startId))

    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        if (token.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        coroutineScope.launch {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    when (event.changes.size) {
                                        2 -> {
                                            val change1 = event.changes[0]
                                            val change2 = event.changes[1]

                                            val distanceCurrent = calculateDistance(
                                                change1.position,
                                                change2.position
                                            )
                                            val distancePrevious = calculateDistance(
                                                change1.previousPosition,
                                                change2.previousPosition
                                            )
                                            scale *= distanceCurrent / distancePrevious
                                            offset += change1.positionChange()
                                            Log.i("TAGTTTTTT",
                                                "distanceCurrent: $distanceCurrent\ndistancePrevious: $distancePrevious\nscale: $scale\noffset: $offset"
                                            )
                                        }
                                        else -> {
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) { page ->
                AsyncImage(
                    model = ImageRequest
                        .Builder(context = LocalContext.current)
                        .data("https://rieapi.rgmf.es/medias/${ids[page]}/data/")
                        .addHeader("Authorization", "Bearer ${token}")
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                    contentDescription = stringResource(R.string.media_data_content_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Image(
                painter = painterResource(R.drawable.loading_img),
                contentDescription = stringResource(R.string.loading),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Euclidean distance between two points in a 2D space.
 */
private fun calculateDistance(point1: Offset, point2: Offset): Float {
    val dx = point1.x - point2.x
    val dy = point1.y - point2.y

    // Pythagorean theorem.
    return sqrt(dx * dx + dy * dy)
}