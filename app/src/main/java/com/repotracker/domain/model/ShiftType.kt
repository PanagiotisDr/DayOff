package com.repotracker.domain.model

/**
 * Τύπος βάρδιας.
 * Χρησιμοποιείται για εναλλαγή πρωινής/απογευματινής βάρδιας.
 */
enum class ShiftType {
    /** Χωρίς βάρδια - δεν γίνεται tracking */
    NONE,
    
    /** Πρωινή βάρδια */
    MORNING,
    
    /** Απογευματινή βάρδια */
    EVENING
}
