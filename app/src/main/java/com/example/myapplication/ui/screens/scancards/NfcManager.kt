package com.example.myapplication.ui.screens.scancards

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NfcManager {

    private val _nfcData = MutableStateFlow<String?>(null)
    val nfcData: StateFlow<String?> = _nfcData

    var hasScanned = MutableStateFlow(false)
        private set

    fun updateData(data: String) {
        _nfcData.value = data
    }

    fun setTrue(){
        hasScanned.value = true
    }

    fun setFalse(){
        hasScanned.value = false
    }
}
