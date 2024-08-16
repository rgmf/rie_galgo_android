package es.rgmf.riegalgoandroid.data

import es.rgmf.riegalgoandroid.model.MediaResponse
import es.rgmf.riegalgoandroid.model.Token
import es.rgmf.riegalgoandroid.network.ApiService

/**
 * Repository that fetch media list from RIE Galgo API.
 */
interface ApiRepository {
    suspend fun login(username: String, password: String): Token
    suspend fun getEphemeris(): MediaResponse
}

/**
 * Network Implementation of Repository that fetch ephemeris medias from RIE Galgo API.
 */
class NetworkApiRepository(
    private val apiService: ApiService
) : ApiRepository {
    override suspend fun login(username: String, password: String): Token = apiService.login(username, password)
    override suspend fun getEphemeris(): MediaResponse = apiService.getEphemeris()
}