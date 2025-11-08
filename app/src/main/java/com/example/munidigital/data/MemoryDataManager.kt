package com.example.munidigital.data

import com.example.munidigital.model.Tramite
import java.util.concurrent.atomic.AtomicInteger

/**
 * Clase MemoryDataManager (cumple con el requisito de nombre exacto)
 * Implementa la interfaz TramiteDataManager.
 */
class MemoryDataManager : TramiteDataManager { // <-- Implementa la interfaz

    private val tramites: MutableList<Tramite> = mutableListOf()
    private val idGenerator = AtomicInteger(0)

    // Métodos CRUD de Tramite aquí (saveTramite, getTramiteById, etc.)
    override fun saveTramite(tramite: Tramite) {
        val newId = idGenerator.incrementAndGet()
        val tramiteToSave = tramite.copy(id = newId) // Creamos el objeto con el ID real
        tramites.add(tramiteToSave)
        // Opcional: retornar el objeto con el ID asignado si la interfaz lo permite.
    }


    override fun getTramiteById(id: Int): Tramite? = tramites.find { it.id == id }

    override fun updateTramite(tramite: Tramite) {
        val index = tramites.indexOfFirst { it.id == tramite.id }
        if (index != -1) { tramites[index] = tramite }
    }

    override fun deleteTramite(tramite: Tramite) { tramites.removeIf { it.id == tramite.id } }

    override fun getAllTramites(): List<Tramite> = tramites.toList()

    override fun getTramitesByEstado(estado: String): List<Tramite> =
        tramites.filter { it.estado.equals(estado, ignoreCase = true) }
}