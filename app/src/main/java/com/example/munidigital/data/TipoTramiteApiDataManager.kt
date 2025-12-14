package com.example.munidigital.data

import com.example.munidigital.model.TipoTramite
import com.example.munidigital.network.ApiService

class TipoTramiteApiDataManager(private val apiService: ApiService) : TipoTramiteDataManager {

    override suspend fun getAllTiposTramite(): List<TipoTramite> {
        return try {
            apiService.getTiposTramite()
        } catch (e: Exception) {
            // Re-throw the exception to be handled by the caller
            throw e
        }
    }

    override suspend fun getTipoTramiteById(id: Int): TipoTramite? {
        return try {
            apiService.getTipoTramiteById(id)
        } catch (e: Exception) {
            null
        }
    }
}