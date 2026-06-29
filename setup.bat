@echo off
REM ============================================================
REM  Equilibrio App - Script de configuracion para Windows
REM  Ejecuta este script UNA VEZ antes de abrir Android Studio
REM ============================================================
echo Configurando Equilibrio App...

set WRAPPER_JAR=gradle\wrapper\gradle-wrapper.jar
set WRAPPER_URL=https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar

echo Descargando gradle-wrapper.jar...
powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"

if exist "%WRAPPER_JAR%" (
    echo gradle-wrapper.jar descargado correctamente
) else (
    echo No se pudo descargar automaticamente.
    echo Descargalo manualmente desde:
    echo %WRAPPER_URL%
    echo y colócalo en: gradle\wrapper\gradle-wrapper.jar
)

echo.
echo Verifica que app\google-services.json sea el de tu proyecto Firebase.
echo.
echo Listo! Abre la carpeta Equilibrio\ en Android Studio.
pause
