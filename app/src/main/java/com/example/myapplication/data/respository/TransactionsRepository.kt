package com.example.myapplication.data.respository

import com.example.myapplication.data.model.transactions.TransactionsResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response

class TransactionsRepository(private val api: ApiService) {
    suspend fun getVendorTransactions(token: String): Response<TransactionsResponse>{
        return  api.getTransactions(
            "Bearer $token"
        )

    }
}