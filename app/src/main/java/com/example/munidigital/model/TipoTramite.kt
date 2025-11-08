package com.example.munidigital.model

/**
 * Entidad para el catálogo de tipos de trámites disponibles en la municipalidad.
 */
data class TipoTramite(
    val id: Int,
    val nombre: String,
    val descripcionCorta: String,
    val requisitos: String
)