package com.example.munidigital.data

import com.example.munidigital.model.TipoTramite
import com.example.munidigital.network.ApiService
import android.util.Log

/**
 * DataManager híbrido que intenta usar la API primero, 
 * pero usa datos locales como fallback si la API falla.
 */
class TipoTramiteHybridDataManager(
    private val apiService: ApiService,
    private val useLocalFirst: Boolean = false
) : TipoTramiteDataManager {

    private val localDataManager = TipoTramiteMemoryDataManager()

    override suspend fun getAllTiposTramite(): List<TipoTramite> {
        // Si se configuró para usar datos locales primero
        if (useLocalFirst) {
            Log.d("TipoTramiteHybrid", "Usando datos locales (configurado)")
            return localDataManager.getAllTiposTramite()
        }

        return try {
            // Intentar obtener desde la API
            val tiposFromApi = apiService.getTiposTramite()
            Log.d("TipoTramiteHybrid", "Datos obtenidos desde API: ${tiposFromApi.size} tipos")
            
            if (tiposFromApi.isEmpty()) {
                Log.w("TipoTramiteHybrid", "API devolvió lista vacía, usando datos locales")
                localDataManager.getAllTiposTramite()
            } else {
                tiposFromApi
            }
        } catch (e: Exception) {
            Log.e("TipoTramiteHybrid", "Error al obtener desde API, usando datos locales", e)
            // Si falla la API, usar datos locales
            localDataManager.getAllTiposTramite()
        }
    }

    override suspend fun getTipoTramiteById(id: Int): TipoTramite? {
        return try {
            apiService.getTipoTramiteById(id)
        } catch (e: Exception) {
            Log.e("TipoTramiteHybrid", "Error al obtener tipo por ID desde API, usando datos locales", e)
            localDataManager.getTipoTramiteById(id)
        }
    }
}
