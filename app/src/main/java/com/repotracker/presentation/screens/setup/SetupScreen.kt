package com.repotracker.presentation.screens.setup

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.repotracker.R
import com.repotracker.domain.model.ShiftType
import com.repotracker.presentation.components.ConfettiEffect
import kotlinx.coroutines.delay
import java.time.LocalDate

/**
 * Οθόνη ρύθμισης προγράμματος (4-step wizard).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    // Permission launcher για Android 13+ (TIRAMISU)
    // Ζητάμε permission κατά την ολοκλήρωση του setup
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Ανεξάρτητα αν δέχτηκε ή όχι, ολοκληρώνουμε το setup
        viewModel.saveAndComplete(onSetupComplete)
    }
    
    // Function για save με permission request
    val handleSaveAndComplete: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - ζητάμε permission πριν αποθηκεύσουμε
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Παλιά Android - δεν χρειάζεται permission
            viewModel.saveAndComplete(onSetupComplete)
        }
    }
    
    val dayNames = listOf(
        1 to stringResource(R.string.day_monday),
        2 to stringResource(R.string.day_tuesday),
        3 to stringResource(R.string.day_wednesday),
        4 to stringResource(R.string.day_thursday),
        5 to stringResource(R.string.day_friday),
        6 to stringResource(R.string.day_saturday),
        7 to stringResource(R.string.day_sunday)
    )
    
    // Confetti state για celebration
    var showConfetti by remember { mutableStateOf(false) }
    
    // LaunchedEffect για confetti delay πριν το navigation
    androidx.compose.runtime.LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(1500) // Περίμενε να δει ο χρήστης το confetti
            handleSaveAndComplete()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.setup_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (viewModel.currentStep + 1) / 5f },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Βήμα ${viewModel.currentStep + 1} από 5",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Animated step content
            AnimatedContent(
                targetState = viewModel.currentStep,
                label = "step_content"
            ) { step ->
                when (step) {
                    0 -> StepCountry(
                        selectedCountry = viewModel.selectedCountry,
                        onSelect = { 
                            viewModel.playClickSound()
                            viewModel.selectCountry(it) 
                        }
                    )
                    1 -> StepWorkDays(
                        dayNames = dayNames,
                        selectedDays = viewModel.selectedWorkDays,
                        onToggle = { 
                            viewModel.playClickSound()
                            viewModel.toggleWorkDay(it) 
                        },
                        notificationSoundsEnabled = viewModel.notificationSoundsEnabled,
                        onNotificationSoundsToggle = { 
                            viewModel.playClickSound()
                            viewModel.toggleNotificationSounds(it) 
                        },
                        clickSoundsEnabled = viewModel.clickSoundsEnabled,
                        onClickSoundsToggle = { 
                            viewModel.playClickSound()
                            viewModel.toggleClickSounds(it) 
                        }
                    )
                    2 -> StepRepoDay(
                        workDays = viewModel.selectedWorkDays.toList(),
                        selectedDate = viewModel.selectedRepoDate,
                        onDateSelected = { 
                            viewModel.playClickSound()
                            viewModel.selectRepoDate(it) 
                        }
                    )
                    3 -> StepRollingType(
                        isRolling = viewModel.isRolling,
                        onSelect = { 
                            viewModel.playClickSound()
                            viewModel.updateRolling(it) 
                        }
                    )
                    4 -> StepShiftType(
                        shiftType = viewModel.shiftType,
                        onSelect = { 
                            viewModel.playClickSound()
                            viewModel.selectShiftType(it) 
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (viewModel.currentStep > 0) {
                    OutlinedButton(onClick = { 
                        viewModel.playClickSound()
                        viewModel.previousStep() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_cancel))
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                
                Button(
                    onClick = {
                        viewModel.playClickSound()
                        if (viewModel.currentStep < 4) {
                            viewModel.nextStep()
                        } else {
                            // Ενεργοποίηση confetti - το save γίνεται μέσω LaunchedEffect
                            showConfetti = true
                        }
                    },
                    enabled = viewModel.canProceed()
                ) {
                    Text(
                        if (viewModel.currentStep < 4) 
                            stringResource(R.string.btn_continue) 
                        else 
                            stringResource(R.string.btn_save)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        if (viewModel.currentStep < 4) 
                            Icons.AutoMirrored.Filled.ArrowForward 
                        else 
                            Icons.Default.Check,
                        null
                    )
                }
            }
        }
    }
    
    // Confetti overlay
    ConfettiEffect(
        isActive = showConfetti,
        particleCount = 60
    )
}

/**
 * Step 0: Επιλογή χώρας.
 * Δύο επιλογές: Ελλάδα (με αργίες) ή Άλλη χώρα (χωρίς).
 */
