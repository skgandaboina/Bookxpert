package com.bookxpert.utils

import android.content.Context
import androidx.core.content.edit

object PreferenceManager {
    private const val PREF_NAME = "app_preferences"
    private const val NOTIFICATION_ENABLED = "notification_enabled"
    private const val DARK_MODE_ENABLED = "dark_mode_enabled"

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(NOTIFICATION_ENABLED, true) // default true
    }

    fun setNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(NOTIFICATION_ENABLED, enabled) }
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(DARK_MODE_ENABLED, false) // default false
    }

    fun setDarkModeEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(DARK_MODE_ENABLED, enabled) }
    }
}
