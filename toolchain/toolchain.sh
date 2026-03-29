#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN_DIR="$SCRIPT_DIR/bin"
CONFIG_FILE="$SCRIPT_DIR/toolchain.properties"
TOOLCHAIN_VERSION_VALUE="${TOOLCHAIN_VERSION:-}"
TOOLCHAIN_REPOSITORY="${TOOLCHAIN_REPOSITORY:-}"
JAR_PATH="${TOOLCHAIN_JAR:-}"
cd "$SCRIPT_DIR"

trim() {
	local value="$1"
	value="${value#"${value%%[![:space:]]*}"}"
	value="${value%"${value##*[![:space:]]}"}"
	printf '%s' "$value"
}

read_toolchain_properties() {
	[ -f "$CONFIG_FILE" ] || return 0

	while IFS= read -r line || [ -n "$line" ]; do
		line="${line%$'\r'}"

		case "$line" in
			""|\#*)
				continue
				;;
			*=*)
				local key
				local value
				key="$(trim "${line%%=*}")"
				value="$(trim "${line#*=}")"

				case "$key" in
					version)
						if [ -z "$TOOLCHAIN_VERSION_VALUE" ]; then
							TOOLCHAIN_VERSION_VALUE="$value"
						fi
						;;
					repository)
						if [ -z "$TOOLCHAIN_REPOSITORY" ]; then
							TOOLCHAIN_REPOSITORY="$value"
						fi
						;;
				esac
				;;
		esac
	done < "$CONFIG_FILE"
}

download_toolchain_jar() {
	local version="$1"
	local repository="$2"
	local target="$BIN_DIR/toolchain-${version}.jar"
	local url="https://github.com/${repository}/releases/download/${version}/toolchain-${version}.jar"

	if ! command -v wget >/dev/null 2>&1; then
		echo "wget is required to download toolchain-${version}.jar when the local jar is missing." >&2
		exit 1
	fi

	mkdir -p "$BIN_DIR"

	if ! wget --quiet --output-document "$target" "$url"; then
		rm -f "$target"
		echo "Failed to download toolchain jar from $url" >&2
		exit 1
	fi

	JAR_PATH="$target"
}

resolve_latest_local_jar() {
	find "$BIN_DIR" -maxdepth 1 -type f -name 'toolchain-*.jar' -printf '%T@\t%p\n' 2>/dev/null | sort -nr | head -n 1 | cut -f2-
}

read_toolchain_properties

if [ -n "$JAR_PATH" ]; then
	if [ ! -f "$JAR_PATH" ]; then
		echo "Configured toolchain jar was not found at $JAR_PATH." >&2
		exit 1
	fi
elif [ -n "$TOOLCHAIN_VERSION_VALUE" ]; then
	candidate="$BIN_DIR/toolchain-${TOOLCHAIN_VERSION_VALUE}.jar"

	if [ -f "$candidate" ]; then
		JAR_PATH="$candidate"
	elif [ -n "$TOOLCHAIN_REPOSITORY" ]; then
		download_toolchain_jar "$TOOLCHAIN_VERSION_VALUE" "$TOOLCHAIN_REPOSITORY"
	else
		echo "toolchain.properties defines version '$TOOLCHAIN_VERSION_VALUE' but no repository." >&2
		echo "Add 'repository = owner/repo' to enable downloads." >&2
		exit 1
	fi
fi

if [ -z "$JAR_PATH" ]; then
	JAR_PATH="$(resolve_latest_local_jar)"
fi

if [ -z "${JAR_PATH:-}" ] || [ ! -f "$JAR_PATH" ]; then
	echo "Could not find a toolchain jar in $BIN_DIR." >&2
	echo "Build one with: ./gradlew toolchainJar" >&2
	echo "Or create toolchain.properties with version/repository to enable downloads." >&2
	exit 1
fi

if [ -n "${TOOLCHAIN_JAVA_OPTS:-}" ]; then
	# shellcheck disable=SC2206
	TOOLCHAIN_JAVA_OPTS_ARRAY=(${TOOLCHAIN_JAVA_OPTS})
else
	TOOLCHAIN_JAVA_OPTS_ARRAY=()
fi

exec java "${TOOLCHAIN_JAVA_OPTS_ARRAY[@]}" -jar "$JAR_PATH" "$@"
