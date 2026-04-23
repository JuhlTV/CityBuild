@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars:
@REM M2_HOME - location of maven's installed home (default is %MAVEN_HOME%)
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF "%MAVEN_PROJECTBASEDIR%"=="" (
	set MAVEN_PROJECTBASEDIR=%CD%
)
set MAVEN_HOME=%~dp0\.mvn\wrapper\maven-3.9.6

@REM workaround for Windows UNC paths
if exist "%MAVEN_HOME%" goto endDetectBaseDir

set MAVEN_PROJECTBASEDIR=%CD%

:endDetectBaseDir

if exist "%MAVEN_HOME%" (
	"%JAVA_HOME%\bin\java.exe" ^
	  -classpath "%MAVEN_HOME%\boot\plexus-classworlds-2.8.1.jar" ^
	  "-Dclassworlds.conf=%MAVEN_HOME%\bin\m2.conf" ^
	  "-Dmaven.home=%MAVEN_HOME%" ^
	  "-Dlibrary.jansi.version=2.4.1" ^
	  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
	  org.codehaus.plexus.classworlds.launcher.Launcher ^
	  %MAVEN_CONFIG% %*
	if ERRORLEVEL 1 goto error
	goto end
)

echo Maven not found. Please download Maven 3.9.6 from https://maven.apache.org/download.cgi
goto error

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, e.g. after.cmd on Windows
if exist "%MAVEN_PROJECTBASEDIR%\mvn\after.cmd" call "%MAVEN_PROJECTBASEDIR%\mvn\after.cmd"
:skipRcPost

@endlocal /B %ERROR_CODE%
