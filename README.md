# DayOff / Ρεπό �

**Native Android εφαρμογή για επαγγελματίες διανομείς**

Μάθε πότε έχεις ρεπό και βάρδια με μια ματιά!

---

## ✨ Χαρακτηριστικά

- 📅 **Ημερολόγιο** - Δες τι ισχύει για κάθε ημέρα
- 🔄 **Κυλιόμενο ρεπό** - Αυτόματος υπολογισμός εναλλαγής
- 🌅 **Εναλλαγή βαρδιών** - Πρωινή/Απογευματινή
- 🎊 **Ελληνικές αργίες** - Αυτόματη αναγνώριση (Πάσχα, 25η Μαρτίου, κλπ)
- 📊 **Στατιστικά** - Μηνιαία αναφορά
- 🌍 **Δίγλωσση** - Ελληνικά & Αγγλικά
- 🌙 **Dark Mode** - Υποστήριξη θέματος
- 🔔 **Ειδοποιήσεις** - Υπενθύμιση βάρδιας
- 🔠 **Μέγεθος γραμματοσειράς** - Accessibility επιλογές

---

## 🎯 Νέο στην v2.0!

- ✨ **Welcome Screen** - Animated πρώτη εκκίνηση
- 🎊 **Confetti Celebration** - Εορτασμός μετά το setup
- � **Font Scaling** - Κανονικό/Μεγάλο/Πολύ Μεγάλο

---

## �📱 Εγκατάσταση

### Απαιτήσεις
- Android Studio Arctic Fox ή νεότερο
- Android SDK 35+
- JDK 17+

### Βήματα
1. Άνοιξε το project στο Android Studio
2. Sync Gradle (File → Sync Project with Gradle Files)
3. Πάτα ▶️ Run για να τρέξεις στο κινητό

---

## 🏗️ Αρχιτεκτονική

```
📁 RepoTrackerKotlin/
├── 📁 app/src/main/java/com/repotracker/
│   ├── 📁 domain/          # Μοντέλα & Use Cases
│   │   ├── model/          # WorkSchedule, GreekHolidays
│   │   ├── repository/     # Interfaces
│   │   └── usecase/        # Business Logic
│   ├── 📁 data/            # Room & DataStore
│   │   ├── local/          # Database, DAO, Preferences
│   │   └── repository/     # Implementations
│   ├── 📁 presentation/    # UI Layer
│   │   ├── screens/        # Composables (Welcome, Home, Setup, Settings)
│   │   ├── components/     # Reusable (ConfettiEffect, Calendar)
│   │   ├── navigation/     # NavHost
│   │   └── theme/          # Material 3
│   └── 📁 di/              # Hilt Modules
└── 📁 app/src/main/res/    # Resources
    ├── values/             # English strings
    └── values-el/          # Greek strings
```

---

## 🛠️ Τεχνολογίες

| Τεχνολογία | Χρήση |
|------------|-------|
| Kotlin 2.0 | Γλώσσα |
| Jetpack Compose | UI Framework |
| Material 3 | Design System |
| Hilt | Dependency Injection |
| Room | Local Database |
| DataStore | Preferences |
| Coroutines + Flow | Async |
| WorkManager | Notifications |

---

## 📝 Σημειώσεις

### Αλλαγή Γλώσσας
Η αλλαγή γλώσσας απαιτεί **επανεκκίνηση** της εφαρμογής.

### Αργίες
Οι ελληνικές αργίες (σταθερές + κινητές βάσει Πάσχα) αναγνωρίζονται αυτόματα.

---

## 📄 Άδεια

MIT License - Ελεύθερη χρήση για προσωπικούς σκοπούς.

---

Made with ❤️ in Greece 🇬🇷
