# Process CI/CD

**Page ID**: 27132217  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27132217

---


# 1. Tổng quan quy trình đóng gói

Hệ thống CI/CD của BDMA được triển khai trên GitHub Actions, tự động hoá toàn bộ chuói biên dịch &rarr; đóng gói &rarr; phân phối từ hai nhánh có chức năng khác nhau.

 

## 1.1 Chiến lược nhánh

**Nhánh**

**Mục đích**

**Hành động**

cicd

Release chính thức

Build &rarr; Đóng gói &rarr; Upload artifact &rarr; Tạo GitHub Release + tag

develop

Kiểm tra / phát triển

Build &rarr; Đóng gói &rarr; Upload artifact (không tạo Release)

 

## 1.2 Sơ đồ các bước CI/CD

Mỗi lần push vào nhánh đã cấu hình, GitHub Actions thực hiện tuần tự các bước sau:

Checkout code và xác định version tự động (1.0.<run_number>)

Cài JDK 21 (Temurin) và Gradle 8.7

Tải JavaFX JMODs 21.0.2 cho Windows x64 từ Gluon

Build Shadow JAR (Fat JAR) qua Gradle task shadowJar

jpackage tạo app-image (runtime tích hợp sẵn, không cần cài JRE riêng)

NSIS đóng gói app-image thành file BDMA-<version>-Setup.exe

Upload artifact (cả 2 nhánh) — chỉ tạo GitHub Release khi push vào cicd

 

# 2. Chi tiết file build.yml

## 2.1 Trigger và quyền

Workflow chứa file được commit tại đường dẫn:

.github/workflows/build.yml

 

**Thuộc tính**

**Giá trị / Giải thích**

Trigger

on.push vào nhánh cicd hoặc develop

Runner

windows-latest (yêu cầu bắt buộc — jpackage và NSIS phụ thuộc Windows)

Permission

contents: write — cần thiết để tạo GitHub Release và upload asset

 

## 2.2 Quản lý phiên bản

Phiên bản được sinh tự động theo công thức:

APP_VERSION = 1.0.<github.run_number>

Giá trị này được truyền vào môi trường GITHUB_ENV và dùng nhất quán xuyên suốt: tên JAR, tham số jpackage, tên file .exe, và tag Release.

 

## 2.3 Các Secret yêu cầu

**Secret**

**Mục đích**

LOGGLY_TOKEN

Token gửi log lên Loggly, được đưa vào JVM option -DLOGGLY_TOKEN khi jpackage

PATCH_MASTER_KEY

Khoá bí mật cho cơ chế patch/update nội bộ, được đưa vào -DPATCH_MASTER_KEY

GITHUB_TOKEN

Token tự động của GitHub Actions, dùng cho step Upload Release Asset

 

## 2.4 Bước jpackage — cấu hình chi tiết

jpackage được gọi ở chế độ app-image (không có installer hệ điều hành, NSIS đảm nhiệm vai trò đó) với các tham số:

**Tham số jpackage**

**Giá trị**

--type

app-image

--main-class

com.app.MainApp

--module-path

$JAVA_HOME\jmods;C:\javafx-jmods

--add-modules

javafx.base, javafx.graphics, javafx.controls, javafx.fxml, java.sql, java.logging, javafx.media

--jlink-options

--strip-debug --compress=2 --no-header-files --no-man-pages --bind-services

--java-options

-DLOGGLY_TOKEN=... -DPATCH_MASTER_KEY=...

Output

build\dist\BDMA\

 

Lưu ý: --jlink-options rút gọn runtime nhưng giữ lại --bind-services để hỗ trợ các dịch vụ phụ thuộc (JDBC, logging provider...).

 

## 2.5 Phân tích bước Build NSIS Installer

Trước khi gọi makensis.exe, step này thực hiện một regex parse tự động trên file AppDataPaths.java để rút ra đường dẫn thư mục dữ liệu của ứng dụng (APP_DATA_DIR). Mục tiêu là truyền đường dẫn này vào NSIS để hỗ trợ tính năng "Uninstall and delete all data".

