#!/bin/sh
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle local não encontrado. Abra o projeto no Android Studio para sincronizar e executar."
echo "Ou instale o Gradle e rode: gradle assembleDebug"
exit 1
