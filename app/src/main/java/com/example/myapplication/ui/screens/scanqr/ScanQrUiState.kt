package com.example.myapplication.ui.screens.scanqr

data class ScanQrUiState (
    val amount: String = "",
    val remark: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
    )