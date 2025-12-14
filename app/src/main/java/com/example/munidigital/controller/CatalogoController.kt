package com.example.munidigital.controller

import com.example.munidigital.data.TipoTramiteDataManager
import com.example.munidigital.model.TipoTramite

class CatalogoController(private val dataManager: TipoTramiteDataManager) {

    suspend fun obtenerTodosLosTipos(): List<TipoTramite> {
        return dataManager.getAllTiposTramite().sortedBy { it.nombre }
    }

    suspend fun obtenerTipoPorId(id: Int): TipoTramite? {
        return dataManager.getTipoTramiteById(id)
    }
}