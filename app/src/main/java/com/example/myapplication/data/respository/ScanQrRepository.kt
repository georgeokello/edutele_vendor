package com.example.myapplication.data.respository

import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response

class ScanQrRepository(
    private val api: ApiService
) {
    suspend fun submitQr(qrData: String, amount: String, remarks: String, token: String): Response<QrResponse> {
        return api.scanqr(
            "Bearer $token",
            QrRequest(
                qrData,
                amount.toInt(),
                remarks
            )
        )
    }

    suspend fun confirmPayment(
        publicId: String,
        token: String,
        pin: String
    ): Response<QrConfirmResponse> {

        return api.confirmPayment(
            "Bearer $token",
            publicId,
            QrConfirmRequest(
                pin
            )
        )
    }
}