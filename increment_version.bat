@echo off
echo Paleo Recipes Version Increment Tool
echo ----------------------------------

if "%1"=="" (
    echo Usage: increment_version.bat [major^|minor^|patch]
    echo Example: increment_version.bat patch
    exit /b 1
)

if "%1"=="major" (
    echo Incrementing major version...
    gradlew incrementMajorVersion
) else if "%1"=="minor" (
    echo Incrementing minor version...
    gradlew incrementMinorVersion
) else if "%1"=="patch" (
    echo Incrementing patch version...
    gradlew incrementPatchVersion
) else (
    echo Invalid version type. Use major, minor, or patch.
    exit /b 1
)

echo Version incremented successfully!
echo Run 'gradlew assembleDebug' or 'gradlew assembleRelease' to build with the new version.