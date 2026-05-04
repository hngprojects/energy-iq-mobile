# Kotlin Starter

KMP + Compose Multiplatform template with Vertical Slice Architecture for HNG project teams.

**Platforms:** Android · iOS · Desktop (JVM) · Web (wasmJs)

## Quick Start

```bash
# 1. Clone
git clone https://github.com/hngprojects/kotlin-starter.git
cd kotlin-starter

# 2. Rename package (optional)
./rename.sh com.yourcompany.appname

# 3. Replace simulated auth with your real API
# composeApp/src/commonMain/.../features/auth/data/remote/AuthApi.kt

# 4. Build & run
./gradlew :androidApp:assembleDebug          # Android APK
./gradlew :composeApp:wasmJsBrowserRun       # Web (dev server)
```

## Architecture

Vertical Slice — each feature is fully self-contained:

```
features/
  featureName/
    FeatureNameContract.kt   ← public API (callbacks, shared types)
    di/FeatureNameModule.kt  ← Koin DI wiring
    data/                    ← repository, API client, DAO, preferences
    domain/                  ← use cases, domain models
    presentation/            ← screens, viewmodels, UI components
```

### Adding a Feature

1. Create `composeApp/src/commonMain/.../features/<name>/` following the structure above
2. Register the Koin module in `core/di/AppModule.kt`
3. Add a destination to `core/navigation/Destinations.kt`
4. Wire the screen in `core/navigation/AppNavigation.kt`

## Structure

```
kotlin-starter/
├── androidApp/          Android application entry point
├── composeApp/          Shared KMP code (UI + logic + data)
│   └── src/
│       ├── commonMain/  Shared across all platforms
│       ├── androidMain/ Android-specific actuals
│       ├── iosMain/     iOS-specific actuals
│       ├── desktopMain/ JVM desktop actuals
│       └── wasmJsMain/  Web actuals
├── iosApp/              Xcode project (SwiftUI wrapper)
└── rename.sh            Package rename helper
```

## Defaults Included

| Feature    | Details                                                                                                     |
|------------|-------------------------------------------------------------------------------------------------------------|
| Onboarding | 3-page horizontal swipe, skip/complete stored in DataStore                                                  |
| Auth       | Login + Register screens, demo creds `jeffery@logickoder.dev` / `Password1`, swap `AuthApi.kt` for real API |
| Home       | Scaffold + top bar with logout                                                                              |
| Theme      | Material 3, light + dark mode, custom color palette                                                         |
| Navigation | Nav3 with explicit back stack                                                                               |

## Stack

| Layer         | Library                                                                     |
|---------------|-----------------------------------------------------------------------------|
| UI            | Compose Multiplatform 1.10.3                                                |
| DI            | Koin 4.x                                                                    |
| HTTP          | Ktor 3.4.2                                                                  |
| HTTP caching  | RetroStash 0.0.7                                                            |
| Database      | Room 3.x (all platforms)                                                    |
| Preferences   | `PreferenceStore` — DataStore on Android/iOS/Desktop, `localStorage` on Web |
| Navigation    | Navigation 3                                                                |
| Serialization | kotlinx.serialization                                                       |

## iOS Setup

Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

If you renamed the package with `rename.sh`, also update the iOS bundle identifier:

1. Open `iosApp/iosApp.xcodeproj` → select the `iosApp` target
2. **General → Identity → Bundle Identifier** — set to your new package name
3. Optionally update the display name under **General → Display Name**

## License

MIT

---

Made with ❤️ by [Jeffery Orazulike](https://logickoder.dev) and the HNG projects team.
