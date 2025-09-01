@echo off
echo Initializing Git Repository for Paleo Recipes
echo ==========================================

REM Check if git is available
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Git is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if we're already in a git repository
git rev-parse --git-dir >nul 2>&1
if %errorlevel% equ 0 (
    echo Warning: Already in a git repository
    echo Current git root: 
    git rev-parse --show-toplevel
    echo.
    echo Do you want to continue anyway? (y/N)
    set /p choice=
    if /i not "%choice%"=="y" (
        echo Operation cancelled
        pause
        exit /b 1
    )
)

echo Initializing git repository...
git init
if %errorlevel% neq 0 (
    echo Error: Failed to initialize git repository
    pause
    exit /b 1
)

echo Adding all files...
git add .
if %errorlevel% neq 0 (
    echo Error: Failed to add files
    pause
    exit /b 1
)

echo Making initial commit...
git commit -m "Initial commit: Paleo Recipes v1.0.0"
if %errorlevel% neq 0 (
    echo Error: Failed to make initial commit
    pause
    exit /b 1
)

echo.
echo Git repository initialized successfully!
echo.
echo To push to GitHub:
echo 1. Create a new repository on GitHub
echo 2. Add the remote: git remote add origin YOUR_REPOSITORY_URL
echo 3. Push the code: git push -u origin main
echo.
echo Repository is ready for GitHub publishing!
echo.

pause