package com.example.munidigital.controller

import com.example.munidigital.data.TramiteDataManager
import com.example.munidigital.model.Tramite

class TramiteController(private val dataManager: TramiteDataManager) {

    suspend fun crearNuevoTramite(
        title: String,
        description: String?,
        userId: Int
    ): Tramite {
        if (title.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio.")
        }

        val nuevoTramite = Tramite(
            title = title,
            description = description,
            userId = userId
        )

        dataManager.saveTramite(nuevoTramite)
        return nuevoTramite
    }

    suspend fun actualizarTramite(
        id: Int,
        title: String?,
        description: String?,
        status: String?
    ) {
        val tramite = dataManager.getTramiteById(id)
        if (tramite != null) {
            val tramiteActualizado = tramite.copy(
                title = title ?: tramite.title,
                description = description ?: tramite.description,
                status = status ?: tramite.status
            )
            dataManager.updateTramite(tramiteActualizado)
        }
    }

    suspend fun eliminarTramite(id: Int) {
        val tramite = dataManager.getTramiteById(id)
        if (tramite != null) {
            dataManager.deleteTramite(tramite)
        } else {
            throw IllegalArgumentException("Trámite no encontrado con ID: $id")
        }
    }

    suspend fun obtenerTodosLosTramites(): List<Tramite> {
        return dataManager.getAllTramites()
    }

    suspend fun obtenerTramitesFiltrados(status: String): List<Tramite> {
        return dataManager.getTramitesByEstado(status)
    }

    suspend fun obtenerTramitePorId(id: Int): Tramite? {
        return dataManager.getTramiteById(id)
    }
}