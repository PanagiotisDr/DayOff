package com.repotracker.domain.repository

import com.repotracker.domain.model.WorkSchedule
import kotlinx.coroutines.flow.Flow

/**
 * Interface για πρόσβαση στο WorkSchedule.
 * Υλοποιείται στο Data Layer.
 */
interface ScheduleRepository {
    
    /**
     * Επιστρέφει το αποθηκευμένο πρόγραμμα εργασίας.
     * Επιστρέφει null αν δεν έχει γίνει setup.
     */
    fun getSchedule(): Flow<WorkSchedule?>
    
    /**
     * Αποθηκεύει το πρόγραμμα εργασίας.
     */
    suspend fun saveSchedule(schedule: WorkSchedule)
    
    /**
     * Διαγράφει το πρόγραμμα (reset).
     */
    suspend fun deleteSchedule()
}
