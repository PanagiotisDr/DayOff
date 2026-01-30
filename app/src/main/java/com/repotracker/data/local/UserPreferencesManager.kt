package com.repotracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension property για DataStore.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manager για user preferences μέσω DataStore.
 * Αποθηκεύει: γλώσσα, θέμα, notifications, setup status.
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    companion object {
        // Κλειδιά για τα preferences
        val KEY_SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_THEME_MODE = intPreferencesKey("theme_mode")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEY_NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val KEY_NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val KEY_NOTIFICATION_SOUNDS_ENABLED = booleanPreferencesKey("notification_sounds_enabled")
        val KEY_CLICK_SOUNDS_ENABLED = booleanPreferencesKey("click_sounds_enabled")
        val KEY_USER_COUNTRY = stringPreferencesKey("user_country")
        val KEY_FONT_SCALE = floatPreferencesKey("font_scale")
        
        // Backward compatibility - παλιό key που μεταφέρεται στο νέο
        val KEY_SOUNDS_ENABLED_LEGACY = booleanPreferencesKey("sounds_enabled")
        
        // Default: 20:00 (8 PM)
        const val DEFAULT_NOTIFICATION_HOUR = 20
        const val DEFAULT_NOTIFICATION_MINUTE = 0
        
        // Default country: Ελλάδα για backward compatibility
        const val DEFAULT_COUNTRY = "GR"
        
        // Font scale επιλογές: 1.0f = Κανονικό, 1.15f = Μεγάλο, 1.3f = Πολύ Μεγάλο
        const val DEFAULT_FONT_SCALE = 1.0f
    }
    
    /** Επιστρέφει αν έχει ολοκληρωθεί το setup */
    val isSetupComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETE] ?: false
    }
    
    /** Επιστρέφει τον κωδικό γλώσσας */
    val language: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "el" // Default: Ελληνικά
    }
    
    /** Επιστρέφει το theme mode (0=System, 1=Light, 2=Dark) */
    val themeMode: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: 0 // Default: System
    }
    
    /** Επιστρέφει αν οι ειδοποιήσεις είναι ενεργές */
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS_ENABLED] ?: true // Default: Ενεργές
    }
    
    /** Επιστρέφει την ώρα notification (hour) */
    val notificationHour: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATION_HOUR] ?: DEFAULT_NOTIFICATION_HOUR
    }
    
    /** Επιστρέφει τα λεπτά notification */
    val notificationMinute: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATION_MINUTE] ?: DEFAULT_NOTIFICATION_MINUTE
    }
    
    /** Επιστρέφει αν οι ήχοι ειδοποιήσεων είναι ενεργοί */
    val notificationSoundsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        // Αν δεν υπάρχει νέο key, ελέγχει το παλιό για backward compatibility
        prefs[KEY_NOTIFICATION_SOUNDS_ENABLED] ?: prefs[KEY_SOUNDS_ENABLED_LEGACY] ?: true
    }
    
    /** Επιστρέφει αν οι ήχοι click είναι ενεργοί */
    val clickSoundsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_CLICK_SOUNDS_ENABLED] ?: true // Default: Ενεργοί
    }
    
    /** Επιστρέφει τη χώρα του χρήστη (GR, OTHER) */
    val userCountry: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_USER_COUNTRY] ?: DEFAULT_COUNTRY
    }
    
    /** Επιστρέφει το font scale (1.0f, 1.15f, 1.3f) */
    val fontScale: Flow<Float> = dataStore.data.map { prefs ->
        prefs[KEY_FONT_SCALE] ?: DEFAULT_FONT_SCALE
    }
    
    /** Θέτει το setup ως ολοκληρωμένο */
    suspend fun setSetupComplete(complete: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_SETUP_COMPLETE] = complete
        }
    }
    
    /** Θέτει τη γλώσσα */
    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = languageCode
        }
    }
    
    /** Θέτει το theme mode */
    suspend fun setThemeMode(mode: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }
    
    /** Ενεργοποιεί/απενεργοποιεί τις ειδοποιήσεις */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /** Θέτει την ώρα notification */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_HOUR] = hour
            prefs[KEY_NOTIFICATION_MINUTE] = minute
        }
    }
    
    /** Καθαρίζει όλα τα preferences (full reset) */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    
    /** Ενεργοποιεί/απενεργοποιεί τους ήχους ειδοποιήσεων */
    suspend fun setNotificationSoundsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_SOUNDS_ENABLED] = enabled
        }
    }
    
    /** Ενεργοποιεί/απενεργοποιεί τους ήχους click */
    suspend fun setClickSoundsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_CLICK_SOUNDS_ENABLED] = enabled
        }
    }
    
    /** Θέτει τη χώρα του χρήστη */
    suspend fun setUserCountry(country: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_COUNTRY] = country
        }
    }
    
    /** Θέτει το font scale για accessibility */
    suspend fun setFontScale(scale: Float) {
        dataStore.edit { prefs ->
            prefs[KEY_FONT_SCALE] = scale
        }
    }
}
