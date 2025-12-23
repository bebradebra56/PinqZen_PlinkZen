package com.pinqze.softclu.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinqze.softclu.data.DayStatistics
import com.pinqze.softclu.ui.components.AnimatedBackground
import com.pinqze.softclu.ui.components.BubbleButton
import com.pinqze.softclu.ui.theme.*
import com.pinqze.softclu.viewmodel.AppViewModel
import kotlin.math.min

@Composable
fun StatisticsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val statistics by viewModel.statistics.collectAsState()
    val consecutiveDays = viewModel.getConsecutiveDays()
    var showResetDialog by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Your Progress",
                color = WhiteText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)
            )
            
            // Streak info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$consecutiveDays days in a row",
                        color = WhiteText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Keep it up!",
                        color = SecondaryText,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Statistics list
            if (statistics.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No data yet",
                            color = SecondaryText,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start your first breathing session!",
                            color = SecondaryText.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(statistics) { stat ->
                        StatisticBubbleItem(stat)
                    }
                }
            }
            
            // Reset button
            if (statistics.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                ) {
                    BubbleButton(
                        text = "Reset",
                        size = 80.dp,
                        fontSize = 16.sp,
                        color1 = Color(0xFFFF6B6B).copy(alpha = 0.7f),
                        color2 = Color(0xFFFF8E8E).copy(alpha = 0.6f),
                        onClick = {
                            viewModel.clearStatistics()
                        }
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatisticBubbleItem(stat: DayStatistics) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "item_scale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isExpanded = !isExpanded }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date bubble
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val day = stat.date.substring(8, 10)
                val month = stat.date.substring(5, 7)
                Text(
                    text = day,
                    color = WhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = month,
                    color = SecondaryText,
                    fontSize = 12.sp
                )
            }
        }
        
        // Time display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(100.dp)
        ) {
            Text(
                text = stat.formattedTime,
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        // Cycles bubble
        StatBubble(
            value = "${stat.cyclesCompleted}",
            label = "cycles",
            size = min(stat.cyclesCompleted * 5 + 50, 100)
        )
        
        // Record indicator
        if (stat.totalSeconds >= 600) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GoldenWhite.copy(alpha = 0.8f),
                                Color(0xFFFFD700).copy(alpha = 0.6f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⭐",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun StatBubble(
    value: String,
    label: String,
    size: Int
) {
    val animatedSize by animateDpAsState(
        targetValue = size.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bubble_size"
    )
    
    Box(
        modifier = Modifier
            .size(animatedSize)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = SecondaryText,
                fontSize = 10.sp
            )
        }
    }
}

