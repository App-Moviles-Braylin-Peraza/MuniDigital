package com.example.munidigital.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la respuesta al subir una foto a un trámite
 */
data class AdjuntoResponse(
    val id: Int,
    @SerializedName("tramite_id")
    val tramiteId: Int,
    val url: String,
    @SerializedName("created_at")
    val createdAt: String,
    val message: String? = null
)

/**
 * Modelo completo de adjunto/foto
 */
data class Adjunto(
    val id: Int,
    @SerializedName("tramite_id")
    val tramiteId: Int,
    val url: String,
    @SerializedName("created_at")
    val createdAt: String
)

/**
 * Request para eliminar un adjunto
 */
data class DeleteAdjuntoRequest(
    @SerializedName("file_url")
    val fileUrl: String
)
