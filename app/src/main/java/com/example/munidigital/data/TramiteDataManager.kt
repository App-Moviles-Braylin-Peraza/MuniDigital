package com.example.munidigital.data

import com.example.munidigital.model.Tramite // Importamos la Entidad

/**
 * Interfaz que define las acciones de gestión de datos para la entidad Tramite.
 * Es el contrato (interface) para la capa de datos.
 */
interface TramiteDataManager {

    fun saveTramite(tramite: Tramite) // CREATE (Crear)
    fun getTramiteById(id: Int): Tramite? // READ (Leer uno)
    fun updateTramite(tramite: Tramite) // UPDATE (Actualizar)
    fun deleteTramite(tramite: Tramite) // DELETE (Eliminar)

    fun getAllTramites(): List<Tramite> // READ (Leer todos)
    fun getTramitesByEstado(estado: String): List<Tramite> // READ (Filtrar)
}