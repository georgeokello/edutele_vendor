package com.example.myapplication.ui.screens.scancards

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.qr.QrConfirmResponse
import com.example.myapplication.data.respository.ScanCardRepository
import com.example.myapplication.data.respository.ScanNfcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

class NfcViewModel(
    private val userPreferences: UserPreferences,
    private  val repository: ScanNfcRepository
): ViewModel() {

    var isLoading = MutableStateFlow(false)
        private set

    var successMessage = MutableStateFlow("")
        private set

    var errorMessage = MutableStateFlow("")
        private set

    var showErrorDialog = MutableStateFlow(false)
        private set

    var publicId = MutableStateFlow("")
        private set

    var cardHolder = MutableStateFlow("")
        private set


    var enterAmountDialog = MutableStateFlow(false)
        private set

    var showPasswordDialog = MutableStateFlow(false)
        private set

    var showConfirmationDialog = MutableStateFlow(false)
        private set

    var qrResponse = MutableStateFlow<QrConfirmResponse?>(null)
        private set

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

    fun checkNfcUid(nfcUid: String){
        viewModelScope.launch {
            isLoading.value = true
            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if (!tokenValue.isNullOrEmpty()) {

                    val response = repository.checkNfcUid(
                        tokenValue, nfcUid
                    )

                    if(response.isSuccessful){
                        publicId.value = response.body()?.public_id ?: ""
                        cardHolder.value = response.body()?.card_holder_name ?: ""

                        openEnterAmountDialog()
                        isLoading.value = false
                        NfcManager.setFalse()

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
                        isLoading.value = false
                        NfcManager.setFalse()
                        openErrorDialog()
                    }

                } else {

                    errorMessage.value =
                        "No token available"
                    isLoading.value = false
                    NfcManager.setFalse()
                }
            }catch (e:Exception){
                errorMessage.value =
                    "Unknown error in catch"
                isLoading.value = false
                NfcManager.setFalse()
            }

        }
    }

    fun processQrCode(publicProcessId: String, amount: String, remarks: String) {

        viewModelScope.launch {

            isLoading.value = true

            try {

                val tokenValue =
                    userPreferences.tokenFlow.first()

                if (!tokenValue.isNullOrEmpty() && publicProcessId.isNotEmpty()) {
                    val response = repository.submitPayAmount(
                        publicProcessId,
                        amount,
                        remarks,
                        tokenValue
                    )

                    if(response.isSuccessful){
                        showPasswordDialog.value = true
                        publicId.value = (response.body()?.public_id ?: 0).toString()
                        isLoading.value = false
                        closeEnterAmountDialog()
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
                        isLoading.value = false
                        openErrorDialog()
                    }


                } else {

                    errorMessage.value =
                        "No token available"

                    isLoading.value = false
                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message ?: "Unknown error"

            } finally {

                isLoading.value = false
            }
        }
    }

    fun confirmPay(publicProcessId: String, pin: String){
        viewModelScope.launch {

            try {

                // get token
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.confirmPayment(publicProcessId,tokenValue,pin)

                    if(response.isSuccessful){
                        showPasswordDialog.value = false
                        showConfirmationDialog.value = true
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
                        openErrorDialog()
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
        showConfirmationDialog.value = false
    }

    fun openEnterAmountDialog(){
        enterAmountDialog.value = true
    }

    fun closeEnterAmountDialog(){
        enterAmountDialog.value = false
        NfcManager.setFalse()
    }

    fun openErrorDialog(){
        showErrorDialog.value = true
    }

    fun closeErrorDialog(){
        showErrorDialog.value = false
    }

}