package com.repotracker.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Unit Tests για τον Rotation Algorithm του WorkSchedule.
 */
class WorkScheduleTest {
    
    /**
     * Test: Σταθερό ρεπό (isRolling = false)
     */
    @Test
    fun `fixed repo should always return same day`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6), // Δευτέρα-Σάββατο
            currentRepoDay = 3, // Τετάρτη
            referenceDate = LocalDate.of(2026, 1, 5), // Δευτέρα
            isRolling = false
        )
        
        // Τρέχουσα εβδομάδα - Τετάρτη πρέπει να είναι REPO
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 7)))
        
        // Επόμενη εβδομάδα - Τετάρτη πρέπει να είναι πάλι REPO
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 14)))
        
        // Δευτέρα πρέπει να είναι WORK
        assertEquals(DayStatus.WORK, schedule.getDayStatus(LocalDate.of(2026, 1, 5)))
    }
    
    /**
     * Test: Κυλιόμενο ρεπό (isRolling = true)
     */
    @Test
    fun `rolling repo should shift forward each week`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6), // Δευτέρα-Σάββατο
            currentRepoDay = 1, // Δευτέρα
            referenceDate = LocalDate.of(2026, 1, 5), // Δευτέρα
            isRolling = true
        )
        
        // Εβδομάδα 0: Δευτέρα (index 0)
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 5)))
        
        // Εβδομάδα 1: Τρίτη (index 1)
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 13)))
        assertEquals(DayStatus.WORK, schedule.getDayStatus(LocalDate.of(2026, 1, 12))) // Δευτέρα
        
        // Εβδομάδα 2: Τετάρτη (index 2)
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 21)))
    }
    
    /**
     * Test: Σταθερές μη-εργάσιμες ημέρες (π.χ. Κυριακή)
     */
    @Test
    fun `non-working days should return FIXED_OFF`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6), // Δευτέρα-Σάββατο (Κυριακή εκτός)
            currentRepoDay = 1,
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = true
        )
        
        // Κυριακή = FIXED_OFF
        assertEquals(DayStatus.FIXED_OFF, schedule.getDayStatus(LocalDate.of(2026, 1, 11)))
    }
    
    /**
     * Test: Negative weeks (παρελθόν)
     */
    @Test
    fun `rolling repo should work for past dates`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 3, // Τετάρτη στη reference week
            referenceDate = LocalDate.of(2026, 1, 12), // Αναφορά: εβδομάδα 12-18 Ιαν
            isRolling = true
        )
        
        // Μία εβδομάδα πριν (weeksDiff = -1): index πρέπει να πάει πίσω
        // Index Τετάρτης = 2, μείον 1 = 1 (Τρίτη)
        assertEquals(DayStatus.REPO, schedule.getDayStatus(LocalDate.of(2026, 1, 6))) // Τρίτη 6 Ιαν
    }
    
    /**
     * Test: Εναλλαγή βάρδιας
     */
    @Test
    fun `shift alternation should toggle weekly`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 1,
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = true,
            shiftType = ShiftType.MORNING
        )
        
        // Εβδομάδα 0 (even): MORNING
        assertEquals(ShiftType.MORNING, schedule.getShiftForDate(LocalDate.of(2026, 1, 5)))
        
        // Εβδομάδα 1 (odd): EVENING
        assertEquals(ShiftType.EVENING, schedule.getShiftForDate(LocalDate.of(2026, 1, 12)))
        
        // Εβδομάδα 2 (even): MORNING πάλι
        assertEquals(ShiftType.MORNING, schedule.getShiftForDate(LocalDate.of(2026, 1, 19)))
    }
    
    /**
     * Test: Εύρεση επόμενου ρεπό
     */
    @Test
    fun `getNextRepoDate should find next repo`() {
        val schedule = WorkSchedule(
            workDays = listOf(1, 2, 3, 4, 5, 6),
            currentRepoDay = 5, // Παρασκευή
            referenceDate = LocalDate.of(2026, 1, 5),
            isRolling = false
        )
        
        // Από Δευτέρα, επόμενο ρεπό = Παρασκευή ίδιας εβδομάδας
        val nextRepo = schedule.getNextRepoDate(LocalDate.of(2026, 1, 5))
        assertEquals(LocalDate.of(2026, 1, 9), nextRepo)
    }
}
