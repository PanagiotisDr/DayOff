package com.repotracker.presentation.screens.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repotracker.data.notification.NotificationScheduler
import com.repotracker.data.sound.SoundManager
import com.repotracker.domain.model.ShiftType
import com.repotracker.domain.model.WorkSchedule
import com.repotracker.domain.repository.PreferencesRepository
import com.repotracker.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel για το Setup Wizard.
 * Διαχειρίζεται τα 4 βήματα ρύθμισης.
 */
@HiltViewModel
class SetupViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationScheduler: NotificationScheduler,
    private val soundManager: SoundManager
) : ViewModel() {
    
    /** Τρέχον βήμα (0-4) - 5 βήματα τώρα με την επιλογή χώρας */
    var currentStep by mutableIntStateOf(0)
        private set
    
    /** Επιλεγμένη χώρα (GR, OTHER) */
    var selectedCountry by mutableStateOf("GR")
        private set
    
    /** Επιλεγμένες εργάσιμες ημέρες (1-7) */
    val selectedWorkDays = mutableStateListOf<Int>()
    
    /** Επιλεγμένη ημέρα ρεπό */
    var selectedRepoDay by mutableIntStateOf(1)
        private set
    
    /** Αν είναι κυλιόμενο ρεπό */
    var isRolling by mutableStateOf(true)
        private set
    
    /** Τύπος βάρδιας */
    var shiftType by mutableStateOf(ShiftType.NONE)
        private set
    
    /** Ήχοι ειδοποιήσεων ενεργοποιημένοι */
    var notificationSoundsEnabled by mutableStateOf(true)
        private set
    
    /** Ήχοι κλικ ενεργοποιημένοι */
    var clickSoundsEnabled by mutableStateOf(true)
        private set
    
    init {
        // Default: Δευτέρα-Σάββατο εργάσιμες
        selectedWorkDays.addAll(listOf(1, 2, 3, 4, 5, 6))
        // Φόρτωση ρύθμισης ήχων ειδοποιήσεων
        viewModelScope.launch {
            preferencesRepository.areNotificationSoundsEnabled().collect { enabled ->
                notificationSoundsEnabled = enabled
            }
        }
        // Φόρτωση ρύθμισης ήχων κλικ
        viewModelScope.launch {
            preferencesRepository.areClickSoundsEnabled().collect { enabled ->
                clickSoundsEnabled = enabled
            }
        }
    }
    
    /** Παίζει ήχο click για UI interactions */
    fun playClickSound() {
        soundManager.playClickSound()
    }
    
    /** Επιλογή χώρας */
    fun selectCountry(country: String) {
        selectedCountry = country
    }
    
    /** Toggle ήχων ειδοποιήσεων */
    fun toggleNotificationSounds(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationSoundsEnabled(enabled)
        }
    }
    
    /** Toggle ήχων κλικ */
    fun toggleClickSounds(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setClickSoundsEnabled(enabled)
        }
    }
    
    /** Toggle ημέρας εργασίας */
    fun toggleWorkDay(day: Int) {
        if (day in selectedWorkDays) {
            selectedWorkDays.remove(day)
        } else {
            selectedWorkDays.add(day)
        }
    }
    
    /** Επιλογή ημέρας ρεπό */
    fun selectRepoDay(day: Int) {
        selectedRepoDay = day
    }
    
    /** Toggle rolling/fixed */
    fun updateRolling(rolling: Boolean) {
        isRolling = rolling
    }
    
    /** Επιλογή βάρδιας */
    fun selectShiftType(type: ShiftType) {
        shiftType = type
    }
    
    /** Μετάβαση στο επόμενο βήμα */
    fun nextStep() {
        if (currentStep < 4) currentStep++
    }
    
    /** Μετάβαση στο προηγούμενο βήμα */
    fun previousStep() {
        if (currentStep > 0) currentStep--
    }
    
    /** Validation για το τρέχον βήμα */
    fun canProceed(): Boolean {
        return when (currentStep) {
            0 -> true // Επιλογή χώρας - πάντα έγκυρο (προεπιλογή GR)
            1 -> selectedWorkDays.isNotEmpty()
            2 -> selectedRepoDay in selectedWorkDays
            else -> true
        }
    }
    
    /**
     * Αποθήκευση και ολοκλήρωση.
     * Μετά την αποθήκευση, προγραμματίζει τα notifications αν είναι ενεργοποιημένα.
     */
    fun saveAndComplete(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Εύρεση της Δευτέρας της τρέχουσας εβδομάδας ως reference
            val today = LocalDate.now()
            val referenceDate = today.minusDays(
                (today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong()
            )
            
            val schedule = WorkSchedule(
                workDays = selectedWorkDays.sorted(),
                currentRepoDay = selectedRepoDay,
                referenceDate = referenceDate,
                isRolling = isRolling,
                shiftType = shiftType
            )
            
            scheduleRepository.saveSchedule(schedule)
            preferencesRepository.setSetupComplete(true)
            preferencesRepository.setUserCountry(selectedCountry)
            
            // Προγραμματισμός notifications για τα επόμενα ρεπό
            notificationScheduler.scheduleNextRepoNotifications()
            
            onComplete()
        }
    }
}

