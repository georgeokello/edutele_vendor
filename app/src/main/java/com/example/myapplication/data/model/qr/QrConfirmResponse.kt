package com.example.myapplication.data.model.qr


data class QrConfirmResponse (
    val public_id: String,
    val type: String,
    val status: String,
    val amount: String,
    val balance_before: String,
    val balance_after: String,
    val reference: String
)