Logic parse hỗ trợ hai cú pháp:

Path.of(System.getProperty("user.home"), "...")

System.getProperty("user.home") + "..."

Sau đó NSIS nhận được 6 định nghĩa qua tham số /D:

**NSIS Define**

**Giá trị**

APP_VERSION

1.0.<run_number>

INSTALLER_OUTPUT_DIR

build\installer

INSTALLER_SOURCE_DIR

build\dist\BDMA

INSTALLER_ICON

src\main\resources\image\logo.ico

APP_DATA_DIR

$PROFILE\<suffix rút từ AppDataPaths.java>

 

# 3. Chi tiết file installer.nsi

## 3.1 Tổng quan

File installer.nsi được viết bằng ngôn ngữ NSIS (Nullsoft Scriptable Install System) và đóng vai trò là lớp vỏ installer cho app-image do jpackage tạo ra. File được lưu tại:

src\main\resources\installer\installer.nsi

 

## 3.2 Cấu hình cơ bản

**Thuộc tính**

**Giá trị**

Tên ứng dụng

BDMA

Output

BDMA-<APP_VERSION>-Setup.exe

Thư mục cài đặt mặc định

%PROGRAMFILES64%\BDMA

Registry key

HKLM\Software\BDMA

Quyền yêu cầu

Admin (RequestExecutionLevel admin)

Mã hóa ký tự

Unicode (Unicode True)

Thuật toán nén

LZMA solid, dictionary 64 MB

Giao diện

MUI2 (Modern UI 2) — ngôn ngữ English

 

## 3.3 Logic kiểm tra cài đặt (.onInit)

Khi khởi chạy, installer kiểm tra registry HKLM\Software\BDMA để xác định trạng thái:

Đã có registry và file BDMA.exe tồn tại &rarr; đặt IsInstalled = 1, hiển thị dialog chọn hành động

Không có registry hoặc file không tồn tại &rarr; xóa registry rác, đặt IsInstalled = 0, UserChoice = 1 (đi thẳng vào cài đặt)

 

## 3.4 Ba hành động được hỗ trợ

Khi phát hiện đã cài đặt, dialog 3 tùy chọn được hiển thị:

**UserChoice**

**Tùy chọn**

**Hành động**

1

Reinstall / Update

Ghi đè toàn bộ file app, cập nhật registry, tạo shortcut mới

2

Uninstall

Xóa app, shortcut, registry (giữ nguyên dữ liệu người dùng)

3

Uninstall + delete data

Xóa dữ liệu APP_DATA_DIR trước, sau đó Uninstall như chức năng 2

 

## 3.5 Cấu hình registry (Add/Remove Programs)

Khi cài đặt, installer ghi đầy đủ thông tin vào registry để hiển thị trong Control Panel:

DisplayName, DisplayVersion, DisplayIcon

UninstallString trỏ vào BDMA-Setup.exe trong thư mục cài đặt

Publisher: DVID

NoModify=1, NoRepair=1 (disable Modify/Repair button)

EstimatedSize tính tự động bằng GetSize

 

## 3.6 Cơ chế Uninstall an toàn

Do installer.exe nằm bên trong thư mục cài đặt (BDMA-Setup.exe), nó không thể tự xóa chính mình. NSIS giải quyết bằng cách viết một file batch tạm vào %TEMP% và chạy nó ở chế độ minimized sau khi uninstaller thoát:

Ping lặp lại để chờ uninstaller đóng hoàn toàn

Del /F /Q file BDMA-Setup.exe

Rmdir /S /Q toàn bộ thư mục $INSTDIR

Tự xóa chính file batch

 

## 3.7 Bảo vệ khi xóa dữ liệu (DeleteData)

