package com.example.munidigital.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

object SessionManager {
    private const val PREF_NAME = "MuniDigitalPrefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    
    private var sharedPreferences: SharedPreferences? = null
    
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }
    
    var authToken: String?
        get() = sharedPreferences?.getString(KEY_AUTH_TOKEN, null)
        set(value) {
            sharedPreferences?.edit()?.putString(KEY_AUTH_TOKEN, value)?.apply()
        }
    
    var userId: Int?
        get() = sharedPreferences?.getInt(KEY_USER_ID, -1)?.takeIf { it != -1 }
        set(value) {
            if (value != null) {
                sharedPreferences?.edit()?.putInt(KEY_USER_ID, value)?.apply()
            }
        }
    
    fun clearSession() {
        sharedPreferences?.edit()?.clear()?.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return authToken != null
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        SessionManager.authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
