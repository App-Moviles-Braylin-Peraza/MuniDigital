package com.example.munidigital.network

import com.example.munidigital.model.Adjunto
import com.example.munidigital.model.AdjuntoResponse
import com.example.munidigital.model.DeleteAdjuntoRequest
import com.example.munidigital.model.LoginRequest
import com.example.munidigital.model.LoginResponse
import com.example.munidigital.model.RegisterRequest
import com.example.munidigital.model.TipoTramite
import com.example.munidigital.model.Tramite
import com.example.munidigital.model.TramiteRequest
import com.example.munidigital.model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ============ AUTENTICACIÓN ============
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // ============ TRÁMITES ============
    
    @GET("tramites")
    suspend fun getTramites(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null
    ): List<Tramite>

    @POST("tramites")
    suspend fun createTramite(@Body request: TramiteRequest): Tramite

    @GET("tramites/{id}")
    suspend fun getTramiteById(@Path("id") id: Int): Tramite

    @PUT("tramites/{id}")
    suspend fun updateTramite(@Path("id") id: Int, @Body request: TramiteRequest): Tramite

    @DELETE("tramites/{id}")
    suspend fun deleteTramite(@Path("id") id: Int): Response<Unit>

    // Tipos de Tramite (Catálogo)
    @GET("tipos-tramite")
    suspend fun getTiposTramite(): List<TipoTramite>

    @GET("tipos-tramite/{id}")
    suspend fun getTipoTramiteById(@Path("id") id: Int): TipoTramite

    // ============ FOTOS/ADJUNTOS ============
    
    /**
     * Sube una foto a un trámite específico
     * @param tramiteId ID del trámite
     * @param foto Archivo de imagen (max 5MB, formatos: JPG, PNG, GIF, WEBP)
     */
    @Multipart
    @POST("tramites/{tramite_id}/adjuntos")
    suspend fun uploadPhoto(
        @Path("tramite_id") tramiteId: Int,
        @Part foto: MultipartBody.Part
    ): AdjuntoResponse

    /**
     * Obtiene todas las fotos de un trámite
     * @param tramiteId ID del trámite
     */
    @GET("tramites/{tramite_id}/adjuntos")
    suspend fun getPhotos(@Path("tramite_id") tramiteId: Int): List<Adjunto>

    /**
     * Elimina una foto específica
     * @param tramiteId ID del trámite
     * @param request Body con file_url
     */
    @HTTP(method = "DELETE", path = "tramites/{tramite_id}/adjuntos", hasBody = true)
    suspend fun deletePhoto(
        @Path("tramite_id") tramiteId: Int,
        @Body request: DeleteAdjuntoRequest
    ): Response<Unit>
}