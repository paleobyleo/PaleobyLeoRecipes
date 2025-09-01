@echo off
echo Paleo Recipes - Release Build Script
echo ====================================

REM Check if gradlew exists
if not exist gradlew.bat (
    echo Error: gradlew.bat not found. Please run this script from the project root directory.
    pause
    exit /b 1
)

echo Cleaning project...
call gradlew clean
if %errorlevel% neq 0 (
    echo Error: Clean failed
    pause
    exit /b 1
)

echo Building release APK...
call gradlew assembleRelease
if %errorlevel% neq 0 (
    echo Error: Release build failed
    pause
    exit /b 1
)

echo.
echo Build completed successfully!
echo Release APK can be found in: app\build\outputs\apk\release\
echo.

REM Check if APK exists
if exist app\build\outputs\apk\release\app-release.apk (
    echo Release APK generated: app-release.apk
) else (
    echo Warning: Release APK not found in expected location
)

echo.
echo To install on a connected device, run:
echo adb install app\build\outputs\apk\release\app-release.apk
echo.

pause