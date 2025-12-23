package com.pinqze.softclu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinqze.softclu.data.SoundType
import com.pinqze.softclu.ui.components.AnimatedBackground
import com.pinqze.softclu.ui.components.SelectableBubble
import com.pinqze.softclu.ui.theme.*
import com.pinqze.softclu.viewmodel.AppViewModel

@Composable
fun SoundsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = "Background Sounds",
                color = WhiteText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)
            )
            
            // Sound options in a grid
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SoundType.values().take(2).forEach { soundType ->
                        val isSelected = settings.selectedSound == soundType
                        SelectableBubble(
                            text = soundType.displayName,
                            emoji = soundType.emoji,
                            size = 110.dp,
                            isSelected = isSelected,
                            color1 = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            color2 = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            onClick = {
                                viewModel.updateSettings(
                                    settings.copy(selectedSound = soundType)
                                )
                            }
                        )
                    }
                }
                
                // Second row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SoundType.values().drop(2).forEach { soundType ->
                        val isSelected = settings.selectedSound == soundType
                        SelectableBubble(
                            text = soundType.displayName,
                            emoji = soundType.emoji,
                            size = 110.dp,
                            isSelected = isSelected,
                            color1 = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            color2 = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            onClick = {
                                viewModel.updateSettings(
                                    settings.copy(selectedSound = soundType)
                                )
                            }
                        )
                    }
                }
            }
            
            // Volume control
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Volume",
                    color = SecondaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Volume bubbles indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .size(
                                        if (index < (settings.soundVolume * 5).toInt()) 16.dp else 12.dp
                                    )
                                    .background(
                                        if (index < (settings.soundVolume * 5).toInt())
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = settings.soundVolume,
                    onValueChange = { newVolume ->
                        viewModel.updateSettings(
                            settings.copy(soundVolume = newVolume)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

