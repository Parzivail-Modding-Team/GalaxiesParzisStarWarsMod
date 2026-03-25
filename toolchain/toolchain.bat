@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "BIN_DIR=%SCRIPT_DIR%bin"
set "JAR_PATH="

cd /d "%SCRIPT_DIR%"

if defined TOOLCHAIN_JAR (
	set "JAR_PATH=%TOOLCHAIN_JAR%"
) else if defined TOOLCHAIN_VERSION (
	set "JAR_PATH=%BIN_DIR%\toolchain-%TOOLCHAIN_VERSION%.jar"
) else (
	for /f "delims=" %%I in ('dir /b /a:-d /o-d "%BIN_DIR%\toolchain-*.jar" 2^>nul') do (
		if not defined JAR_PATH set "JAR_PATH=%BIN_DIR%\%%I"
	)
)

if not defined JAR_PATH (
	echo Could not find a toolchain jar in %BIN_DIR%.
	echo Build one with: gradlew.bat toolchainJar
	exit /b 1
)

if not exist "%JAR_PATH%" (
	echo Could not find a toolchain jar at %JAR_PATH%.
	echo Build one with: gradlew.bat toolchainJar
	exit /b 1
)

java %TOOLCHAIN_JAVA_OPTS% -jar "%JAR_PATH%" %*
