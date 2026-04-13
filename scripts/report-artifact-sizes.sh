#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
GROUP_PATH="${HOME}/.m2/repository/io/github/anandkumarkparmar"
REPORT_PATH="${ROOT_DIR}/.github/artifact-size-report.md"
BADGE_DIR="${ROOT_DIR}/.github/badges"
SUMMARY_ENV_PATH="${ROOT_DIR}/.github/artifact-size-summary.env"

ENFORCE_BUDGETS=false
if [[ "${1:-}" == "--enforce" ]]; then
  ENFORCE_BUDGETS=true
fi

MAX_TOTAL_PUBLISHED_BYTES="${MAX_TOTAL_PUBLISHED_BYTES:-5242880}"

file_size_bytes() {
  stat -f%z "$1" 2>/dev/null || stat -c%s "$1"
}

to_kb() {
  awk "BEGIN {printf \"%.2f\", $1/1024}"
}

smart_size() {
  local bytes=$1
  if (( bytes >= 1048576 )); then
    awk "BEGIN {printf \"%.2f MB\", $bytes/1048576}"
  else
    awk "BEGIN {printf \"%.2f KB\", $bytes/1024}"
  fi
}

infer_platform() {
  local artifact_name="$1"
  local file_name="$2"
  local normalized
  normalized="$(printf '%s %s' "$artifact_name" "$file_name" | tr '[:upper:]' '[:lower:]')"

  if [[ "$normalized" == *"android"* ]] || [[ "$file_name" == *.aar ]]; then
    echo "Android"
  elif [[ "$normalized" == *"iossimulatorarm64"* ]]; then
    echo "iOS Simulator (arm64)"
  elif [[ "$normalized" == *"iosx64"* ]]; then
    echo "iOS Simulator (x64)"
  elif [[ "$normalized" == *"iosarm64"* ]]; then
    echo "iOS Device (arm64)"
  elif [[ "$normalized" == *"ios"* ]]; then
    echo "iOS"
  elif [[ "$normalized" == *"metadata"* ]]; then
    echo "Common Metadata"
  elif [[ "$normalized" == *"kotlinmultiplatform"* ]]; then
    echo "Multiplatform Root"
  elif [[ "$normalized" == *"testing"* ]]; then
    echo "Testing"
  else
    echo "Core"
  fi
}

mkdir -p "${BADGE_DIR}"

if [[ ! -d "${GROUP_PATH}" ]]; then
  echo "Maven local group path not found: ${GROUP_PATH}" >&2
  echo "Run ./scripts/publish.sh first." >&2
  exit 1
fi

declare -a rows

total_bytes=0
core_published_bytes=0
android_published_bytes=0
ios_published_bytes=0
testing_published_bytes=0

while IFS= read -r artifact_dir; do
  artifact_name="$(basename "$artifact_dir")"
  version_dir="$(find "$artifact_dir" -mindepth 1 -maxdepth 1 -type d | sort -V | tail -n 1)"

  if [[ -z "${version_dir}" ]]; then
    continue
  fi

  while IFS= read -r artifact_file; do
    file_name="$(basename "$artifact_file")"

    if [[ "$file_name" == *"-sources."* ]] || [[ "$file_name" == *"-javadoc."* ]]; then
      continue
    fi

    size_bytes="$(file_size_bytes "$artifact_file")"
    platform="$(infer_platform "$artifact_name" "$file_name")"
    size_kb="$(to_kb "$size_bytes")"

    rows+=("| ${platform} | ${artifact_name} | ${file_name} | ${size_bytes} | ${size_kb} KB |")
    total_bytes=$((total_bytes + size_bytes))

    case "$artifact_name" in
      *platformsense-core*)    core_published_bytes=$((core_published_bytes + size_bytes)) ;;
      *platformsense-android*) android_published_bytes=$((android_published_bytes + size_bytes)) ;;
      *platformsense-ios*)     ios_published_bytes=$((ios_published_bytes + size_bytes)) ;;
      *platformsense-testing*) testing_published_bytes=$((testing_published_bytes + size_bytes)) ;;
    esac
  done < <(find "$version_dir" -maxdepth 1 -type f \( -name "*.aar" -o -name "*.jar" -o -name "*.klib" -o -name "*.pom" -o -name "*.module" \) | sort)
