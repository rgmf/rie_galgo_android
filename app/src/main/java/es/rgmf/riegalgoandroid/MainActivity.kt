package es.rgmf.riegalgoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.rgmf.riegalgoandroid.ui.RieGalgoApp
import es.rgmf.riegalgoandroid.ui.theme.RieGalgoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RieGalgoTheme {
                RieGalgoApp()
            }
        }
    }
}