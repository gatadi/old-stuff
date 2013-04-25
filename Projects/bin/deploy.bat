@ECHO OFF
setlocal
SET BRANCH=dev
SET SPEC=vijayspec.pl
SET HOST=gatadi

if "%1" == "-b" SET BRANCH=%2%
if "%1" == "-spec" SET SPEC=%2%
if "%1" == "-host" SET HOST=%2%

if "%3" == "-b" SET BRANCH=%4%
if "%3" == "-spec" SET SPEC=%4%
if "%3" == "-host" SET HOST=%4%

if "%5" == "-b" SET BRANCH=%6%
if "%5" == "-spec" SET SPEC=%6%
if "%5" == "-host" SET HOST=%6%

e:
CD c:\development\snapfish\%BRANCH%\install
rem rsync --recursive rsync://localhost/%BRANCH%.install //c/temp/install --verbose
rmdir /S /Q %TEMP%\install
perl c:\Development\snapfish\%BRANCH%\install\bootstrap.pl -b  %BRANCH%  -i //c/apps -q rsync://localhost %SPEC% %HOST%



@if errorlevel 1 goto end
@if errorlevel 2 goto end

:end

endlocal





