package com.example.myapplication.ui.screens.scanqr

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.respository.ScanQrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import org.json.JSONObject


class ScanQrViewModel(
    private val repository: ScanQrRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    var isLoading = MutableStateFlow(false)
        private set

    var successMessage = MutableStateFlow("")

    var errorMessage = MutableStateFlow("")
        private set

    var publicId = MutableStateFlow("")
        private set

    var cardHolder = MutableStateFlow("")
        private set

    var enterAccessValueDialog = MutableStateFlow(false)
        private set

    var showPasswordDialog = MutableStateFlow(false)
        private set

    var showVerifyAccessDialog = MutableStateFlow(false)
        private set

    var showErrorDialog = MutableStateFlow(false)
    private

    var qrResponse = MutableStateFlow<QrConfirmResponse?>(null)
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

    fun checkToken(qrToken: String){
        viewModelScope.launch {

            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if (!tokenValue.isNullOrEmpty()) {

                    val response = repository.checkQrData(
                        tokenValue, qrToken
                    )

                    if(response.isSuccessful){
                        publicId.value = response.body()?.public_id ?: ""

                        cardHolder.value = response.body()?.card_holder_name ?: ""

                        openAccessValueDialog()
                    } else {

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }
                        errorMessage.value = errorDetail ?: "Unknown error"
                        Log.d("SCAN_QR", "Request failed")
                        showErrorDialog.value = true
                    }

                } else {

                    errorMessage.value =
                        "No token available"

                }
            }catch (e:Exception){
                errorMessage.value =
                    e.message ?: "Unknown error"
            }

        }
    }

    fun processQrCode(
        publicId: String,
        accessValue: String,
        remarks: String
    ) {

        if (hasScanned) return

        hasScanned = true

        viewModelScope.launch {

            isLoading.value = true

            try {

                val tokenValue =
                    userPreferences.tokenFlow.first()

                if (!tokenValue.isNullOrEmpty() && publicId.isNotEmpty()) {
                    val response = repository.submitAccessValue(
                        publicId,
                        accessValue,
                            remarks,
                            tokenValue
                        )

                    if(response.isSuccessful){
                        showPasswordDialog.value = true
                    } else {

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }

                        errorMessage.value = errorDetail ?: "Unknown error"
                        showErrorDialog.value = true
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

    fun verifyAccess(pin: String){
        viewModelScope.launch {

            try {

                // get token
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = publicId.let { repository.verifyAccess(it.value,tokenValue,pin) }

                    if(response.isSuccessful){
                        showPasswordDialog.value = false
                        qrResponse.value = response.body()
                        showVerifyAccessDialog.value = true
                        successMessage.value = "Access Received Successfully"

                    }else{

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }

                        errorMessage.value = errorDetail ?: "Unknown error"
                        showErrorDialog.value = true

                    }

                }else {
                    errorMessage.value =
                        "No token available"

                }

            }catch (e:Exception){
                errorMessage.value =
                    e.message ?: "Unknown error"

            }


        }

    }
    fun dismissConfirmDialog() {
        showVerifyAccessDialog.value = false
    }

    fun openAccessValueDialog(){
        enterAccessValueDialog.value = true
    }

    fun closeAccessValueDialog(){
        enterAccessValueDialog.value = false
    }

    fun dismissErrorDialog(){
        showErrorDialog.value = false
    }

}