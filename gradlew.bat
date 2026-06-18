@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    gradle %*
    exit /b %ERRORLEVEL%
)

echo Gradle local nao encontrado. Abra o projeto no Android Studio para sincronizar e executar.
echo Ou instale o Gradle e rode: gradle assembleDebug
exit /b 1
