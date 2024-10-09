package es.rgmf.riegalgoandroid.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

@Serializable
enum class MediaType(val type: String) {
    @SerialName("image") IMAGE("image"),
    @SerialName("video") VIDEO("video"),
    @SerialName("unknown") UNKNOWN("unknown")
}

object MediaTypeSerializer : KSerializer<MediaType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MediaType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MediaType {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be loaded only by JSON")
        val element: JsonElement = jsonDecoder.decodeJsonElement()
        return when (val value = element.jsonPrimitive.content) {
            "image" -> MediaType.IMAGE
            "video" -> MediaType.VIDEO
            else -> MediaType.UNKNOWN
        }
    }

    override fun serialize(encoder: Encoder, value: MediaType) {
        val stringValue = when (value) {
            MediaType.IMAGE -> "image"
            MediaType.VIDEO -> "video"
            MediaType.UNKNOWN -> "unknown"
        }
        encoder.encodeString(stringValue)
    }
}

@Serializable
data class Media(
    val id: Int,
    val name: String,
    @SerialName(value = "mime_type")
    val mimeType: String,
    @SerialName(value = "media_type")
    @Serializable(with = MediaTypeSerializer::class)
    val mediaType: MediaType
)

@Serializable
data class MediaResponse(val data: List<Media>)