package com.example.munidigital.model

/**
 * Clase de Entidad (Entity Class) que representa una solicitud o trámite municipal.
 * Contiene claves foráneas para establecer las relaciones con Ciudadano y TipoTramite.
 */
data class Tramite(
    // Identificador único, clave principal.
    val id: Int,

    // [CORRECCIÓN] Clave foránea al Ciudadano que realiza el trámite.
    val ciudadanoId: Int,

    // [CORRECCIÓN] Clave foránea al TipoTramite (Patente, Permiso) para buscar su catálogo.
    val tipoTramiteId: Int,

    // Descripción detallada de la solicitud.
    val descripcion: String,

    // Estado del trámite: "Pendiente", "Aprobado", "Rechazado".
    val estado: String,

    // Ruta (String) al archivo adjunto (foto/video). Opcional (null).
    val rutaAdjunto: String? = null,

    // Marca de tiempo de creación del trámite.
    val fechaCreacion: Long
)