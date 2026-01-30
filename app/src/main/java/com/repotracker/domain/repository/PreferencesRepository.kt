package com.repotracker.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface για user preferences (DataStore).
 * Περιλαμβάνει: γλώσσα, θέμα, notifications, ήχους, setup status.
 */
interface PreferencesRepository {
    
    /** Επιστρέφει αν έχει ολοκληρωθεί το setup */
    fun isSetupComplete(): Flow<Boolean>
    
    /** Θέτει το setup ως ολοκληρωμένο */
    suspend fun setSetupComplete(complete: Boolean)
    
    /** Επιστρέφει τον κωδικό γλώσσας (el, en) */
    fun getLanguage(): Flow<String>
    
    /** Θέτει τη γλώσσα */
    suspend fun setLanguage(languageCode: String)
    
    /** Επιστρέφει το theme mode (0=System, 1=Light, 2=Dark) */
    fun getThemeMode(): Flow<Int>
    
    /** Θέτει το theme mode */
    suspend fun setThemeMode(mode: Int)
    
    /** Επιστρέφει αν οι ειδοποιήσεις είναι ενεργές */
    fun areNotificationsEnabled(): Flow<Boolean>
    
    /** Ενεργοποιεί/απενεργοποιεί τις ειδοποιήσεις */
    suspend fun setNotificationsEnabled(enabled: Boolean)
    
    /** Επιστρέφει την ώρα notification (0-23) */
    fun getNotificationHour(): Flow<Int>
    
    /** Επιστρέφει τα λεπτά notification (0-59) */
    fun getNotificationMinute(): Flow<Int>
    
    /** Θέτει την ώρα notification */
    suspend fun setNotificationTime(hour: Int, minute: Int)
    
    /** Επιστρέφει αν οι ήχοι ειδοποιήσεων είναι ενεργοί */
    fun areNotificationSoundsEnabled(): Flow<Boolean>
    
    /** Ενεργοποιεί/απενεργοποιεί τους ήχους ειδοποιήσεων */
    suspend fun setNotificationSoundsEnabled(enabled: Boolean)
    
    /** Επιστρέφει αν οι ήχοι click είναι ενεργοί */
    fun areClickSoundsEnabled(): Flow<Boolean>
    
    /** Ενεργοποιεί/απενεργοποιεί τους ήχους click */
    suspend fun setClickSoundsEnabled(enabled: Boolean)
    
    /** Επιστρέφει τη χώρα του χρήστη (GR, OTHER) */
    fun getUserCountry(): Flow<String>
    
    /** Θέτει τη χώρα του χρήστη */
    suspend fun setUserCountry(country: String)
    
    /** Επιστρέφει το font scale για accessibility */
    fun getFontScale(): Flow<Float>
    
    /** Θέτει το font scale */
    suspend fun setFontScale(scale: Float)
    
    // Backward compatibility - διατήρηση για υπάρχοντα κώδικα
    @Deprecated("Use areNotificationSoundsEnabled() instead", ReplaceWith("areNotificationSoundsEnabled()"))
    fun areSoundsEnabled(): Flow<Boolean> = areNotificationSoundsEnabled()
    
    @Deprecated("Use setNotificationSoundsEnabled() instead", ReplaceWith("setNotificationSoundsEnabled(enabled)"))
    suspend fun setSoundsEnabled(enabled: Boolean) = setNotificationSoundsEnabled(enabled)
}
