package com.pinqze.softclu.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinqze.softclu.data.ThemeType
import com.pinqze.softclu.ui.components.AnimatedBackground
import com.pinqze.softclu.ui.components.SelectableBubble
import com.pinqze.softclu.ui.theme.*
import com.pinqze.softclu.viewmodel.AppViewModel

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedBackground()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Title
                Text(
                    text = "Settings",
                    color = WhiteText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 40.dp, bottom = 32.dp)
                )
            }
            
            // Toggle settings
            item {
                SettingToggle(
                    title = "Vibration",
                    isEnabled = settings.vibrationEnabled,
                    onToggle = {
                        viewModel.updateSettings(settings.copy(vibrationEnabled = it))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                SettingToggle(
                    title = "Show Breathing Time",
                    isEnabled = settings.showBreathingTime,
                    onToggle = {
                        viewModel.updateSettings(settings.copy(showBreathingTime = it))
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "About",
                    color = SecondaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                PrivacyPolicyItem()
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Theme selection
            item {
                Text(
                    text = "Themes",
                    color = SecondaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ThemeType.values().forEach { theme ->
                        SelectableBubble(
                            text = theme.displayName,
                            size = 90.dp,
                            isSelected = settings.selectedTheme == theme,
                            color1 = theme.primaryColor,
                            color2 = theme.secondaryColor,
                            onClick = {
                                viewModel.updateSettings(settings.copy(selectedTheme = theme))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Breathing duration sliders
            item {
                Text(
                    text = "Breathing Phases",
                    color = SecondaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            item {
                BreathingPhaseSlider(
                    title = "Inhale",
                    value = settings.inhaleDuration,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(inhaleDuration = it))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                BreathingPhaseSlider(
                    title = "Hold",
                    value = settings.holdDuration,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(holdDuration = it))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                BreathingPhaseSlider(
                    title = "Exhale",
                    value = settings.exhaleDuration,
                    onValueChange = {
                        viewModel.updateSettings(settings.copy(exhaleDuration = it))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PrivacyPolicyItem(
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pinqzen.com/privacy-policy.html"))
                context.startActivity(intent)
            }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Privacy Policy",
            color = WhiteText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Tap to read",
            color = WhiteText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingToggle(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = WhiteText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WhiteText,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
private fun BreathingPhaseSlider(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            // Value bubble
            Box(
                modifier = Modifier
                    .size(50.dp)
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
                Text(
                    text = "$value",
                    color = WhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Slider with bubble indicators
        Box(modifier = Modifier.fillMaxWidth()) {
            // Bubble indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 2..8 step 2) {
                    Box(
                        modifier = Modifier
                            .size(if (i == value) 16.dp else 12.dp)
                            .background(
                                if (i <= value)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 2f..8f,
            steps = 5,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "2s",
                color = SecondaryText,
                fontSize = 12.sp
            )
            Text(
                text = "8s",
                color = SecondaryText,
                fontSize = 12.sp
            )
        }
    }
}

