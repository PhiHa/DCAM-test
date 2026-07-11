# Version update feature

**Page ID**: 28672007  
**Version**: 2  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/28672007

---


# 1. Tổng quan tính năng

Tính năng App Update cho phép BDMA tự động phát hiện phiên bản mới trên GitHub Releases và cung cấp trải nghiệm nâng cấp một bước: thông báo → tải file cài đặt → khởi chạy installer → thoát app.

 

Hai luồng chính:

Kiểm tra định kỳ (startup): mỗi tuần một lần, chạy ngầm khi khởi động app, không ảnh hưởng UX nếu không có bản mới.

Kiểm tra thủ công: người dùng bấm nút "Kiểm tra cập nhật" trong màn hình Settings.

 

# 2. Kiến trúc & Các lớp liên quan

## 2.1. Sơ đồ phụ thuộc

Luồng phụ thuộc từ ngoài vào trong:

**Lớp**

**Vai trò**

AdminSettingsDialogController

Màn hình Settings (sau login). Wires btnUpdate → AppUpdateController.onCheckUpdateManual().

PreLoginSettingsPopupHelper

Popup settings trước login. Truyền onCheckUpdateAction xuống PreLoginSettingsPopupController.

AppUpdateController

Điều phối UI: chạy Task nền, hiện dialog, callback trạng thái.

AppUpdateService

Business logic: gọi GitHub API, so sánh version, download installer.

AppConfigService

Đọc/ghi table app_config: appUpdate.lastCheckDate, appUpdate.skippedVersion.

## 2.2. File nguồn

**File**

**Package**

AppUpdateController.java

com.app.common.modules.appupdate.controllers

AppUpdateService.java

com.app.common.modules.appupdate.services

AppUpdateInfo.java

com.app.common.modules.appupdate.models

AdminSettingsDialogController.java

com.app.admin.settingsdialog.controllers

PreLoginSettingsPopupHelper.java

com.app.common.modules.preloginsettingspopup.helpers

admin-settings-dialog.fxml

resources/views/admin

prelogin-settings-popup.fxml

resources/views/common

# 3. Luồng nghiệp vụ chi tiết

## 3.1. Kiểm tra khi khởi động (checkOnStartup)

Được gọi từ AdminLayoutController hoặc màn hình khởi động sau khi login thành công.

**Bước**

**Mô tả**

1

shouldCheckThisWeek() đọc KEY_LAST_CHECK_DATE từ app-config.json. Nếu đã check trong tuần ISO hiện tại → bỏ qua.

2

saveCheckDate() ghi ngày hôm nay vào config.

3

Tạo Task<AppUpdateInfo> chạy trên background thread → gọi AppUpdateService.checkLatestVersion().

4

Nếu info == null hoặc !info.hasUpdate() → kết thúc yên lặng.

5

Đọc KEY_SKIPPED_VERSION. Nếu phiên bản mới trùng với version đã skip → không hiện dialog.

6

Platform.runLater() → showUpdateDialog(info).

## 3.2. Kiểm tra thủ công (onCheckUpdateManual)

Được kích hoạt khi người dùng bấm nút Kiểm tra cập nhật trong Settings.

**Bước**

**Mô tả**

1

Gọi callback onCheckStart (ví dụ: disable nút, spinner) và cập nhật status "Đang kiểm tra..." qua onStatusChange.

2

Chạy Task<AppUpdateInfo> nền → checkLatestVersion().

3a

Task thành công, !hasUpdate → onCheckEnd + status "Đang dùng phiên bản mới nhất".

3b

Task thành công, hasUpdate → onCheckEnd + status "Có bản mới: vX.Y.Z" + showUpdateDialog.

3c

Task thất bại → onCheckEnd + status "Kiểm tra thất bại" + log.warn.

## 3.3. Hộp thoại cập nhật (showUpdateDialog)

Alert xác nhận với 3 lựa chọn:

**Nút**

**Hành động**

Cập nhật ngay

Gọi downloadAndInstall(info): tải .exe về ~/Downloads, mở ProcessBuilder, gọi Platform.exit().

Bỏ qua phiên bản

Gọi saveSkippedVersion(latestVersion): ghi KEY_SKIPPED_VERSION vào config. Lần check tiếp theo sẽ bỏ qua phiên bản này.

Nhắc sau

Đóng dialog, không làm gì thêm.

## 3.4. Tải & cài đặt (downloadAndInstaller)

URL lấy từ AppUpdateInfo.downloadUrl() – asset .exe đầu tiên tìm thấy trong GitHub Release.

File lưu tại: %USERPROFILE%\Downloads\BDMA-{version}.exe

Kết nối HTTP với connectTimeout = 10 giây.

Sau khi tải xong: hiện Alert thông báo → khởi chạy installer bằng ProcessBuilder → Platform.exit().

# 4. Logic kiểm tra phiên bản

## 4.1. Lấy phiên bản hiện tại (resolveCurrentVersion)

Ưu tiên theo thứ tự:

getPackage().getImplementationVersion() – đọc từ MANIFEST.MF của JAR (được Gradle ghi khi build).

System property app.version – có thể truyền qua jpackage launcher.

Fallback: chuỗi "dev" – dùng khi chạy trong IDE.

Chuẩn hóa: luôn có tiền tố "v" (ví dụ: "1.2.3" → "v1.2.3"). Môi trường dev trả về "dev" nguyên văn để tránh false positive.

## 4.2. Lấy phiên bản mới nhất (GitHub API)

Endpoint: AppConstants.GITHUB_API (dạng [https://api.github.com/repos/{owner}/{repo}/releases](https://api.github.com/repos/{owner}/{repo}/releases) )

OkHttpClient với connectTimeout = 5 giây, readTimeout = 5 giây.

Parse JSON array → lấy phần tử đầu tiên (release mới nhất) → tag_name.

Tìm asset có tên kết thúc bằng .exe → lấy browser_download_url.

hasUpdate = !latestVersion.equals(currentVersion).

Mọi exception đều được catch, trả về AppUpdateInfo với hasUpdate = false để tránh crash.

## 4.3. Lịch kiểm tra định kỳ

**Config key**

**Ý nghĩa**

KEY_LAST_CHECK_DATE

(appUpdate.lastCheckDate)

Ngày check gần nhất (ISO date). So sánh theo tuần ISO.

KEY_SKIPPED_VERSION

(appUpdate.skippedVersion)

Version người dùng chọn "Bỏ qua". So sánh chính xác chuỗi tag_name.

# 5. Model dữ liệu

AppUpdateInfo là một Java record bất biến:

**Field**

**Kiểu**

**Ý nghĩa**

latestVersion

String

tag_name từ GitHub Releases (ví dụ: "v1.3.0")

downloadUrl

String

URL trực tiếp của file .exe asset. Rỗng nếu không tìm thấy.

hasUpdate

boolean

true nếu latestVersion != currentVersion

# 6. Cấu hình & Hằng số

**Hằng số (AppConstants)**

**Giá trị / Ý nghĩa**

GITHUB_API

URL GitHub Releases API của repo BDMA.

KEY_LAST_CHECK_DATE

Key lưu ngày check cuối trong table app_config.

KEY_SKIPPED_VERSION

Key lưu version người dùng skip trong table app_config.

DATE_FORMATTER

DateTimeFormatter dùng để parse/format ngày.