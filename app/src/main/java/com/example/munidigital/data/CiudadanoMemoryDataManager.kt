package com.example.munidigital.data

import com.example.munidigital.model.Ciudadano
import java.util.concurrent.atomic.AtomicInteger

/**
 * 1. Interfaz (Contrato) que define las acciones de datos para la entidad Ciudadano.
 */
interface CiudadanoDataManager {
    fun saveCiudadano(ciudadano: Ciudadano)
    fun getCiudadanoById(id: Int): Ciudadano?
    fun existeCedula(cedula: String): Boolean
    fun updateCiudadano(ciudadano: Ciudadano) // <-- Método añadido para el Controller
}

/**
 * 2. Implementación de la gestión de datos de Ciudadano en memoria (RAM).
 * Implementa la interfaz CiudadanoDataManager.
 */
class CiudadanoMemoryDataManager : CiudadanoDataManager {

    private val ciudadanos: MutableList<Ciudadano> = mutableListOf()
    private val idGenerator = AtomicInteger(0)

    override fun saveCiudadano(ciudadano: Ciudadano) {
        val newId = idGenerator.incrementAndGet()
        // Asigna el nuevo ID antes de guardar
        val ciudadanoToSave = ciudadano.copy(id = newId)
        ciudadanos.add(ciudadanoToSave)
    }

    override fun getCiudadanoById(id: Int): Ciudadano? {
        return ciudadanos.find { it.id == id }
    }

    override fun existeCedula(cedula: String): Boolean {
        // Verifica si ya existe un ciudadano con esa cédula
        return ciudadanos.any { it.cedula == cedula }
    }

    override fun updateCiudadano(ciudadano: Ciudadano) {
        // Busca la posición del objeto a actualizar por su ID
        val index = ciudadanos.indexOfFirst { it.id == ciudadano.id }
        if (index != -1) {
            // Reemplaza el objeto existente con el objeto actualizado
            ciudadanos[index] = ciudadano
        }
    }
}