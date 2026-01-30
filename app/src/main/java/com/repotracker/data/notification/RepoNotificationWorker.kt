package com.repotracker.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.repotracker.R
import com.repotracker.domain.repository.PreferencesRepository
import com.repotracker.presentation.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Worker που εκτελείται μέσω WorkManager για να εμφανίσει notification.
 * Χρησιμοποιεί @HiltWorker για σωστό Dependency Injection.
 */
@HiltWorker
class RepoNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesRepository: PreferencesRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "RepoNotificationWorker"
        const val CHANNEL_ID = "repo_notifications"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME_PREFIX = "repo_notification_"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker εκτελείται - δημιουργία notification")
        
        // Έλεγχος αν οι ήχοι ειδοποιήσεων είναι ενεργοί
        val soundsEnabled = preferencesRepository.areNotificationSoundsEnabled().first()
        Log.d(TAG, "Notification sounds enabled: $soundsEnabled")
        
        // Δημιουργία notification channel (απαιτείται για Android 8+)
        createNotificationChannel(soundsEnabled)
        
        // Έλεγχος permission για notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Δεν υπάρχει POST_NOTIFICATIONS permission")
                return Result.success()
            }
        }
        
        // Δημιουργία και εμφάνιση notification
        showNotification(soundsEnabled)
        Log.d(TAG, "Notification εμφανίστηκε επιτυχώς")
        
        return Result.success()
    }

    /**
     * Δημιουργεί το NotificationChannel για Android 8+.
     * Χρησιμοποιεί custom ή silent sound ανάλογα με τις ρυθμίσεις.
     */
    private fun createNotificationChannel(soundsEnabled: Boolean) {
        val name = context.getString(R.string.notification_channel_name)
        val description = context.getString(R.string.notification_channel_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH
        
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
            // "Γιορτινό" vibration pattern - πιο χαρούμενο!
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 300, 200, 300, 200, 300)
            
            // Custom ήχος μόνο αν είναι ενεργοί οι ήχοι
            if (soundsEnabled) {
                val soundUri = Uri.parse(
                    "android.resource://${context.packageName}/${R.raw.notification_celebration}"
                )
                setSound(soundUri, audioAttributes)
            } else {
                setSound(null, null)
            }
        }
        
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "NotificationChannel δημιουργήθηκε: $CHANNEL_ID (sound: $soundsEnabled)")
    }

    /**
     * Εμφανίζει το notification με localized strings.
     * Όταν ο χρήστης πατήσει, ανοίγει η εφαρμογή στην αρχική οθόνη.
     */
    private fun showNotification(soundsEnabled: Boolean) {
        // Intent για άνοιγμα της MainActivity όταν πατηθεί το notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 300))
        
        // Custom ήχος μόνο αν είναι ενεργοί οι ήχοι
        if (soundsEnabled) {
            val soundUri = Uri.parse(
                "android.resource://${context.packageName}/${R.raw.notification_celebration}"
            )
            builder.setSound(soundUri)
        } else {
            builder.setSilent(true)
        }

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}
