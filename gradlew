#!/usr/bin/env sh
set -e
VERSION="8.13"
BASE="${HOME}/.gradle/signal555"
DIST="${BASE}/gradle-${VERSION}"
ZIP="${BASE}/gradle-${VERSION}-bin.zip"
URL="https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip"

if [ ! -x "${DIST}/bin/gradle" ]; then
  mkdir -p "${BASE}"
  echo "Downloading Gradle ${VERSION}..."
  if command -v curl >/dev/null 2>&1; then
    curl -L "${URL}" -o "${ZIP}"
  elif command -v wget >/dev/null 2>&1; then
    wget "${URL}" -O "${ZIP}"
  else
    echo "curl or wget is required to bootstrap Gradle." >&2
    exit 1
  fi
  unzip -q -o "${ZIP}" -d "${BASE}"
fi

exec "${DIST}/bin/gradle" "$@"
