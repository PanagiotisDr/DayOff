package com.repotracker.presentation.screens.welcome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repotracker.R
import kotlinx.coroutines.delay

/**
 * Welcome Screen - Πρώτη οθόνη καλωσορίσματος.
 * Εμφανίζεται μόνο στην πρώτη εκκίνηση πριν το Setup.
 */
@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200) // Μικρή καθυστέρηση για smooth entrance
        showContent = true
    }
    
    // Animated values
    val logoScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.5f,
        animationSpec = tween(durationMillis = 600),
        label = "logo_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "content_alpha"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 600),
        label = "button_alpha"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Icon με animation
        Text(
            text = "📅",
            fontSize = 80.sp,
            modifier = Modifier
                .scale(logoScale)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Welcome title
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(contentAlpha)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(contentAlpha)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Start button με bounce effect
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .alpha(buttonAlpha)
                .size(width = 200.dp, height = 56.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_button),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
