package es.rgmf.riegalgoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import es.rgmf.riegalgoandroid.ui.MediasApp
import es.rgmf.riegalgoandroid.ui.components.ErrorScreen
import es.rgmf.riegalgoandroid.ui.theme.RieGalgoTheme
import java.util.ArrayList

class MediasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val mediaId: Int = intent.getIntExtra(EXTRA_SELECTED_MEDIA_ID, -1)
        val mediasId: ArrayList<Int>? = intent.getIntegerArrayListExtra(EXTRA_MEDIAS_ID)

        setContent { 
            RieGalgoTheme {
                if (mediasId.isNullOrEmpty() || !mediasId.contains(mediaId)) {
                    ErrorScreen(
                        text = stringResource(id = R.string.error_no_medias_or_not_valid_selected_media),
                        retryAction = {  }
                    )
                } else {
                    MediasApp(mediaId, mediasId)
                }
            }
        }
    }

    companion object {
        const val EXTRA_SELECTED_MEDIA_ID = "extra_selected_media_id"
        const val EXTRA_MEDIAS_ID = "extra_medias_id"
    }
}