#!/bin/bash
# ============================================================
#  Equilibrio App – Script de configuración inicial
#  Ejecuta este script UNA VEZ antes de abrir Android Studio
# ============================================================
echo "🌿 Configurando Equilibrio App..."

# Descargar gradle-wrapper.jar
WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
WRAPPER_URL="https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"

echo "📦 Descargando gradle-wrapper.jar..."
curl -sL "$WRAPPER_URL" -o "$WRAPPER_JAR"

if [ ! -s "$WRAPPER_JAR" ]; then
  # Fallback: usar wget
  wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR" 2>/dev/null
fi

if [ -s "$WRAPPER_JAR" ]; then
  echo "✅ gradle-wrapper.jar descargado correctamente"
else
  echo "⚠️  No se pudo descargar automáticamente."
  echo "   Descárgalo manualmente desde:"
  echo "   https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
  echo "   y colócalo en: gradle/wrapper/gradle-wrapper.jar"
fi

# Verificar google-services.json
if grep -q "_INSTRUCCION" app/google-services.json 2>/dev/null; then
  echo ""
  echo "⚠️  IMPORTANTE: Reemplaza app/google-services.json con el tuyo de Firebase"
  echo "   1. Ve a https://console.firebase.google.com"
  echo "   2. Crea proyecto 'equilibrio-app' con package 'com.equilibrio'"
  echo "   3. Descarga google-services.json y cópialo a la carpeta app/"
else
  echo "✅ google-services.json encontrado"
fi

echo ""
echo "🚀 ¡Listo! Ahora abre la carpeta Equilibrio/ en Android Studio."
