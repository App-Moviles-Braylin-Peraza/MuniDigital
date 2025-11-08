package com.example.munidigital.model

/**
 * Entidad para el catálogo de tipos de trámites disponibles en la municipalidad.
 */
data class TipoTramite(
    val id: Int,
    val nombre: String, // Ej: "Patente Comercial"
    val descripcionCorta: String,
    // Lista de requisitos o documentos necesarios.
    val requisitos: String // Podría ser una lista de strings en un sistema más complejo.
)