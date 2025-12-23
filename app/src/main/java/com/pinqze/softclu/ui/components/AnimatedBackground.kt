package com.pinqze.softclu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pinqze.softclu.data.BreathingPhase
import com.pinqze.softclu.ui.theme.*
import kotlin.random.Random

@Composable
fun AnimatedBackground(
    breathingPhase: BreathingPhase = BreathingPhase.INHALE,
    modifier: Modifier = Modifier
) {
    val baseColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    val targetColor1 = when (breathingPhase) {
        BreathingPhase.INHALE -> baseColor
        BreathingPhase.HOLD -> surfaceColor
        BreathingPhase.EXHALE -> surfaceColor
        BreathingPhase.REST -> baseColor
    }
    
    val targetColor2 = when (breathingPhase) {
        BreathingPhase.INHALE -> baseColor.copy(alpha = 0.8f)
        BreathingPhase.HOLD -> baseColor
        BreathingPhase.EXHALE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
        BreathingPhase.REST -> baseColor.copy(alpha = 0.8f)
    }
    
    val animatedColor1 by animateColorAsState(
        targetValue = targetColor1,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "color1"
    )
    
    val animatedColor2 by animateColorAsState(
        targetValue = targetColor2,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "color2"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedColor1, animatedColor2)
                )
            )
    )
}

