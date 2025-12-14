package com.example.munidigital.util

/**
 * Clase Enum que define los estados válidos y posibles de un Trámite.
 * Esto asegura consistencia en toda la aplicación (Controller, DataManager, UI).
 */
enum class TramiteEstado(val valor: String) {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");

    // Método de utilidad para facilitar la comparación
    companion object {
        fun fromString(str: String) = entries.find { it.valor.equals(str, ignoreCase = true) }
    }
}