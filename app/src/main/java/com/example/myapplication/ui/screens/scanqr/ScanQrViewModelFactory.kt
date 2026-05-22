package com.example.myapplication.ui.screens.scanqr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.respository.ScanQrRepository

class ScanQrViewModelFactory(private val userPreferences: UserPreferences): ViewModelProvider.Factory {
    override fun <T:ViewModel> create(
        modelClass: Class<T>
    ): T {
        val api = RetrofitInstance.api
        val repository = ScanQrRepository(api)
        return ScanQrViewModel(repository, userPreferences) as T
    }
}