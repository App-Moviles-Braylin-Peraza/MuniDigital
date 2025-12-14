package com.example.munidigital.data

import com.example.munidigital.model.Tramite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Clase MemoryDataManager que implementa la interfaz TramiteDataManager para pruebas locales.
 */
class MemoryDataManager : TramiteDataManager {

    private val tramites: MutableList<Tramite> = mutableListOf()
    private val idGenerator = AtomicInteger(0)

    override suspend fun saveTramite(tramite: Tramite) {
        withContext(Dispatchers.IO) {
            val newId = idGenerator.incrementAndGet()
            val tramiteToSave = tramite.copy(id = newId)
            tramites.add(tramiteToSave)
        }
    }

    override suspend fun getTramiteById(id: Int): Tramite? = withContext(Dispatchers.IO) {
        tramites.find { it.id == id }
    }

    override suspend fun updateTramite(tramite: Tramite) {
        withContext(Dispatchers.IO) {
            val index = tramites.indexOfFirst { it.id == tramite.id }
            if (index != -1) { tramites[index] = tramite }
        }
    }

    override suspend fun deleteTramite(tramite: Tramite) {
        withContext(Dispatchers.IO) {
            tramites.removeIf { it.id == tramite.id }
        }
    }

    override suspend fun getAllTramites(): List<Tramite> = withContext(Dispatchers.IO) {
        tramites.toList()
    }

    override suspend fun getTramitesByEstado(estado: String): List<Tramite> = withContext(Dispatchers.IO) {
        // CORRECCIÓN DEFINITIVA: Se usa "status" para coincidir con el modelo de datos actualizado.
        tramites.filter { it.status.equals(estado, ignoreCase = true) }
    }
}