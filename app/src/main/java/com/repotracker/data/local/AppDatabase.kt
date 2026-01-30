package com.repotracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room Database για το RepoTracker.
 * Περιέχει μόνο τον πίνακα work_schedule.
 */
@Database(
    entities = [WorkScheduleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun workScheduleDao(): WorkScheduleDao
    
    companion object {
        const val DATABASE_NAME = "repotracker_db"
    }
}
