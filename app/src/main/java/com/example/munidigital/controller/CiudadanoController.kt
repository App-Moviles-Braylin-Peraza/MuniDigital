package com.example.munidigital.controller

import com.example.munidigital.data.CiudadanoDataManager
import com.example.munidigital.model.Ciudadano

/**
 * Controlador para la gestión de la lógica de negocio del Ciudadano (Usuario).
 * Responsable de la creación y gestión del perfil.
 */
class CiudadanoController(
    private val dataManager: CiudadanoDataManager
) {
    /**
     * Registra un nuevo ciudadano en el sistema (ej. al inicio de la app).
     */
    fun registrarCiudadano(nombre: String, cedula: String, email: String): Ciudadano {
        // 1. Lógica de validación: ¿La cédula ya existe?
        if (dataManager.existeCedula(cedula)) {
            throw IllegalArgumentException("La cédula ya está registrada.")
        }

        // 2. Crear y guardar la entidad
        val nuevoCiudadano = Ciudadano(
            id = 0, // ID asignado por el DataManager
            nombreCompleto = nombre,
            cedula = cedula,
            email = email,
            telefono = null
        )
        dataManager.saveCiudadano(nuevoCiudadano)
        return nuevoCiudadano // Retorna el objeto después de guardarlo
    }

    /**
     * Obtiene el perfil de un ciudadano por su ID.
     */
    fun obtenerPerfil(id: Int): Ciudadano? {
        return dataManager.getCiudadanoById(id)
    }

    /**
     * [ADICIÓN] Actualiza el correo electrónico de un ciudadano existente.
     * Requiere el método updateCiudadano() en la capa de datos.
     */
    fun actualizarEmail(id: Int, nuevoEmail: String) {
        val ciudadano = dataManager.getCiudadanoById(id)
        if (ciudadano != null) {
            // Crea una copia del objeto con el nuevo email
            val actualizado = ciudadano.copy(email = nuevoEmail)
            dataManager.updateCiudadano(actualizado)
        }
    }
}