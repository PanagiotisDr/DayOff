package com.repotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.repotracker.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * MainViewModel - Κεντρικό ViewModel για app-level state.
 * Χειρίζεται: theme mode, setup status, language.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    /** Αν έχει ολοκληρωθεί το setup */
    val isSetupComplete: Flow<Boolean> = preferencesRepository.isSetupComplete()
    
    /** Theme mode (0=System, 1=Light, 2=Dark) */
    val themeMode: Flow<Int> = preferencesRepository.getThemeMode()
    
    /** Επιλεγμένη γλώσσα */
    val language: Flow<String> = preferencesRepository.getLanguage()
}
