package com.example.sneakerhub.helpers

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper {
    companion object {
        private const val PREFS_NAME = "store"

        // Save to Preferences
        fun savePrefs(context: Context, key: String, value: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        // Get from Preferences
        fun getPrefs(context: Context, key: String, defaultValue: String? = ""): String? {
            val pref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return pref.getString(key, defaultValue) ?: defaultValue
        }

        // Remove an Item from Preferences
        fun clearPrefsByKey(context: Context, key: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.remove(key)
            editor.apply()
        }

        // Clear All from Preferences
        fun clearPrefs(context: Context) {
            val pref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.clear()
            editor.apply()
        }
    }
}
