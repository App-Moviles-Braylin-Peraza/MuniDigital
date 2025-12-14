package com.example.munidigital.data

import com.example.munidigital.model.TipoTramite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TipoTramiteMemoryDataManager : TipoTramiteDataManager {

    private val catalogo: List<TipoTramite> = listOf(
        TipoTramite(
            id = 1,
            nombre = "Solicitud servicio",
            descripcionCorta = "Solicitud de servicios municipales",
            requisitos = "Descripción del servicio solicitado"
        ),
        TipoTramite(
            id = 2,
            nombre = "Reporte general",
            descripcionCorta = "Reporte de situaciones generales",
            requisitos = "Descripción detallada del reporte"
        ),
        TipoTramite(
            id = 3,
            nombre = "Reporte avería",
            descripcionCorta = "Reporte de averías en servicios públicos",
            requisitos = "Descripción de la avería y ubicación"
        )
    )

    override suspend fun getAllTiposTramite(): List<TipoTramite> = withContext(Dispatchers.IO) {
        catalogo.toList()
    }

    override suspend fun getTipoTramiteById(id: Int): TipoTramite? = withContext(Dispatchers.IO) {
        catalogo.find { it.id == id }
    }
}