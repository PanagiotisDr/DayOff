package com.repotracker.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.repotracker.R
import com.repotracker.domain.model.DayStatus
import com.repotracker.domain.model.HolidayType
import com.repotracker.domain.model.ShiftType
import com.repotracker.presentation.theme.EveningPurple
import com.repotracker.presentation.theme.HalfHolidayRed
import com.repotracker.presentation.theme.MorningYellow
import com.repotracker.presentation.theme.OffDayOrange
import com.repotracker.presentation.theme.RepoGreen
import com.repotracker.presentation.theme.WorkIndigo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Κύρια οθόνη της εφαρμογής.
 * Εμφανίζει calendar, κάρτα εβδομάδας, και αποτέλεσμα ημέρας.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val dayStatus by viewModel.dayStatus.collectAsState()
    val currentShift by viewModel.currentShift.collectAsState()
    val weekRepoDay by viewModel.weekRepoDay.collectAsState()
    val weekRepoDate by viewModel.weekRepoDate.collectAsState()
    val nextRepoDate by viewModel.nextRepoDate.collectAsState()
    val isHoliday by viewModel.isHoliday.collectAsState()
    val holidayType by viewModel.holidayType.collectAsState()
    val monthRepoDates by viewModel.monthRepoDates.collectAsState()
    val monthHolidays by viewModel.monthHolidays.collectAsState()
    val isGreece by viewModel.isGreece.collectAsState()
    
    // Μετάφραση HolidayType σε String
    val holidayName = holidayType?.let { type ->
        when (type) {
            HolidayType.NEW_YEAR -> stringResource(R.string.holiday_new_year)
            HolidayType.EPIPHANY -> stringResource(R.string.holiday_epiphany)
            HolidayType.INDEPENDENCE_DAY -> stringResource(R.string.holiday_independence_day)
            HolidayType.MAY_DAY -> stringResource(R.string.holiday_may_day)
            HolidayType.ASSUMPTION -> stringResource(R.string.holiday_assumption)
            HolidayType.OXI_DAY -> stringResource(R.string.holiday_oxi_day)
            HolidayType.CHRISTMAS -> stringResource(R.string.holiday_christmas)
            HolidayType.CHRISTMAS_SECOND -> stringResource(R.string.holiday_christmas_second)
            HolidayType.CLEAN_MONDAY -> stringResource(R.string.holiday_clean_monday)
            HolidayType.GOOD_FRIDAY -> stringResource(R.string.holiday_good_friday)
            HolidayType.EASTER_SUNDAY -> stringResource(R.string.holiday_easter_sunday)
            HolidayType.EASTER_MONDAY -> stringResource(R.string.holiday_easter_monday)
            HolidayType.HOLY_SPIRIT -> stringResource(R.string.holiday_holy_spirit)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    if (!viewModel.isToday()) {
                        IconButton(onClick = { 
                            viewModel.playClickSound()
                            viewModel.goToToday() 
                        }) {
                            Icon(Icons.Default.Today, stringResource(R.string.btn_today))
                        }
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.BarChart, stringResource(R.string.nav_stats))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, stringResource(R.string.nav_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            if (!viewModel.isToday()) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        viewModel.playClickSound()
                        viewModel.goToToday() 
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    icon = { Icon(Icons.Default.Today, null) },
                    text = { Text(stringResource(R.string.btn_today)) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Κάρτα εβδομάδας
            WeekRepoCard(
                weekRepoDay = weekRepoDay,
                weekRepoDate = weekRepoDate,
                currentShift = currentShift,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Custom Calendar με markers για ρεπό/αργίες
            CustomCalendar(
                selectedDate = viewModel.selectedDate,
                repoDates = monthRepoDates,
                holidays = monthHolidays,
                isGreece = isGreece,
                onDateSelected = { date -> viewModel.selectDate(date) },
                onMonthChanged = { date -> viewModel.selectDate(date) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Κάρτα αποτελέσματος
            ResultCard(
                date = viewModel.selectedDate,
                status = dayStatus,
                isHoliday = isHoliday,
                holidayName = holidayName,
                modifier = Modifier.padding(16.dp)
            )
            
            // Επόμενο ρεπό
            nextRepoDate?.let { date ->
                NextRepoCard(
                    date = date,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Κάρτα που δείχνει το ρεπό της τρέχουσας εβδομάδας και τη βάρδια.
 */
@Composable
private fun WeekRepoCard(
    weekRepoDay: Int?,
    weekRepoDate: LocalDate?,
    currentShift: ShiftType?,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf(
        1 to stringResource(R.string.day_monday),
        2 to stringResource(R.string.day_tuesday),
        3 to stringResource(R.string.day_wednesday),
        4 to stringResource(R.string.day_thursday),
        5 to stringResource(R.string.day_friday),
        6 to stringResource(R.string.day_saturday),
        7 to stringResource(R.string.day_sunday)
    ).toMap()
    
    // Formatter για ημερομηνία (π.χ. "29 Ιαν")
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_this_week),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = weekRepoDay?.let { dayNames[it] } ?: "-",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1
                )
                // Εμφάνιση ημερομηνίας κάτω από την ημέρα
                weekRepoDate?.let { date ->
                    Text(
                        text = date.format(dateFormatter),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
            
            currentShift?.let { shift ->
                ShiftBadge(shift = shift)
            }
        }
    }
}

/**
 * Badge για βάρδια (Πρωινή/Απογευματινή).
 */
@Composable
private fun ShiftBadge(shift: ShiftType) {
    val (icon, text, bgColor) = when (shift) {
        ShiftType.MORNING -> Triple("☀️", stringResource(R.string.shift_morning), MorningYellow)
        ShiftType.EVENING -> Triple("🌙", stringResource(R.string.shift_evening), EveningPurple)
        else -> return
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$icon $text",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Custom ημερολόγιο με visual markers για ρεπό και αργίες.
 * Πράσινο dot κάτω από την ημερομηνία = Ρεπό
 * Πορτοκαλί dot κάτω από την ημερομηνία = Αργία
 */
@Composable
private fun CustomCalendar(
    selectedDate: LocalDate,
    repoDates: List<LocalDate>,
    holidays: List<Pair<LocalDate, HolidayType>>,
    isGreece: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayedMonth by androidx.compose.runtime.remember { 
        mutableStateOf(selectedDate.withDayOfMonth(1)) 
    }
    
    // Ονόματα ημερών (Δ, Τ, Τ, Π, Π, Σ, Κ)
    val dayNames = listOf(
        stringResource(R.string.day_mon),
        stringResource(R.string.day_tue),
        stringResource(R.string.day_wed),
        stringResource(R.string.day_thu),
        stringResource(R.string.day_fri),
        stringResource(R.string.day_sat),
        stringResource(R.string.day_sun)
    )
    
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    val holidayDates = holidays.map { it.first }.toSet()
    val repoDatesSet = repoDates.toSet()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        // Swipe state με offset για real-time drag feedback
        var dragOffset by remember { mutableStateOf(0f) }
        var slideDirection by remember { mutableStateOf(1) }
        val swipeThreshold = 100f // pixels
        
        // Animated offset για smooth snap-back
        val animatedOffset by androidx.compose.animation.core.animateFloatAsState(
            targetValue = dragOffset,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
            ),
            label = "DragOffset"
        )
        
        Column(
            modifier = Modifier
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // Σύρσιμο προς τα δεξιά = προηγούμενος μήνας
                            if (dragOffset > swipeThreshold) {
                                slideDirection = -1
                                displayedMonth = displayedMonth.minusMonths(1)
                                onMonthChanged(displayedMonth)
                            }
                            // Σύρσιμο προς τα αριστερά = επόμενος μήνας
                            else if (dragOffset < -swipeThreshold) {
                                slideDirection = 1
                                displayedMonth = displayedMonth.plusMonths(1)
                                onMonthChanged(displayedMonth)
                            }
                            // Snap back
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { _, delta ->
                            // Ακολούθησε το δάχτυλο με ελαφρύ resistance
                            dragOffset += delta * 0.8f
                        }
                    )
                }
        ) {
            // Header με μήνα και navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    slideDirection = -1 // Slide from left
                    displayedMonth = displayedMonth.minusMonths(1)
                    onMonthChanged(displayedMonth)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                
                Text(
                    text = displayedMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = { 
                    slideDirection = 1 // Slide from right
                    displayedMonth = displayedMonth.plusMonths(1)
                    onMonthChanged(displayedMonth)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Legend - εμφάνιση αργιών μόνο για Ελλάδα (όταν holidays δεν είναι άδειο)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = RepoGreen, label = stringResource(R.string.legend_repo))
                // Αργίες εμφανίζονται μόνο στην Ελλάδα
                if (isGreece) {
                    Spacer(modifier = Modifier.width(12.dp))
                    LegendItem(color = OffDayOrange, label = stringResource(R.string.legend_holiday))
                    Spacer(modifier = Modifier.width(12.dp))
                    LegendItem(color = HalfHolidayRed, label = stringResource(R.string.legend_half_holiday))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ονόματα ημερών (στατικά - δεν κάνουν animate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayNames.forEach { dayName ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid ημερών με slide animation (carousel effect)
            AnimatedContent(
                targetState = displayedMonth,
                transitionSpec = {
                    // Slide animation βάσει κατεύθυνσης
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> slideDirection * fullWidth },
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -slideDirection * fullWidth },
                        animationSpec = tween(300)
                    )
                },
                label = "CalendarSlide"
            ) { targetMonth ->
                Column {
                    val firstOfMonth = targetMonth.withDayOfMonth(1)
                    val daysInMonth = targetMonth.lengthOfMonth()
                    val startDayOfWeek = firstOfMonth.dayOfWeek.value // 1=Monday
                    
                    // Υπολογισμός εβδομάδων
                    val totalCells = startDayOfWeek - 1 + daysInMonth
                    val weeks = (totalCells + 6) / 7
                    
                    for (week in 0 until weeks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (dayOfWeek in 1..7) {
                                val cellIndex = week * 7 + dayOfWeek
                                val dayOfMonth = cellIndex - (startDayOfWeek - 1)
                                
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayOfMonth in 1..daysInMonth) {
                                        val date = targetMonth.withDayOfMonth(dayOfMonth)
                                        val isSelected = date == selectedDate
                                        val isRepo = date in repoDatesSet
                                        val holidayPair = holidays.find { it.first == date }
                                        val isToday = date == LocalDate.now()
                                        
                                        CalendarDay(
                                            day = dayOfMonth,
                                            isSelected = isSelected,
                                            isToday = isToday,
                                            isRepo = isRepo,
                                            holidayType = holidayPair?.second,
                                            onClick = { onDateSelected(date) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Μεμονωμένη ημέρα στο calendar.
 * Αν η αργία είναι GOOD_FRIDAY, δείχνει κόκκινο dot (ημι-αργία).
 */
@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isRepo: Boolean,
    holidayType: HolidayType?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    // Προσδιορισμός χρώματος αργίας
    val holidayColor = when (holidayType) {
        HolidayType.GOOD_FRIDAY -> HalfHolidayRed // Ημι-αργία = κόκκινο
        null -> null
        else -> OffDayOrange // Κανονική αργία = πορτοκαλί
    }
    
    Column(
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )
        
        // Dots κάτω από τον αριθμό
        if (isRepo || holidayColor != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (isRepo) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(RepoGreen)
                    )
                }
                holidayColor?.let { color ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

/**
 * Μεμονωμένο item του legend με γεμάτο κύκλο και label.
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Κάρτα αποτελέσματος με χρωματική κωδικοποίηση.
 * 
 * Αν η ημέρα είναι αργία, εμφανίζει την αργία πρώτα, μετά το status εργασίας.
 */
@Composable
private fun ResultCard(
    date: LocalDate,
    status: DayStatus?,
    isHoliday: Boolean,
    holidayName: String?,
    modifier: Modifier = Modifier
) {
    // Αν είναι αργία, χρησιμοποιούμε χρυσό/πορτοκαλί χρώμα
    val holidayColor = androidx.compose.ui.graphics.Color(0xFFFF9800)
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isHoliday -> holidayColor
            status == DayStatus.REPO -> RepoGreen
            status == DayStatus.WORK -> WorkIndigo
            status == DayStatus.FIXED_OFF -> OffDayOrange
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "result_bg"
    )
    
    val statusText = when {
        isHoliday -> holidayName ?: stringResource(R.string.status_holiday)
        status == DayStatus.REPO -> stringResource(R.string.status_repo)
        status == DayStatus.WORK -> stringResource(R.string.status_work)
        status == DayStatus.FIXED_OFF -> stringResource(R.string.status_fixed_off)
        else -> "-"
    }
    
    val statusEmoji = when {
        isHoliday -> holidayName?.takeLast(2) ?: "🎊" // Το emoji είναι στο τέλος του holidayName
        status == DayStatus.REPO -> "🎉"
        status == DayStatus.WORK -> "💼"
        status == DayStatus.FIXED_OFF -> "🏠"
        else -> ""
    }
    
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_selected_date),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusEmoji,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            // Αν είναι αργία ΚΑΙ εργάσιμη, δείξε και το status
            if (isHoliday && status != null && status != DayStatus.FIXED_OFF) {
                Spacer(modifier = Modifier.height(8.dp))
                val holidayMessage = when (status) {
                    DayStatus.WORK -> stringResource(R.string.status_holiday_gained)
                    DayStatus.REPO -> stringResource(R.string.status_holiday_lost)
                    else -> ""
                }
                if (holidayMessage.isNotEmpty()) {
                    Text(
                        text = holidayMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Κάρτα επόμενου ρεπό.
 */
@Composable
private fun NextRepoCard(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
    val today = LocalDate.now()
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, date)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_next_repo),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = date.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RepoGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when {
                        daysUntil == 0L -> stringResource(R.string.home_today)
                        daysUntil == 1L -> stringResource(R.string.home_tomorrow)
                        else -> stringResource(R.string.home_in_days, daysUntil.toInt())
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = RepoGreen
                )
            }
        }
    }
}
