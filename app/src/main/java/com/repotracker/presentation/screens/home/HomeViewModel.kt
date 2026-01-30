package com.repotracker.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repotracker.data.sound.SoundManager
import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.model.GreekHolidays
import com.repotracker.domain.model.HolidayType
import com.repotracker.domain.model.ShiftType
import com.repotracker.domain.model.WorkSchedule
import com.repotracker.domain.repository.PreferencesRepository
import com.repotracker.domain.repository.ScheduleRepository
import com.repotracker.domain.usecase.GetDayStatusUseCase
import com.repotracker.domain.usecase.GetNextRepoDayUseCase
import com.repotracker.domain.usecase.GetShiftForDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel για το Home Screen.
 * Διαχειρίζεται επιλεγμένη ημερομηνία, υπολογισμούς status, shift και αργίες.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val getDayStatusUseCase: GetDayStatusUseCase,
    private val getShiftForDateUseCase: GetShiftForDateUseCase,
    private val getNextRepoDayUseCase: GetNextRepoDayUseCase,
    private val soundManager: SoundManager
) : ViewModel() {
    
    /** Χώρα χρήστη (GR, OTHER) */
    private var userCountry = "GR"
    
    /** Επιλεγμένη ημερομηνία */
    var selectedDate by mutableStateOf(LocalDate.now())
        private set
    
    /** Κατάσταση επιλεγμένης ημέρας */
    private val _dayStatus = MutableStateFlow<DayStatus?>(null)
    val dayStatus: StateFlow<DayStatus?> = _dayStatus.asStateFlow()
    
    /** Έλεγχος αν η ημέρα είναι αργία */
    private val _isHoliday = MutableStateFlow(false)
    val isHoliday: StateFlow<Boolean> = _isHoliday.asStateFlow()
    
    /** Τύπος αργίας (για μετάφραση στο UI) */
    private val _holidayType = MutableStateFlow<HolidayType?>(null)
    val holidayType: StateFlow<HolidayType?> = _holidayType.asStateFlow()
    
    /** Βάρδια επιλεγμένης εβδομάδας */
    private val _currentShift = MutableStateFlow<ShiftType?>(null)
    val currentShift: StateFlow<ShiftType?> = _currentShift.asStateFlow()
    
    /** Ημέρα ρεπό τρέχουσας εβδομάδας (για το WeekRepoCard) */
    private val _weekRepoDay = MutableStateFlow<Int?>(null)
    val weekRepoDay: StateFlow<Int?> = _weekRepoDay.asStateFlow()
    
    /** Ημερομηνία ρεπό τρέχουσας εβδομάδας */
    private val _weekRepoDate = MutableStateFlow<LocalDate?>(null)
    val weekRepoDate: StateFlow<LocalDate?> = _weekRepoDate.asStateFlow()
    
    /** Επόμενο ρεπό (για το NextRepoCard) */
    private val _nextRepoDate = MutableStateFlow<LocalDate?>(null)
    val nextRepoDate: StateFlow<LocalDate?> = _nextRepoDate.asStateFlow()
    
    /** Ημερομηνίες ρεπό του εμφανιζόμενου μήνα */
    private val _monthRepoDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val monthRepoDates: StateFlow<List<LocalDate>> = _monthRepoDates.asStateFlow()
    
    /** Αργίες του εμφανιζόμενου μήνα (ζευγάρι ημερομηνία-τύπος) */
    private val _monthHolidays = MutableStateFlow<List<Pair<LocalDate, HolidayType>>>(emptyList())
    val monthHolidays: StateFlow<List<Pair<LocalDate, HolidayType>>> = _monthHolidays.asStateFlow()
    
    /** Είναι η χώρα Ελλάδα; (για το legend του calendar) */
    private val _isGreece = MutableStateFlow(true)
    val isGreece: StateFlow<Boolean> = _isGreece.asStateFlow()
    
    init {
        // Reactive observation της χώρας - αλλαγές εφαρμόζονται live
        viewModelScope.launch {
            preferencesRepository.getUserCountry().collect { country ->
                userCountry = country
                _isGreece.value = country == "GR"
                loadData()
            }
        }
    }
    
    /** Παίζει ήχο click για UI interactions */
    fun playClickSound() {
        soundManager.playClickSound()
    }
    
    /** Αλλαγή επιλεγμένης ημερομηνίας */
    fun selectDate(date: LocalDate) {
        selectedDate = date
        loadData()
    }
    
    /** Επιστροφή στο σήμερα */
    fun goToToday() {
        selectDate(LocalDate.now())
    }
    
    /** Έλεγχος αν η επιλεγμένη είναι σήμερα */
    fun isToday(): Boolean = selectedDate == LocalDate.now()
    
    /** Φόρτωση δεδομένων για την επιλεγμένη ημερομηνία */
    private fun loadData() {
        // Έλεγχος αργίας ΜΟΝΟ αν η χώρα είναι Ελλάδα
        if (userCountry == "GR") {
            _isHoliday.value = GreekHolidays.isHoliday(selectedDate)
            _holidayType.value = GreekHolidays.getHolidayType(selectedDate)
        } else {
            _isHoliday.value = false
            _holidayType.value = null
        }
        
        viewModelScope.launch {
            // Status ημέρας
            getDayStatusUseCase(selectedDate).collect { status ->
                _dayStatus.value = status
            }
        }
        
        viewModelScope.launch {
            // Βάρδια εβδομάδας
            getShiftForDateUseCase(selectedDate).collect { shift ->
                _currentShift.value = shift
            }
        }
        
        viewModelScope.launch {
            // Επόμενο ρεπό
            getNextRepoDayUseCase().collect { date ->
                _nextRepoDate.value = date
            }
        }
        
        viewModelScope.launch {
            // Ρεπό τρέχουσας εβδομάδας
            scheduleRepository.getSchedule().collect { schedule ->
                schedule?.let {
                    val (day, date) = calculateWeekRepoInfo(schedule, selectedDate)
                    _weekRepoDay.value = day
                    _weekRepoDate.value = date
                    
                    // Υπολογισμός ρεπό του μήνα
                    _monthRepoDates.value = calculateMonthRepoDates(schedule, selectedDate)
                }
            }
        }
        
        // Υπολογισμός αργιών του μήνα ΜΟΝΟ αν η χώρα είναι Ελλάδα
        _monthHolidays.value = if (userCountry == "GR") {
            calculateMonthHolidays(selectedDate)
        } else {
            emptyList()
        }
    }
    
    /**
     * Υπολογίζει όλες τις ημερομηνίες ρεπό του μήνα που περιέχει την date.
     */
    private fun calculateMonthRepoDates(schedule: WorkSchedule, date: LocalDate): List<LocalDate> {
        val repoDates = mutableListOf<LocalDate>()
        val firstOfMonth = date.withDayOfMonth(1)
        val lastOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        
        var currentDate = firstOfMonth
        while (!currentDate.isAfter(lastOfMonth)) {
            if (schedule.getDayStatus(currentDate) == DayStatus.REPO) {
                repoDates.add(currentDate)
            }
            currentDate = currentDate.plusDays(1)
        }
        return repoDates
    }
    
    /**
     * Υπολογίζει όλες τις αργίες του μήνα που περιέχει την date.
     */
    private fun calculateMonthHolidays(date: LocalDate): List<Pair<LocalDate, HolidayType>> {
        val holidays = mutableListOf<Pair<LocalDate, HolidayType>>()
        val firstOfMonth = date.withDayOfMonth(1)
        val lastOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        
        var currentDate = firstOfMonth
        while (!currentDate.isAfter(lastOfMonth)) {
            GreekHolidays.getHolidayType(currentDate)?.let { type ->
                holidays.add(currentDate to type)
            }
            currentDate = currentDate.plusDays(1)
        }
        return holidays
    }
    
    /**
     * Υπολογίζει ποια ημέρα και ημερομηνία είναι το ρεπό στην εβδομάδα που περιέχει την date.
     */
    private fun calculateWeekRepoInfo(schedule: WorkSchedule, date: LocalDate): Pair<Int, LocalDate?> {
        val weekStart = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        
        for (i in 0..6) {
            val checkDate = weekStart.plusDays(i.toLong())
            if (schedule.getDayStatus(checkDate) == DayStatus.REPO) {
                return Pair(checkDate.dayOfWeek.value, checkDate)
            }
        }
        return Pair(schedule.currentRepoDay, null)
    }
}
