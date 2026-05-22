package com.example.myapplication.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.respository.TransactionsRepository

class TransactionViewModelFactory(private  val userPreferences: UserPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitInstance.api
        val repository = TransactionsRepository(api)

        return TransactionViewModel(userPreferences, repository) as T

    }
}