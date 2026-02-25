# Contributing

How to contribute to PlatformSense KMP — from reporting issues to adding new providers.

---

## Getting Started

### Clone and Build

```bash
git clone https://github.com/anandkumarkparmar/platformsense-kmp.git
cd platformsense-kmp
./gradlew build
```

### Run Quality Checks

```bash
# Full build + lint
./gradlew build

# Detekt (static analysis)
./gradlew detekt

# ktlint (code style)
./gradlew ktlintCheck
```

> [!TIP]
> All three checks run as part of `./gradlew build`, but running them individually helps isolate issues.

---

## Project Structure

```
platformsense-kmp/
├── platformsense-core/          # Domain models, facade, repositories, providers
│   └── src/
│       ├── commonMain/          # Shared code (interfaces, domain, facade)
│       └── iosMain/             # iOS provider implementations
├── platformsense-android/       # Android provider implementations
│   └── src/
│       └── androidMain/
├── platformsense-testing/       # Fake providers, test rule
│   └── src/
│       └── commonMain/
├── sample/                      # Sample app demonstrating usage
│   ├── commonApp/               # Shared Compose UI
│   └── androidApp/              # Android entry point
├── config/                      # Detekt configuration
├── docs/                        # Documentation (you are here!)
└── .github/workflows/           # CI configuration
```

---

## Reporting Issues

### Bug Reports

Open an issue with:

1. **Steps to reproduce** — minimal code or project showing the bug
2. **Expected behavior** — what you expected to happen
3. **Actual behavior** — what actually happened
4. **Environment** — Kotlin version, Gradle version, OS, device/emulator, PlatformSense version

### Feature Requests

Open an issue describing:

1. **Use case** — what you're trying to achieve
2. **Proposed API** — how you'd like it to work (code example)
3. **Alternatives considered** — workarounds you've tried

---

## Submitting Changes

### Workflow

1. **Fork** the repository
2. **Create a branch** from `main`:
   - Features: `feature/your-feature-name`
   - Fixes: `fix/issue-description`
3. **Implement** your change (see guidelines below)
4. **Add or update tests** using the `platformsense-testing` module
5. **Run checks**: `./gradlew build` must pass
6. **Open a Pull Request** against `main` with a clear title and description
7. **Reference issues** — link any related GitHub issues

### PR Guidelines

- Keep changes **small and focused** — one feature or fix per PR
- Write a **clear PR description** explaining the what and why
- Include **before/after** examples if changing behavior
- Ensure **CI passes** before requesting review

---

## Adding a New Provider

Step-by-step guide for adding a new environment or capability signal.

### Step 1: Define the Domain Model

Create a new data class in `platformsense-core/src/commonMain/kotlin/io/platformsense/domain/`:

```kotlin
// Example: AccessibilityInfo.kt
package io.github.anandkumarkparmar.platformsense.domain

data class AccessibilityInfo(
    val isTalkBackEnabled: Boolean = false,
    val fontScale: Float = 1.0f
)
```

### Step 2: Create the Provider Interface

Create the interface in `platformsense-core/src/commonMain/kotlin/io/platformsense/core/provider/`:

```kotlin
// Example: AccessibilityProvider.kt
package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.domain.AccessibilityInfo
import kotlinx.coroutines.flow.Flow

interface AccessibilityProvider {
    fun current(): AccessibilityInfo
    fun flow(): Flow<AccessibilityInfo>
}
```

### Step 3: Add Platform Implementations

**Android** — in `platformsense-android/src/androidMain/`:

```kotlin
class AndroidAccessibilityProvider(
    private val context: Context
) : AccessibilityProvider {
    override fun current(): AccessibilityInfo { /* ... */ }
    override fun flow(): Flow<AccessibilityInfo> { /* ... */ }
}
```

**iOS** — in `platformsense-core/src/iosMain/`:

```kotlin
class IosAccessibilityProvider : AccessibilityProvider {
    override fun current(): AccessibilityInfo { /* ... */ }
    override fun flow(): Flow<AccessibilityInfo> { /* ... */ }
}
```

### Step 4: Create a Fake Provider

In `platformsense-testing/src/commonMain/`:

```kotlin
class FakeAccessibilityProvider : AccessibilityProvider {
    var currentValue: AccessibilityInfo = AccessibilityInfo()
    override fun current(): AccessibilityInfo = currentValue
    override fun flow(): Flow<AccessibilityInfo> = MutableStateFlow(currentValue)
}
```

### Step 5: Wire Into Repositories

Add the provider to `EnvironmentRepository` (or `CapabilitiesRepository`) and update the wiring classes:
- `AndroidPlatformSenseWiring`
- `IosPlatformSenseWiring`
- `FakePlatformSenseWiring`

### Step 6: Update Domain Snapshots

Add the new property to `EnvironmentSnapshot` (or `CapabilitiesSnapshot`):

```kotlin
data class EnvironmentSnapshot(
    // ... existing properties ...
    val accessibilityInfo: AccessibilityInfo = AccessibilityInfo()
)
```

### Step 7: Update Documentation

- Add the new model to `docs/api-reference.md`
- Update the support matrix in `docs/platform-support.md`
- Add usage examples to `docs/use-cases-and-recipes.md`

---

## Adding a New Platform

### Step 1: Create Platform Module (or Source Set)

For platforms that use a separate module (like Android):

```kotlin
// settings.gradle.kts
include(":platformsense-desktop")
```

For platforms within `platformsense-core` (like iOS):
- Add a new source set (e.g. `desktopMain`)

### Step 2: Implement All Providers

Create platform-specific implementations for every provider interface. For unsupported signals, return safe defaults:

```kotlin
class DesktopPowerProvider : PowerProvider {
    override fun current(): PowerInfo = PowerInfo() // UNKNOWN defaults
    override fun flow(): Flow<PowerInfo> = flowOf(current())
}
```

### Step 3: Create Wiring

```kotlin
class DesktopPlatformSenseWiring : PlatformSenseWiring {
    override fun environmentRepository() = EnvironmentRepository(
        networkProvider  = { DesktopNetworkProvider() },
        powerProvider    = { DesktopPowerProvider() },
        // ... all providers ...
    )
    override fun capabilitiesRepository() = CapabilitiesRepository(
        biometricProvider = { DesktopBiometricProvider() },
    )
}
```

### Step 4: Update Documentation

- Update the support matrix in `docs/platform-support.md`
- Add initialization instructions to `docs/getting-started.md`
- Update platform timeline in `docs/roadmap.md`

---

## Code Style

| Rule | Details |
|------|---------|
| **Formatting** | Follow `.editorconfig` in the project root |
| **Static analysis** | Detekt configuration in `config/` directory |
| **Code style** | ktlint enforces standard Kotlin style |
| **Domain purity** | Never import platform types in `io.github.anandkumarkparmar.platformsense.domain` |
| **Immutability** | All domain models use `val` properties only |
| **Defaults** | Every property must have a safe default value |
| **KDoc** | All public classes, interfaces, and functions must have KDoc |

---

## Review Process

What maintainers look for in PRs:

- ✅ Follows the provider pattern
- ✅ Has matching fake provider in the testing module
- ✅ Domain models are immutable with safe defaults
- ✅ No platform types leak into the public API
- ✅ All checks pass (`./gradlew build`)
- ✅ Documentation updated where applicable
- ✅ Tests cover both happy path and edge cases

---

## See Also

- **[Architecture](architecture.md)** — Understand the codebase before contributing
- **[Design Principles](design-principles.md)** — The philosophy to follow
- **[Roadmap](roadmap.md)** — Items looking for contributors
