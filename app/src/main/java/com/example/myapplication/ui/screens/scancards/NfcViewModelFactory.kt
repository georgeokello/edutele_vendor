package com.example.myapplication.ui.screens.scancards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.respository.ScanCardRepository
import com.example.myapplication.data.respository.ScanNfcRepository

class NfcViewModelFactory (private val userPreferences: UserPreferences): ViewModelProvider.Factory {

    override fun <T:ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val repository = ScanNfcRepository(api)
        return NfcViewModel(userPreferences, repository) as T
    }

}