# Building a JavaFX Application and NSIS Installer Steps

**Page ID**: 26443883  
**Version**: 17  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/26443883

---


**Bước 1 — Tải JavaFX JMODs:**

Mở cmd chạy lệnh:
`curl -L "https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-jmods.zip" -o javafx-jmods.zip`

Mở powersheell:
`Expand-Archive -Path javafx-jmods.zip -DestinationPath C:\`
`Rename-Item "C:\javafx-jmods-21.0.2" "C:\javafx-jmods"`

Tải NSIS tại: [https://nsis.sourceforge.io](https://nsis.sourceforge.io) 

**Bước 2 — Set version & token**

`$env:APP_VERSION = "1.0.0"`
`$env:LOGGLY_TOKEN = "......" `
`$env:TOTP_SECRET = "......" `
`$env:BODYCAM_CRYPTO_PASSWORD = "......"`
`$env:PATCH_MASTER_KEY = "......"`
`$env:APP_UPDATE_GITHUB_TOKEN = "......"`

**Bước 3 — Build Shadow JAR**

Cd vào thư mục chứa project rồi chạy lệnh:
`./gradlew clean shadowJar`
output: build/libs/app-1.0.0.jar 

**Bước 4 — Chuẩn bị package folder**

`mkdir build\package -Force`
`copy build\libs\app-*.jar build\package\`
`copy src\main\resources\image\logo.ico build\package\`

**Bước 5 — Build app-image bằng jpackage**

`$version = $env:APP_VERSION`
`$modulePath = "$env:JAVA_HOME\jmods;C:\javafx-jmods"`

`jpackage   --type app-image ``
` --name BDMA   --input build\package ``
` --main-jar app-$version.jar   --main-class com.app.MainApp ``
` --app-version $version   --icon build\package\logo.ico ``
` --dest build\dist   --module-path "$modulePath" ``
` --add-modules "javafx.base,javafx.graphics,javafx.controls,javafx.fxml,javafx.media,javafx.web,java.sql,java.logging,java.net.http,jdk.httpserver" ``
` --java-options "-DLOGGLY_TOKEN=$env:LOGGLY_TOKEN -DTOTP_SECRET=$env:TOTP_SECRET  -DBODYCAM_CRYPTO_PASSWORD=$env:BODYCAM_CRYPTO_PASSWORD -DBODYCAM_CRYPTO_PASSWORD=$env:BODYCAM_CRYPTO_PASSWORD  -DPATCH_MASTER_KEY=$env:PATCH_MASTER_KEY  -DAPP_UPDATE_GITHUB_TOKEN=$env:APP_UPDATE_GITHUB_TOKEN" ``
` --jlink-options "--strip-debug --compress=2 --no-header-files --no-man-pages --bind-services"`

**Bước 6 — Build NSIS installer**

`$env:APP_VERSION = "1.0.0"`
`$version = $env:APP_VERSION`
`$outputDir = Join-Path (Get-Location).Path "build\installer"`
`$sourceDir = Join-Path (Get-Location).Path "build\dist\BDMA"`
`$iconPath = Join-Path (Get-Location).Path "src\main\resources\image\logo.ico"`
`$appPathsSource = Get-Content "src\main\java\com\app\common\definitions\AppDataPaths.java" -Raw`
`$appDirMatch = [regex]::Match($appPathsSource, 'APP_DIR\s*=\s*(?:Path\.of\(\s*System\.getProperty\("user\.home"\)\s*,\s*(?<pathOfSuffixExpression>.*?)\)\s*\.toString\(\)|System\.getProperty\("user\.home"\)\s*\+\s*(?<concatSuffixExpression>.*?));', [System.Text.RegularExpressions.RegexOptions]::Singleline)`

`$isPathOfExpression = $appDirMatch.Groups['pathOfSuffixExpression'].Success`
`$pathSeparator = [IO.Path]::DirectorySeparatorChar`
`$suffixExpression = if ($isPathOfExpression) { $appDirMatch.Groups['pathOfSuffixExpression'].Value } else { $appDirMatch.Groups['concatSuffixExpression'].Value }`
`$suffixParts = [regex]::Matches($suffixExpression, '"([^"]*)"') | ForEach-Object { $_.Groups[1].Value.Replace('/', $pathSeparator) }`
`$appDataDir = '$PROFILE' + $(if ($isPathOfExpression) { $pathSeparator + ($suffixParts -join $pathSeparator) } else { $suffixParts -join '' })`

`New-Item -ItemType Directory -Force -Path build\installer | Out-Null`

`& "C:\Program Files (x86)\NSIS\makensis.exe" /INPUTCHARSET UTF8 ``
`  "/DAPP_VERSION=$version" ``
`  "/DINSTALLER_OUTPUT_DIR=$outputDir" ``
`  "/DINSTALLER_SOURCE_DIR=$sourceDir" ``
`  "/DINSTALLER_ICON=$iconPath" ``
`  "/DAPP_DATA_DIR=$appDataDir" ``
`  "src\main\resources\installer\installer.nsi"`

***Note: Khi build xong ra file .exe. Nếu chạy mà không lên app để check log. Mở cmd trong thư mục chứa BDMA.exe chạy lệnh: ***
.\*BDMA.exe > log.txt 2>&1*
***Rồi mở file log.txt để check xem lỗi do đâu.***