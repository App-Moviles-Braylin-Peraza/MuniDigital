package com.example.munidigital.model

/**
 * Clase de Entidad (Entity Class) que representa una solicitud o trámite municipal.
 * Contiene claves foráneas para establecer las relaciones con Ciudadano y TipoTramite.
 */
data class Tramite(

    val id: Int,
    val ciudadanoId: Int,
    val tipoTramiteId: Int,
    val descripcion: String,
    val estado: String,
    val rutaAdjunto: String? = null,
    val fechaCreacion: Long
)