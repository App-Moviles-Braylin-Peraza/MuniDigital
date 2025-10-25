package com.munidigital.data

import com.munidigital.model.Tramite
import java.util.concurrent.atomic.AtomicInteger

class MemoryDataManager : DataManagerActions {

    // Almacén de datos en memoria (ID a Objeto Tramite)
    private val tramiteStore = mutableMapOf<String, Tramite>()

    // Generador atómico de IDs para asegurar unicidad y thread-safety
    private val idCounter = AtomicInteger(0)

    override fun crearTramite(nuevoTramite: Tramite): Tramite {
        val id = idCounter.incrementAndGet().toString()
        val tramiteConId = nuevoTramite.copy(
            id = id,
            fechaCreacion = System.currentTimeMillis(),
            estado = Tramite.ESTADO_PENDIENTE // Asegurar el estado inicial
        )
        tramiteStore[id] = tramiteConId
        return tramiteConId
    }

    override fun obtenerTodosLosTramites(): List<Tramite> {
        // Devolver una copia para evitar modificación externa
        return tramiteStore.values.toList()
    }

    override fun obtenerTramitePorId(id: String): Tramite? {
        return tramiteStore[id]
    }

    override fun actualizarTramite(tramiteActualizado: Tramite): Tramite? {
        val id = tramiteActualizado.id ?: return null // Debe tener ID para actualizar
        if (tramiteStore.containsKey(id)) {
            tramiteStore[id] = tramiteActualizado
            return tramiteActualizado
        }
        return null // No se encontró el trámite
    }

    override fun eliminarTramite(id: String): Boolean {
        // El método remove de Map devuelve el valor que eliminó, si existe.
        return tramiteStore.remove(id) != null
    }

    override fun obtenerTramitesPorEstado(estado: String): List<Tramite> {
        return tramiteStore.values.filter {
            it.estado.equals(estado, ignoreCase = true)
        }.toList()
    }
}