package com.example.myapplication.ui.screens.scanqr

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.respository.ScanQrRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first


class ScanQrViewModel(
    private val repository: ScanQrRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    var isLoading = mutableStateOf(false)
        private set

    var resultMessage = mutableStateOf("")

    var errorMessage = mutableStateOf("")
        private set

    var publicId = mutableStateOf("")
        private set

    var showPasswordDialog = mutableStateOf(false)
        private set

    var showConfirmationDialog = mutableStateOf(false)
        private set

    var qrResponse = mutableStateOf<QrConfirmResponse?>(null)
        private set

    private var hasScanned = false


    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun dismissPasswordDialog() {

        showPasswordDialog.value = false
    }


    fun processQrCode(
        qrData: String,
        amount: String,
        remarks: String
    ) {

        if (hasScanned) return

        hasScanned = true

        viewModelScope.launch {

            isLoading.value = true

            try {

                val tokenValue =
                    userPreferences.tokenFlow.first()

                if (!tokenValue.isNullOrEmpty()) {

                    Log.d("SCAN_QR", "token Value: $tokenValue")


                    val response = repository.submitQr(
                            qrData,
                            amount,
                            remarks,
                            tokenValue
                        )

                    if(response.isSuccessful){

                        val body = response.body()
                        Log.d("SCAN_QR", "response body: $body")

                        if (body != null) {
                            publicId.value = body.public_id
                        }

                        resultMessage.value = " request went through"
                        showPasswordDialog.value = true

                        Log.d("SCAN_QR", "Request went through")

                    } else {

                        errorMessage.value =
                            response.message()
                        Log.d("SCAN_QR", "Request failed")

                    }


                } else {

                    errorMessage.value =
                        "No token available"
                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message ?: "Unknown error"

            } finally {

                isLoading.value = false
            }
        }
    }

    fun confirmPay(pin: String){
        viewModelScope.launch {

            try {

                // get token
                val tokenValue = userPreferences.tokenFlow.first()
                Log.d("SCAN_QR", "Confirm pay tokenValue: $tokenValue")


                if(!tokenValue.isNullOrEmpty()){
                    val response = publicId.let { repository.confirmPayment(it.value,tokenValue,pin) }

                    if(response.isSuccessful){
                        showPasswordDialog.value = false
                        qrResponse.value = response.body()
                        showConfirmationDialog.value = true

                        Log.d("SCAN_QR", "Confirmation went through")

                    }else{
                        Log.d("SCAN_QR", "Confirm pay Request failed")
                    }

                }else {
                    errorMessage.value =
                        "No token available"
                    Log.d("SCAN_QR", "confirm pay No token available")
                }

            }catch (e:Exception){
                errorMessage.value =
                    e.message ?: "Unknown error"
                Log.d("SCAN_QR", "confirm pay Unknown error")
            }


        }

    }
    fun dismissConfirmDialog() {
        showConfirmationDialog.value = false
    }


}