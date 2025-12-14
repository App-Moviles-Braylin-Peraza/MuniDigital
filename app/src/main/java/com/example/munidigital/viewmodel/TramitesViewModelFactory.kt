package com.example.munidigital.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.munidigital.repository.TramiteRepository

class TramitesViewModelFactory(
    private val tramiteRepository: TramiteRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TramitesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TramitesViewModel(tramiteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
