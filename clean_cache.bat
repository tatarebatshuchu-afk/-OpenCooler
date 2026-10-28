@echo off
call gradlew --stop
rmdir /s /q .gradle build
rmdir /s /q "%USERPROFILE%\.gradle\caches\minecraft"
rmdir /s /q "%USERPROFILE%\.gradle\caches\forge_gradle"
for /d %%D in ("%USERPROFILE%\.gradle\caches\modules-2\files-2.1\net.minecraftforge*") do rmdir /s /q "%%D"
for /d %%D in ("%USERPROFILE%\.gradle\caches\modules-2\files-2.1\de.oceanlabs*") do rmdir /s /q "%%D"
for /d %%D in ("%USERPROFILE%\.gradle\caches\modules-2\files-2.1\com.anatawa12*") do rmdir /s /q "%%D"
for /d %%D in ("%USERPROFILE%\.gradle\caches\modules-2\metadata-*") do rmdir /s /q "%%D"
