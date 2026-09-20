@echo off
setlocal
set VERSION=8.13
set BASE=%USERPROFILE%\.gradle\signal555
set DIST=%BASE%\gradle-%VERSION%
set ZIP=%BASE%\gradle-%VERSION%-bin.zip
set URL=https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip

if not exist "%DIST%\bin\gradle.bat" (
    if not exist "%BASE%" mkdir "%BASE%"
    echo Downloading Gradle %VERSION%...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP%'; Expand-Archive -Path '%ZIP%' -DestinationPath '%BASE%' -Force"
    if errorlevel 1 exit /b 1
)

call "%DIST%\bin\gradle.bat" %*
endlocal
