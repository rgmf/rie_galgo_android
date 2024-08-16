package es.rgmf.riegalgoandroid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName(value = "access_token")
    val accessToken: String,
    @SerialName(value = "token_type")
    val tokenType: String
)
