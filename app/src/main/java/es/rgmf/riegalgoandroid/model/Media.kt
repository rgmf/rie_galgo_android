package es.rgmf.riegalgoandroid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: Int,
    val name: String,
    @SerialName(value = "mime_type")
    val mimeType: String,
    @SerialName(value = "media_type")
    val mediaType: String
)

@Serializable
data class MediaResponse(val data: List<Media>)