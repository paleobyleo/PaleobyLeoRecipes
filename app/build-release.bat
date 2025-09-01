@echo off
echo Building release APK for PaleoRecipes...

call ./gradlew clean
call ./gradlew assembleRelease

echo.
echo Copying APK to Downloads folder...
mkdir "%USERPROFILE%\Downloads\PaleoRecipes" 2>nul
copy "app\build\outputs\apk\release\app-release.apk" "%USERPROFILE%\Downloads\PaleoRecipes\PaleoRecipes.apk"

echo.
echo Release APK created successfully!
echo Location: %USERPROFILE%\Downloads\PaleoRecipes\PaleoRecipes.apk
echo.
echo You can now install this APK on other devices or transfer it to your SD card.
pause