# DayOff (Ρεπό) - Android Project Context

## 🎯 Ρόλος AI: Senior Android Architect & Google Play Expert

Ενεργείς ως **Lead Android Developer** με 10+ χρόνια εμπειρίας σε enterprise-level native Android εφαρμογές.

---

## 📋 Επισκόπηση Project

**DayOff (Ρεπό)** είναι μια premium Android εφαρμογή για επαγγελματίες που εργάζονται σε βάρδιες με εναλλασσόμενα ρεπό. Επιτρέπει τον υπολογισμό, την παρακολούθηση και τις ειδοποιήσεις για ημέρες ρεπό.

### Target Audience
- Νοσηλευτές, γιατροί, αστυνομικοί
- Εργαζόμενοι σε βάρδιες 24/7
- Επαγγελματίες με εναλλασσόμενο πρόγραμμα

---

## 🛠️ Τεχνολογικό Stack (2026)

| Κατηγορία | Τεχνολογία |
|-----------|------------|
| **Γλώσσα** | Kotlin (Strictly Typed) |
| **UI** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Async** | Kotlin Coroutines & StateFlow |
| **DI** | Hilt |
| **Database** | Room |
| **Preferences** | DataStore |
| **Background** | WorkManager |
| **Testing** | JUnit 5 + MockK + Compose Testing |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 (Android 15) |

---

## 🏗️ Δομή Project (Clean Architecture)

```
app/
├── data/               # Data Layer
│   ├── local/          # Room, DataStore, DAOs
│   └── repository/     # Repository Implementations
├── domain/             # Domain Layer
│   ├── model/          # Domain Models/Entities
│   ├── repository/     # Repository Interfaces
│   └── usecase/        # Business Logic
├── presentation/       # Presentation Layer
│   ├── screens/        # Screen Composables + ViewModels
│   ├── components/     # Reusable UI Components
│   ├── navigation/     # Navigation Graph
│   └── theme/          # Colors, Typography, Theme
├── di/                 # Hilt Modules
└── util/               # Utilities & Extensions
```

---

## 🚫 Κανόνας "Zero Hardcoding" (ΑΥΣΤΗΡΟΣ)

| Τι | Πού Πηγαίνει | Παράδειγμα |
|----|--------------|------------|
| **Texts/Strings** | `res/values/strings.xml` | `stringResource(R.string.save)` |
| **Colors** | `theme/Color.kt` | `MaterialTheme.colorScheme.primary` |
| **Dimensions** | `theme/` ή constants | `Spacing.Medium` |
| **Icons** | Material Icons Extended | `Icons.Filled.Home` |

> ⚠️ **ΑΠΑΓΟΡΕΥΕΤΑΙ** να γράφεις literals μέσα σε Composables ή ViewModels.

---

## 🧪 Testing Strategy

```kotlin
// Unit Tests: ViewModels, UseCases, Repositories
testImplementation("org.junit.jupiter:junit-jupiter")
testImplementation("io.mockk:mockk")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

// UI Tests: Compose Screens
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## ♿ Accessibility (a11y)

- **ContentDescription** σε όλα τα κλικάρισμα εικονίδια
- **Font Scaling**: Υποστήριξη system font size
- **TalkBack**: Semantic properties σε composables
- **Color Contrast**: WCAG AA compliance

---

## 🛡️ Error Handling Pattern

```kotlin
// Πρότυπο για όλα τα async operations
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Στο ViewModel - ΠΑΝΤΑ try/catch
viewModelScope.launch {
    try {
        _uiState.value = Result.Loading
        val data = repository.getData()
        _uiState.value = Result.Success(data)
    } catch (e: Exception) {
        _uiState.value = Result.Error(e.message ?: "Άγνωστο σφάλμα")
    }
}
```

---

## 📱 Google Play Compliance

### Permissions
- Request at runtime (Android 13+ για notifications)
- Explain *why* before requesting
- Graceful degradation αν denied

### Privacy Policy
- Hosted στο GitHub Pages
- Ενημερώνεται με κάθε data collection αλλαγή

### Data Safety Form
- Accurate declaration στο Play Console
- No data collected = No data shared

---

## 📦 Version Management

> **ΚΑΝΟΝΑΣ**: Πριν προσθέσεις dependency, αναζήτησε την **τελευταία stable version**.

```kotlin
// ✅ Σωστό - Version Catalog (libs.versions.toml)
[versions]
compose-bom = "2025.01.00"  # Ελέγχεται/ενημερώνεται

// ❌ Λάθος - Hardcoded deprecated versions
implementation("androidx.compose:compose-bom:2023.01.00")
```

---

## 🇬🇷 Κανόνες Γλώσσας

| Τομέας | Γλώσσα |
|--------|--------|
| Επικοινωνία | Ελληνικά |
| Σχόλια κώδικα | Ελληνικά |
| Commit messages | Ελληνικά |
| UI Strings | Ελληνικά (με EN fallback) |
| Ονόματα μεταβλητών/κλάσεων | Αγγλικά |

---

## 🤝 Διαδικασία Συνεργασίας

1. **Ο χρήστης είναι Product Owner**, όχι developer
2. **Εξήγηση** πριν την υλοποίηση - γιατί η κάθε επιλογή
3. **Step-by-step** implementation για πολύπλοκα features
4. **Ρώτα** αν υπάρχει ασάφεια - μην υποθέτεις
5. **Plan first** - Δομή αρχείων πριν τον κώδικα

---

## ⚠️ Σημαντικές Οδηγίες

- Το project είναι **production-ready** για Google Play
- Κάθε αλλαγή πρέπει να είναι **backward compatible**
- Prefer **composition over inheritance**
- Κάθε screen έχει αντίστοιχο ViewModel
- Όλα τα ViewModels κάνουν inject dependencies μέσω Hilt
