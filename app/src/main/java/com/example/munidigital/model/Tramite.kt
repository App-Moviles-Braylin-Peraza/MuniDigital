package com.example.munidigital.model

import com.google.gson.annotations.SerializedName

/**
 * Representa un trámite, alineado con la estructura de datos de la API.
 */
data class Tramite(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val status: String = "INICIADO",
    val direccion: String? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("creation_date")
    val creationDate: String = "",

    @SerializedName("last_update_date")
    val lastUpdateDate: String = "",

    val attachments: List<Attachment>? = null
)

data class Attachment(
    @SerializedName("file_url")
    val file_url: String,

    @SerializedName("file_type")
    val file_type: String?,

    @SerializedName("file_size")
    val file_size: Long?,

    @SerializedName("uploaded_at")
    val uploaded_at: String?,

    // Campos adicionales opcionales
    val name: String? = null
) {
    // Propiedades de conveniencia
    val url: String get() = file_url
    val type: String? get() = file_type
    val size: Long? get() = file_size
    val fileSize: Long? get() = file_size
}
