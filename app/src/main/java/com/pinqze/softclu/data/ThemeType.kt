package com.pinqze.softclu.data

import androidx.compose.ui.graphics.Color
import com.pinqze.softclu.ui.theme.Cyan
import com.pinqze.softclu.ui.theme.LightTurquoise
import com.pinqze.softclu.ui.theme.Lilac

enum class ThemeType(val displayName: String, val primaryColor: Color, val secondaryColor: Color) {
    BLUE("Blue", Color(0xFF0066A8), Color(0xFF5EFFFF)),
    TURQUOISE("Turquoise", LightTurquoise, Cyan),
    PURPLE("Purple", Lilac, Color(0xFFC266FF))
}

