package com.repotracker.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repotracker.data.notification.NotificationScheduler
import com.repotracker.data.sound.SoundManager
import com.repotracker.domain.repository.PreferencesRepository
import com.repotracker.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel για την οθόνη ρυθμίσεων.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val scheduleRepository: ScheduleRepository,
    private val notificationScheduler: NotificationScheduler,
    private val soundManager: SoundManager
) : ViewModel() {
    
    /** Παίζει ήχο click για UI interactions */
    fun playClickSound() {
        soundManager.playClickSound()
    }
    
    /** Τρέχουσα γλώσσα */
    val language: Flow<String> = preferencesRepository.getLanguage()
    
    /** Theme mode */
    val themeMode: Flow<Int> = preferencesRepository.getThemeMode()
    
    /** Notifications enabled */
    val notificationsEnabled: Flow<Boolean> = preferencesRepository.areNotificationsEnabled()
    
    /** Ώρα notification */
    val notificationHour: Flow<Int> = preferencesRepository.getNotificationHour()
    
    /** Λεπτά notification */
    val notificationMinute: Flow<Int> = preferencesRepository.getNotificationMinute()
    
    /** Ήχοι ειδοποιήσεων ενεργοί */
    val notificationSoundsEnabled: Flow<Boolean> = preferencesRepository.areNotificationSoundsEnabled()
    
    /** Ήχοι click ενεργοί */
    val clickSoundsEnabled: Flow<Boolean> = preferencesRepository.areClickSoundsEnabled()
    
    /** Χώρα χρήστη (GR, OTHER) */
    val userCountry: Flow<String> = preferencesRepository.getUserCountry()
    
    /**
     * Αλλαγή γλώσσας με callback για να γνωρίζει πότε ολοκληρώθηκε.
     * Το callback καλείται ΑΦΟΥ αποθηκευτεί η ρύθμιση.
     */
    fun setLanguage(code: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(code)
            onComplete()
        }
    }
    
    /** Αλλαγή θέματος */
    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }
    
    /**
     * Toggle notifications.
     * Όταν ενεργοποιούνται, προγραμματίζει τα επόμενα ρεπό.
     * Όταν απενεργοποιούνται, ακυρώνει όλα τα pending.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
            if (enabled) {
                notificationScheduler.scheduleNextRepoNotifications()
            } else {
                notificationScheduler.cancelAll()
            }
        }
    }
    
    /**
     * Αλλαγή ώρας notification.
     * Μετά την αλλαγή, επαναπρογραμματίζει τα notifications.
     */
    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setNotificationTime(hour, minute)
            // Επαναπρογραμματισμός με τη νέα ώρα
            notificationScheduler.scheduleNextRepoNotifications()
        }
    }
    
    /** Reset schedule */
    fun resetSchedule(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Ακύρωση notifications πριν τη διαγραφή
            notificationScheduler.cancelAll()
            scheduleRepository.deleteSchedule()
            preferencesRepository.setSetupComplete(false)
            onComplete()
        }
    }
    
    /**
     * Αποστολή test notification για debugging.
     * Εμφανίζεται αμέσως.
     */
    fun sendTestNotification() {
        viewModelScope.launch {
            notificationScheduler.scheduleTestNotification()
        }
    }
    
    /** Ενεργοποίηση/απενεργοποίηση ήχων ειδοποιήσεων */
    fun setNotificationSoundsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationSoundsEnabled(enabled)
        }
    }
    
    /** Ενεργοποίηση/απενεργοποίηση ήχων click */
    fun setClickSoundsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setClickSoundsEnabled(enabled)
        }
    }
    
    /** Αλλαγή χώρας χρήστη */
    fun setUserCountry(country: String) {
        viewModelScope.launch {
            preferencesRepository.setUserCountry(country)
        }
    }
    
    /** Font scale για accessibility */
    val fontScale: Flow<Float> = preferencesRepository.getFontScale()
    
    /** Αλλαγή font scale */
    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            preferencesRepository.setFontScale(scale)
        }
    }
}
