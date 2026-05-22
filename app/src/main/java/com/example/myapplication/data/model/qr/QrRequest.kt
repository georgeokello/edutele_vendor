package com.example.myapplication.data.model.qr

data class QrRequest (
    val card_qr_token: String,
    val amount: Int,
    val remarks: String
)