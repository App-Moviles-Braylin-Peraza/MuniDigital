package com.munidigital.model

data class Tramite(
    // Nullable (se genera al guardar)
    val id: String? = null,
    val tipo: String,
    val descripcion: String,
    val fechaCreacion: Long? = null, // Usamos Long para un timestamp (milisegundos)
    var estado: String, // Usamos 'var' porque el estado puede cambiar (Pendiente -> Aprobado)
    val rutasArchivos: List<String> = emptyList(), // Lista de rutas (URIs) a fotos/videos
    val notasAdministrativas: String? = null
) {
    // Definir constantes para los estados comunes
    companion object {
        const val ESTADO_PENDIENTE = "Pendiente"
        const val ESTADO_APROBADO = "Aprobado"
        const val ESTADO_RECHAZADO = "Rechazado"
    }
}