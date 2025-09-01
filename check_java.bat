@echo off
echo === Java Environment Information ===
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

echo === System Environment Variables ===
set | findstr /i "JAVA_HOME GRADLE_HOME ANDROID_HOME"
echo.

echo === Java Executables in PATH ===
where java
where javac
where javaw
echo.

echo === Java Version Details ===
java -XshowSettings:properties -version 2>&1 | findstr /i "java.version java.vendor java.home"
echo.

echo === Gradle Properties ===
type gradle.properties 2>nul
echo.

echo === End of Java Environment Information ===echo off
echo Java Version:
"C:\Program Files\Java\jdk-17\bin\java.exe" -version 2>&1
echo.
echo JAVA_HOME:
echo %JAVA_HOME%
echo.
echo Path:
echo %PATH%
