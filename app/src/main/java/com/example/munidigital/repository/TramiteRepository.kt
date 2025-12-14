package com.example.munidigital.repository

import android.content.Context
import android.net.Uri
import com.example.munidigital.model.Adjunto
import com.example.munidigital.model.AdjuntoResponse
import com.example.munidigital.model.DeleteAdjuntoRequest
import com.example.munidigital.model.Tramite
import com.example.munidigital.model.TramiteRequest
import com.example.munidigital.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class TramiteRepository(private val apiService: ApiService) {

    suspend fun getTramites(
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        sort: String? = null,
        order: String? = null
    ): List<Tramite> {
        return apiService.getTramites(page, limit, status, sort, order)
    }

    suspend fun createTramite(title: String, description: String?, direccion: String? = null): Tramite {
        val request = TramiteRequest(title = title, description = description, direccion = direccion)
        android.util.Log.d("TramiteRepository", "Enviando trámite a la API - Direccion: $direccion")
        return apiService.createTramite(request)
    }

    suspend fun getTramiteById(id: Int): Tramite {
        return apiService.getTramiteById(id)
    }

    suspend fun updateTramite(
        id: Int,
        title: String? = null,
        description: String? = null,
        status: String? = null
    ): Tramite {
        val request = TramiteRequest(title, description, status)
        return apiService.updateTramite(id, request)
    }

    suspend fun deleteTramite(id: Int) {
        apiService.deleteTramite(id)
    }

    // ============ MÉTODOS PARA FOTOS/ADJUNTOS ============

    /**
     * Sube una foto a un trámite
     * @param tramiteId ID del trámite
     * @param context Contexto de la aplicación
     * @param photoUri URI de la foto seleccionada
     * @return AdjuntoResponse con la URL de Cloudinary
     */
    suspend fun uploadPhoto(tramiteId: Int, context: Context, photoUri: Uri): AdjuntoResponse {
        // Crear archivo temporal desde URI
        val inputStream = context.contentResolver.openInputStream(photoUri)
            ?: throw IllegalArgumentException("No se pudo abrir la imagen")
        
        val tempFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        // Validar tamaño (max 5MB)
        val maxSizeBytes = 5 * 1024 * 1024 // 5MB
        if (tempFile.length() > maxSizeBytes) {
            tempFile.delete()
            throw IllegalArgumentException("La imagen excede el tamaño máximo de 5MB")
        }

        // Determinar tipo MIME
        val mimeType = context.contentResolver.getType(photoUri) ?: "image/jpeg"
        
        // Validar formato
        val allowedFormats = listOf("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp")
        if (!allowedFormats.contains(mimeType.lowercase())) {
            tempFile.delete()
            throw IllegalArgumentException("Formato no soportado. Use JPG, PNG, GIF o WEBP")
        }

        // Crear MultipartBody.Part
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("foto", tempFile.name, requestBody)

        return try {
            val response = apiService.uploadPhoto(tramiteId, photoPart)
            tempFile.delete()
            response
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }

    /**
     * Obtiene todas las fotos de un trámite
     */
    suspend fun getPhotos(tramiteId: Int): List<Adjunto> {
        val photos = apiService.getPhotos(tramiteId)
        android.util.Log.d("TramiteRepository", "getPhotos para trámite $tramiteId: ${photos.size} fotos")
        photos.forEachIndexed { index, foto ->
            android.util.Log.d("TramiteRepository", "Foto ${index + 1}: id=${foto.id}, url='${foto.url}'")
        }
        return photos
    }

    /**
     * Elimina una foto específica
     * @param tramiteId ID del trámite
     * @param fileUrl URL del archivo a eliminar
     */
    suspend fun deletePhoto(tramiteId: Int, fileUrl: String) {
        val request = DeleteAdjuntoRequest(fileUrl)
        apiService.deletePhoto(tramiteId, request)
    }
}
