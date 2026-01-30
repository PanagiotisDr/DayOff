package com.repotracker.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Data class για ένα confetti particle.
 */
private data class ConfettiParticle(
    val x: Float,           // Αρχική θέση X (0-1)
    val speed: Float,       // Ταχύτητα πτώσης
    val size: Float,        // Μέγεθος
    val color: Color,       // Χρώμα
    val wobble: Float       // Κύμανση
)

/**
 * Confetti animation effect.
 * Εμφανίζει falling confetti particles για celebration.
 */
@Composable
fun ConfettiEffect(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    if (!isActive) return
    
    // Χρώματα confetti
    val colors = listOf(
        Color(0xFFFF6B6B), // Κόκκινο
        Color(0xFF4ECDC4), // Τυρκουάζ
        Color(0xFFFFE66D), // Κίτρινο
        Color(0xFF95E1D3), // Mint
        Color(0xFFF38181), // Coral
        Color(0xFF7C83FD), // Μωβ
        Color(0xFFFCE38A)  // Χρυσό
    )
    
    // Δημιουργία particles
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                size = 8f + Random.nextFloat() * 12f,
                color = colors.random(),
                wobble = Random.nextFloat() * 2f - 1f
            )
        }
    }
    
    // Animation progress (0 to 1)
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(isActive) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        particles.forEach { particle ->
            // Υπολογισμός θέσης βάσει progress
            val yProgress = progress.value * particle.speed * 1.5f
            val y = -50f + (height + 100f) * yProgress
            val x = particle.x * width + 
                    kotlin.math.sin(yProgress * 10f + particle.wobble * 5f) * 30f
            
            // Fade out στο τέλος
            val alpha = if (progress.value > 0.7f) {
                1f - ((progress.value - 0.7f) / 0.3f)
            } else 1f
            
            // Σχεδίαση confetti
            if (y in -50f..height + 50f) {
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}
