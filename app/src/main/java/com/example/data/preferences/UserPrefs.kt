package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chekunets_prefs", Context.MODE_PRIVATE)

    var liquidLevelMl: Int
        get() = prefs.getInt(KEY_LIQUID_LEVEL, 0)
        set(value) = prefs.edit().putInt(KEY_LIQUID_LEVEL, value.coerceIn(0, MAX_VOLUME_ML)).apply()

    var isCapClosed: Boolean
        get() = prefs.getBoolean(KEY_CAP_CLOSED, false)
        set(value) = prefs.edit().putBoolean(KEY_CAP_CLOSED, value).apply()

    var totalDrunkMl: Int
        get() = prefs.getInt(KEY_TOTAL_DRUNK, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_DRUNK, value).apply()

    var barTargetSum: Double
        get() = prefs.getFloat(KEY_BAR_TARGET_SUM, 500f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_BAR_TARGET_SUM, value.toFloat()).apply()

    var accumulatedSum: Double
        get() = prefs.getFloat(KEY_ACCUMULATED_SUM, 0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_ACCUMULATED_SUM, value.toFloat()).apply()

    companion object {
        const val MAX_VOLUME_ML = 250
        private const val KEY_LIQUID_LEVEL = "liquid_level_ml"
        private const val KEY_CAP_CLOSED = "is_cap_closed"
        private const val KEY_TOTAL_DRUNK = "total_drunk_ml"
        private const val KEY_BAR_TARGET_SUM = "bar_target_sum"
        private const val KEY_ACCUMULATED_SUM = "accumulated_sum"
    }
}
