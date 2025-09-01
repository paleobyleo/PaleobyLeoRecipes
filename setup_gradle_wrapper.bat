@echo off
setlocal

REM Create gradle/wrapper directory if it doesn't exist
if not exist "gradle\wrapper" mkdir "gradle\wrapper"

REM Download gradle-wrapper.jar
curl -o gradle\wrapper\gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.0.2/gradle/wrapper/gradle-wrapper.jar

REM Create gradle-wrapper.properties
echo distributionBase=GRADLE_USER_HOME> gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.0.2-bin.zip>> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME>> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists>> gradle\wrapper\gradle-wrapper.properties

REM Create gradlew.bat
(
echo @if "%%DEBUG%%" == "" @echo off
) > gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem Copyright 2015 the original author or authors.
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem Licensed under the Apache License, Version 2.0 (the "License");
) >> gradlew.bat

(
echo @rem you may not use this file except in compliance with the License.
) >> gradlew.bat

(
echo @rem You may obtain a copy of the License at
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem      https://www.apache.org/licenses/LICENSE-2.0
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem Unless required by applicable law or agreed to in writing, software
) >> gradlew.bat

(
echo @rem distributed under the License is distributed on an "AS IS" BASIS,
) >> gradlew.bat

(
echo @rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
) >> gradlew.bat

(
echo @rem See the License for the specific language governing permissions and
) >> gradlew.bat

(
echo @rem limitations under the License.
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem ##########################################################################
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem   Gradle startup script for Windows
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @if "%%DEBUG%%" == "" @echo off
) >> gradlew.bat

(
echo @rem ##########################################################################
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem  Gradle
) >> gradlew.bat

(
echo @rem
) >> gradlew.bat

(
echo @rem ##########################################################################
) >> gradlew.bat

(
echo @rem Set local scope for the variables with windows NT shell
) >> gradlew.bat

(
echo if "%%OS%%"=="Windows_NT" setlocal
) >> gradlew.bat

(
echo set DIRNAME=%%~dp0
) >> gradlew.bat

(
echo if "%%DIRNAME%%" == "" set DIRNAME=.
) >> gradlew.bat

(
echo set APP_BASE_NAME=%%~n0
) >> gradlew.bat

(
echo set APP_HOME=%%DIRNAME%%
) >> gradlew.bat

(
echo @rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
) >> gradlew.bat

(
echo set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
) >> gradlew.bat

(
echo @rem Find java.exe
) >> gradlew.bat

(
echo if defined JAVA_HOME goto findJavaFromJavaHome
) >> gradlew.bat

(
echo set JAVA_EXE=java.exe
) >> gradlew.bat

(
echo %JAVA_EXE% -version >NUL 2>&1
) >> gradlew.bat

(
echo if "%%ERRORLEVEL%%" == "0" goto init
) >> gradlew.bat

(
echo echo.
) >> gradlew.bat

(
echo echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
) >> gradlew.bat

(
echo echo.
) >> gradlew.bat

(
echo echo Please set the JAVA_HOME variable in your environment to match the
) >> gradlew.bat

(
echo echo location of your Java installation.
) >> gradlew.bat

(
echo goto fail
) >> gradlew.bat

(
echo :findJavaFromJavaHome
) >> gradlew.bat

(
echo set JAVA_HOME=%%JAVA_HOME:"=%%
) >> gradlew.bat

(
echo set JAVA_EXE=%%JAVA_HOME%%/bin/java.exe
) >> gradlew.bat

(
echo if exist "%%JAVA_EXE%%" goto init
) >> gradlew.bat

(
echo echo.
) >> gradlew.bat

(
echo echo ERROR: JAVA_HOME is set to an invalid directory: %%JAVA_HOME%%
) >> gradlew.bat

(
echo echo.
) >> gradlew.bat

(
echo echo Please set the JAVA_HOME variable in your environment to match the
) >> gradlew.bat

(
echo echo location of your Java installation.
) >> gradlew.bat

(
echo goto fail
) >> gradlew.bat

(
echo :init
) >> gradlew.bat

(
echo @rem Get command-line arguments, handling Windows variants
) >> gradlew.bat

(
echo if not "%%OS%%"=="Windows_NT" goto win9xME_args
) >> gradlew.bat

(
echo if "%%@eval[2+2]" == "4" goto 4NT_args
) >> gradlew.bat

(
echo :win9xME_args
) >> gradlew.bat

(
echo @rem Slurp the command line arguments.
) >> gradlew.bat

(
echo set CMD_LINE_ARGS=
) >> gradlew.bat

(
echo :_next
) >> gradlew.bat

(
echo if "%%1"=="" goto execute
) >> gradlew.bat

(
echo set CMD_LINE_ARGS=%%*
) >> gradlew.bat

(
echo goto execute
) >> gradlew.bat

(
echo :4NT_args
) >> gradlew.bat

(
echo @rem Get arguments from the 4NT Shell from JP Software
) >> gradlew.bat

(
echo set CMD_LINE_ARGS=%%$
) >> gradlew.bat

(
echo :execute
) >> gradlew.bat

(
echo @rem Setup the command line
) >> gradlew.bat

(
echo set CLASSPATH=%%APP_HOME%%\\gradle\\wrapper\\gradle-wrapper.jar
) >> gradlew.bat

(
echo @rem Execute Gradle
) >> gradlew.bat

(
echo "%%JAVA_EXE%%" %%DEFAULT_JVM_OPTS%% %%JAVA_OPTS%% %%GRADLE_OPTS%% "-Dorg.gradle.appname=%%APP_BASE_NAME%%" -classpath "%%CLASSPATH%%" org.gradle.wrapper.GradleWrapperMain %%CMD_LINE_ARGS%% %%*
) >> gradlew.bat

(
echo :fail
) >> gradlew.bat

(
echo rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
) >> gradlew.bat

(
echo rem the _cmd.exe /c_ return code!
) >> gradlew.bat

(
echo if  not "" == "%%GRADLE_EXIT_CONSOLE%%" exit 1
) >> gradlew.bat

(
echo exit /b 1
) >> gradlew.bat

echo Gradle wrapper setup complete.
echo Please run 'gradlew.bat wrapper --gradle-version=8.0.2' to complete the setup.

endlocal
