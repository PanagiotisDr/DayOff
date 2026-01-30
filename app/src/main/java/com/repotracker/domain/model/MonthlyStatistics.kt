package com.repotracker.domain.model

import java.time.LocalDate

/**
 * Μοντέλο μηνιαίων στατιστικών.
 * Χρησιμοποιείται στην οθόνη Statistics.
 *
 * @param month Μήνας (1-12)
 * @param year Έτος
 * @param workDays Αριθμός εργάσιμων ημερών
 * @param repoDays Αριθμός κυλιόμενων ρεπό
 * @param fixedOffDays Αριθμός σταθερών ρεπό
 * @param weeklyDistribution Κατανομή ρεπό ανά ημέρα εβδομάδας (1-7 -> count)
 * @param repoDates Λίστα ημερομηνιών ρεπό
 */
data class MonthlyStatistics(
    val month: Int,
    val year: Int,
    val workDays: Int,
    val repoDays: Int,
    val fixedOffDays: Int,
    val weeklyDistribution: Map<Int, Int>,
    val repoDates: List<LocalDate> = emptyList()
) {
    /** Συνολικές ημέρες ρεπό (κυλιόμενα + σταθερά) */
    val totalOffDays: Int get() = repoDays + fixedOffDays
    
    /** Συνολικές ημέρες του μήνα */
    val totalDays: Int get() = workDays + totalOffDays
}
