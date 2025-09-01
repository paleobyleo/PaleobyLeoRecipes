@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java from: %JAVA_HOME%
java -version

echo.
echo Running Gradle build...
call gradlew %*
