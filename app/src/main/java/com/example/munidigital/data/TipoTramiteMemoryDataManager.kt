package com.example.munidigital.data

import com.example.munidigital.model.TipoTramite

// Interfaz (TipoTramiteDataManager.kt - Debe definirse antes)
interface TipoTramiteDataManager {
    fun getAllTiposTramite(): List<TipoTramite>
    fun getTipoTramiteById(id: Int): TipoTramite?
}

// Implementación
class TipoTramiteMemoryDataManager : TipoTramiteDataManager {


    private val catalogo: List<TipoTramite> = listOf(
        TipoTramite(
            id = 1,
            nombre = "Patente Comercial",
            descripcionCorta = "Solicitud para iniciar operaciones comerciales.",
            requisitos = "Copia de cédula, plano catastrado, uso de suelo."
        ),
        TipoTramite(
            id = 2,
            nombre = "Permiso de Construcción",
            descripcionCorta = "Solicitud para levantar una estructura nueva.",
            requisitos = "Planos visados, estudio de suelos, formulario D1."
        ),
        TipoTramite(
            id = 3,
            nombre = "Certificación de Residencia",
            descripcionCorta = "Documento que prueba su domicilio en el cantón.",
            requisitos = "Recibo de servicio público a su nombre."
        )
    )

    override fun getAllTiposTramite(): List<TipoTramite> = catalogo.toList()

    override fun getTipoTramiteById(id: Int): TipoTramite? = catalogo.find { it.id == id }
}