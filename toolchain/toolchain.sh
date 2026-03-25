#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN_DIR="$SCRIPT_DIR/bin"
cd "$SCRIPT_DIR"

if [ -n "${TOOLCHAIN_JAR:-}" ]; then
	JAR_PATH="$TOOLCHAIN_JAR"
elif [ -n "${TOOLCHAIN_VERSION:-}" ]; then
	JAR_PATH="$BIN_DIR/toolchain-${TOOLCHAIN_VERSION}.jar"
else
	JAR_PATH="$(find "$BIN_DIR" -maxdepth 1 -type f -name 'toolchain-*.jar' -printf '%T@\t%p\n' 2>/dev/null | sort -nr | head -n 1 | cut -f2-)"
fi

if [ -z "${JAR_PATH:-}" ] || [ ! -f "$JAR_PATH" ]; then
	echo "Could not find a toolchain jar in $BIN_DIR." >&2
	echo "Build one with: ./gradlew toolchainJar" >&2
	exit 1
fi

exec java ${TOOLCHAIN_JAVA_OPTS:-} -jar "$JAR_PATH" "$@"
