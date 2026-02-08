# PlatformSense Sample

Sample Android app demonstrating PlatformSense library usage. It is part of the main project and uses the library via project dependencies.

## Run

From the repository root:

```bash
./gradlew :sample:androidApp:installDebug
```

## Build

From the repository root:

```bash
./gradlew build
```

This builds the library modules and the sample. Lint (ktlint, detekt) runs on all modules including the sample.

## Architecture

- `commonApp/` – KMP + Compose Multiplatform shared UI
- `androidApp/` – Android application entry point

## Features Demonstrated

- Network state detection (WiFi, Cellular, Metered)
- Power state monitoring (Normal, Low Power, Charging)
- Biometric capability detection
- Device class identification
- Locale and timezone information
- Reactive updates via Flow