@Composable
private fun StepCountry(
    selectedCountry: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.setup_step_country_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.setup_step_country_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Ελλάδα
        Surface(
            onClick = { onSelect("GR") },
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (selectedCountry == "GR") 8.dp else 0.dp,
            color = if (selectedCountry == "GR") 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🇬🇷", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.country_greece),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.country_greece_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Άλλη χώρα
        Surface(
            onClick = { onSelect("OTHER") },
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (selectedCountry == "OTHER") 8.dp else 0.dp,
            color = if (selectedCountry == "OTHER") 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌍", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.country_other),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.country_other_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepWorkDays(
    dayNames: List<Pair<Int, String>>,
    selectedDays: List<Int>,
    onToggle: (Int) -> Unit,
    notificationSoundsEnabled: Boolean,
    onNotificationSoundsToggle: (Boolean) -> Unit,
    clickSoundsEnabled: Boolean,
    onClickSoundsToggle: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.setup_step1_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.setup_step1_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dayNames.forEach { (day, name) ->
                FilterChip(
                    selected = day in selectedDays,
                    onClick = { onToggle(day) },
                    label = { Text(name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Notification Sounds toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_notification_sounds),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.settings_notification_sounds_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = notificationSoundsEnabled,
                onCheckedChange = onNotificationSoundsToggle
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Click Sounds toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_click_sounds),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.settings_click_sounds_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = clickSoundsEnabled,
                onCheckedChange = onClickSoundsToggle
            )
        }
    }
}

/**
 * Step 2: Επιλογή ημερομηνίας ρεπό με Date Picker.
 * Εμφανίζει τις επόμενες εργάσιμες ημέρες για επιλογή.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepRepoDay(
    workDays: List<Int>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    // State για το DatePickerDialog
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Υπολογισμός επιτρεπόμενων ημερομηνιών (επόμενες 14 ημέρες που είναι εργάσιμες)
    val today = LocalDate.now()
    
    Column {
        Text(
            text = stringResource(R.string.setup_step2_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.setup_step2_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Κάρτα επιλογής ημερομηνίας
        Surface(
            onClick = { showDatePicker = true },
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (selectedDate != null) 8.dp else 0.dp,
            color = if (selectedDate != null) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📅", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (selectedDate != null) {
                        // Εμφάνιση επιλεγμένης ημερομηνίας
                        val dayName = when (selectedDate.dayOfWeek.value) {
                            1 -> stringResource(R.string.day_monday)
                            2 -> stringResource(R.string.day_tuesday)
                            3 -> stringResource(R.string.day_wednesday)
                            4 -> stringResource(R.string.day_thursday)
                            5 -> stringResource(R.string.day_friday)
                            6 -> stringResource(R.string.day_saturday)
                            else -> stringResource(R.string.day_sunday)
                        }
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.setup_step2_hint),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Εμφάνιση επόμενων εργάσιμων ημερών ως γρήγορες επιλογές
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.setup_step2_quick),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Βρες τις επόμενες 5 εργάσιμες ημέρες (συμπεριλαμβανομένου του σήμερα)
        val nextWorkDays = (0..13).mapNotNull { daysAhead ->
            val date = today.plusDays(daysAhead.toLong())
            if (date.dayOfWeek.value in workDays) date else null
        }.take(5)
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            nextWorkDays.forEach { date ->
                val dayName = when (date.dayOfWeek.value) {
                    1 -> stringResource(R.string.day_mon)
                    2 -> stringResource(R.string.day_tue)
                    3 -> stringResource(R.string.day_wed)
                    4 -> stringResource(R.string.day_thu)
                    5 -> stringResource(R.string.day_fri)
                    6 -> stringResource(R.string.day_sat)
                    else -> stringResource(R.string.day_sun)
                }
                FilterChip(
                    selected = selectedDate == date,
                    onClick = { onDateSelected(date) },
                    label = { Text("$dayName ${date.dayOfMonth}/${date.monthValue}") }
                )
            }
        }
    }
    
    // DatePickerDialog
    if (showDatePicker) {
        // Χρήση SelectableDates για φιλτράρισμα επιτρεπόμενων ημερομηνιών
        val selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = java.time.Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDate()
                // Επιτρέπουμε μόνο εργάσιμες ημέρες μετά το σήμερα
                return date.dayOfWeek.value in workDays && !date.isBefore(today)
            }
        }
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.let { 
                it.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
            } ?: today.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = selectableDates
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()
                            // Έλεγχος αν είναι εργάσιμη ημέρα
                            if (selected.dayOfWeek.value in workDays && !selected.isBefore(today)) {
                                onDateSelected(selected)
                            }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepRollingType(
    isRolling: Boolean,
    onSelect: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.setup_step3_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.setup_step3_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = isRolling,
                onClick = { onSelect(true) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text(stringResource(R.string.rolling_enabled))
            }
            SegmentedButton(
                selected = !isRolling,
                onClick = { onSelect(false) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text(stringResource(R.string.rolling_disabled))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepShiftType(
    shiftType: ShiftType,
    onSelect: (ShiftType) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.setup_step4_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.setup_step4_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = shiftType == ShiftType.MORNING,
                onClick = { onSelect(ShiftType.MORNING) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
            ) {
                Text("☀️ " + stringResource(R.string.shift_morning))
            }
            SegmentedButton(
                selected = shiftType == ShiftType.EVENING,
                onClick = { onSelect(ShiftType.EVENING) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
            ) {
                Text("🌙 " + stringResource(R.string.shift_evening))
            }
            SegmentedButton(
                selected = shiftType == ShiftType.NONE,
                onClick = { onSelect(ShiftType.NONE) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
            ) {
                Text(stringResource(R.string.shift_none))
            }
        }
    }
}
