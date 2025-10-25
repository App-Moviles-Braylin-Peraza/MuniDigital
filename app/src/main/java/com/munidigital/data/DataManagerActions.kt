package com.munidigital.data

import com.munidigital.model.Tramite

interface DataManagerActions {
    // Operaciones CRUD
    fun crearTramite(nuevoTramite: Tramite): Tramite
    fun obtenerTodosLosTramites(): List<Tramite>
    fun obtenerTramitePorId(id: String): Tramite?
    fun actualizarTramite(tramiteActualizado: Tramite): Tramite?
    fun eliminarTramite(id: String): Boolean

    // Operación específica (Listas personalizadas)
    fun obtenerTramitesPorEstado(estado: String): List<Tramite>
}