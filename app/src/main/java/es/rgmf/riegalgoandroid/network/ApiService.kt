package es.rgmf.riegalgoandroid.network

import es.rgmf.riegalgoandroid.model.MediaResponse
import es.rgmf.riegalgoandroid.model.Token
import es.rgmf.riegalgoandroid.model.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Token

    @GET("users/me/")
    suspend fun getUser(): UserResponse

    @GET("medias/ephemeris/")
    suspend fun getEphemeris(): MediaResponse
}