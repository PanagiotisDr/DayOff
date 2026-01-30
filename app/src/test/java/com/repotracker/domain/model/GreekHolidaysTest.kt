package com.repotracker.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month

/**
 * Unit Tests για τις ελληνικές αργίες.
 * Ελέγχει σταθερές και κινητές αργίες.
 */
class GreekHolidaysTest {
    
    /**
     * Test: Σταθερές αργίες 2026
     */
    @Test
    fun `fixed holidays should be correctly identified`() {
        // Πρωτοχρονιά
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.JANUARY, 1)))
        assertEquals(HolidayType.NEW_YEAR, GreekHolidays.getHolidayType(LocalDate.of(2026, 1, 1)))
        
        // Θεοφάνεια
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.JANUARY, 6)))
        assertEquals(HolidayType.EPIPHANY, GreekHolidays.getHolidayType(LocalDate.of(2026, 1, 6)))
        
        // 25η Μαρτίου
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.MARCH, 25)))
        assertEquals(HolidayType.INDEPENDENCE_DAY, GreekHolidays.getHolidayType(LocalDate.of(2026, 3, 25)))
        
        // Πρωτομαγιά
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.MAY, 1)))
        assertEquals(HolidayType.MAY_DAY, GreekHolidays.getHolidayType(LocalDate.of(2026, 5, 1)))
        
        // Δεκαπενταύγουστος
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.AUGUST, 15)))
        assertEquals(HolidayType.ASSUMPTION, GreekHolidays.getHolidayType(LocalDate.of(2026, 8, 15)))
        
        // 28η Οκτωβρίου
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.OCTOBER, 28)))
        assertEquals(HolidayType.OXI_DAY, GreekHolidays.getHolidayType(LocalDate.of(2026, 10, 28)))
        
        // Χριστούγεννα
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.DECEMBER, 25)))
        assertEquals(HolidayType.CHRISTMAS, GreekHolidays.getHolidayType(LocalDate.of(2026, 12, 25)))
        
        // 2η μέρα Χριστουγέννων
        assertTrue(GreekHolidays.isHoliday(LocalDate.of(2026, Month.DECEMBER, 26)))
        assertEquals(HolidayType.CHRISTMAS_SECOND, GreekHolidays.getHolidayType(LocalDate.of(2026, 12, 26)))
    }
    
    /**
     * Test: Κινητές αργίες 2026 βάσει Ορθόδοξου Πάσχα.
     * Πάσχα 2026: 12 Απριλίου
     */
    @Test
    fun `moveable holidays 2026 should be correctly calculated`() {
        // Πάσχα 2026 = 12 Απριλίου
        val easter2026 = LocalDate.of(2026, Month.APRIL, 12)
        
        // Καθαρά Δευτέρα (48 ημέρες πριν το Πάσχα)
        val cleanMonday = easter2026.minusDays(48)
        assertTrue(GreekHolidays.isHoliday(cleanMonday))
        assertEquals(HolidayType.CLEAN_MONDAY, GreekHolidays.getHolidayType(cleanMonday))
        
        // Μεγάλη Παρασκευή (2 ημέρες πριν)
        val goodFriday = easter2026.minusDays(2)
        assertTrue(GreekHolidays.isHoliday(goodFriday))
        assertEquals(HolidayType.GOOD_FRIDAY, GreekHolidays.getHolidayType(goodFriday))
        
        // Κυριακή Πάσχα
        assertTrue(GreekHolidays.isHoliday(easter2026))
        assertEquals(HolidayType.EASTER_SUNDAY, GreekHolidays.getHolidayType(easter2026))
        
        // Δευτέρα Πάσχα
        val easterMonday = easter2026.plusDays(1)
        assertTrue(GreekHolidays.isHoliday(easterMonday))
        assertEquals(HolidayType.EASTER_MONDAY, GreekHolidays.getHolidayType(easterMonday))
        
        // Αγίου Πνεύματος (50 ημέρες μετά)
        val holySpirit = easter2026.plusDays(50)
        assertTrue(GreekHolidays.isHoliday(holySpirit))
        assertEquals(HolidayType.HOLY_SPIRIT, GreekHolidays.getHolidayType(holySpirit))
    }
    
    /**
     * Test: Μη αργίες πρέπει να επιστρέφουν false
     */
    @Test
    fun `non-holidays should return false`() {
        // Τυχαία ημερομηνία
        assertFalse(GreekHolidays.isHoliday(LocalDate.of(2026, Month.FEBRUARY, 15)))
        assertNull(GreekHolidays.getHolidayType(LocalDate.of(2026, 2, 15)))
        
        // 24 Δεκεμβρίου (Παραμονή, όχι αργία)
        assertFalse(GreekHolidays.isHoliday(LocalDate.of(2026, Month.DECEMBER, 24)))
    }
    
    /**
     * Test: Συνολικός αριθμός αργιών ανά έτος (13 αργίες)
     */
    @Test
    fun `year should have 13 holidays`() {
        val holidays2026 = GreekHolidays.getHolidaysForYear(2026)
        assertEquals(13, holidays2026.size)
    }
}
