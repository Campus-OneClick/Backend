@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "DEFAULT_CRED_PATH=%SCRIPT_DIR%src\main\resources\firebase-service-account.json"

if "%GOOGLE_APPLICATION_CREDENTIALS%"=="" (
  if exist "%DEFAULT_CRED_PATH%" (
    set "GOOGLE_APPLICATION_CREDENTIALS=%DEFAULT_CRED_PATH%"
    echo [INFO] GOOGLE_APPLICATION_CREDENTIALS was not set.
    echo [INFO] Using %GOOGLE_APPLICATION_CREDENTIALS%
  )
)

if "%GOOGLE_APPLICATION_CREDENTIALS%"=="" (
  echo [ERROR] Firebase service account is missing.
  echo [ERROR] Put file at src\main\resources\firebase-service-account.json
  echo [ERROR] or set GOOGLE_APPLICATION_CREDENTIALS env var.
  exit /b 1
)

if not exist "%GOOGLE_APPLICATION_CREDENTIALS%" (
  echo [ERROR] GOOGLE_APPLICATION_CREDENTIALS path does not exist:
  echo %GOOGLE_APPLICATION_CREDENTIALS%
  exit /b 1
)

cd /d "%SCRIPT_DIR%"
call gradlew.bat bootRun
