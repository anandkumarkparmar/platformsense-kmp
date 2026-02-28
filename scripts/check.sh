#!/usr/bin/env bash
# check.sh — Local CI mirror for PlatformSense KMP
#
# Mirrors what ci.yml does on GitHub Actions. Run this before opening a PR or merging to main.
#
# Scope: ALL modules including sample/commonApp (iOS framework targets).
# The sample is intentionally included — it is the only end-to-end integration test
# that verifies platformsense-ios compiles and links correctly as a consumed dependency.
# Requires macOS (Kotlin/Native iOS compilation).
#
# Usage:
#   ./scripts/check.sh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "==> [1/3] Running ktlint..."
./gradlew ktlintCheck
echo "    ✓ ktlint passed"

echo ""
echo "==> [2/3] Running detekt..."
./gradlew detekt
echo "    ✓ detekt passed"

echo ""
echo "==> [3/3] Building and running tests..."
./gradlew build
echo "    ✓ build & tests passed"

echo ""
echo "All checks passed. Ready to push."
