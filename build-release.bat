@echo off
echo Building release APK for PaleoRecipes...

REM Use the full path to gradlew.bat instead of using ./
call gradlew clean
call gradlew assembleRelease

echo.
echo Copying APK to Downloads folder...
mkdir "%USERPROFILE%\Downloads\PaleoRecipes" 2>nul

REM Check if the APK exists before trying to copy it
if exist "app\build\outputs\apk\release\app-release.apk" (
    copy "app\build\outputs\apk\release\app-release.apk" "%USERPROFILE%\Downloads\PaleoRecipes\PaleoRecipes.apk"
    echo.
    echo Release APK created successfully!
    echo Location: %USERPROFILE%\Downloads\PaleoRecipes\PaleoRecipes.apk
) else (
    echo.
    echo ERROR: APK file not found at app\build\outputs\apk\release\app-release.apk
    echo Build may have failed. Check the build output for errors.
)

echo.
echo You can now install this APK on other devices or transfer it to your SD card.
pause