package com.pinqze.softclu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pinqze.softclu.data.ThemeType

@Composable
fun PinqZenTheme(
    themeType: ThemeType = ThemeType.BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.BLUE -> darkColorScheme(
            primary = Aquamarine,
            secondary = LightTurquoise,
            tertiary = Color(0xFF5EFFFF),
            background = DeepPurpleBlue,
            surface = DarkerBackground,
            onPrimary = WhiteText,
            onSecondary = WhiteText,
            onTertiary = WhiteText,
            onBackground = WhiteText,
            onSurface = SecondaryText
        )
        ThemeType.TURQUOISE -> darkColorScheme(
            primary = LightTurquoise,
            secondary = Cyan,
            tertiary = Color(0xFF00E6E6),
            background = Color(0xFF004466),
            surface = Color(0xFF003344),
            onPrimary = WhiteText,
            onSecondary = WhiteText,
            onTertiary = WhiteText,
            onBackground = WhiteText,
            onSurface = SecondaryText
        )
        ThemeType.PURPLE -> darkColorScheme(
            primary = Lilac,
            secondary = PinkPurple,
            tertiary = Color(0xFFB266FF),
            background = Color(0xFF2D004D),
            surface = Color(0xFF1A0033),
            onPrimary = WhiteText,
            onSecondary = WhiteText,
            onTertiary = WhiteText,
            onBackground = WhiteText,
            onSurface = SecondaryText
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}