Chức năng "Uninstall and delete all data" có cơ chế kiểm tra an toàn: chỉ cho phép xóa nếu APP_DATA_DIR kết thúc bằng chuỗi bdma (4 ký tự cuối). Nếu điều kiện không thoả, hiển thị cảnh báo và huỷ thao tác. Trước khi xóa, attrib được gọi để bỏ các cờ ReadOnly/Hidden/System trên toàn bộ cây thư mục.

 

## 3.8 Tắt AutoPlay

Trong bước cài đặt, NSIS ghi registry:

HKLM\Software\Microsoft\Windows\CurrentVersion\Policies\Explorer

NoDriveTypeAutoRun = 0xFF

Giá trị 0xFF tắt AutoPlay/AutoRun trên mọi loại ổ đĩa. Mục đích: ngăn Windows hiển thị dialog AutoPlay mỗi khi kết nối body camera qua USB (cần thiết để BDMA kiểm soát luồng phát hiện thiết bị). Giá trị này được xóa khi uninstall.

 

# 4. Hướng dẫn vận hành

## 4.1 Kích hoạt build

**Muốn**

**Làm**

Tạo Release chính thức

Push commit vào nhánh cicd

Kiểm tra build không release

Push commit vào nhánh develop

Tải artifact

Vào Actions > build > Artifacts, tải BDMA-<version>

 

## 4.2 Cài đặt / nâng cấp

Chạy file BDMA-<version>-Setup.exe với quyền Admin

Nếu chưa cài: wizard di thẳng vào bước cài đặt

Nếu đã cài: dialog hiển thị 3 tùy chọn (Reinstall/Uninstall/Uninstall+data)

Shortcut được tạo tự động trên Desktop và Start Menu

 

## 4.3 Gỡ cài đặt

Có 2 cách:

Chạy lại file Setup.exe và chọn Uninstall

Vào Control Panel > Add/Remove Programs > BDMA > Uninstall

Khi đó installer ở trong thư mục cài đặt cũng sẽ được khởi chạy. Nếu các tiến trình BDMA đang chạy, người dùng sẽ được nhắc đồng ý đóng trước khi tiếp tục.

 

# 5. Cấu trúc file liên quan

**Đường dẫn**

**Mô tả**

.github/workflows/build.yml

GitHub Actions workflow chính

src/main/resources/installer/installer.nsi

NSIS script tạo installer

src/main/resources/image/logo.ico

Icon ứng dụng (dùng cho installer và shortcut)

src/main/java/com/app/common/definitions/AppDataPaths.java

Nguồn để parse APP_DATA_DIR (dùng bởi build.yml)

build/libs/app-<version>.jar

Shadow JAR đầu ra của Gradle

build/dist/BDMA/

App-image do jpackage tạo (runtime tích hợp)

build/installer/BDMA-<version>-Setup.exe

File installer cuối cùng

 

# 6. Lưu ý và rủi ro cần biết

Runner phải là windows-latest: jpackage và NSIS chỉ chạy trên Windows. Không thể build installer trên Linux/macOS runner.

APP_DATA_DIR phải kết thúc bằng bdma: đây là ràng buộc bảo vệ trong DeleteData. Nếu tên thư mục thay đổi, cần cập nhật logic kiểm tra trong installer.nsi.

Phiên bản JavaFX JMODs phải khớp với JDK: hiện tại dùng OpenJFX 21.0.2 và JDK 21 (Temurin). Không được sủ dụng cross-version.

Artifact có retention-days: 7: artifact tải về từ nhánh develop sẽ hết hạn sau 7 ngày. Release trên nhánh cicd được lưu vĩnh viễn.

LOGGLY_TOKEN và PATCH_MASTER_KEY được nhúng trong file runtime: cần đảm bảo 2 secret này được cấu hình đúng trong Settings > Secrets của repository trước khi chạy workflow.

Uninstall từ Control Panel: do UninstallString trỏ vào BDMA-Setup.exe trong $INSTDIR (không phải Uninstall.exe riêng), khi chạy sẽ mở installer như bình thường và người dùng chọn Uninstall.