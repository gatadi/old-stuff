@ECHO OFF

SET USER=GATADI64
SET DBHOST=bolinuxdb1
SET SID=SFODEV02
SET BRANCH=DEV

IF "%1" == "/h" goto end
IF "%1" == "-h" goto end
IF "%1" == "--h" goto end


if "%1" == ""  goto _USER_END

SET USER=%1%
:_USER_END

if "%2" == ""  goto _BRANCH_END
SET BRANCH=%2%
:_BRANCH_END

if "%3" == ""  goto _DBHOST_END

SET DBHOST=%3%
:_DBHOST_END

if "%4" == ""  goto _SID_END

SET SID=%4%
:_SID_END

@echo building DB objects
@setlocal
@cd E:\Development\snapfish\%BRANCH%\build\
pmake USER=%USER% DBHOST=%DBHOST% SID=%SID% -f Root.mk buildSFSchemaRep
@endlocal

@if errorlevel 1 goto end
@if errorlevel 2 goto end

:end

@echo -----------------------------------------------------
@echo Example : buildsfschemarep GATADI Dev milano SFODEV01
@echo -----------------------------------------------------


