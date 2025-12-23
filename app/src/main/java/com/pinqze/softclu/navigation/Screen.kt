package com.pinqze.softclu.navigation

sealed class Screen(val route: String) {
    object Breathing : Screen("breathing")
    object Sounds : Screen("sounds")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}

