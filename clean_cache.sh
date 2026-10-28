#!/bin/sh
# Linux/macOS: полная очистка кешей ForgeGradle
./gradlew --stop 2>/dev/null
rm -rf .gradle build run/.cache
rm -rf ~/.gradle/caches/minecraft
rm -rf ~/.gradle/caches/forge_gradle
rm -rf ~/.gradle/caches/modules-2/files-2.1/net.minecraftforge*
rm -rf ~/.gradle/caches/modules-2/files-2.1/de.oceanlabs*
rm -rf ~/.gradle/caches/modules-2/files-2.1/com.anatawa12*
rm -rf ~/.gradle/caches/modules-2/metadata-*
