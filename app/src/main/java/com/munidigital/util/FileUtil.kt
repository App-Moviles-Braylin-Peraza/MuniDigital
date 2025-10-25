// FileUtil.kt en el paquete 'util'
package com.munidigital.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtil { // 'object' en Kotlin es un Singleton, perfecto para Utilities

    /**
     * Crea un nombre de archivo único para una nueva foto o video.
     * @param context Contexto de la aplicación (Necesario en Android para directorios).
     * @param extension La extensión del archivo ("jpg", "mp4").
     * @return El path absoluto (String) donde se debe guardar el archivo.
     */
    fun crearArchivoMultimediaTemporal(context: Context, extension: String): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "MUNI_${timeStamp}.${extension}"

        // Esto asume un entorno Android. Retorna la ruta segura.
        val storageDir = context.getExternalFilesDir(null)
        val newFile = File(storageDir, fileName)
        return newFile.absolutePath
    }

    /**
     * Función utilitaria para mostrar diálogos de confirmación o alerta.
     * En Android, esta función simplemente encapsularía la lógica de un AlertDialog.Builder
     */
    fun mostrarDialogo(context: Context, titulo: String, mensaje: String, onConfirm: () -> Unit = {}) {
        // Lógica real de Android para mostrar el cuadro de diálogo
        // System.out.println es un placeholder para la salida en consola
        println("--- DIÁLOGO: $titulo ---")
        println(mensaje)
        // Aquí se mostraría el AlertDialog

        // Si hay una acción de confirmación (ej: enviar trámite) la ejecutamos
        if (titulo.contains("Confirm")) {
            // Simular clic en "Aceptar"
            onConfirm()
        }
    }
}