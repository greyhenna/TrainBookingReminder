# 🚆 IRCTC Train Booking Reminder & Calculator

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.2.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-purple.svg)](https://developer.android.com/jetpack/compose)
[![MinSDK](https://img.shields.io/badge/MinSDK-26%20%28Android%208.0%2B%29-orange.svg)](https://developer.android.com)
[![TargetSDK](https://img.shields.io/badge/TargetSDK-37-brightgreen.svg)](https://developer.android.com)

A utility Android application designed for **Indian Railways (IRCTC)** passengers. It automatically calculates exact opening dates and times for **General Quota Advance Reservation Period (ARP)** and **Tatkal Bookings**, schedules high-priority exact alarm notifications with authentic **Indian Railway Station Announcement** ringtones, and integrates booking opening windows directly into your device calendar.

---

## 📅 Core IRCTC Booking Rules (IST Time Zone)

The app standardizes all calculations against **Indian Standard Time (IST - UTC+05:30)**:

| Booking Quota | Advance Reservation Period | Opening Time (IST) | Reminder Alert Time (-15m) |
| :--- | :--- | :--- | :--- |
| **General Quota (ARP)** | **60 Days** before journey | **08:00 AM IST** | **07:45 AM IST** |
| **AC Tatkal Quota** (1A, 2A, 3A, 3E, CC, EC) | **1 Day** before journey | **10:00 AM IST** | **09:45 AM IST** |
| **Non-AC / Sleeper (SL) Tatkal** (SL, 2S) | **1 Day** before journey | **11:00 AM IST** | **10:45 AM IST** |

---

## ✨ Features

- **🌐 Multilingual Support (13 Indian Languages + English)**:
  - English (`en`), Hindi (`hi`), Bengali (`bn`), Telugu (`te`), Marathi (`mr`), Tamil (`ta`), Gujarati (`gu`), Kannada (`kn`), Malayalam (`ml`), Punjabi (`pa`), Odia (`or`), Assamese (`as`), and Urdu (`ur`).
  - Implements Android 13+ per-app `LocaleManager` API with `AppCompatDelegate.setApplicationLocales` fallback for seamless, instant full-app translation.

- **🎨 Dark Mode & Material Design 3 (Material You)**:
  - Dynamic color support (`MaterialTheme`) with `System Default`, `Light Mode`, and `Dark Mode` options.
  - Theme preference persisted asynchronously across app restarts using **Preferences DataStore**.

- **⏰ High-Priority Exact Alarms & Custom Station Announcement Ringtone**:
  - Uses `AlarmManager.setExactAndAllowWhileIdle` to trigger notifications precisely 15 minutes before booking windows open.
  - Plays an authentic **Indian Railway Station Announcement** sound (`notification_tone.mp3`) when alarms trigger.
  - Handles Android 13+ (`POST_NOTIFICATIONS`) and Android 12+ (`SCHEDULE_EXACT_ALARM`) runtime permissions.

- **📅 Device Calendar Integration**:
  - Adds pre-filled booking opening events (`Intent.ACTION_INSERT` with `CalendarContract.Events`) directly to Google Calendar or default device calendar with opening times, descriptions, and IRCTC tips.

- **📱 Interactive Date Picker**:
  - Material 3 `DatePickerDialog` with quick shortcut selection chips (`Tomorrow`, `+7 Days`, `+30 Days`, `+60 Days`).

---

## 🛠 Tech Stack & Architecture

- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **UI Framework**: Jetpack Compose with Material Design 3 Expressive components
- **State Management**: Kotlin `StateFlow` & `MutableStateFlow` with `ViewModel`
- **Persistence**: Preferences DataStore (`androidx.datastore:datastore-preferences`) & `SharedPreferences`
- **Notifications & Alarms**: `AlarmManager`, `NotificationChannel`, `NotificationCompat`, `BroadcastReceiver`
- **Language Management**: Android 13+ `LocaleManager` & `AppCompatDelegate` (`locales_config.xml`)

---

## 📁 Repository Structure

```
TrainBookingReminder/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/trainbookingreminder/
│   │   │   ├── data/
│   │   │   │   ├── model/         # BookingType, BookingInfo, AppLanguage, ThemeMode
│   │   │   │   ├── repository/    # ThemePreferencesRepository (DataStore)
│   │   │   │   └── util/          # IrctcCalculator, CalendarHelper, LanguageHelper
│   │   │   ├── notification/      # AlarmScheduler, BookingReminderReceiver
│   │   │   ├── ui/
│   │   │   │   ├── components/    # DatePickerCard, BookingCard, LanguageThemeSelector
│   │   │   │   ├── screens/       # MainScreen
│   │   │   │   ├── theme/         # Color, Theme, Type
│   │   │   │   └── viewmodel/     # MainViewModel
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── raw/               # notification_tone.mp3 (Railway Station Tone)
│   │   │   ├── values/            # strings.xml (English - Default)
│   │   │   ├── values-hi/         # Hindi
│   │   │   ├── values-bn/         # Bengali
│   │   │   ├── values-te/         # Telugu
│   │   │   ├── values-mr/         # Marathi
│   │   │   ├── values-ta/         # Tamil
│   │   │   ├── values-gu/         # Gujarati
│   │   │   ├── values-kn/         # Kannada
│   │   │   ├── values-ml/         # Malayalam
│   │   │   ├── values-pa/         # Punjabi
│   │   │   ├── values-or/         # Odia
│   │   │   ├── values-as/         # Assamese
│   │   │   ├── values-ur/         # Urdu
│   │   │   └── xml/               # locales_config.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Building & Installing

### Download Signed Release APK
You can download the pre-compiled, signed APK directly from the GitHub Releases page or build it locally using Gradle:

```bash
# Clone the repository
git clone https://github.com/greyhenna/TrainBookingReminder.git
cd TrainBookingReminder

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The APKs will be generated at:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Signed Release APK**: `app/build/outputs/apk/release/TrainBookingReminder-v1.0.apk`

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).
