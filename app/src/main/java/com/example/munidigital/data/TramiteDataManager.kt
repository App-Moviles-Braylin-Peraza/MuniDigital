package com.example.munidigital.data

import com.example.munidigital.model.Tramite

/**
 * Interfaz que define las acciones de gestión de datos para la entidad Tramite.
 * Es el contrato (interface) para la capa de datos.
 */
interface TramiteDataManager {

    suspend fun saveTramite(tramite: Tramite)
    suspend fun getTramiteById(id: Int): Tramite?
    suspend fun updateTramite(tramite: Tramite)
    suspend fun deleteTramite(tramite: Tramite)

    suspend fun getAllTramites(): List<Tramite>
    suspend fun getTramitesByEstado(estado: String): List<Tramite>
}