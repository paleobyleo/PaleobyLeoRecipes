@echo off
echo PaleobyLeoRecipes GitHub Deployment Script
echo ==========================================

REM Check if Git is available
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Git is not installed or not in PATH
    pause
    exit /b 1
)

REM Check current directory
cd /d "%~dp0"
echo Current directory: %cd%

REM Add all files and commit if there are changes
echo Checking for uncommitted changes...
git add .
git diff-index --quiet HEAD || (
    echo Committing changes...
    git commit -m "Update from deployment script"
)

REM Set upstream and push
echo Pushing to GitHub...
git push -u origin main

if %errorlevel% equ 0 (
    echo Successfully pushed to GitHub!
    echo Repository URL: https://github.com/%GITHUB_USERNAME%/PaleobyLeoRecipes
) else (
    echo Failed to push to GitHub. Please check your repository settings.
)

pause