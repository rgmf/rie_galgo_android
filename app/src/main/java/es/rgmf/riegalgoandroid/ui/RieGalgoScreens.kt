package es.rgmf.riegalgoandroid.ui

import androidx.annotation.StringRes
import es.rgmf.riegalgoandroid.R

enum class RieGalgoScreens(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    User(title = R.string.user_title)
}