package com.example.myapplication.data.respository

import com.example.myapplication.data.model.home.DashboardResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response


class HomeRepository(
    private val api: ApiService
) {
    suspend fun getCardInfo(token: String): Response<DashboardResponse> {
        return api.getCardInfo(
            "Bearer $token"
        )
    }
}