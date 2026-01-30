package com.repotracker.data.notification

import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.repotracker.data.local.UserPreferencesManager
import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.model.WorkSchedule
import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service για χρονοδρομολόγηση notifications μέσω WorkManager.
 * Προγραμματίζει ειδοποιήσεις για τα επόμενα 4 ρεπό στην ώρα που έχει επιλέξει ο χρήστης.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val preferencesManager: UserPreferencesManager,
    private val scheduleRepository: ScheduleRepository
) {
    companion object {
        private const val TAG = "NotificationScheduler"
        
        // Πόσα ρεπό να προγραμματίσουμε εκ των προτέρων
        private const val REPO_DAYS_TO_SCHEDULE = 4
    }

    /**
     * Προγραμματίζει notifications για τα επόμενα ρεπό.
     * Καλείται όταν:
     * - Ο χρήστης ενεργοποιεί τις ειδοποιήσεις
     * - Αποθηκεύεται νέο πρόγραμμα
     * - Αλλάζει η ώρα notification
     */
    suspend fun scheduleNextRepoNotifications() {
        // Έλεγχος αν οι ειδοποιήσεις είναι ενεργοποιημένες
        val notificationsEnabled = preferencesManager.notificationsEnabled.first()
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications disabled, cancelling all")
            cancelAll()
            return
        }
        
        // Λήψη του τρέχοντος schedule
        val schedule = scheduleRepository.getSchedule().first()
        if (schedule == null) {
            Log.d(TAG, "No schedule found, skipping")
            return
        }
        
        // Λήψη ώρας notification από preferences
        val notificationHour = preferencesManager.notificationHour.first()
        val notificationMinute = preferencesManager.notificationMinute.first()
        val notificationTime = LocalTime.of(notificationHour, notificationMinute)
        
        Log.d(TAG, "Scheduling notifications for time: $notificationTime")
        
        // Ακύρωση τυχόν υπαρχόντων για να ξεκινήσουμε καθαρά
        cancelAll()
        
        // Εύρεση επόμενων ρεπό και προγραμματισμός
        scheduleForSchedule(schedule, notificationTime)
    }

    /**
     * Προγραμματίζει notifications για ένα συγκεκριμένο schedule.
     */
    private fun scheduleForSchedule(schedule: WorkSchedule, notificationTime: LocalTime) {
        val today = LocalDate.now()
        var reposScheduled = 0
        var checkDate = today
        
        // Αναζήτηση μέχρι και 60 ημέρες μπροστά
        val maxDaysToSearch = 60
        
        repeat(maxDaysToSearch) {
            if (reposScheduled >= REPO_DAYS_TO_SCHEDULE) return
            
            val status = schedule.getDayStatus(checkDate)
            if (status == DayStatus.REPO) {
                scheduleNotificationForDate(checkDate, reposScheduled, notificationTime)
                reposScheduled++
            }
            
            checkDate = checkDate.plusDays(1)
        }
        
        Log.d(TAG, "Scheduled $reposScheduled notifications")
    }

    /**
     * Προγραμματίζει notification για μια συγκεκριμένη ημέρα ρεπό.
     * Το notification θα εμφανιστεί στην επιλεγμένη ώρα της ΠΡΟΗΓΟΥΜΕΝΗΣ ημέρας.
     */
    private fun scheduleNotificationForDate(
        repoDate: LocalDate, 
        index: Int,
        notificationTime: LocalTime
    ) {
        // Υπολογισμός χρόνου notification (επιλεγμένη ώρα προηγούμενης ημέρας)
        val notificationDateTime = LocalDateTime.of(
            repoDate.minusDays(1),
            notificationTime
        )
        
        val now = LocalDateTime.now()
        
        // Αν η ώρα έχει περάσει, δεν προγραμματίζουμε
        if (notificationDateTime.isBefore(now)) {
            Log.d(TAG, "Skipping past notification for repo date: $repoDate")
            return
        }
        
        // Υπολογισμός delay
        val delay = Duration.between(now, notificationDateTime)
        
        Log.d(TAG, "Scheduling notification for $repoDate at $notificationDateTime (delay: ${delay.toHours()}h ${delay.toMinutes() % 60}m)")
        
        // Δημιουργία WorkRequest
        val workRequest = OneTimeWorkRequestBuilder<RepoNotificationWorker>()
            .setInitialDelay(delay)
            .addTag("repo_notification")
            .build()
        
        // Unique name για κάθε notification (για να μπορούμε να τα ακυρώσουμε)
        val workName = "${RepoNotificationWorker.WORK_NAME_PREFIX}$index"
        
        // Προγραμματισμός με REPLACE policy για να αντικαταστήσει τυχόν υπάρχον
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Ακυρώνει όλα τα προγραμματισμένα notifications.
     */
    fun cancelAll() {
        Log.d(TAG, "Cancelling all scheduled notifications")
        repeat(REPO_DAYS_TO_SCHEDULE) { index ->
            val workName = "${RepoNotificationWorker.WORK_NAME_PREFIX}$index"
            workManager.cancelUniqueWork(workName)
        }
    }
    
    /**
     * Test notification - εκτελείται ΑΜΕΣΑ για debugging.
     */
    fun scheduleTestNotification() {
        Log.d(TAG, "Scheduling IMMEDIATE test notification")
        
        // Immediate execution - χωρίς delay για debugging
        val workRequest = OneTimeWorkRequestBuilder<RepoNotificationWorker>()
            .addTag("repo_notification_test")
            .build()
        
        workManager.enqueueUniqueWork(
            "repo_notification_test",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Test notification enqueued IMMEDIATELY, work ID: ${workRequest.id}")
    }
}

