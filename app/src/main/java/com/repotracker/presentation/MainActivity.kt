package com.repotracker.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.repotracker.data.local.UserPreferencesManager
import com.repotracker.presentation.navigation.RepoTrackerNavHost
import com.repotracker.presentation.theme.RepoTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * MainActivity - Το κεντρικό Activity της εφαρμογής.
 * 
 * Extends AppCompatActivity για per-app language support.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var preferencesManager: UserPreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Εφαρμογή γλώσσας ΜΕΤΑ το super (για να είναι έτοιμο το Hilt injection)
        applyLanguageFromPreferences()
        
        // Ενεργοποίηση edge-to-edge
        enableEdgeToEdge()
        
        setContent {
            // Παρακολούθηση theme mode και font scale
            val themeMode by preferencesManager.themeMode.collectAsState(initial = 0)
            val fontScale by preferencesManager.fontScale.collectAsState(initial = 1.0f)
            
            RepoTrackerTheme(themeMode = themeMode, fontScale = fontScale) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RepoTrackerNavHost()
                }
            }
        }
    }
    
    /**
     * Εφαρμόζει τη γλώσσα από τα preferences.
     */
    private fun applyLanguageFromPreferences() {
        val savedLanguage = runBlocking { 
            preferencesManager.language.first() 
        }
        
        val currentLocale = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .takeIf { it.isNotEmpty() }
        
        // Αλλαγή μόνο αν διαφέρει
        if (currentLocale == null || !currentLocale.startsWith(savedLanguage)) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(savedLanguage)
            )
        }
    }
}
