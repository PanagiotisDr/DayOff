package com.repotracker.domain.model

import java.time.LocalDate
import java.time.Month

/**
 * Enum με τις ελληνικές αργίες.
 * Κάθε αργία έχει ένα ID για αντιστοίχιση με string resources.
 */
enum class HolidayType {
    NEW_YEAR,           // Πρωτοχρονιά
    EPIPHANY,           // Θεοφάνεια
    INDEPENDENCE_DAY,   // 25η Μαρτίου
    MAY_DAY,            // Πρωτομαγιά
    ASSUMPTION,         // Κοίμηση Θεοτόκου
    OXI_DAY,            // 28η Οκτωβρίου
    CHRISTMAS,          // Χριστούγεννα
    CHRISTMAS_SECOND,   // 2η μέρα Χριστουγέννων
    CLEAN_MONDAY,       // Καθαρά Δευτέρα
    GOOD_FRIDAY,        // Μεγάλη Παρασκευή
    EASTER_SUNDAY,      // Κυριακή Πάσχα
    EASTER_MONDAY,      // Δευτέρα Πάσχα
    HOLY_SPIRIT;        // Αγίου Πνεύματος
}

/**
 * Υπολογισμός ελληνικών αργιών.
 * 
 * Περιλαμβάνει:
 * - Σταθερές αργίες (Πρωτοχρονιά, 25η Μαρτίου, κλπ)
 * - Κινητές αργίες βάσει Ορθόδοξου Πάσχα
 */
object GreekHolidays {
    
    /**
     * Επιστρέφει όλες τις αργίες για ένα έτος.
     */
    fun getHolidaysForYear(year: Int): Set<LocalDate> {
        val holidays = mutableSetOf<LocalDate>()
        
        // Σταθερές αργίες
        holidays.add(LocalDate.of(year, Month.JANUARY, 1))
        holidays.add(LocalDate.of(year, Month.JANUARY, 6))
        holidays.add(LocalDate.of(year, Month.MARCH, 25))
        holidays.add(LocalDate.of(year, Month.MAY, 1))
        holidays.add(LocalDate.of(year, Month.AUGUST, 15))
        holidays.add(LocalDate.of(year, Month.OCTOBER, 28))
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25))
        holidays.add(LocalDate.of(year, Month.DECEMBER, 26))
        
        // Κινητές αργίες βάσει Πάσχα
        val easter = calculateOrthodoxEaster(year)
        holidays.add(easter.minusDays(48))
        holidays.add(easter.minusDays(2))
        holidays.add(easter)
        holidays.add(easter.plusDays(1))
        holidays.add(easter.plusDays(50))
        
        return holidays
    }
    
    /**
     * Ελέγχει αν μια ημερομηνία είναι αργία.
     */
    fun isHoliday(date: LocalDate): Boolean {
        return date in getHolidaysForYear(date.year)
    }
    
    /**
     * Επιστρέφει τον τύπο αργίας (για αντιστοίχιση με string resources).
     */
    fun getHolidayType(date: LocalDate): HolidayType? {
        if (!isHoliday(date)) return null
        
        val easter = calculateOrthodoxEaster(date.year)
        
        return when (date) {
            LocalDate.of(date.year, Month.JANUARY, 1) -> HolidayType.NEW_YEAR
            LocalDate.of(date.year, Month.JANUARY, 6) -> HolidayType.EPIPHANY
            LocalDate.of(date.year, Month.MARCH, 25) -> HolidayType.INDEPENDENCE_DAY
            LocalDate.of(date.year, Month.MAY, 1) -> HolidayType.MAY_DAY
            LocalDate.of(date.year, Month.AUGUST, 15) -> HolidayType.ASSUMPTION
            LocalDate.of(date.year, Month.OCTOBER, 28) -> HolidayType.OXI_DAY
            LocalDate.of(date.year, Month.DECEMBER, 25) -> HolidayType.CHRISTMAS
            LocalDate.of(date.year, Month.DECEMBER, 26) -> HolidayType.CHRISTMAS_SECOND
            easter.minusDays(48) -> HolidayType.CLEAN_MONDAY
            easter.minusDays(2) -> HolidayType.GOOD_FRIDAY
            easter -> HolidayType.EASTER_SUNDAY
            easter.plusDays(1) -> HolidayType.EASTER_MONDAY
            easter.plusDays(50) -> HolidayType.HOLY_SPIRIT
            else -> null
        }
    }
    
    /**
     * Υπολογισμός Ορθόδοξου Πάσχα (αλγόριθμος Anonymous Gregorian).
     */
    private fun calculateOrthodoxEaster(year: Int): LocalDate {
        val a = year % 4
        val b = year % 7
        val c = year % 19
        val d = (19 * c + 15) % 30
        val e = (2 * a + 4 * b - d + 34) % 7
        val month = (d + e + 114) / 31
        val day = ((d + e + 114) % 31) + 1
        
        // Προσθήκη 13 ημερών για Γρηγοριανό ημερολόγιο
        return LocalDate.of(year, month, day).plusDays(13)
    }
}
