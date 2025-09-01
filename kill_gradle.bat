@echo off
echo Killing Gradle daemon processes...
taskkill /F /IM java.exe
echo Gradle processes terminated.
pause