package com.example.myapplication.ui.screens.scancards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences

class NfcViewModelFactory (private val userPreferences: UserPreferences): ViewModelProvider.Factory {

    override fun <T:ViewModel> create(modelClass: Class<T>): T {
        return NfcViewModel(userPreferences) as T
    }

}