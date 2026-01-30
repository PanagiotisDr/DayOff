// Αρχείο ρυθμίσεων build για το root project
// Ορίζει τα plugins που χρησιμοποιούνται σε όλο το project

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
