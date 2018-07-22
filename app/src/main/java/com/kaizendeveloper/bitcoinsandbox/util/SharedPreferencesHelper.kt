package com.kaizendeveloper.bitcoinsandbox.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesHelper private constructor(context: Context) {

    init {
        prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
    }

    companion object {

        private const val SETTINGS_NAME = "bitcoin_sandbox_settings"
        private const val KEY_IS_BOOTSTRAPPED = "isBootstrapped"

        private lateinit var prefs: SharedPreferences

        @Volatile
        private var INSTANCE: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferencesHelper(context).also { INSTANCE = it }
            }
        }
    }

    fun setBootstrapped(isBootstrapped: Boolean) {
        prefs.edit { putBoolean(KEY_IS_BOOTSTRAPPED, isBootstrapped) }
    }

    fun isBootstrapped() = prefs.getBoolean(KEY_IS_BOOTSTRAPPED, false)
}
