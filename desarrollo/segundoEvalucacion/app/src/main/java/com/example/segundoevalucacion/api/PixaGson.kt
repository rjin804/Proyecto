package com.example.segundoevalucacion.api

import com.google.gson.annotations.SerializedName

/**
 * Pixa gson
 *
 * @property user
 * @property likes
 * @property imagen
 * @constructor Create empty Pixa gson
 */
data class PixaGson (
    @SerializedName("user")var user: String,
    @SerializedName("likes")var likes: Int,
    @SerializedName("webformatURL")var imagen : String

)

//carga el array de hits que contiene vario cosa, y sacamos lo que necesitamos
data class ListaPixaGson(
    @SerializedName("hits")var listaPixaGson: List<PixaGson>
)