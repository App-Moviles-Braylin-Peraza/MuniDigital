package com.example.munidigital.model

/**
 * Entidad que representa al usuario de la aplicación (el Ciudadano).
 */
data class Ciudadano(
    val id: Int,
    val nombreCompleto: String,
    val cedula: String,
    val email: String,
    val telefono: String? = null
)