package com.example.myapplication.data.remote


import com.example.myapplication.data.model.login.LoginDetailsResponse
import com.example.myapplication.data.model.login.LoginRequest
import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.model.transactions.TransactionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginDetailsResponse>

    @GET("me")
    suspend fun getUserDetails(
         @Header("Authorization") token: String
    ): Response<LoginDetailsResponse>

    @POST("payments/qr/scan")
    suspend fun scanqr(
        @Header("Authorization") token: String,
        @Body request: QrRequest
    ): Response<QrResponse>

    @POST("payments/confirm/{public_id}")
    suspend fun confirmPayment(
        @Header("Authorization") token: String,
        @Path("public_id")
        publicId: String,
        @Body request: QrConfirmRequest

    ): Response<QrConfirmResponse>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token : String

    ):Response<TransactionsResponse>

}