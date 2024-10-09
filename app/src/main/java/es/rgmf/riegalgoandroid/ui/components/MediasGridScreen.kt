package es.rgmf.riegalgoandroid.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.rgmf.riegalgoandroid.MediasActivity
import es.rgmf.riegalgoandroid.R
import es.rgmf.riegalgoandroid.model.Media
import es.rgmf.riegalgoandroid.model.MediaType
import es.rgmf.riegalgoandroid.network.thumbnailUrl
import es.rgmf.riegalgoandroid.ui.PreferencesViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json

@Composable
fun MediasGridScreen(
    medias: List<Media>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lazyList = rememberLazyListState()
    val buffer = 2

    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyList.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItems - buffer)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier,
    ) {
        items(items = medias, key = { media -> media.id }) { media ->
            MediaCard(
                media,
                onClickMedia = {
                    onClickMedia(
                        context = context,
                        clickedMedia = media,
                        allMedias = medias.map { Json.encodeToString(Media.serializer(), it) }
                    )
                },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
            )
        }

        item {
            LaunchedEffect(loadMore) {
                snapshotFlow { loadMore.value }
                    .distinctUntilChanged()
                    .collect {
                        onLoadMore()
                    }
            }
        }
    }
}

fun onClickMedia(context: Context, clickedMedia: Media, allMedias: List<String>) {

    val intent = Intent(context, MediasActivity::class.java).apply {
        putExtra(MediasActivity.EXTRA_SELECTED_MEDIA_ID, clickedMedia.id)
        putStringArrayListExtra(MediasActivity.EXTRA_MEDIAS, allMedias as ArrayList<String>)
    }
    context.startActivity(intent)
}

@Composable
fun MediaCard(
    media: Media,
    onClickMedia: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefsViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val token by prefsViewModel.tokenFlow.collectAsState(initial = "")

    val context = LocalContext.current

    Card(
        modifier = modifier
            .clickable { onClickMedia(media.id) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            if (token.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest
                        .Builder(context = LocalContext.current)
                        .data(thumbnailUrl(media.id.toString()))
                        .addHeader("Authorization", "Bearer ${token}")
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                    contentDescription = stringResource(R.string.media_thumbnail_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.loading_img),
                    contentDescription = stringResource(R.string.loading),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (media.mediaType == MediaType.VIDEO) {
                Icon(
                    painter = painterResource(R.drawable.play_circle),
                    contentDescription = stringResource(R.string.play_video),
                    tint = Color.Green,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }
    }
}
