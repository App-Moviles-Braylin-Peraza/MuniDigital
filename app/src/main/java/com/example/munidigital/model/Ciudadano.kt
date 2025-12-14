package com.example.munidigital.model


data class Ciudadano(
    val id: Int,
    val nombreCompleto: String,
    val cedula: String,
    val email: String,
    val telefono: String? = null
)