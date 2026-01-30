package com.repotracker.domain.usecase

import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.model.ShiftType
import com.repotracker.domain.model.WorkSchedule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Unit Tests για τον υπολογισμό επόμενου ρεπό.
 * Ελέγχει την εύρεση επόμενης ημερομηνίας ρεπό.
 */
class GetNextRepoDayUseCaseTest {
    
    /**
     * Test: Σταθερό ρεπό - βρίσκει το επόμενο στην ίδια εβδομάδα
     */
    @Test
    fun `fixed repo finds next repo in same week`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6), // Δευτέρα-Σάββατο
            currentRepoDay = 5, // Παρασκευή
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = false
        )
        
        // Από Δευτέρα, επόμενο ρεπό = Παρασκευή
        val fromMonday = LocalDate.of(2026, 1, 5)
        val nextRepo = schedule.getNextRepoDate(fromMonday)
        assertEquals(LocalDate.of(2026, 1, 9), nextRepo) // Παρασκευή
    }
    
    /**
     * Test: Σταθερό ρεπό - αν είμαστε μετά το ρεπό, πηγαίνει στην επόμενη εβδομάδα
     */
    @Test
    fun `fixed repo finds next repo in next week when past current week repo`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 2, // Τρίτη
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = false
        )
        
        // Από Πέμπτη, επόμενο ρεπό = Τρίτη επόμενης εβδομάδας
        val fromThursday = LocalDate.of(2026, 1, 8)
        val nextRepo = schedule.getNextRepoDate(fromThursday)
        assertEquals(LocalDate.of(2026, 1, 13), nextRepo) // Τρίτη
    }
    
    /**
     * Test: Κυλιόμενο ρεπό - βρίσκει το σωστό ρεπό
     */
    @Test
    fun `rolling repo finds correct next repo`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 1, // Δευτέρα στη reference week
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = true
        )
        
        // Εβδομάδα 0: Δευτέρα ρεπό
        // Εβδομάδα 1: Τρίτη ρεπό
        val fromWeek1 = LocalDate.of(2026, 1, 12)
        val nextRepo = schedule.getNextRepoDate(fromWeek1)
        assertEquals(LocalDate.of(2026, 1, 13), nextRepo) // Τρίτη
    }
    
    /**
     * Test: Αν η τρέχουσα ημέρα είναι ρεπό, επιστρέφει την ίδια
     */
    @Test
    fun `returns today if today is repo day`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 3, // Τετάρτη
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = false
        )
        
        val wednesday = LocalDate.of(2026, 1, 7) // Τετάρτη
        val nextRepo = schedule.getNextRepoDate(wednesday)
        assertEquals(wednesday, nextRepo)
    }
    
    /**
     * Test: Υπολογισμός ημερών μέχρι το ρεπό
     */
    @Test
    fun `calculates days until repo correctly`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 5, // Παρασκευή
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = false
        )
        
        val monday = LocalDate.of(2026, 1, 5)
        val nextRepo = schedule.getNextRepoDate(monday)
        
        // Δευτέρα (5) -> Παρασκευή (9) = 4 ημέρες
        val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(monday, nextRepo)
        assertEquals(4, daysUntil)
    }
}
