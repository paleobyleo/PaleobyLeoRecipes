@echo off
echo Cleaning build directories...

if exist app\build (
    rmdir /s /q app\build
    echo Deleted app\build
)

if exist .gradle (
    rmdir /s /q .gradle
    echo Deleted .gradle
)

echo Clean complete.
