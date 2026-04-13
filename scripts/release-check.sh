#!/usr/bin/env bash
# release-check.sh — Run before every release to validate full build & tests
# Usage: ./scripts/release-check.sh [--skip-ios] [--skip-samples]
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT_DIR"

# ── Flags ──────────────────────────────────────────────────────────────────────
SKIP_IOS=false
SKIP_SAMPLES=false
for arg in "$@"; do
  case $arg in
    --skip-ios)     SKIP_IOS=true ;;
    --skip-samples) SKIP_SAMPLES=true ;;
  esac
done

# ── Helpers ────────────────────────────────────────────────────────────────────
PASS=0; FAIL=0; SKIP=0
declare -a RESULTS=()

run_step() {
  local label="$1"; shift
  local cmd=("$@")
  printf "\n\033[1;34m▶ %s\033[0m\n" "$label"
  if "${cmd[@]}"; then
    printf "\033[1;32m✔ %s\033[0m\n" "$label"
    RESULTS+=("PASS | $label")
    ((PASS++)) || true
  else
    printf "\033[1;31m✘ %s\033[0m\n" "$label"
    RESULTS+=("FAIL | $label")
    ((FAIL++)) || true
  fi
}

skip_step() {
  local label="$1"
  printf "\033[1;33m⏭  %s (skipped)\033[0m\n" "$label"
  RESULTS+=("SKIP | $label")
  ((SKIP++)) || true
}

GW="./gradlew"

# ── 1. Gradle wrapper validation ───────────────────────────────────────────────
run_step "Gradle wrapper integrity" \
  bash -c "grep -q 'distributionSha256Sum' gradle/wrapper/gradle-wrapper.properties && echo 'sha256 present'"

# ── 2. Configuration check (no compilation) ────────────────────────────────────
run_step "Gradle configuration" \
  $GW help --quiet

# ── 3. ktlint ──────────────────────────────────────────────────────────────────
run_step "ktlint" \
  $GW ktlintCheck --stacktrace

# ── 4. detekt ──────────────────────────────────────────────────────────────────
run_step "detekt" \
  $GW detekt --stacktrace

# ── 5. API compatibility ───────────────────────────────────────────────────────
run_step "API compatibility (apiCheck)" \
  $GW apiCheck --stacktrace

# ── 6. Library — Android ──────────────────────────────────────────────────────
run_step "Library: build Android (core + android + testing)" \
  $GW :platformsense-core:assemble \
      :platformsense-android:assemble \
      :platformsense-testing:assemble \
      --stacktrace

# ── 7. Library — iOS ──────────────────────────────────────────────────────────
if $SKIP_IOS; then
  skip_step "Library: build iOS (--skip-ios)"
else
  run_step "Library: build iOS" \
    $GW :platformsense-ios:build --stacktrace --max-workers=2
fi

# ── 8. Tests ───────────────────────────────────────────────────────────────────
run_step "Tests: core + testing" \
  $GW :platformsense-core:allTests \
      :platformsense-testing:allTests \
      --stacktrace --max-workers=2

# ── 9. Maven Local publication ─────────────────────────────────────────────────
run_step "Publish to Maven Local" \
  $GW :platformsense-core:publishToMavenLocal \
      :platformsense-android:publishToMavenLocal \
      :platformsense-ios:publishToMavenLocal \
      :platformsense-testing:publishToMavenLocal \
      --stacktrace

run_step "Maven local: verify artifacts exist" \
  bash -c "ls ~/.m2/repository/io/github/anandkumarkparmar/platformsense-core/ 2>/dev/null | grep -q . && echo 'artifacts found'"

# ── 10. Sample apps ────────────────────────────────────────────────────────────
if $SKIP_SAMPLES; then
  skip_step "Sample: Android debug APK (--skip-samples)"
else
  run_step "Sample: Android debug APK" \
    $GW :sample:androidApp:assembleDebug --stacktrace

  if ! $SKIP_IOS; then
    run_step "Sample: iOS framework" \
      $GW :sample:commonApp:linkDebugFrameworkIosSimulatorArm64 --stacktrace
  else
    skip_step "Sample: iOS framework (--skip-ios)"
  fi
fi

# ── 11. Artifact size check ───────────────────────────────────────────────────
if [[ -f "scripts/report-artifact-sizes.sh" ]]; then
  run_step "Artifact size report" \
    bash -c "chmod +x scripts/report-artifact-sizes.sh && ./scripts/report-artifact-sizes.sh"
else
  skip_step "Artifact size report (script not found)"
fi

# ── Summary ───────────────────────────────────────────────────────────────────
printf "\n\033[1m══════════════════════════════════════════\033[0m\n"
printf "\033[1m  RELEASE CHECK SUMMARY\033[0m\n"
printf "\033[1m══════════════════════════════════════════\033[0m\n"
for r in "${RESULTS[@]}"; do
  status="${r%% |*}"
  label="${r#* | }"
  case $status in
    PASS) printf "  \033[32m✔ PASS\033[0m  %s\n" "$label" ;;
    FAIL) printf "  \033[31m✘ FAIL\033[0m  %s\n" "$label" ;;
    SKIP) printf "  \033[33m⏭ SKIP\033[0m  %s\n" "$label" ;;
  esac
done
printf "\033[1m══════════════════════════════════════════\033[0m\n"
printf "  Passed: \033[32m%d\033[0m  Failed: \033[31m%d\033[0m  Skipped: \033[33m%d\033[0m\n" \
  "$PASS" "$FAIL" "$SKIP"
printf "\033[1m══════════════════════════════════════════\033[0m\n\n"

if [[ $FAIL -gt 0 ]]; then
  printf "\033[1;31mRELEASE CHECK FAILED — fix the items above before releasing.\033[0m\n\n"
  exit 1
else
  printf "\033[1;32mRELEASE CHECK PASSED — ready to release!\033[0m\n\n"
  exit 0
fi
