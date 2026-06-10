package com.example.myapplication.ui.screens.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.home.AccessAllocation
import com.example.myapplication.data.model.home.MainStats
import com.example.myapplication.data.model.home.QuickStats
import com.example.myapplication.data.model.home.RecentRedemptions
import com.example.myapplication.data.model.home.TopBranch
import com.example.myapplication.data.respository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject


data class DashboardUiState(
    val isLoading: Boolean = false,
    val mainStats: MainStats? = null,
    val quickStats: QuickStats? =null,
    val recentRedemptions: List<RecentRedemptions> = emptyList(),
    val topBranches: List<TopBranch> = emptyList(),
    val accessAllocation: AccessAllocation? =null,
    val error: String? = null
)

class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val repository: HomeRepository
): ViewModel(){

    var uiState = MutableStateFlow(DashboardUiState())
        private set

    val token: StateFlow<String?> =
        userPreferences.tokenFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    init {
        getCardInfo()
    }

    fun getCardInfo() {

        viewModelScope.launch {

            uiState.value = uiState.value.copy(
                isLoading = true
            )


            try {

                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.getCardInfo(tokenValue)
                    if (response.isSuccessful){
                        uiState.value = DashboardUiState(
                            isLoading = false,
                            mainStats = response.body()?.main_stats,
                            quickStats = response.body()?.quick_stats,
                            recentRedemptions = response.body()?.recent_activities ?: emptyList(),
                            topBranches = response.body()?.top_branches ?: emptyList(),
                            accessAllocation = response.body()?.float_allocation,
                            error = null
                        )
                        Log.d("Dashboard", response.body().toString())
                    } else {
                        // handle error/ request unsuccessful

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        uiState.value = uiState.value.copy(
                            error = errorDetail,
                            isLoading = false
                        )
                    }

                }else{
                    uiState.value = uiState.value.copy(
                        error = "No token found",
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                uiState.value = uiState.value.copy(
                    error = "Try again Later",
                    isLoading = false
                )
            }
        }
    }


}