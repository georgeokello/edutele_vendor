package com.example.myapplication.data.model.qr

data class QrResponse (
    val public_id: String,
    val card_holder_name: String,
    val card_number_masked: String,
    val amount: String,
    val currency: String,
    val expires_at: String,
    val status: String
)


