package com.pinqze.softclu.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinqze.softclu.ui.theme.*
import com.pinqze.softclu.viewmodel.AppViewModel

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    ),
                )
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(
                icon = "🫁",
                label = "Breathe",
                isSelected = currentRoute == "breathing",
                onClick = { onNavigate("breathing") }
            )
            
            NavigationItem(
                icon = "🔊",
                label = "Sounds",
                isSelected = currentRoute == "sounds",
                onClick = { onNavigate("sounds") }
            )
            
            NavigationItem(
                icon = "📈",
                label = "Stats",
                isSelected = currentRoute == "statistics",
                onClick = { onNavigate("statistics") }
            )
            
            NavigationItem(
                icon = "⚙️",
                label = "Settings",
                isSelected = currentRoute == "settings",
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .scale(scale)
                .background(
                    brush = if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect when selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .blur(10.dp)
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
            }
            
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

