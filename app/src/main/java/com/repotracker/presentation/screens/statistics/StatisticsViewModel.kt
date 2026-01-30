package com.repotracker.presentation.screens.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repotracker.domain.model.MonthlyStatistics
import com.repotracker.domain.usecase.GetMonthlyStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel για την οθόνη στατιστικών.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getMonthlyStatisticsUseCase: GetMonthlyStatisticsUseCase
) : ViewModel() {
    
    /** Επιλεγμένος μήνας */
    var selectedMonth by mutableStateOf(YearMonth.now())
        private set
    
    /** Στατιστικά μήνα */
    private val _statistics = MutableStateFlow<MonthlyStatistics?>(null)
    val statistics: StateFlow<MonthlyStatistics?> = _statistics.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    /** Αλλαγή μήνα */
    fun selectMonth(yearMonth: YearMonth) {
        selectedMonth = yearMonth
        loadStatistics()
    }
    
    /** Προηγούμενος μήνας */
    fun previousMonth() {
        selectMonth(selectedMonth.minusMonths(1))
    }
    
    /** Επόμενος μήνας */
    fun nextMonth() {
        selectMonth(selectedMonth.plusMonths(1))
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            getMonthlyStatisticsUseCase(selectedMonth).collect { stats ->
                _statistics.value = stats
            }
        }
    }
}
