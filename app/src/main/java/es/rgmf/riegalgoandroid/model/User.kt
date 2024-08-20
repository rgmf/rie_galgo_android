package es.rgmf.riegalgoandroid.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String
)

@Serializable
data class UserResponse(val data: User)
