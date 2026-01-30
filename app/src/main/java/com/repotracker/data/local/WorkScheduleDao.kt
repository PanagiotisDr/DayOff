package com.repotracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object για το WorkSchedule.
 * Παρέχει μεθόδους CRUD για τη βάση δεδομένων.
 */
@Dao
interface WorkScheduleDao {
    
    /**
     * Επιστρέφει το αποθηκευμένο schedule ως Flow.
     * Flow για reactive updates όταν αλλάζει η βάση.
     */
    @Query("SELECT * FROM work_schedule LIMIT 1")
    fun getSchedule(): Flow<WorkScheduleEntity?>
    
    /**
     * Αποθηκεύει ή ενημερώνει το schedule.
     * REPLACE: Αντικαθιστά αν υπάρχει ήδη εγγραφή.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: WorkScheduleEntity)
    
    /**
     * Διαγράφει όλα τα schedules (reset).
     */
    @Query("DELETE FROM work_schedule")
    suspend fun deleteAll()
}
