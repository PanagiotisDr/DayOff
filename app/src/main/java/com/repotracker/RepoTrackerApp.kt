package com.repotracker

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Κύρια κλάση Application για το RepoTracker.
 * Υλοποιεί Configuration.Provider για custom WorkManager initialization με Hilt.
 */
@HiltAndroidApp
class RepoTrackerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Log.d("RepoTrackerApp", "Application onCreate - Hilt initialized")
    }

    override val workManagerConfiguration: Configuration
        get() {
            Log.d("RepoTrackerApp", "Providing custom WorkManager configuration with HiltWorkerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        }
}
