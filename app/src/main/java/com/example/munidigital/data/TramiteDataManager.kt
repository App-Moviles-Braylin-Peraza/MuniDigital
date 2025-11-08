package com.example.munidigital.data

import com.example.munidigital.model.Tramite

/**
 * Interfaz que define las acciones de gestión de datos para la entidad Tramite.
 * Es el contrato (interface) para la capa de datos.
 */
interface TramiteDataManager {

    fun saveTramite(tramite: Tramite)
    fun getTramiteById(id: Int): Tramite?
    fun updateTramite(tramite: Tramite)
    fun deleteTramite(tramite: Tramite)

    fun getAllTramites(): List<Tramite>
    fun getTramitesByEstado(estado: String): List<Tramite>
}