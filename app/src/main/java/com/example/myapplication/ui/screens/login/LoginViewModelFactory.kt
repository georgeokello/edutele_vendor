package com.example.myapplication.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.respository.AuthRepository
import com.example.myapplication.data.remote.RetrofitInstance


class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val prefs = UserPreferences(context)
        val repository = AuthRepository(api, prefs)

        return LoginViewModel(repository) as T
    }
}