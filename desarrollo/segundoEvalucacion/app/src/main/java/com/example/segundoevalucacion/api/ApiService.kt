package com.example.segundoevalucacion.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Api service
 *
 * @constructor Create empty Api service
 */
interface ApiService {
    @GET
    suspend fun getDatosPixaBay(@Url url: String): Response<ListaPixaGson>
}