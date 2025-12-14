package com.example.munidigital.data

import com.example.munidigital.model.TipoTramite

interface TipoTramiteDataManager {
    suspend fun getAllTiposTramite(): List<TipoTramite>
    suspend fun getTipoTramiteById(id: Int): TipoTramite?
}
