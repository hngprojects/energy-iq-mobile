# EnergyIQ Mobile

EnergyIQ Mobile is a Kotlin Multiplatform app built with Compose Multiplatform for the EnergyIQ ecosystem. The shared application code lives in `composeApp/` and is currently wired for Android and iOS, with desktop and wasm targets available for local development and UI testing.

## Overview

- Shared UI and business logic in Kotlin Multiplatform
- Android app module in `androidApp/`
- iOS wrapper project in `iosApp/`
- Compose Desktop and wasm targets for faster iteration outside mobile devices
- Ktor networking, Koin dependency injection, Room 3, and DataStore-backed preferences

## Project Structure

```text
energy-iq-mobile/
|- androidApp/                   Android application entry point
|- composeApp/                   Shared KMP code
|  |- src/commonMain/            Shared UI, navigation, data, and feature logic
|  |- src/androidMain/           Android-specific implementations
|  |- src/iosMain/               iOS-specific implementations
|  |- src/desktopMain/           Desktop-specific implementations
|  `- src/wasmJsMain/            Browser preview target
|- iosApp/                       Xcode project and SwiftUI host
|- gradle/                       Gradle version catalog and wrapper config
`- README.md
```

## Current App Flow

The app currently includes these main user flows:

- Onboarding: a 3-screen intro experience shown before authentication is completed
- Authentication: login, registration, forgot password, reset password, and Google sign-in handoff
- Inverter setup: users choose an inverter type, provide connection details, and complete initial setup
- Home: post-auth landing screen

The inverter setup flow currently includes options for:

- Victron
- Luminous
- Growatt
- Su-kam
- Sunsynk
- Others

## Local Configuration

Important local configuration keys are stored in `local.properties`.

`composeApp/build.gradle.kts` reads:

- `BASE_URL` from `local.properties`

## Getting Started

### Prerequisites

- JDK 17
- Android Studio for Android development
- Xcode for iOS development on macOS

### Install dependencies

Gradle dependencies are resolved automatically through the wrapper:

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

## Running the App

### Android

Build a debug APK:

```bash
./gradlew :androidApp:assembleDebug
```

Install on a connected device or emulator:

```bash
./gradlew :androidApp:installDebug
```

You can also open the project in Android Studio and run the `androidApp` configuration directly.

### iOS

Open the Xcode project:

```text
iosApp/iosApp.xcodeproj
```

Then run the `iosApp` target on a simulator or device from Xcode.

If you want Gradle to build the iOS framework manually, a useful task is:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Desktop

Run the desktop target:

```bash
./gradlew :composeApp:desktopRun
```

### Web Preview

This repository also includes a wasm target for browser-based previews and development:

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

This is helpful for shared UI iteration, but the project itself is primarily a mobile KMP app.

## Useful Gradle Tasks

```text
:androidApp:assembleDebug                     Build Android debug APK
:androidApp:installDebug                      Install Android debug build
:composeApp:desktopRun                        Run desktop app
:composeApp:wasmJsBrowserDevelopmentRun       Run wasm dev server
:composeApp:allTests                          Run shared test suite
:composeApp:build                             Build shared module
build                                         Build the full project
```

## Architecture

The shared module follows a feature-oriented structure with platform-specific implementations where needed.

Typical feature layout:

```text
composeApp/src/commonMain/kotlin/com/hng14/energyiq/features/
`- feature-name/
   |- data/
   |- di/
   |- domain/
   |- presentation/
   `- FeatureContract.kt
```

Important shared areas:

- `core/di/` for dependency wiring
- `core/navigation/` for app destinations and navigation flow
- `core/network/` for HTTP client setup and API configuration
- `core/storage/` for preference storage abstractions
- `core/database/` for Room database setup

## Authentication Notes

Authentication requests are implemented in the shared Ktor client under:

- `composeApp/src/commonMain/kotlin/com/hng14/energyiq/features/auth/data/remote/AuthApi.kt`

The Google sign-in button currently opens:

```text
{BASE_URL}/auth/google
```

That means your backend must handle the OAuth flow and redirect the user back into the app experience as appropriate for your platform.

## Testing

Run all shared tests:

```bash
./gradlew :composeApp:allTests
```

Run a full project build:

```bash
./gradlew build
```

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for contribution guidelines.
