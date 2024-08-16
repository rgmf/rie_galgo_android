package es.rgmf.riegalgoandroid.data

import es.rgmf.riegalgoandroid.model.MediaResponse
import es.rgmf.riegalgoandroid.model.Token
import es.rgmf.riegalgoandroid.network.RieGalgoService

/**
 * Repository that fetch media list from RIE Galgo API.
 */
interface RieGalgoRepository {
    suspend fun login(username: String, password: String): Token
    suspend fun getEphemeris(token: String): MediaResponse
}

/**
 * Network Implementation of Repository that fetch ephemeris medias from RIE Galgo API.
 */
class NetworkRieGalgoRepository(
    private val rieGalgoService: RieGalgoService
) : RieGalgoRepository {
    override suspend fun login(username: String, password: String): Token = rieGalgoService.login(username, password)
    override suspend fun getEphemeris(token: String): MediaResponse = rieGalgoService.getEphemeris("Bearer ${token}")
}