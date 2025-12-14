package com.example.munidigital.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munidigital.model.Adjunto
import com.example.munidigital.model.AdjuntoResponse
import com.example.munidigital.model.Tramite
import com.example.munidigital.network.Result
import com.example.munidigital.network.safeApiCall
import com.example.munidigital.repository.TramiteRepository
import kotlinx.coroutines.launch

class TramitesViewModel(private val tramiteRepository: TramiteRepository) : ViewModel() {

    private val _tramites = MutableLiveData<Result<List<Tramite>>>()
    val tramites: LiveData<Result<List<Tramite>>> = _tramites

    private val _createResult = MutableLiveData<Result<Tramite>>()
    val createResult: LiveData<Result<Tramite>> = _createResult

    private val _updateResult = MutableLiveData<Result<Tramite>>()
    val updateResult: LiveData<Result<Tramite>> = _updateResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para operaciones de fotos
    private val _uploadPhotoResult = MutableLiveData<Result<AdjuntoResponse>>()
    val uploadPhotoResult: LiveData<Result<AdjuntoResponse>> = _uploadPhotoResult

    private val _photos = MutableLiveData<Result<List<Adjunto>>>()
    val photos: LiveData<Result<List<Adjunto>>> = _photos

    private val _deletePhotoResult = MutableLiveData<Result<Unit>>()
    val deletePhotoResult: LiveData<Result<Unit>> = _deletePhotoResult

    /**
     * Carga todos los trámites con filtros opcionales
     */
    fun loadTramites(
        page: Int? = null,
        limit: Int? = null,
        status: String? = null,
        sort: String? = null,
        order: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _tramites.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.getTramites(page, limit, status, sort, order)
            }
            
            _tramites.value = result
            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo trámite
     */
    fun createTramite(title: String, description: String?, direccion: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _createResult.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.createTramite(title, description, direccion)
            }
            
            _createResult.value = result
            _isLoading.value = false
            
            // Si fue exitoso, recargar la lista
            if (result is Result.Success) {
                loadTramites()
            }
        }
    }

    /**
     * Actualiza un trámite existente
     */
    fun updateTramite(
        id: Int,
        title: String? = null,
        description: String? = null,
        status: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateResult.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.updateTramite(id, title, description, status)
            }
            
            _updateResult.value = result
            _isLoading.value = false
            
            // Si fue exitoso, recargar la lista
            if (result is Result.Success) {
                loadTramites()
            }
        }
    }

    fun deleteTramite(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteResult.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.deleteTramite(id)
            }
            
            _deleteResult.value = result
            _isLoading.value = false

            if (result is Result.Success) {
                loadTramites()
            }
        }
    }

    /**
     * Limpia los resultados de operaciones
     */
    fun clearResults() {
        _createResult.value = null
        _updateResult.value = null
        _deleteResult.value = null
        _uploadPhotoResult.value = null
        _deletePhotoResult.value = null
    }

    // ============ MÉTODOS PARA FOTOS ============

    /**
     * Sube una foto a un trámite existente
     * @param tramiteId ID del trámite
     * @param context Contexto de la aplicación
     * @param photoUri URI de la foto
     */
    fun uploadPhoto(tramiteId: Int, context: Context, photoUri: Uri) {
        viewModelScope.launch {
            _uploadPhotoResult.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.uploadPhoto(tramiteId, context, photoUri)
            }
            
            _uploadPhotoResult.value = result
            
            // Si fue exitoso, recargar las fotos del trámite
            if (result is Result.Success) {
                loadPhotos(tramiteId)
            }
        }
    }

    /**
     * Carga todas las fotos de un trámite
     */
    fun loadPhotos(tramiteId: Int) {
        viewModelScope.launch {
            _photos.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.getPhotos(tramiteId)
            }
            
            _photos.value = result
        }
    }


    fun deletePhoto(tramiteId: Int, fileUrl: String) {
        viewModelScope.launch {
            _deletePhotoResult.value = Result.Loading
            
            val result = safeApiCall {
                tramiteRepository.deletePhoto(tramiteId, fileUrl)
            }
            
            _deletePhotoResult.value = result
            
            // Si fue exitoso, recargar las fotos
            if (result is Result.Success) {
                loadPhotos(tramiteId)
            }
        }
    }
}
