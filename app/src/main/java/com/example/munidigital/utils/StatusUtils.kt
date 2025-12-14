package com.example.munidigital.utils

import android.content.Context
import android.graphics.Color
import com.example.munidigital.R

object StatusUtils {
    
    /**
     * Convierte el estado del trámite a texto legible en español
     */
    fun getStatusText(context: Context, status: String): String {
        return when (status) {
            "INICIADO" -> context.getString(R.string.status_iniciado)
            "EN_PROCESO" -> context.getString(R.string.status_en_proceso)
            "FINALIZADO" -> context.getString(R.string.status_finalizado)
            else -> status
        }
    }
    
    /**
     * Obtiene el color asociado a cada estado
     */
    fun getStatusColor(status: String): Int {
        return when (status) {
            "INICIADO" -> Color.parseColor("#2196F3")
            "EN_PROCESO" -> Color.parseColor("#FF9800")
            "FINALIZADO" -> Color.parseColor("#4CAF50")
            else -> Color.GRAY
        }
    }
    
    /**
     * Obtiene el color de fondo (más claro) para el estado
     */
    fun getStatusBackgroundColor(status: String): Int {
        return when (status) {
            "INICIADO" -> Color.parseColor("#E3F2FD")
            "EN_PROCESO" -> Color.parseColor("#FFF3E0")
            "FINALIZADO" -> Color.parseColor("#E8F5E9")
            else -> Color.parseColor("#F5F5F5")
        }
    }
}
