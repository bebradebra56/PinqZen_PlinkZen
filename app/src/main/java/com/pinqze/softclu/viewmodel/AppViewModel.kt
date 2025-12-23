package com.pinqze.softclu.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinqze.softclu.data.*
import com.pinqze.softclu.utils.SoundPlayer
import com.pinqze.softclu.utils.VibrationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val context: Context) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    private val vibrationHelper = VibrationHelper(context)
    private val soundPlayer = SoundPlayer(context)
    
    // Settings
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    // Breathing state
    private val _breathingPhase = MutableStateFlow(BreathingPhase.INHALE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()
    
    private val _isBreathing = MutableStateFlow(false)
    val isBreathing: StateFlow<Boolean> = _isBreathing.asStateFlow()
    
    private val _timeRemaining = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()
    
    private val _cyclesCompleted = MutableStateFlow(0)
    val cyclesCompleted: StateFlow<Int> = _cyclesCompleted.asStateFlow()
    
    private val _totalMinutes = MutableStateFlow(0)
    val totalMinutes: StateFlow<Int> = _totalMinutes.asStateFlow()
    
    // Statistics
    private val _statistics = MutableStateFlow<List<DayStatistics>>(emptyList())
    val statistics: StateFlow<List<DayStatistics>> = _statistics.asStateFlow()
    
    private var breathingJob: Job? = null
    private var sessionStartTime: Long = 0
    
    init {
        viewModelScope.launch {
            preferencesManager.appSettings.collect { settings ->
                val previousSound = _settings.value.selectedSound
                val previousVolume = _settings.value.soundVolume
                _settings.value = settings
                
                // Update sound only if changed
                if (settings.selectedSound != previousSound) {
                    soundPlayer.playSound(settings.selectedSound, settings.soundVolume)
                } else if (settings.soundVolume != previousVolume) {
                    soundPlayer.setVolume(settings.soundVolume)
                }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.getStatistics().collect { stats ->
                _statistics.value = stats
            }
        }
    }
    
    fun pauseSound() {
        soundPlayer.pauseSound()
    }
    
    fun resumeSound() {
        soundPlayer.resumeSound()
    }
    
    fun stopSound() {
        soundPlayer.stopSound()
    }
    
    override fun onCleared() {
        super.onCleared()
        soundPlayer.release()
    }
    
    fun startBreathing() {
        if (_isBreathing.value) return
        
        _isBreathing.value = true
        sessionStartTime = System.currentTimeMillis()
        _cyclesCompleted.value = 0
        
        breathingJob = viewModelScope.launch {
            while (_isBreathing.value) {
                // Inhale
                _breathingPhase.value = BreathingPhase.INHALE
                if (_settings.value.vibrationEnabled) vibrationHelper.vibrateShort()
                countDown(_settings.value.inhaleDuration)
                
                // Hold
                if (!_isBreathing.value) break
                _breathingPhase.value = BreathingPhase.HOLD
                if (_settings.value.vibrationEnabled) vibrationHelper.vibrateShort()
                countDown(_settings.value.holdDuration)
                
                // Exhale
                if (!_isBreathing.value) break
                _breathingPhase.value = BreathingPhase.EXHALE
                if (_settings.value.vibrationEnabled) vibrationHelper.vibrateShort()
                countDown(_settings.value.exhaleDuration)
                
                // Rest (short pause)
                if (!_isBreathing.value) break
                _breathingPhase.value = BreathingPhase.REST
                delay(1000)
                
                _cyclesCompleted.value++
                if (_settings.value.vibrationEnabled) vibrationHelper.vibrateLong()
            }
        }
    }
    
    fun pauseBreathing() {
        if (!_isBreathing.value) return
        
        _isBreathing.value = false
        breathingJob?.cancel()
        
        // Save session if at least 1 cycle completed
        if (_cyclesCompleted.value > 0) {
            val sessionSeconds = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()
            val sessionMinutes = sessionSeconds / 60
            
            _totalMinutes.value += sessionMinutes
            viewModelScope.launch {
                // Save seconds, not minutes!
                preferencesManager.addBreathingSession(sessionSeconds, _cyclesCompleted.value)
            }
        }
    }
    
    fun toggleBreathing() {
        if (_isBreathing.value) {
            pauseBreathing()
        } else {
            startBreathing()
        }
    }
    
    private suspend fun countDown(seconds: Int) {
        for (i in seconds downTo 1) {
            if (!_isBreathing.value) break
            _timeRemaining.value = i
            delay(1000)
        }
    }
    
    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch {
            preferencesManager.updateSettings(settings)
        }
    }
    
    fun clearStatistics() {
        viewModelScope.launch {
            preferencesManager.clearStatistics()
        }
    }
    
    fun getConsecutiveDays(): Int {
        val stats = _statistics.value
        if (stats.isEmpty()) return 0
        
        var consecutive = 0
        var currentDate = java.util.Calendar.getInstance()
        
        for (i in 0..30) {
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(currentDate.time)
            
            if (stats.any { it.date == dateStr }) {
                consecutive++
                currentDate.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        
        return consecutive
    }
}

