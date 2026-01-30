package com.repotracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.repotracker.presentation.screens.home.HomeScreen
import com.repotracker.presentation.screens.language.LanguageScreen
import com.repotracker.presentation.screens.settings.SettingsScreen
import com.repotracker.presentation.screens.setup.SetupScreen
import com.repotracker.presentation.screens.statistics.StatisticsScreen
import com.repotracker.presentation.screens.welcome.WelcomeScreen
import com.repotracker.presentation.viewmodels.MainViewModel

/**
 * Κεντρικό Navigation Host της εφαρμογής.
 * Διαχειρίζεται το routing μεταξύ οθονών.
 */
@Composable
fun RepoTrackerNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isSetupComplete by mainViewModel.isSetupComplete.collectAsState(initial = null)
    
    // Προσδιορισμός αρχικής οθόνης βάσει setup status
    val startDestination = when (isSetupComplete) {
        true -> Screen.Home.route
        false -> Screen.Language.route
        null -> return // Loading state
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Language.route) {
            LanguageScreen(
                onLanguageSelected = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Language.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToStats = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSchedule = {
                    navController.navigate(Screen.Language.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
