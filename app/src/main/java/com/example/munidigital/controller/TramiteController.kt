package com.example.munidigital.controller

import com.example.munidigital.data.TramiteDataManager
// import com.example.munidigital.data.MemoryDataManager <-- Ya no se importa aquí
import com.example.munidigital.model.Tramite
import com.example.munidigital.util.TramiteEstado

/**
 * Clase Controlador (Controller) que maneja la lógica de negocio para los trámites.
 */
class TramiteController(
    // Inyectamos solo la Interfaz. La implementación se pasa desde fuera.
    private val dataManager: TramiteDataManager
) {
    // --- LÓGICA DE GESTIÓN (CRUD) ---

    /**
     * Crea y guarda un nuevo trámite. Ahora requiere los IDs de las claves foráneas.
     */
    fun crearNuevoTramite(
        ciudadanoId: Int, // ID del usuario que lo crea
        tipoTramiteId: Int, // ID del tipo de trámite seleccionado del catálogo
        descripcion: String,
        rutaAdjunto: String?
    ): Tramite {
        // 1. Validación de campos obligatorios.
        if (descripcion.isBlank()) {
            throw IllegalArgumentException("La descripción es obligatoria.")
        }
        // Nota: Las validaciones para que ciudadanoId/tipoTramiteId existan irían aquí.

        // 2. Creamos la entidad Tramite con el modelo corregido.
        val nuevoTramite = Tramite(
            id = 0, // ID asignado por el DataManager
            ciudadanoId = ciudadanoId,
            tipoTramiteId = tipoTramiteId,
            descripcion = descripcion,
            estado = TramiteEstado.PENDIENTE.valor, // Estado inicial fijo
            rutaAdjunto = rutaAdjunto,
            fechaCreacion = System.currentTimeMillis()
        )

        // 3. Enviamos al DataManager para guardar
        dataManager.saveTramite(nuevoTramite)
        return nuevoTramite
    }

    /**
     * Actualiza el estado de un trámite.
     */
    fun actualizarEstadoTramite(id: Int, nuevoEstado: String) {
        val tramite = dataManager.getTramiteById(id)
        if (tramite != null) {
            // Lógica de negocio (opcional)
            // if (tramite.estado != "TramiteEstado.PENDIENTE.valor" && nuevoEstado == "Pendiente") { ... }

            val tramiteActualizado = tramite.copy(estado = nuevoEstado)
            dataManager.updateTramite(tramiteActualizado)
        }
    }

    /**
     * Elimina un trámite de forma permanente.
     */
    fun eliminarTramite(id: Int) {
        val tramite = dataManager.getTramiteById(id)
        if (tramite != null) {
            dataManager.deleteTramite(tramite)
        }
    }


    // --- LÓGICA DE CONSULTA ---

    /** Obtiene todos los trámites, ordenados por fecha de creación (los más recientes primero). */
    fun obtenerTodosLosTramites() = dataManager.getAllTramites().sortedByDescending { it.fechaCreacion }

    /** Obtiene trámites filtrados por el estado requerido. */
    fun obtenerTramitesFiltrados(estado: String) = dataManager.getTramitesByEstado(estado)

    /** Obtiene un trámite por su ID. */
    fun obtenerTramitePorId(id: Int) = dataManager.getTramiteById(id)
}