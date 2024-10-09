package es.rgmf.riegalgoandroid.network

const val BASE_URL = "http://192.168.1.23:8000/"
//const val BASE_URL = "https://rieapi.rgmf.es/"

fun thumbnailUrl(mediaId: String): String = "${BASE_URL}medias/$mediaId/thumbnail/"
fun dataUrl(mediaId: String): String = "${BASE_URL}medias/$mediaId/data/"