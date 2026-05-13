#!/bin/sh
# bootstrap.sh — Run this ONCE before using ./gradlew
# It downloads the Gradle wrapper JAR needed to run the project.

JAR_PATH="gradle/wrapper/gradle-wrapper.jar"
JAR_URL="https://github.com/gradle/gradle/releases/download/v8.5.0/gradle-8.5-wrapper.jar"
FALLBACK_URL="https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.5/gradle-wrapper-8.5.jar"

echo "Downloading Gradle wrapper JAR..."

if command -v curl >/dev/null 2>&1; then
    curl -fL -o "$JAR_PATH" "$JAR_URL" 2>/dev/null || \
    curl -fL -o "$JAR_PATH" "$FALLBACK_URL"
elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$JAR_PATH" "$JAR_URL" 2>/dev/null || \
    wget -q -O "$JAR_PATH" "$FALLBACK_URL"
else
    echo "ERROR: Neither curl nor wget found. Please install one and retry."
    exit 1
fi

if [ -f "$JAR_PATH" ] && [ "$(wc -c < "$JAR_PATH")" -gt 1000 ]; then
    echo "✅ Wrapper JAR downloaded. You can now run: ./gradlew run"
else
    echo ""
    echo "Automatic download failed (network or access issue)."
    echo ""
    echo "MANUAL FIX — choose ONE of these options:"
    echo ""
    echo "  Option A (Homebrew — easiest on Mac):"
    echo "    brew install gradle"
    echo "    gradle wrapper"
    echo "    ./gradlew run"
    echo ""
    echo "  Option B (SDKMAN):"
    echo "    curl -s https://get.sdkman.io | bash"
    echo "    sdk install gradle 8.5"
    echo "    gradle wrapper"
    echo "    ./gradlew run"
    echo ""
    echo "  Option C (direct run without wrapper):"
    echo "    gradle run"
fi
