package com.example.munidigital.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    private val shortDisplayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    /**
     * Convierte una fecha ISO 8601 (UTC) a formato legible local

     */
    fun formatDate(isoDateString: String?): String {
        if (isoDateString.isNullOrEmpty()) return "Sin fecha"
        
        return try {
            val date = isoFormat.parse(isoDateString)
            date?.let { displayFormat.format(it) } ?: "Fecha inválida"
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }
    
    /**
     * Convierte una fecha ISO 8601 a formato corto (solo fecha)
     * Ejemplo: "2025-12-12T10:30:00.000Z" -> "12/12/2025"
     */
    fun formatDateShort(isoDateString: String?): String {
        if (isoDateString.isNullOrEmpty()) return "Sin fecha"
        
        return try {
            val date = isoFormat.parse(isoDateString)
            date?.let { shortDisplayFormat.format(it) } ?: "Fecha inválida"
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }
    
    /**
     * Calcula el tiempo transcurrido desde una fecha
     * Ejemplo: "Hace 2 días", "Hace 3 horas"
     */
    fun getTimeAgo(isoDateString: String?): String {
        if (isoDateString.isNullOrEmpty()) return "Fecha desconocida"
        
        return try {
            val date = isoFormat.parse(isoDateString)
            if (date == null) return "Fecha inválida"
            
            val now = System.currentTimeMillis()
            val diff = now - date.time
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            
            when {
                days > 0 -> "Hace ${days} día${if (days > 1) "s" else ""}"
                hours > 0 -> "Hace ${hours} hora${if (hours > 1) "s" else ""}"
                minutes > 0 -> "Hace ${minutes} minuto${if (minutes > 1) "s" else ""}"
                else -> "Hace unos segundos"
            }
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }
}
