package es.rgmf.riegalgoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import es.rgmf.riegalgoandroid.model.Media
import es.rgmf.riegalgoandroid.ui.MediasApp
import es.rgmf.riegalgoandroid.ui.components.ErrorScreen
import es.rgmf.riegalgoandroid.ui.theme.RieGalgoTheme
import kotlinx.serialization.json.Json

class MediasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val mediaId: Int = intent.getIntExtra(EXTRA_SELECTED_MEDIA_ID, -1)
        val mediasDecoded = intent.getStringArrayListExtra(EXTRA_MEDIAS)
        val medias = mediasDecoded?.map { Json.decodeFromString(Media.serializer(), it) }

        setContent { 
            RieGalgoTheme {
                if (medias.isNullOrEmpty() || !medias.map { it.id }.contains(mediaId)) {
                    ErrorScreen(
                        text = stringResource(id = R.string.error_no_medias_or_not_valid_selected_media),
                        retryAction = {  }
                    )
                } else {
                    MediasApp(mediaId, medias)
                }
            }
        }
    }

    companion object {
        const val EXTRA_SELECTED_MEDIA_ID = "extra_selected_media_id"
        const val EXTRA_MEDIAS = "extra_medias"
    }
}