package com.example.myapplication.data.respository

import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.model.qr.checkQrTokenRequest
import com.example.myapplication.data.model.qr.checkQrTokenResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response

class ScanQrRepository(
    private val api: ApiService
) {

    suspend fun checkQrData(token:String, qrToken: String): Response<checkQrTokenResponse>{
        return api.checkQrToken(
            "Bearer $token",
            checkQrTokenRequest(qrToken)
        )
    }

    suspend fun submitAccessValue(path:String, amount: String, remarks: String, token: String): Response<QrResponse> {
        return api.setAccessValue(
            "Bearer $token",
            path,
            QrRequest(
                amount.toInt(),
                remarks
            )
        )
    }

    suspend fun verifyAccess(
        publicId: String,
        token: String,
        pin: String
    ): Response<QrConfirmResponse> {

        return api.verifyAccess(
            "Bearer $token",
            publicId,
            QrConfirmRequest(
                pin
            )
        )
    }
}