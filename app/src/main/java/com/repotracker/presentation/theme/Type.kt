package com.repotracker.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography configuration για Material 3.
 * Χρησιμοποιεί το default font για καλύτερη ελληνική υποστήριξη.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Δημιουργεί Typography με scale factor για accessibility.
 * @param scale Ο πολλαπλασιαστής (1.0f = κανονικό, 1.15f = μεγάλο, 1.3f = πολύ μεγάλο)
 */
fun scaledTypography(scale: Float): Typography {
    // Αν scale = 1.0f, επιστρέφει την κανονική Typography
    if (scale == 1.0f) return Typography
    
    return Typography(
        displayLarge = Typography.displayLarge.copy(fontSize = (57 * scale).sp, lineHeight = (64 * scale).sp),
        displayMedium = Typography.displayMedium.copy(fontSize = (45 * scale).sp, lineHeight = (52 * scale).sp),
        displaySmall = Typography.displaySmall.copy(fontSize = (36 * scale).sp, lineHeight = (44 * scale).sp),
        headlineLarge = Typography.headlineLarge.copy(fontSize = (32 * scale).sp, lineHeight = (40 * scale).sp),
        headlineMedium = Typography.headlineMedium.copy(fontSize = (28 * scale).sp, lineHeight = (36 * scale).sp),
        headlineSmall = Typography.headlineSmall.copy(fontSize = (24 * scale).sp, lineHeight = (32 * scale).sp),
        titleLarge = Typography.titleLarge.copy(fontSize = (22 * scale).sp, lineHeight = (28 * scale).sp),
        titleMedium = Typography.titleMedium.copy(fontSize = (16 * scale).sp, lineHeight = (24 * scale).sp),
        titleSmall = Typography.titleSmall.copy(fontSize = (14 * scale).sp, lineHeight = (20 * scale).sp),
        bodyLarge = Typography.bodyLarge.copy(fontSize = (16 * scale).sp, lineHeight = (24 * scale).sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = (14 * scale).sp, lineHeight = (20 * scale).sp),
        bodySmall = Typography.bodySmall.copy(fontSize = (12 * scale).sp, lineHeight = (16 * scale).sp),
        labelLarge = Typography.labelLarge.copy(fontSize = (14 * scale).sp, lineHeight = (20 * scale).sp),
        labelMedium = Typography.labelMedium.copy(fontSize = (12 * scale).sp, lineHeight = (16 * scale).sp),
        labelSmall = Typography.labelSmall.copy(fontSize = (11 * scale).sp, lineHeight = (16 * scale).sp)
    )
}
