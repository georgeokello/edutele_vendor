package com.example.myapplication.data.respository

import com.example.myapplication.data.model.nfc.nfcUidRequest
import com.example.myapplication.data.model.nfc.nfcUidResponse
import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.model.qr.checkQrTokenRequest
import com.example.myapplication.data.model.qr.checkQrTokenResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response

class ScanNfcRepository(private val api:ApiService) {

    suspend fun checkNfcUid(token:String, nfcUid: String): Response<nfcUidResponse> {
        return api.checkNfcUid(
            "Bearer $token",
            nfcUidRequest(nfcUid)
        )
    }

    suspend fun submitPayAmount(path:String, amount: String, remarks: String, token: String): Response<QrResponse> {
        return api.setAmount(
            "Bearer $token",
            path,
            QrRequest(
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