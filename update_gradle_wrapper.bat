@echo off
setlocal enabledelayedexpansion

REM Set the desired Gradle version
set GRADLE_VERSION=8.2

REM Download the Gradle wrapper
call gradle wrapper --gradle-version %GRADLE_VERSION% --distribution-type bin

REM Display the updated version
call gradlew --version

pause
