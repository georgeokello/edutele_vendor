package com.example.myapplication.ui.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.respository.HomeRepository

class HomeViewModelFactory(
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitInstance.api
        val repository = HomeRepository(api)

        return HomeViewModel(userPreferences, repository) as T
    }
}