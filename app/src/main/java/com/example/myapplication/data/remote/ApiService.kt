package com.example.myapplication.data.remote


import com.example.myapplication.data.model.home.DashboardResponse
import com.example.myapplication.data.model.login.LoginDetailsResponse
import com.example.myapplication.data.model.login.LoginRequest
import com.example.myapplication.data.model.nfc.nfcUidRequest
import com.example.myapplication.data.model.nfc.nfcUidResponse
import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.model.qr.checkQrTokenRequest
import com.example.myapplication.data.model.qr.checkQrTokenResponse
import com.example.myapplication.data.model.redemptions.RedemptionsResponse
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

    // set Amount
    @POST("redemptions/{public_id}/amount")
    suspend fun setAccessValue(
        @Header("Authorization") token: String,
        @Path("public_id") publicId: String,
        @Body request: QrRequest
    ): Response<QrResponse>

    @POST("redemptions/confirm/{public_id}")
    suspend fun verifyAccess(
        @Header("Authorization") token: String,
        @Path("public_id")
        publicId: String,
        @Body request: QrConfirmRequest

    ): Response<QrConfirmResponse>

    @GET("access-events")
    suspend fun getRedemptions(
        @Header("Authorization") token : String

    ):Response<RedemptionsResponse>

    // check qr token
    @POST("redemptions/qr/scan")
    suspend fun checkQrToken(
        @Header("Authorization") token: String,
        @Body request: checkQrTokenRequest
    ):Response<checkQrTokenResponse>

    // check nfc uid
    @POST("redemptions/nfc/scan")
    suspend fun checkNfcUid(
        @Header("Authorization") token: String,
        @Body request: nfcUidRequest

    ): Response<nfcUidResponse>


    @GET("dashboard")
    suspend fun getCardInfo(
        @Header("Authorization") token : String
    ):Response<DashboardResponse>

}