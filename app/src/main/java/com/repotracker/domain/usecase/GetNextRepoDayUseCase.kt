package com.repotracker.domain.usecase

import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case: Εύρεση επόμενης ημέρας ρεπό.
 * Χρησιμοποιείται στο Home screen για το "Next Day Off" card.
 */
class GetNextRepoDayUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    /**
     * Βρίσκει την επόμενη ημέρα ρεπό από σήμερα.
     * 
     * @return Flow με LocalDate (ή null αν δεν υπάρχει schedule)
     */
    operator fun invoke(): Flow<LocalDate?> {
        return scheduleRepository.getSchedule().map { schedule ->
            schedule?.getNextRepoDate()
        }
    }
}
