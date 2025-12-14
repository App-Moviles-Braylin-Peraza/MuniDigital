package com.example.munidigital.data

import com.example.munidigital.model.Tramite
import com.example.munidigital.model.TramiteRequest
import com.example.munidigital.network.ApiService

class TramiteApiDataManager(private val apiService: ApiService) : TramiteDataManager {

    override suspend fun saveTramite(tramite: Tramite) {
        val request = TramiteRequest(
            title = tramite.title,
            description = tramite.description,
            status = tramite.status,
            direccion = tramite.direccion
        )
        apiService.createTramite(request)
    }

    override suspend fun getTramiteById(id: Int): Tramite? {
        return try {
            apiService.getTramiteById(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateTramite(tramite: Tramite) {
        val request = TramiteRequest(
            title = tramite.title,
            description = tramite.description,
            status = tramite.status,
            direccion = tramite.direccion
        )
        apiService.updateTramite(tramite.id, request)
    }

    override suspend fun deleteTramite(tramite: Tramite) {
        apiService.deleteTramite(tramite.id)
    }

    override suspend fun getAllTramites(): List<Tramite> {
        return try {
            apiService.getTramites()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTramitesByEstado(estado: String): List<Tramite> {
        return try {
            apiService.getTramites(status = estado)
        } catch (e: Exception) {
            emptyList()
        }
    }
}