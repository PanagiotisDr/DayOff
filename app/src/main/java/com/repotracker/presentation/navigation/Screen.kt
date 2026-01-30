package com.repotracker.presentation.navigation

/**
 * Ορισμός των routes για το Navigation.
 * Sealed class για type-safe navigation.
 */
sealed class Screen(val route: String) {
    /** Οθόνη επιλογής γλώσσας */
    data object Language : Screen("language")
    
    /** Οθόνη καλωσορίσματος */
    data object Welcome : Screen("welcome")
    
    /** Οθόνη ρύθμισης προγράμματος (wizard) */
    data object Setup : Screen("setup")
    
    /** Κεντρική οθόνη */
    data object Home : Screen("home")
    
    /** Στατιστικά */
    data object Statistics : Screen("statistics")
    
    /** Ρυθμίσεις */
    data object Settings : Screen("settings")
}
