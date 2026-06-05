package com.example.myapplication.data.respository

import com.example.myapplication.data.model.qr.QrConfirmRequest
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.model.qr.QrRequest
import com.example.myapplication.data.model.qr.QrResponse
import com.example.myapplication.data.remote.ApiService
import retrofit2.Response


class ScanCardRepository(
    private val api: ApiService
) {

}