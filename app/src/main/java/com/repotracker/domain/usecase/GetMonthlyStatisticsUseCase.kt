package com.repotracker.domain.usecase

import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.model.MonthlyStatistics
import com.repotracker.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * Use Case: Υπολογισμός μηνιαίων στατιστικών.
 * Αναλύει τις εργάσιμες ημέρες και τα ρεπό για έναν μήνα.
 */
class GetMonthlyStatisticsUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    /**
     * Υπολογίζει τα στατιστικά για έναν συγκεκριμένο μήνα.
     * 
     * @param yearMonth Μήνας/Έτος για ανάλυση
     * @return Flow με MonthlyStatistics (ή null αν δεν υπάρχει schedule)
     */
    operator fun invoke(yearMonth: YearMonth): Flow<MonthlyStatistics?> {
        return scheduleRepository.getSchedule().map { schedule ->
            if (schedule == null) return@map null
            
            var workDays = 0
            var repoDays = 0
            var fixedOffDays = 0
            val distribution = mutableMapOf<Int, Int>()
            val repoDates = mutableListOf<LocalDate>()
            
            // Ανάλυση κάθε ημέρας του μήνα
            for (day in 1..yearMonth.lengthOfMonth()) {
                val date = LocalDate.of(yearMonth.year, yearMonth.month, day)
                val dayOfWeek = date.dayOfWeek.value
                
                when (schedule.getDayStatus(date)) {
                    DayStatus.WORK -> workDays++
                    DayStatus.REPO -> {
                        repoDays++
                        repoDates.add(date)
                        // Καταγραφή για weekly distribution
                        distribution[dayOfWeek] = (distribution[dayOfWeek] ?: 0) + 1
                    }
                    DayStatus.FIXED_OFF -> fixedOffDays++
                }
            }
            
            MonthlyStatistics(
                month = yearMonth.monthValue,
                year = yearMonth.year,
                workDays = workDays,
                repoDays = repoDays,
                fixedOffDays = fixedOffDays,
                weeklyDistribution = distribution,
                repoDates = repoDates
            )
        }
    }
}
