package com.pinqze.softclu.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedBubble(
    size: Dp,
    color1: Color,
    color2: Color,
    offsetY: Dp = 0.dp,
    scale: Float = 1f,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    
    Box(
        modifier = Modifier
            .offset(y = offsetY + animatedOffset.dp)
            .size(size)
            .scale(animatedScale * scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color1.copy(alpha = 0.7f),
                        color2.copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner glow
        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        content()
    }
}

@Composable
fun PulsatingBubble(
    size: Dp,
    color1: Color,
    color2: Color,
    pulsating: Boolean,
    scaleFactor: Float = 1f,
    offsetY: Float = 0f,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (pulsating) scaleFactor else 1f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )
    
    val offset by animateFloatAsState(
        targetValue = if (pulsating) offsetY else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "offset"
    )
    
    Box(
        modifier = Modifier
            .offset(y = offset.dp)
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color1.copy(alpha = 0.8f),
                        color2.copy(alpha = 0.6f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner glow
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .blur(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Highlight
        Box(
            modifier = Modifier
                .size(size * 0.3f)
                .offset(x = (-size * 0.15f), y = (-size * 0.15f))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        content()
    }
}

@Composable
fun BubbleButton(
    text: String,
    size: Dp = 80.dp,
    fontSize: TextUnit = 16.sp,
    color1: Color,
    color2: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color1.copy(alpha = 0.9f),
                        color2.copy(alpha = 0.7f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner glow
        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isPressed) 0.5f else 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun SelectableBubble(
    text: String,
    emoji: String = "",
    size: Dp = 100.dp,
    isSelected: Boolean,
    color1: Color,
    color2: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selection_scale"
    )
    
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color1.copy(alpha = if (isSelected) 0.9f else 0.6f),
                        color2.copy(alpha = if (isSelected) 0.8f else 0.5f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Glow effect when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(size * 0.8f)
                    .blur(15.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (emoji.isNotEmpty()) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

