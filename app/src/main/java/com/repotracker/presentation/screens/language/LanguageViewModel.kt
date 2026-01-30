package com.repotracker.presentation.screens.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repotracker.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για την οθόνη επιλογής γλώσσας.
 */
@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    /**
     * Αποθηκεύει την επιλεγμένη γλώσσα.
     * @param languageCode "el" για Ελληνικά, "en" για Αγγλικά
     * @param onComplete Callback μετά την αποθήκευση
     */
    fun selectLanguage(languageCode: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(languageCode)
            onComplete()
        }
    }
}
