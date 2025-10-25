// TramiteController.kt en el paquete 'controller'
package com.munidigital.controller

import com.munidigital.data.DataManagerActions
import com.munidigital.model.Tramite

// El constructor recibe la interfaz, facilitando el cambio de implementación (Memory -> DB)
class TramiteController(private val dataManager: DataManagerActions) {

    // 1. INICIAR TRÁMITE (CRUD: Crear)
    fun iniciarNuevoTramite(tipo: String, descripcion: String, rutasArchivos: List<String>): Tramite {

        // Verificar que la descripción no esté vacía antes de crear.
        require(descripcion.isNotBlank()) { "La descripción del trámite no puede estar vacía." }


        val nuevo = Tramite(
            tipo = tipo,
            descripcion = descripcion,
            rutasArchivos = rutasArchivos,
            estado = Tramite.ESTADO_PENDIENTE // <-- AÑADE ESTA LÍNEA
            // El DataManager se encargará de asignar ID y fecha al momento de crearlo en la "base de datos"
        )
        return dataManager.crearTramite(nuevo)
    }

    // 2. VER TRÁMITES (CRUD: Leer)
    fun verTodosLosTramites(): List<Tramite> {
        return dataManager.obtenerTodosLosTramites()
    }

    // 3. VER TRÁMITES FILTRADOS (Listas personalizadas)
    fun verTramitesPorEstado(estado: String): List<Tramite> {
        // **LÓGICA DE NEGOCIO AQUÍ:**
        // Asegurar que el estado es uno válido antes de la consulta
        require(estado in listOf(Tramite.ESTADO_PENDIENTE, Tramite.ESTADO_APROBADO, Tramite.ESTADO_RECHAZADO)) {
            "Estado de filtrado no válido."
        }
        return dataManager.obtenerTramitesPorEstado(estado)
    }

    // 4. MODIFICAR TRÁMITE (CRUD: Actualizar) - Solo se permite modificar la descripción en borrador.
    fun modificarDescripcion(id: String, nuevaDescripcion: String): Tramite? {
        val tramite = dataManager.obtenerTramitePorId(id)
        if (tramite != null && tramite.estado == Tramite.ESTADO_PENDIENTE) {
            val actualizado = tramite.copy(descripcion = nuevaDescripcion)
            return dataManager.actualizarTramite(actualizado)
        }
        return null // No encontrado o no se puede modificar en ese estado
    }

    // 5. CANCELAR TRÁMITE (CRUD: Eliminar)
    fun cancelarTramite(id: String): Boolean {
        return dataManager.eliminarTramite(id)
    }
}