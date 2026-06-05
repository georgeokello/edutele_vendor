package com.example.myapplication.ui.util.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NfcDebug {
    var lastMessage by mutableStateOf("Waiting for NFC...")
}