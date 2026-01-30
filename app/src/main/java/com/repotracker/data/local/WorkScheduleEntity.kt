package com.repotracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.repotracker.domain.model.ShiftType
import com.repotracker.domain.model.WorkSchedule
import java.time.LocalDate

/**
 * Room Entity για αποθήκευση του WorkSchedule στη βάση.
 */
@Entity(tableName = "work_schedule")
@TypeConverters(Converters::class)
data class WorkScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workDays: String,           // JSON string: "[1,2,3,4,5,6]"
    val currentRepoDay: Int,
    val referenceDate: Long,        // Epoch days
    val isRolling: Boolean,
    val shiftType: String           // "NONE", "MORNING", "EVENING"
) {
    /**
     * Μετατρέπει το Entity σε Domain Model.
     */
    fun toDomainModel(): WorkSchedule {
        return WorkSchedule(
            id = id,
            workDays = workDays.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().toInt() },
            currentRepoDay = currentRepoDay,
            referenceDate = LocalDate.ofEpochDay(referenceDate),
            isRolling = isRolling,
            shiftType = ShiftType.valueOf(shiftType)
        )
    }
    
    companion object {
        /**
         * Δημιουργεί Entity από Domain Model.
         */
        fun fromDomainModel(schedule: WorkSchedule): WorkScheduleEntity {
            return WorkScheduleEntity(
                id = schedule.id,
                workDays = schedule.workDays.joinToString(",", "[", "]"),
                currentRepoDay = schedule.currentRepoDay,
                referenceDate = schedule.referenceDate.toEpochDay(),
                isRolling = schedule.isRolling,
                shiftType = schedule.shiftType.name
            )
        }
    }
}

/**
 * Type Converters για Room.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}
