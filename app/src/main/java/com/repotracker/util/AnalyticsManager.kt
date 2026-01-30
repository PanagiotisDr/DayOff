package com.repotracker.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper για Firebase Analytics.
 * Παρέχει τυποποιημένα events για tracking χρήσης της εφαρμογής.
 */
@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context)
    }
    
    /**
     * Log event: Ο χρήστης ολοκλήρωσε το setup wizard.
     */
    fun logSetupComplete(workDaysCount: Int, isRolling: Boolean) {
        val params = Bundle().apply {
            putInt("work_days_count", workDaysCount)
            putBoolean("is_rolling", isRolling)
        }
        firebaseAnalytics.logEvent("setup_complete", params)
    }
    
    /**
     * Log event: Ο χρήστης άνοιξε συγκεκριμένη οθόνη.
     */
    fun logScreenView(screenName: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
    
    /**
     * Log event: Ο χρήστης ενεργοποίησε/απενεργοποίησε notifications.
     */
    fun logNotificationToggle(enabled: Boolean) {
        val params = Bundle().apply {
            putBoolean("notifications_enabled", enabled)
        }
        firebaseAnalytics.logEvent("notification_toggle", params)
    }
    
    /**
     * Log event: Ο χρήστης άλλαξε theme.
     */
    fun logThemeChange(theme: String) {
        val params = Bundle().apply {
            putString("theme", theme)
        }
        firebaseAnalytics.logEvent("theme_change", params)
    }
    
    /**
     * Log event: Ο χρήστης άλλαξε γλώσσα.
     */
    fun logLanguageChange(language: String) {
        val params = Bundle().apply {
            putString("language", language)
        }
        firebaseAnalytics.logEvent("language_change", params)
    }
}
