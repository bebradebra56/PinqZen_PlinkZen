package com.pinqze.softclu.data

data class AppSettings(
    val vibrationEnabled: Boolean = true,
    val showBreathingTime: Boolean = true,
    val selectedTheme: ThemeType = ThemeType.BLUE,
    val inhaleDuration: Int = 4,
    val holdDuration: Int = 4,
    val exhaleDuration: Int = 4,
    val selectedSound: SoundType = SoundType.SILENCE,
    val soundVolume: Float = 0.5f
)

