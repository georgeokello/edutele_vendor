package com.example.myapplication.data.respository

import com.example.myapplication.data.model.redemptions.RedemptionsResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response

class RedemptionsRepository(private val api: ApiService) {
    suspend fun getVendorRedemptions(token: String): Response<RedemptionsResponse>{
        return  api.getRedemptions(
            "Bearer $token"
        )

    }
}