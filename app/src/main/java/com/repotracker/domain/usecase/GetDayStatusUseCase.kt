package com.repotracker.domain.usecase

import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case: Υπολογισμός κατάστασης ημέρας (Εργάσιμη/Ρεπό/Σταθερό Ρεπό).
 * Κεντρική λογική για το Home screen.
 */
class GetDayStatusUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    /**
     * Επιστρέφει την κατάσταση μιας ημερομηνίας.
     * 
     * @param date Η ημερομηνία προς έλεγχο
     * @return Flow με την κατάσταση (ή null αν δεν υπάρχει schedule)
     */
    operator fun invoke(date: LocalDate): Flow<DayStatus?> {
        return scheduleRepository.getSchedule().map { schedule ->
            schedule?.getDayStatus(date)
        }
    }
}
