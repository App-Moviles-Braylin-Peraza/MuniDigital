package com.example.munidigital.controller

import com.example.munidigital.data.TipoTramiteDataManager // Suponemos la existencia de su interfaz
import com.example.munidigital.model.TipoTramite

/**
 * Controlador para la gestión del catálogo de Tipos de Trámites disponibles.
 * Responsable de proveer las opciones a la Interfaz de Usuario.
 */
class CatalogoController(
    // Inyectamos la dependencia de su DataManager
    private val dataManager: TipoTramiteDataManager
) {
    /**
     * Obtiene la lista completa de tipos de trámites para mostrar en un spinner o lista.
     */
    fun obtenerTodosLosTipos(): List<TipoTramite> {
        // Lógica de negocio: Filtrar tipos inactivos, ordenar alfabéticamente, etc.
        return dataManager.getAllTiposTramite().sortedBy { it.nombre }
    }

    /**
     * Obtiene un TipoTramite específico por su ID.
     */
    fun obtenerTipoPorId(id: Int): TipoTramite? {
        return dataManager.getTipoTramiteById(id)
    }
}