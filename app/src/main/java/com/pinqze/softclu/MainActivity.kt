package com.pinqze.softclu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.pinqze.softclu.ui.screens.MainScreen
import com.pinqze.softclu.ui.theme.PinqZenTheme
import com.pinqze.softclu.viewmodel.AppViewModel
import com.pinqze.softclu.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: AppViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make the app fullscreen with transparent status bar
        enableEdgeToEdge()
        
        // Initialize ViewModel
        val factory = AppViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]
        
        setContent {
            val settings by viewModel.settings.collectAsState()
            
            PinqZenTheme(themeType = settings.selectedTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Pause breathing and sound when app goes to background
        viewModel.pauseBreathing()
        viewModel.pauseSound()
    }
    
    override fun onResume() {
        super.onResume()
        // Resume sound when app comes back
        viewModel.resumeSound()
    }
    
    override fun onStop() {
        super.onStop()
        // Stop sound completely when app is stopped
        viewModel.stopSound()
    }
}