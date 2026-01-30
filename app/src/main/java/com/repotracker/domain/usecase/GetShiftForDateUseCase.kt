package com.repotracker.domain.usecase

import com.repotracker.domain.model.ShiftType
import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case: Υπολογισμός βάρδιας για συγκεκριμένη ημέρα.
 * Χρησιμοποιείται για την εμφάνιση Morning/Evening badge.
 */
class GetShiftForDateUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    /**
     * Επιστρέφει τη βάρδια για μια ημερομηνία.
     * 
     * @param date Η ημερομηνία
     * @return Flow με ShiftType (ή null αν shift=NONE ή δεν υπάρχει schedule)
     */
    operator fun invoke(date: LocalDate): Flow<ShiftType?> {
        return scheduleRepository.getSchedule().map { schedule ->
            schedule?.getShiftForDate(date)
        }
    }
}
