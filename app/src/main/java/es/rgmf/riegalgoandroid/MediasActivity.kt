package es.rgmf.riegalgoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import es.rgmf.riegalgoandroid.ui.MediasApp
import es.rgmf.riegalgoandroid.ui.theme.RieGalgoTheme

class MediasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val mediaId: Int = intent.getIntExtra(EXTRA_MEDIA_ID, -1)

        setContent { 
            RieGalgoTheme {
                MediasApp(mediaId)
            }
        }
    }

    companion object {
        const val EXTRA_MEDIA_ID = "extra_media_url"
    }
}