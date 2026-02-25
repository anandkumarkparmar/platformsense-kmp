# PlatformSense Sample

Sample app (Android + iOS) demonstrating PlatformSense library usage. Uses the library via project dependencies.

## Run — Android

From the repository root:

```bash
./gradlew :sample:androidApp:installDebug
```

## Run — iOS

1. Build the shared framework first:

```bash
./gradlew :sample:commonApp:assembleDebugXCFramework
# or for the simulator:
./gradlew :sample:commonApp:iosSimulatorArm64Binaries
```

2. Open the Xcode project:

```bash
open sample/iosApp/iosApp.xcodeproj
```

3. Select an iPhone Simulator and press **Run** (⌘R).

## Build (all modules)

From the repository root:

```bash
./gradlew build
```

This builds the library modules and the sample. Lint (ktlint, detekt) runs on all modules including the sample.

## Architecture

- `commonApp/` – KMP + Compose Multiplatform shared UI (targets Android + iOS)
- `androidApp/` – Android application entry point
- `iosApp/` – iOS application entry point (Xcode project)

## Features Demonstrated

- Network state detection (WiFi, Cellular, Metered)
- Power state monitoring (Normal, Low Power, Charging)
- Biometric capability detection
- Device class identification
- Locale and timezone information
- Reactive updates via Flow
