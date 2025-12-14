package com.example.munidigital.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Objeto de utilidad para manejar el formato de fechas y horas.
 * Un 'object' en Kotlin es un Singleton, accesible directamente sin instanciar.
 */
object DateUtil {

    private const val DATE_FORMAT = "dd/MM/yyyy HH:mm" // Formato de 24 horas

    /**
     * Convierte un timestamp (Long) a una cadena de texto formateada para la UI.
     * @param timestamp El valor Long de System.currentTimeMillis().
     * @return La fecha formateada como String.
     */
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        // Usamos Locale.getDefault() para respetar la configuración regional del usuario
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }
}