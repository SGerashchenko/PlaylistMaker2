package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val APP_PREFERENCES = "app_preferences"
const val THEME_KEY = "theme_text"

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences
    private var isDarkThemeEnabled: Boolean = false

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        isDarkThemeEnabled = sharedPrefs.getBoolean(THEME_KEY, false)
        applyTheme(isDarkThemeEnabled)
    }

    fun isDarkTheme(): Boolean = isDarkThemeEnabled

    fun toggleTheme(enabled: Boolean) {
        isDarkThemeEnabled = enabled
        applyTheme(enabled)
        saveThemePreference(enabled)
    }

    private fun applyTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun saveThemePreference(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, enabled).apply()
    }
}


