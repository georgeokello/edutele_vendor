package com.example.myapplication.data.model.redemptions

data class RedemptionsResponse(
val items: List<RedemptionsItem>,
val total: Int
)

data class RedemptionsItem(
    val public_id: String,
    val type: String,
    val status: String,
    val amount: String,
    val remaining_before: String,
    val remaining_after: String,
    val reference: String,
    val remarks: String,
    val timestamp: String,
    val verified_at: String,
    val fee_amount: String,
    val gross_amount: String
)