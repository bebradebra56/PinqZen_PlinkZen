package com.pinqze.softclu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinqze.softclu.data.BreathingPhase
import com.pinqze.softclu.ui.components.*
import com.pinqze.softclu.ui.theme.*
import com.pinqze.softclu.viewmodel.AppViewModel

@Composable
fun BreathingScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val breathingPhase by viewModel.breathingPhase.collectAsState()
    val isBreathing by viewModel.isBreathing.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val settings by viewModel.settings.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedBackground(breathingPhase = breathingPhase)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = "Relax Mode",
                color = WhiteText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp)
            )
            
            // Central breathing bubble
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val phaseText = when (breathingPhase) {
                        BreathingPhase.INHALE -> "Breathe In"
                        BreathingPhase.HOLD -> "Hold"
                        BreathingPhase.EXHALE -> "Breathe Out"
                        BreathingPhase.REST -> "Rest"
                    }
                    
                    val scaleFactor = when (breathingPhase) {
                        BreathingPhase.INHALE -> 1.3f
                        BreathingPhase.HOLD -> 1.3f
                        BreathingPhase.EXHALE -> 0.9f
                        BreathingPhase.REST -> 1.0f
                    }
                    
                    val offsetY = when (breathingPhase) {
                        BreathingPhase.INHALE -> -30f
                        BreathingPhase.HOLD -> -30f
                        BreathingPhase.EXHALE -> 20f
                        BreathingPhase.REST -> 0f
                    }
                    
                    PulsatingBubble(
                        size = 200.dp,
                        color1 = MaterialTheme.colorScheme.secondary,
                        color2 = MaterialTheme.colorScheme.primary,
                        pulsating = isBreathing,
                        scaleFactor = scaleFactor,
                        offsetY = offsetY
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = phaseText,
                                color = WhiteText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (settings.showBreathingTime && isBreathing) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$timeRemaining",
                                    color = WhiteText.copy(alpha = 0.8f),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Phase indicator bubbles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PhaseIndicatorBubble(
                            isActive = breathingPhase == BreathingPhase.INHALE,
                            duration = settings.inhaleDuration
                        )
                        PhaseIndicatorBubble(
                            isActive = breathingPhase == BreathingPhase.HOLD,
                            duration = settings.holdDuration
                        )
                        PhaseIndicatorBubble(
                            isActive = breathingPhase == BreathingPhase.EXHALE,
                            duration = settings.exhaleDuration
                        )
                    }
                }
            }
            
            // Start/Pause button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                BubbleButton(
                    text = if (isBreathing) "Pause" else "Start",
                    size = 100.dp,
                    fontSize = 20.sp,
                    color1 = MaterialTheme.colorScheme.primary,
                    color2 = MaterialTheme.colorScheme.secondary,
                    onClick = { viewModel.toggleBreathing() }
                )
            }
        }
    }
}

@Composable
private fun PhaseIndicatorBubble(
    isActive: Boolean,
    duration: Int
) {
    val activeColor1 = MaterialTheme.colorScheme.primary
    val activeColor2 = MaterialTheme.colorScheme.secondary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBubble(
            size = if (isActive) 50.dp else 40.dp,
            color1 = if (isActive) activeColor1 else inactiveColor,
            color2 = if (isActive) activeColor2 else inactiveColor.copy(alpha = 0.2f),
            scale = if (isActive) 1f else 0.8f
        ) {
            Text(
                text = "$duration",
                color = WhiteText,
                fontSize = 16.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

