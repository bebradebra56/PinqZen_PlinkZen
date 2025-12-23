package com.pinqze.softclu.utils

import android.content.Context
import android.media.MediaPlayer
import com.pinqze.softclu.R
import com.pinqze.softclu.data.SoundType

class SoundPlayer(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: SoundType = SoundType.SILENCE
    private var currentVolume: Float = 0.5f
    
    fun playSound(soundType: SoundType, volume: Float) {
        // Stop current sound
        stopSound()
        
        currentSound = soundType
        currentVolume = volume
        
        if (soundType == SoundType.SILENCE) return
        
        val resourceId = when (soundType) {
            SoundType.OCEAN -> R.raw.ocean_sound
            SoundType.RAIN -> R.raw.rain_sound
            SoundType.BIRDS -> R.raw.birds_sound
            SoundType.SILENCE -> return
        }
        
        try {
            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                isLooping = true
                setVolume(volume, volume)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stopSound() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()
                }
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer = null
    }
    
    fun pauseSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
            }
        }
    }
    
    fun resumeSound() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
            }
        }
    }
    
    fun setVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer?.setVolume(volume, volume)
    }
    
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
    
    fun release() {
        stopSound()
    }
}

