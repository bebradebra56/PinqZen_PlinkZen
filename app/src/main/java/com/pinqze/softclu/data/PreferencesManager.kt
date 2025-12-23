package com.pinqze.softclu.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pinqzen_preferences")

class PreferencesManager(private val context: Context) {
    
    private object PreferencesKeys {
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SHOW_BREATHING_TIME = booleanPreferencesKey("show_breathing_time")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val INHALE_DURATION = intPreferencesKey("inhale_duration")
        val HOLD_DURATION = intPreferencesKey("hold_duration")
        val EXHALE_DURATION = intPreferencesKey("exhale_duration")
        val SELECTED_SOUND = stringPreferencesKey("selected_sound")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val TOTAL_MINUTES_PREFIX = "total_minutes_"
        val CYCLES_PREFIX = "cycles_"
    }
    
    val appSettings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            showBreathingTime = preferences[PreferencesKeys.SHOW_BREATHING_TIME] ?: true,
            selectedTheme = ThemeType.valueOf(
                preferences[PreferencesKeys.SELECTED_THEME] ?: ThemeType.BLUE.name
            ),
            inhaleDuration = preferences[PreferencesKeys.INHALE_DURATION] ?: 4,
            holdDuration = preferences[PreferencesKeys.HOLD_DURATION] ?: 4,
            exhaleDuration = preferences[PreferencesKeys.EXHALE_DURATION] ?: 4,
            selectedSound = SoundType.valueOf(
                preferences[PreferencesKeys.SELECTED_SOUND] ?: SoundType.SILENCE.name
            ),
            soundVolume = preferences[PreferencesKeys.SOUND_VOLUME] ?: 0.5f
        )
    }
    
    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = settings.vibrationEnabled
            preferences[PreferencesKeys.SHOW_BREATHING_TIME] = settings.showBreathingTime
            preferences[PreferencesKeys.SELECTED_THEME] = settings.selectedTheme.name
            preferences[PreferencesKeys.INHALE_DURATION] = settings.inhaleDuration
            preferences[PreferencesKeys.HOLD_DURATION] = settings.holdDuration
            preferences[PreferencesKeys.EXHALE_DURATION] = settings.exhaleDuration
            preferences[PreferencesKeys.SELECTED_SOUND] = settings.selectedSound.name
            preferences[PreferencesKeys.SOUND_VOLUME] = settings.soundVolume
        }
    }
    
    suspend fun addBreathingSession(seconds: Int, cycles: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        
        context.dataStore.edit { preferences ->
            val totalSecondsKey = intPreferencesKey("total_seconds_$today")
            val cyclesKey = intPreferencesKey("${PreferencesKeys.CYCLES_PREFIX}$today")
            
            val currentSeconds = preferences[totalSecondsKey] ?: 0
            val currentCycles = preferences[cyclesKey] ?: 0
            
            preferences[totalSecondsKey] = currentSeconds + seconds
            preferences[cyclesKey] = currentCycles + cycles
        }
    }
    
    fun getStatistics(): Flow<List<DayStatistics>> = context.dataStore.data.map { preferences ->
        val stats = mutableListOf<DayStatistics>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Get last 30 days
        for (i in 0..29) {
            val date = dateFormat.format(calendar.time)
            val totalSecondsKey = intPreferencesKey("total_seconds_$date")
            val cyclesKey = intPreferencesKey("${PreferencesKeys.CYCLES_PREFIX}$date")
            
            val seconds = preferences[totalSecondsKey] ?: 0
            val cycles = preferences[cyclesKey] ?: 0
            
            if (seconds > 0 || cycles > 0) {
                stats.add(DayStatistics(date, seconds, cycles))
            }
            
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        stats.sortedByDescending { it.date }
    }
    
    suspend fun clearStatistics() {
        context.dataStore.edit { preferences ->
            val keysToRemove = preferences.asMap().keys.filter { 
                it.name.startsWith("total_seconds_") ||
                it.name.startsWith(PreferencesKeys.CYCLES_PREFIX)
            }
            keysToRemove.forEach { preferences.remove(it) }
        }
    }
}

