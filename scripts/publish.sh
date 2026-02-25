#!/usr/bin/env bash
# publish.sh — Publish PlatformSense KMP library modules to Maven Local
#
# Mirrors the jitpack.yml install step for local testing.
# Use this to verify that all library modules publish correctly before tagging a release.
#
# Usage:
#   ./scripts/publish.sh
#
# After running, add mavenLocal() to your test project's repositories block and use:
#   implementation("io.github.anandkumarkparmar:platformsense-core:<version>")

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

# Read version from libs.versions.toml
VERSION=$(grep '^platformsense' gradle/libs.versions.toml | head -1 | sed 's/.*= *"\(.*\)".*/\1/')

echo "==> Publishing PlatformSense KMP v${VERSION} to Maven Local..."
echo ""

echo "==> [1/4] Publishing platformsense-core..."
./gradlew :platformsense-core:publishToMavenLocal
echo "    ✓ platformsense-core published"

echo ""
echo "==> [2/4] Publishing platformsense-android..."
./gradlew :platformsense-android:publishToMavenLocal
echo "    ✓ platformsense-android published"

echo ""
echo "==> [3/4] Publishing platformsense-ios..."
./gradlew :platformsense-ios:publishToMavenLocal
echo "    ✓ platformsense-ios published"

echo ""
echo "==> [4/4] Publishing platformsense-testing..."
./gradlew :platformsense-testing:publishToMavenLocal
echo "    ✓ platformsense-testing published"

MAVEN_LOCAL="${HOME}/.m2/repository/io/github/anandkumarkparmar"
echo ""
echo "All modules published to Maven Local."
echo "Location: ${MAVEN_LOCAL}"
echo ""
echo "Group ID: io.github.anandkumarkparmar"
echo "The same coordinates work for both Maven Local and JitPack — only the repository URL differs."
echo ""
echo "Maven Local (for local testing):"
echo "  repositories { mavenLocal() }"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-core:${VERSION}\")"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-android:${VERSION}\")"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-ios:${VERSION}\")"
echo "  testImplementation(\"io.github.anandkumarkparmar:platformsense-testing:${VERSION}\")"
echo ""
echo "JitPack (after tagging v${VERSION} and pushing):"
echo "  repositories { maven(\"https://jitpack.io\") }"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-core:${VERSION}\")"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-android:${VERSION}\")"
echo "  implementation(\"io.github.anandkumarkparmar:platformsense-ios:${VERSION}\")"
echo "  testImplementation(\"io.github.anandkumarkparmar:platformsense-testing:${VERSION}\")"
