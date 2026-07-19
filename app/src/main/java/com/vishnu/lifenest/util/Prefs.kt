package com.vishnu.lifenest.util

import android.content.Context

object Prefs {
    private const val NAME = "lifenest_settings"

    fun get(context: Context) = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun notificationsEnabled(context: Context) = get(context).getBoolean("notifications_enabled", true)
    fun darkModeEnabled(context: Context) = get(context).getBoolean("dark_mode_enabled", true)
    fun pinEnabled(context: Context) = get(context).getBoolean("pin_enabled", false)
    fun pinCode(context: Context): String? = get(context).getString("pin_code", null)
    fun fontScale(context: Context) = get(context).getFloat("font_scale", 1.0f)

    fun setPinCode(context: Context, pin: String) {
        get(context).edit().putString("pin_code", pin).apply()
    }

    fun clearPinCode(context: Context) {
        get(context).edit().remove("pin_code").apply()
    }

    fun setFontScale(context: Context, scale: Float) {
        get(context).edit().putFloat("font_scale", scale).apply()
    }
}
