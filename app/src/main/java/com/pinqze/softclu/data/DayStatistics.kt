package com.pinqze.softclu.data

data class DayStatistics(
    val date: String,
    val totalSeconds: Int,
    val cyclesCompleted: Int
) {
    val minutes: Int get() = totalSeconds / 60
    val seconds: Int get() = totalSeconds % 60
    
    val formattedTime: String get() = when {
        totalSeconds < 60 -> "$totalSeconds sec"
        seconds == 0 -> "$minutes min"
        else -> "$minutes min $seconds sec"
    }
}

