@echo off
setlocal enabledelayedexpansion

echo Creating GitHub Release for PaleobyLeoRecipes v1.0.2
echo ===================================================

REM Check if required files exist
if not exist "app\build\outputs\apk\release\paleobyleorecipes-1.0.2.apk" (
    echo Error: APK file not found at app\build\outputs\apk\release\paleobyleorecipes-1.0.2.apk
    echo Please build the release APK first using ./gradlew assembleRelease
    pause
    exit /b 1
)

if not exist "RELEASE_NOTES.md" (
    echo Error: RELEASE_NOTES.md not found
    pause
    exit /b 1
)

REM Set variables
set REPO_OWNER=paleobyleo
set REPO_NAME=PaleobyLeoRecipes
set TAG_NAME=v1.0.2
set RELEASE_NAME=Release v1.0.2
set APK_PATH=app\build\outputs\apk\release\paleobyleorecipes-1.0.2.apk

echo.
echo Please create a GitHub Personal Access Token with 'repo' scope at:
echo https://github.com/settings/tokens/new
echo.
echo The token needs permissions to create releases.
echo.

REM Prompt for GitHub token
set /p GITHUB_TOKEN="Enter your GitHub Personal Access Token: "

if "%GITHUB_TOKEN%"=="" (
    echo Error: GitHub token is required
    pause
    exit /b 1
)

echo.
echo Creating release...

REM Create release using curl
curl -X POST ^
  -H "Authorization: token %GITHUB_TOKEN%" ^
  -H "Accept: application/vnd.github.v3+json" ^
  https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/releases ^
  -d "{\"tag_name\":\"%TAG_NAME%\",\"name\":\"%RELEASE_NAME%\",\"body\":\"$(type RELEASE_NOTES.md)\",\"draft\":false,\"prerelease\":false}"

if %errorlevel% neq 0 (
    echo Error: Failed to create release
    pause
    exit /b 1
)

echo Release created successfully!
echo.
echo Now uploading APK asset...

REM Get release ID (this would need to be parsed from the previous response)
echo Please visit https://github.com/%REPO_OWNER%/%REPO_NAME%/releases to upload the APK manually:
echo %CD%\%APK_PATH%

echo.
echo Release process completed!
pause