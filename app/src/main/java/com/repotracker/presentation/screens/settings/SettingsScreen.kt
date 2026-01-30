package com.repotracker.presentation.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.RadioButton
import com.repotracker.R

/**
 * Οθόνη ρυθμίσεων.
 * 
 * ΣΗΜΑΝΤΙΚΟ: Η αλλαγή γλώσσας απαιτεί επανεκκίνηση της εφαρμογής.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onResetSchedule: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState(initial = "el")
    val themeMode by viewModel.themeMode.collectAsState(initial = 0)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val notificationHour by viewModel.notificationHour.collectAsState(initial = 20)
    val notificationMinute by viewModel.notificationMinute.collectAsState(initial = 0)
    val notificationSoundsEnabled by viewModel.notificationSoundsEnabled.collectAsState(initial = true)
    val clickSoundsEnabled by viewModel.clickSoundsEnabled.collectAsState(initial = true)
    val userCountry by viewModel.userCountry.collectAsState(initial = "GR")
    val fontScale by viewModel.fontScale.collectAsState(initial = 1.0f)
    
    var showResetDialog by remember { mutableStateOf(false) }
    var showLanguageRestartDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var pendingLanguage by remember { mutableStateOf("") }
    
    // Permission launcher για notifications (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setNotificationsEnabled(true)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Γλώσσα (με dialog για restart)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_language)) },
                    supportingContent = { 
                        Text(
                            text = stringResource(R.string.settings_language_restart),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Language, null) },
                    trailingContent = {
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = language == "el",
                                onClick = { 
                                    if (language != "el") {
                                        pendingLanguage = "el"
                                        showLanguageRestartDialog = true
                                    }
                                },
                                shape = SegmentedButtonDefaults.itemShape(0, 2)
                            ) { Text("🇬🇷") }
                            SegmentedButton(
                                selected = language == "en",
                                onClick = { 
                                    if (language != "en") {
                                        pendingLanguage = "en"
                                        showLanguageRestartDialog = true
                                    }
                                },
                                shape = SegmentedButtonDefaults.itemShape(1, 2)
                            ) { Text("🇬🇧") }
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Χώρα (επηρεάζει αργίες)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_country)) },
                    supportingContent = { 
                        Text(
                            text = stringResource(R.string.settings_country_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingContent = { Text("🌍", style = MaterialTheme.typography.headlineSmall) },
                    trailingContent = {
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = userCountry == "GR",
                                onClick = { 
                                    viewModel.playClickSound()
                                    viewModel.setUserCountry("GR")
                                },
                                shape = SegmentedButtonDefaults.itemShape(0, 2)
                            ) { Text("🇬🇷") }
                            SegmentedButton(
                                selected = userCountry == "OTHER",
                                onClick = { 
                                    viewModel.playClickSound()
                                    viewModel.setUserCountry("OTHER")
                                },
                                shape = SegmentedButtonDefaults.itemShape(1, 2)
                            ) { Text("🌐") }
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Μέγεθος Γραμματοσειράς (accessibility)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_font_size)) },
                        supportingContent = { 
                            Text(
                                text = stringResource(R.string.settings_font_size_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingContent = { Text("🔤", style = MaterialTheme.typography.headlineSmall) }
                    )
                    
                    // 3 επιλογές: Κανονικό, Μεγάλο, Πολύ Μεγάλο
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Κανονικό (1.0f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.0f) 
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = fontScale == 1.0f,
                                onClick = { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.0f) 
                                }
                            )
                            Text(
                                text = stringResource(R.string.font_size_normal),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        
                        // Μεγάλο (1.15f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.15f) 
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = fontScale == 1.15f,
                                onClick = { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.15f) 
                                }
                            )
                            Text(
                                text = stringResource(R.string.font_size_large),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        
                        // Πολύ Μεγάλο (1.3f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.3f) 
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = fontScale == 1.3f,
                                onClick = { 
                                    viewModel.playClickSound()
                                    viewModel.setFontScale(1.3f) 
                                }
                            )
                            Text(
                                text = stringResource(R.string.font_size_extra_large),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Θέμα (εφαρμόζεται αμέσως)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_theme)) },
                        leadingContent = {
                            Icon(
                                when (themeMode) {
                                    1 -> Icons.Default.LightMode
                                    2 -> Icons.Default.DarkMode
                                    else -> Icons.Default.SettingsBrightness
                                },
                                null
                            )
                        }
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        SegmentedButton(
                            selected = themeMode == 0,
                            onClick = { viewModel.setThemeMode(0) },
                            shape = SegmentedButtonDefaults.itemShape(0, 3)
                        ) { Text(stringResource(R.string.theme_system)) }
                        SegmentedButton(
                            selected = themeMode == 1,
                            onClick = { viewModel.setThemeMode(1) },
                            shape = SegmentedButtonDefaults.itemShape(1, 3)
                        ) { Text(stringResource(R.string.theme_light)) }
                        SegmentedButton(
                            selected = themeMode == 2,
                            onClick = { viewModel.setThemeMode(2) },
                            shape = SegmentedButtonDefaults.itemShape(2, 3)
                        ) { Text(stringResource(R.string.theme_dark)) }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ειδοποιήσεις
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_notifications)) },
                        supportingContent = { Text(stringResource(R.string.settings_notifications_desc)) },
                        leadingContent = { Icon(Icons.Default.Notifications, null) },
                        trailingContent = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        // Έλεγχος και αίτηση permission για Android 13+
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            if (ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.POST_NOTIFICATIONS
                                                ) != PackageManager.PERMISSION_GRANTED
                                            ) {
                                                notificationPermissionLauncher.launch(
                                                    Manifest.permission.POST_NOTIFICATIONS
                                                )
                                                return@Switch
                                            }
                                        }
                                    }
                                    viewModel.setNotificationsEnabled(enabled)
                                }
                            )
                        }
                    )
                    
                    // Ώρα ειδοποίησης (εμφανίζεται μόνο όταν είναι ενεργοποιημένες)
                    if (notificationsEnabled) {
                        ListItem(
                            modifier = Modifier.clickable { showTimePickerDialog = true },
                            headlineContent = { Text(stringResource(R.string.settings_notification_time)) },
                            supportingContent = { 
                                Text(
                                    text = String.format("%02d:%02d", notificationHour, notificationMinute),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            leadingContent = { Icon(Icons.Default.AccessTime, null) }
                        )
                        
                        // Test Notification button
                        ListItem(
                            modifier = Modifier.clickable { 
                                // Έλεγχος permission πριν την αποστολή test notification
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                        return@clickable
                                    }
                                }
                                viewModel.playClickSound()
                                viewModel.sendTestNotification()
                            },
                            headlineContent = { Text(stringResource(R.string.settings_test_notification)) },
                            supportingContent = { Text(stringResource(R.string.settings_test_notification_desc)) },
                            leadingContent = { Icon(Icons.Filled.Notifications, null) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ήχοι ειδοποιήσεων
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_notification_sounds)) },
                    supportingContent = { Text(stringResource(R.string.settings_notification_sounds_desc)) },
                    leadingContent = { Icon(Icons.Default.VolumeUp, null) },
                    trailingContent = {
                        Switch(
                            checked = notificationSoundsEnabled,
                            onCheckedChange = { 
                                viewModel.playClickSound()
                                viewModel.setNotificationSoundsEnabled(it) 
                            }
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ήχοι click
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_click_sounds)) },
                    supportingContent = { Text(stringResource(R.string.settings_click_sounds_desc)) },
                    leadingContent = { Icon(Icons.Default.TouchApp, null) },
                    trailingContent = {
                        Switch(
                            checked = clickSoundsEnabled,
                            onCheckedChange = { 
                                viewModel.playClickSound()
                                viewModel.setClickSoundsEnabled(it) 
                            }
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Reset
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showResetDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_reset_schedule),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Refresh,
                            null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                )
            }
        }
    }
    
    // Dialog επανεκκίνησης για αλλαγή γλώσσας
    if (showLanguageRestartDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageRestartDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = { Text(stringResource(R.string.settings_language_restart_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLanguageRestartDialog = false
                        // Αποθήκευση ΠΡΩΤΑ, restart ΜΕΤΑ την ολοκλήρωση
                        viewModel.setLanguage(pendingLanguage) {
                            // Επανεκκίνηση της εφαρμογής ΑΦΟΥ αποθηκευτεί
                            val intent = context.packageManager
                                .getLaunchIntentForPackage(context.packageName)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            Runtime.getRuntime().exit(0)
                        }
                    }
                ) {
                    Text(stringResource(R.string.btn_restart))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLanguageRestartDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
    
    // Time Picker Dialog
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationHour,
            initialMinute = notificationMinute,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text(stringResource(R.string.settings_notification_time)) },
            text = {
                TimeInput(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePickerDialog = false
                        viewModel.setNotificationTime(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    }
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
    
    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.settings_reset_schedule)) },
            text = { Text(stringResource(R.string.settings_reset_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetSchedule(onResetSchedule)
                    }
                ) {
                    Text(stringResource(R.string.btn_reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

