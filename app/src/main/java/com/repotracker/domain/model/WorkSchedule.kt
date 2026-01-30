package com.repotracker.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Μοντέλο εργασιακού προγράμματος.
 * Αποθηκεύει τις ρυθμίσεις του χρήστη για τον υπολογισμό ρεπό.
 *
 * @param id Μοναδικό αναγνωριστικό
 * @param workDays Λίστα εργάσιμων ημερών (1=Δευτέρα ... 7=Κυριακή)
 * @param currentRepoDay Ημέρα ρεπό της εβδομάδας αναφοράς (1-7)
 * @param referenceDate Ημερομηνία αναφοράς για τους υπολογισμούς
 * @param isRolling Αν το ρεπό είναι κυλιόμενο ή σταθερό
 * @param shiftType Τύπος βάρδιας (Πρωινή/Απογευματινή/Καμία)
 */
data class WorkSchedule(
    val id: Long = 0,
    val workDays: List<Int>,
    val currentRepoDay: Int,
    val referenceDate: LocalDate,
    val isRolling: Boolean = true,
    val shiftType: ShiftType = ShiftType.NONE
) {
    /**
     * Υπολογίζει την κατάσταση μιας συγκεκριμένης ημερομηνίας.
     * 
     * @param targetDate Η ημερομηνία προς έλεγχο
     * @return Η κατάσταση της ημέρας (WORK, REPO, ή FIXED_OFF)
     */
    fun getDayStatus(targetDate: LocalDate): DayStatus {
        val dayOfWeek = targetDate.dayOfWeek.value // 1=Monday ... 7=Sunday
        
        // Έλεγχος αν η ημέρα δεν είναι εργάσιμη (σταθερό ρεπό)
        if (dayOfWeek !in workDays) {
            return DayStatus.FIXED_OFF
        }
        
        // Υπολογισμός κυλιόμενου ρεπό
        val repoDay = if (isRolling) {
            calculateRollingRepo(targetDate)
        } else {
            currentRepoDay
        }
        
        return if (dayOfWeek == repoDay) DayStatus.REPO else DayStatus.WORK
    }
    
    /**
     * Υπολογίζει την ημέρα ρεπό για μια συγκεκριμένη εβδομάδα.
     * Αλγόριθμος: Το ρεπό μετακινείται κατά μία θέση κάθε εβδομάδα στη λίστα workDays.
     */
    private fun calculateRollingRepo(targetDate: LocalDate): Int {
        // Κανονικοποίηση στην αρχή της εβδομάδας (Δευτέρα)
        val refStart = getWeekStart(referenceDate)
        val targetStart = getWeekStart(targetDate)
        
        // Υπολογισμός εβδομάδων διαφοράς
        val weeksDiff = ChronoUnit.WEEKS.between(refStart, targetStart).toInt()
        
        // Ταξινομημένη λίστα εργάσιμων ημερών
        val sortedWorkDays = workDays.sorted()
        
        // Εύρεση της αρχικής θέσης του ρεπό
        var currentIndex = sortedWorkDays.indexOf(currentRepoDay)
        if (currentIndex == -1) currentIndex = 0
        
        // Εφαρμογή rotation με robust modulo για αρνητικές τιμές
        val totalDays = sortedWorkDays.size
        val newIndex = ((currentIndex + weeksDiff) % totalDays + totalDays) % totalDays
        
        return sortedWorkDays[newIndex]
    }
    
    /**
     * Υπολογίζει τη βάρδια για μια συγκεκριμένη ημερομηνία.
     * Εναλλάσσεται κάθε εβδομάδα (even=αρχική, odd=αντίθετη).
     * 
     * @return null αν shiftType == NONE, αλλιώς MORNING ή EVENING
     */
    fun getShiftForDate(targetDate: LocalDate): ShiftType? {
        if (shiftType == ShiftType.NONE) return null
        
        val refStart = getWeekStart(referenceDate)
        val targetStart = getWeekStart(targetDate)
        val weeksDiff = ChronoUnit.WEEKS.between(refStart, targetStart).toInt()
        
        val isEvenWeek = weeksDiff % 2 == 0
        
        return when (shiftType) {
            ShiftType.MORNING -> if (isEvenWeek) ShiftType.MORNING else ShiftType.EVENING
            ShiftType.EVENING -> if (isEvenWeek) ShiftType.EVENING else ShiftType.MORNING
            else -> null
        }
    }
    
    /**
     * Βρίσκει την επόμενη ημερομηνία ρεπό από μια δεδομένη ημέρα.
     */
    fun getNextRepoDate(fromDate: LocalDate = LocalDate.now()): LocalDate {
        var checkDate = fromDate
        // Μέγιστο 30 ημέρες αναζήτηση
        repeat(30) {
            if (getDayStatus(checkDate) == DayStatus.REPO) {
                return checkDate
            }
            checkDate = checkDate.plusDays(1)
        }
        return fromDate // Fallback
    }
    
    /**
     * Βοηθητική: Επιστρέφει τη Δευτέρα της εβδομάδας.
     */
    private fun getWeekStart(date: LocalDate): LocalDate {
        return date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    }
}
