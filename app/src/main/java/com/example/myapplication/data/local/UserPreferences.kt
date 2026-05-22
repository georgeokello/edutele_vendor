package com.example.myapplication.data.local


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("username")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveUser(token: String, userId: String, username:String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token
            prefs[USER_ID] = userId
            prefs[USER_NAME] = username
            prefs[IS_LOGGED_IN] = true
        }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map {
        it[TOKEN]
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map {
        it[USER_NAME]
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map {
        it[USER_ID]
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}