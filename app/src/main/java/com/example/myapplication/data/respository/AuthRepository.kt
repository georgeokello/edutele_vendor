package com.example.myapplication.data.respository

import android.util.Log
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.login.LoginRequest
import com.example.myapplication.data.remote.ApiService
import org.json.JSONObject


class AuthRepository(
    private val api: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun login(email: String, password: String): Result<Unit> {

        var user_token = ""
        var user_id = ""
        return try {
            val response = api.login(LoginRequest(email, password))
            Log.d("LOGIN_Direct", "User ID: ${response.body()}")
            if (response.isSuccessful) {
                //store token, and userID
                user_token = response.body()!!.access_token
                user_id = response.body()!!.user_id
                val response2 = api.getUserDetails("Bearer ${response.body()!!.access_token}")
                if (response2.isSuccessful){
                    Result.success(response2.body()!!)
                    // save username/ vendor Id
                    userPreferences.saveUser(
                        token = user_token,
                        userId = user_id,
                        username = response2.body()!!.full_name,
                    )
                    Result.success(Unit)

                }else{
                    Result.failure(Exception("Me failed"))
                }

            } else {
                val errorBody = response.errorBody()?.string()

                val errorDetail = errorBody?.let {
                    try {
                        JSONObject(it).getString("detail")
                    } catch (e: Exception) {
                        "Unknown error, Try again Later"
                    }
                }
                Result.failure(Exception(errorDetail))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}