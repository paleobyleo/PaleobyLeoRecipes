@echo off
echo === Environment Information ===
echo.

echo Java Version:
java -version 2>&1
echo.

echo Java Home:
echo %JAVA_HOME%
echo.

echo Path:
echo %PATH%
echo.

echo Gradle Version:
call gradlew -v
echo.

echo Android SDK Path:
type local.properties | findstr sdk.dir
echo.

echo Java Home from local.properties:
type local.properties | findstr java.home
echo.

echo === End of Environment Information ===echo off
echo Checking Java installation...

where java
java -version

echo.
echo Environment Variables:
echo JAVA_HOME=%JAVA_HOME%
echo PATH=%PATH%

echo.
echo Java installation in Program Files:
dir /b "C:\Program Files\Java"

echo.
echo Android Studio JDK:
dir /b "%LOCALAPPDATA%\Android\Sdk\jbr" 2>nul || echo Android Studio JDK not found
