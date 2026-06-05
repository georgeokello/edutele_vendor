package com.example.myapplication.ui.screens.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.transactions.TransactionItem
import com.example.myapplication.data.respository.TransactionsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// 📊 UI State
data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionItem> = emptyList(),
    val error: String? = null
)

class TransactionViewModel (
    private val userPreferences: UserPreferences,
    private val repository: TransactionsRepository

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun fetchTransactions() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){

                    val response = repository.getVendorTransactions(tokenValue)

                    if (response.isSuccessful) {

                        val transactions =
                            response.body()?.items ?: emptyList()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            transactions = transactions
                        )

                    } else {

                        // handle error/ request unsuccessful

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail ?: "Unknown Server error",
                            isLoading = false
                        )

                    }

                }else{
                    _uiState.value = _uiState.value.copy(
                        error = "No token found"
                    )
                }


            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load transactions"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimestamp(timestamp: String): String {

        val instant = Instant.parse(timestamp)

        val localDateTime = instant.atZone(
            ZoneId.systemDefault()
        )

        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy, h:mm a"
        )

        return localDateTime.format(formatter)
    }
}