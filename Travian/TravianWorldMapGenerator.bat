@echo off

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set ARGS=
:Win9xApp
if %1a==a goto endInit
set ARGS=%ARGS% %1
shift
goto Win9xApp

:endInit

@REM Start APP
:runm2
java -cp .;bin;lib/jcommon-1.0.12.jar;lib/jfreechart-1.0.9.jar travian.application.MapGenerator %ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set ARGS=
goto postExec

:endNT
@endlocal

:postExec