package com.example.munidigital.network

import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * Sealed class para representar el resultado de operaciones de red
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Función helper para ejecutar llamadas a la API de forma segura
 * Maneja errores comunes y los convierte en mensajes amigables
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val message = parseErrorMessage(errorBody) ?: when (e.code()) {
            400 -> "Datos inválidos. Verifica la información."
            401 -> "No autorizado. Inicia sesión nuevamente."
            403 -> "No tienes permisos para esta acción."
            404 -> "Recurso no encontrado."
            500 -> "Error del servidor. Intenta más tarde."
            else -> "Error en la petición: ${e.code()}"
        }
        Result.Error(message, e.code())
    } catch (e: IOException) {
        Result.Error("Error de conexión. Verifica tu internet.")
    } catch (e: Exception) {
        Result.Error("Error inesperado: ${e.message ?: "Desconocido"}")
    }
}

/**
 * Parsea el cuerpo de error de la API para extraer el mensaje
 */
private fun parseErrorMessage(errorBody: String?): String? {
    if (errorBody.isNullOrEmpty()) return null
    
    return try {
        val jsonObject = JSONObject(errorBody)
        
        // Intentar obtener mensaje de diferentes formatos de error
        when {
            jsonObject.has("message") -> jsonObject.getString("message")
            jsonObject.has("error") -> jsonObject.getString("error")
            jsonObject.has("errors") -> {
                // Para errores de validación
                val errors = jsonObject.getJSONArray("errors")
                if (errors.length() > 0) {
                    val firstError = errors.getJSONObject(0)
                    firstError.optString("msg", null)
                } else null
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
