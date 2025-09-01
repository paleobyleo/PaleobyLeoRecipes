@echo off
echo Testing Java installation...

where java
java -version

echo.
echo Environment Variables:
echo JAVA_HOME=%JAVA_HOME%
echo PATH=%PATH%

pause
