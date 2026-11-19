@echo off
setlocal enabledelayedexpansion

rem Build Linux release archive via WSL (runs Gradle inside default WSL distro)
set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_DIR=%%~fI"

set "WSL_PROJECT_DIR="
for /f "usebackq delims=" %%I in (`wsl.exe wslpath -a "%PROJECT_DIR%"`) do set "WSL_PROJECT_DIR=%%I"

if not defined WSL_PROJECT_DIR (
  echo [wsl-archive-release-linux] Failed to resolve WSL path for "%PROJECT_DIR%". Ensure WSL is installed and a default distro is available.>&2
  exit /b 1
)

echo [wsl-archive-release-linux] Project: %PROJECT_DIR%
echo [wsl-archive-release-linux] WSL path: %WSL_PROJECT_DIR%
echo [wsl-archive-release-linux] Running: BUILD_TYPE=release ./gradlew :composeApp:generateReleaseArchive

wsl.exe -e bash -lc "cd '%WSL_PROJECT_DIR%' && tr -d '\r' < ./gradlew | BUILD_TYPE=release bash -s -- :composeApp:generateReleaseArchive"
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
  echo [wsl-archive-release-linux] Build failed with exit code %EXIT_CODE%.>&2
)

exit /b %EXIT_CODE%
