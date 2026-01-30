package com.repotracker.data.repository

import com.repotracker.data.local.UserPreferencesManager
import com.repotracker.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Υλοποίηση του PreferencesRepository.
 * Χρησιμοποιεί DataStore για persistence.
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : PreferencesRepository {
    
    override fun isSetupComplete(): Flow<Boolean> = preferencesManager.isSetupComplete
    
    override suspend fun setSetupComplete(complete: Boolean) {
        preferencesManager.setSetupComplete(complete)
    }
    
    override fun getLanguage(): Flow<String> = preferencesManager.language
    
    override suspend fun setLanguage(languageCode: String) {
        preferencesManager.setLanguage(languageCode)
    }
    
    override fun getThemeMode(): Flow<Int> = preferencesManager.themeMode
    
    override suspend fun setThemeMode(mode: Int) {
        preferencesManager.setThemeMode(mode)
    }
    
    override fun areNotificationsEnabled(): Flow<Boolean> = preferencesManager.notificationsEnabled
    
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferencesManager.setNotificationsEnabled(enabled)
    }
    
    override fun getNotificationHour(): Flow<Int> = preferencesManager.notificationHour
    
    override fun getNotificationMinute(): Flow<Int> = preferencesManager.notificationMinute
    
    override suspend fun setNotificationTime(hour: Int, minute: Int) {
        preferencesManager.setNotificationTime(hour, minute)
    }
    
    override fun areNotificationSoundsEnabled(): Flow<Boolean> = preferencesManager.notificationSoundsEnabled
    
    override suspend fun setNotificationSoundsEnabled(enabled: Boolean) {
        preferencesManager.setNotificationSoundsEnabled(enabled)
    }
    
    override fun areClickSoundsEnabled(): Flow<Boolean> = preferencesManager.clickSoundsEnabled
    
    override suspend fun setClickSoundsEnabled(enabled: Boolean) {
        preferencesManager.setClickSoundsEnabled(enabled)
    }
    
    override fun getUserCountry(): Flow<String> = preferencesManager.userCountry
    
    override suspend fun setUserCountry(country: String) {
        preferencesManager.setUserCountry(country)
    }
    
    override fun getFontScale(): Flow<Float> = preferencesManager.fontScale
    
    override suspend fun setFontScale(scale: Float) {
        preferencesManager.setFontScale(scale)
    }
}