done < <(find "$GROUP_PATH" -mindepth 1 -maxdepth 1 -type d -name 'platformsense-*' | sort)

if [[ ${#rows[@]} -eq 0 ]]; then
  echo "No published artifacts were found under ${GROUP_PATH}." >&2
  echo "Run ./scripts/publish.sh first." >&2
  exit 1
fi

core_kb="$(to_kb "$core_published_bytes")"
android_kb="$(to_kb "$android_published_bytes")"
ios_kb="$(to_kb "$ios_published_bytes")"
testing_kb="$(to_kb "$testing_published_bytes")"
total_kb="$(to_kb "$total_bytes")"

{
  echo "## Artifact Size Matrix"
  echo
  echo "Generated on: $(date -u +"%Y-%m-%d %H:%M:%S UTC")"
  echo
  echo "| Platform | Artifact | File | Size (bytes) | Size |"
  echo "|---|---|---|---:|---:|"
  for row in "${rows[@]}"; do
    echo "$row"
  done
  echo "| **Total (all published artifacts)** |  |  | **${total_bytes}** | **$(to_kb "$total_bytes") KB** |"
} > "$REPORT_PATH"

cat > "${BADGE_DIR}/core-published-size.json" <<EOF
{
  "schemaVersion": 1,
  "label": "Core",
  "message": "$(smart_size "$core_published_bytes")",
  "color": "blue"
}
EOF

cat > "${BADGE_DIR}/android-published-size.json" <<EOF
{
  "schemaVersion": 1,
  "label": "Android",
  "message": "$(smart_size "$android_published_bytes")",
  "color": "brightgreen"
}
EOF

cat > "${BADGE_DIR}/ios-published-size.json" <<EOF
{
  "schemaVersion": 1,
  "label": "iOS",
  "message": "$(smart_size "$ios_published_bytes")",
  "color": "lightgrey"
}
EOF

cat > "${BADGE_DIR}/testing-published-size.json" <<EOF
{
  "schemaVersion": 1,
  "label": "Testing",
  "message": "$(smart_size "$testing_published_bytes")",
  "color": "orange"
}
EOF

cat > "${BADGE_DIR}/total-published-size.json" <<EOF
{
  "schemaVersion": 1,
  "label": "Total Published",
  "message": "$(smart_size "$total_bytes")",
  "color": "blue"
}
EOF

{
  echo "CORE_PUBLISHED_BYTES=${core_published_bytes}"
  echo "ANDROID_PUBLISHED_BYTES=${android_published_bytes}"
  echo "IOS_PUBLISHED_BYTES=${ios_published_bytes}"
  echo "TESTING_PUBLISHED_BYTES=${testing_published_bytes}"
  echo "TOTAL_PUBLISHED_BYTES=${total_bytes}"
  echo "CORE_PUBLISHED_KB=${core_kb}"
  echo "ANDROID_PUBLISHED_KB=${android_kb}"
  echo "IOS_PUBLISHED_KB=${ios_kb}"
  echo "TESTING_PUBLISHED_KB=${testing_kb}"
  echo "TOTAL_PUBLISHED_KB=${total_kb}"
} > "$SUMMARY_ENV_PATH"

if [[ "$ENFORCE_BUDGETS" == true ]]; then
  if (( total_bytes > MAX_TOTAL_PUBLISHED_BYTES )); then
    echo "Total published size budget failed: ${total_bytes} > ${MAX_TOTAL_PUBLISHED_BYTES}" >&2
    exit 1
  fi
fi

echo "Artifact size report: ${REPORT_PATH}"
echo "Badge JSON files: ${BADGE_DIR}/"
echo "Summary env file: ${SUMMARY_ENV_PATH}"
echo "Core published: ${core_published_bytes} bytes (${core_kb} KB)"
echo "Android published: ${android_published_bytes} bytes (${android_kb} KB)"
echo "iOS published: ${ios_published_bytes} bytes (${ios_kb} KB)"
echo "Testing published: ${testing_published_bytes} bytes (${testing_kb} KB)"
echo "Total published artifacts: ${total_bytes} bytes (${total_kb} KB)"
