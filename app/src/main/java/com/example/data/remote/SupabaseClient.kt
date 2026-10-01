package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SupabaseUser(
    val id: String,
    val email: String,
    val isConfirmed: Boolean = true
)

data class SupabaseSession(
    val accessToken: String,
    val tokenType: String,
    val user: SupabaseUser
)

sealed class AuthResult {
    data class Success(val user: SupabaseUser, val message: String) : AuthResult()
    data class NeedsConfirmation(val email: String, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

object SupabaseClient {

    private const val TAG = "SupabaseClient"
    const val PROJECT_NAME = "Trackie"
    const val PROJECT_ID = "utqvepqvblwhjnepxdvk"
    const val BASE_URL = "https://utqvepqvblwhjnepxdvk.supabase.co"
    const val ANON_KEY = "sb_publishable_V99KuTXT1LU5CkZ0cySreA_ORWtWl4o"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val PREFS_NAME = "trackie_supabase_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"

    suspend fun signUp(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/signup")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val root = JSONObject(body)
                val userId = root.optString("id", "")
                val userEmail = root.optString("email", email)
                val token = root.optString("access_token", "")

                val user = SupabaseUser(id = userId, email = userEmail, isConfirmed = token.isNotBlank())
                if (token.isNotBlank()) {
                    AuthResult.Success(user, "Account created successfully!")
                } else {
                    AuthResult.NeedsConfirmation(
                        userEmail,
                        "Verification link sent to $userEmail. You can also sign in directly once confirmed."
                    )
                }
            } else {
                val errorJson = try { JSONObject(body) } catch (e: Exception) { null }
                val errorMsg = errorJson?.optString("msg")
                    ?: errorJson?.optString("error_description")
                    ?: errorJson?.optString("message")
                    ?: "Signup failed (${response.code})"
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in signUp", e)
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun signIn(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/token?grant_type=password")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val root = JSONObject(body)
                val token = root.optString("access_token", "")
                val userObj = root.optJSONObject("user")
                val userId = userObj?.optString("id", "") ?: root.optString("id", "")
                val userEmail = userObj?.optString("email", email) ?: email

                val user = SupabaseUser(id = userId, email = userEmail, isConfirmed = true)
                AuthResult.Success(user, "Welcome back!")
            } else {
                val errorJson = try { JSONObject(body) } catch (e: Exception) { null }
                val errorMsg = errorJson?.optString("error_description")
                    ?: errorJson?.optString("msg")
                    ?: errorJson?.optString("message")
                    ?: "Invalid credentials (${response.code})"
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in signIn", e)
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun signOut(token: String?): Boolean = withContext(Dispatchers.IO) {
        if (token.isNullOrBlank()) return@withContext true
        try {
            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/logout")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer $token")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Error in signOut", e)
            true
        }
    }

    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/health")
                .addHeader("apikey", ANON_KEY)
                .get()
                .build()
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resetPasswordForEmail(email: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("email", email.trim())
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/recover")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (response.isSuccessful) {
                AuthResult.Success(
                    SupabaseUser(id = "recovery", email = email.trim(), isConfirmed = true),
                    "Password reset instructions have been sent to $email. Please check your inbox."
                )
            } else {
                val errorJson = try { JSONObject(body) } catch (e: Exception) { null }
                val errorMsg = errorJson?.optString("msg")
                    ?: errorJson?.optString("error_description")
                    ?: errorJson?.optString("message")
                    ?: "Password reset request failed (${response.code})"
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in resetPasswordForEmail", e)
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    private const val KEY_STAY_LOGGED_IN = "stay_logged_in"

    fun saveStayLoggedIn(context: Context, stayLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_STAY_LOGGED_IN, stayLoggedIn).apply()
    }

    fun isStayLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_STAY_LOGGED_IN, true)
    }

    fun saveSession(context: Context, session: SupabaseSession, stayLoggedIn: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_USER_ID, session.user.id)
            .putString(KEY_USER_EMAIL, session.user.email)
            .putBoolean(KEY_STAY_LOGGED_IN, stayLoggedIn)
            .apply()
    }

    fun loadSession(context: Context): SupabaseSession? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stayLoggedIn = prefs.getBoolean(KEY_STAY_LOGGED_IN, true)
        if (!stayLoggedIn) return null

        val token = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val id = prefs.getString(KEY_USER_ID, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "user@trackie.app") ?: "user@trackie.app"

        return SupabaseSession(
            accessToken = token,
            tokenType = "Bearer",
            user = SupabaseUser(id = id, email = email, isConfirmed = true)
        )
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

