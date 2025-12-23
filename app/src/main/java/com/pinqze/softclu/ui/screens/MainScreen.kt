package com.pinqze.softclu.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pinqze.softclu.navigation.Screen
import com.pinqze.softclu.ui.components.BottomNavigationBar
import com.pinqze.softclu.viewmodel.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Breathing.route
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Breathing.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navController = navController,
            startDestination = Screen.Breathing.route
        ) {
            composable(Screen.Breathing.route) {
                BreathingScreen(viewModel = viewModel)
            }
            composable(Screen.Sounds.route) {
                SoundsScreen(viewModel = viewModel)
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

