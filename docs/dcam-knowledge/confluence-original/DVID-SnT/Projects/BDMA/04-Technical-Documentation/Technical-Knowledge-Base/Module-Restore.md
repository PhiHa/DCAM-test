# Module Restore

**Page ID**: 29360133  
**Version**: 3  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/29360133

---


# 1. Tổng Quan Chức Năng

Chức năng Restore Data cho phép admin khôi phục toàn bộ file dữ liệu từ thư mục Backup về thư mục Sync (data_bdma). Đây là tính năng dùng trong trường hợp thư mục Sync bị mất hoặc hỏng, cần phục hồi từ bản sao lưu.

Restore đọc tất cả file từ Backup Dir, sao chép sang Sync Dir, đồng thời cập nhật DB để đảm bảo metadata nhất quán.

Quá trình chạy trên background thread, không block UI. Progress được cập nhật real-time mỗi 3 giây.

Hỗ trợ retry cho các file bị lỗi ở lần restore trước thông qua bảng backup_restore_failures. 

## 1.1. Các Trạng Thái Restore

**Trạng thái**

**Trigger**

**Mô tả**

Chưa chạy

Khởi động app

Không có file lỗi, UI sạch

Đang chạy

Nhấn "Restore"

Progress bar hiện thị, controls bị disable

Thành công

Kết thúc không lỗi

Label xanh, tự ẩn sau 3 giây

Có file lỗi

Một số file copy thất bại

Label đỏ, hiện nút "Retry"

Disk Full

Ổ đĩa đầy trong quá trình copy

Chờ sự kiện StorageUnavailableEvent, thử phục hồi

Cancelled

Gọi cancel()

Reset progress, dừng ngay lập tức

# 2. Kiến Trúc & Các Class Liên Quan

## 2.1. Mô Tả Từng Class

**Class**

**Package**

**Vai trò**

AdminSettingsDialogController

admin.settingsdialog.controllers

UI controller: xử lý button click, cập nhật progress label, quản lý thread

AdminSettingsDialogService

admin.settingsdialog.services

Service layer mỏng: delegate restore() và retryFailed() xuống RestoreService, đọc config

RestoreService

admin.settingsdialog.services

Core logic: scan file, copy với retry, upsert DB, xử lý disk full, lưu failure

StorageProgress

common.dtos

DTO thread-safe: track total/success/failed/checksumInvalid/skipped

RestoreFailureRepository

common.repositories

CRUD cho bảng backup_restore_failures

FolderManagerService

common.modules.foldermanager

Cung cấp getBackupDir() và getSyncDir()

FolderSecurityService

common.modules.foldermanager

Lock/unlock folder, validate checksum file

FileService

common.services

batchResolveUsers/Devices + upsertFileFromRestore

DriveResolverService

common.services

invalidateCache() sau khi restore xong

AppConfigService

common.services

Lưu KEY_LAST_RESTORE_PROGRESS vào app-config.json

# 3. Luồng Xử Lý Chi Tiết

## 3.1. Luồng Restore Đầy Đủ (Nhấn Nút "Restore")

**#**

**Bước / Class**

**Mô tả**

**1**

**Controller onRestore()**

Validate: backupPath và dataPath không được rỗng. Kiểm tra getStorageBlockedReason() (đang restore / device sync / data backup).

**2**

**Controller**

Hiện dialog confirm. Nếu user xác nhận → gọi runStorageRecoveryOperation(adminSettingsService::restore).

**3**

**Controller runStorageRecoveryOperation()**

Disable controls, đăng ký callback onProgressInitialized, spawn background Thread.

**4**

**AdminSettingsDialogService restore()**

Thin delegate: gọi restoreService.restore().

**5**

**RestoreService restore()**

AtomicBoolean lock (compareAndSet). Lấy backupDir và dataDir từ FolderManagerService.

**6**

**RestoreService scanFiles()**

Unlock folder → Files.walk() → filter regular files, bỏ .sha256 sidecar và .tmp → lock lại folder.

**7**

**RestoreService**

clearAll() failure cũ. Init StorageProgress(total = số file). Gọi onProgressInitialized callback.

**8**

**RestoreService**

batchResolveUsers() và batchResolveDevices() từ FileService để build lookup map.

**9**

**RestoreService processRestoreFiles()**

Vòng lặp từng [file: validat](#)e checksum, so sánh size, copyWithRetry(), upsertFileFromRestore(), cập nhật progress.

**10**

**RestoreService copyWithRetry()**

Copy sang file .tmp trước, verify size, atomic move sang đích. Retry tối đa MAX_RETRY lần. Nếu disk full → throw DiskFullException.

**11**

**RestoreService**

Nếu DiskFullException: publish StorageUnavailableEvent, wait recoveryLock, tiếp tục sau khi disk recovered.

**12**

**RestoreService persistRestoreOutcome()**

Lưu danh sách failure vào DB. Ghi last progress vào app-config.json.

**13**

**RestoreService**

invalidateCache() DriveResolverService. Trả về BackupSyncResult.

**14**

**Controller (Platform.runLater)**

Dừng progress scheduler. Cập nhật label màu xanh/đỏ. Nếu thành công → tự ẩn sau 3 giây. Nếu lỗi → hiện nút Retry.

## 3.2. Luồng Retry Failed

**#**

**Bước / Class**

**Mô tả**

**1**

**Controller onRetryFailedRestore()**

Gọi runStorageRecoveryOperation(adminSettingsService::retryFailed).

**2**

**RestoreService retryFailed()**

Đọc danh sách file lỗi từ RestoreFailureRepository. Nếu rỗng → trả về success(0).

**3**

**RestoreService**

Init StorageProgress với số file lỗi. Gọi processRestoreFiles() với retryMode = true.

**4**

**RestoreService persistRestoreOutcome()**

Sau retry: xóa các file đã thành công khỏi failure table (deleteByPaths). Lưu file còn lỗi.

## 3.3. Kiểm Tra Blocking Trước Khi Chạy

Kiểm tra xem có luồng nào đang chạy không theo thứ tự:

restoreService.isRunning() → đang có restore/retry chạy

deviceSyncQueue.isActive() → đang sync thiết bị

dataBackupQueue.isActive() → đang backup data

Nếu bất kỳ điều kiện nào đúng → hiện notice lỗi, không chạy restore.

# 4. Cơ Chế Progress Tracking

## 4.1. StorageProgress – Các Counter

**Field**

**Kiểu**

**Ý nghĩa**

total

int

Tổng số file được scan từ Backup Dir

success

int

Số file copy thành công (bao gồm file đã tồn tại cùng size)

failed

int

Số file copy thất bại sau MAX_RETRY lần

checksumInvalid

int

Số file bị xóa do checksum không hợp lệ

skipped

int

Số file bị bỏ qua khi disk full và không recover được

## 4.2. Progress Updater

Controller spawn một ScheduledExecutorService cập nhật label trên mỗi 3 giây trên Platform.runLater() trong quá trình restore. 

## 4.3. Trạng Thái Label UI

**Trạng thái**

**CSS class**

**Hành động**

Đang chạy, không lỗi

status-success

Label xanh, cập nhật liên tục

Đang chạy, có file lỗi

status-error

Label đỏ, cập nhật liên tục

Hoàn thành không lỗi

status-success

Label xanh, tự ẩn sau 3 giây

Hoàn thành có lỗi

status-error

Label đỏ, hiện nút btnRetryFailedRestore

Restore bị cancel

(reset)

hideProgressUI() – label ẩn hoàn toàn

# 5. Xử Lý Lỗi & Edge Cases

## 5.1. File Copy – copyWithRetry()

**Tình huống**

**Xử lý**

**Kết quả**

Copy thành công

ATOMIC_MOVE .tmp → dest

incrementSuccess()

IOException thường

Retry tối đa MAX_RETRY lần, xóa .tmp

Sau hết retry → addFailure, incrementFailed()

Disk full (No space left / not enough space)

throw DiskFullException

publishEvent StorageUnavailableEvent, wait recovery

isCancelled = true

throw IOException("cancelled")

Reset progress, return failure result

Size mismatch sau copy

throw IOException("Size mismatch")

Retry lại, xóa .tmp

## 5.2. Checksum Validation – shouldSkipFile()

Trước khi restore, mỗi file được kiểm tra xem file có hợp lệ không 

## 5.3. Disk Full Recovery

**#**

**Bước / Class**

**Mô tả**

**1**

**RestoreService**

Gặp DiskFullException → extractDriveLetter(destPath)

**2**

**RestoreService**

Publish StorageUnavailableEvent(FolderType.SYNC, LOW_SPACE)

**3**

**RestoreService waitForDiskRecovery()**

Block thread trên recoveryLock.wait()

**4**

**FolderManager (external)**

Xử lý recovery, publish StorageDirRestoredEvent hoặc StorageRecoveryDeferredEvent

**5**

**RestoreService @EventListener**

onStorageDirRestored → notifyAll, recoveryDeferred = false; hoặc onStorageDirRecoveryDeferred → notifyAll, recoveryDeferred = true

**6**

**RestoreService**

Nếu recovered: lấy syncDir mới, thử copy lại. Nếu deferred: incrementSkipped, return failure

## 5.4. Persistence Failure (backup_restore_failures)

Đầu mỗi lần restore mới: clearAll() xóa toàn bộ record cũ.
Cuối mỗi lần restore: saveAll(failures) ghi tất cả file lỗi.
Cuối retry: deleteByPaths(successPaths) xóa file đã thành công, giữ lại file còn lỗi.
Khi user thay đổi thư mục Backup hoặc Sync: clearAll() + hideProgressUI() (reset trạng thái). 

# 6. UI Components (FXML)

**fx:id**

**Kiểu**

**Mô tả**

btnRestore

Button

Trigger restore. onAction=#onRestore

btnRetryFailedRestore

Button

Retry file lỗi. Ẩn mặc định (managed=false, visible=false). Hiện khi có failure.

lblStorageProgress

Label

Hiển thị progress. Có thể thêm CSS class status-success / status-error

storageActionBox

HBox

Container cho label và retry button. Ẩn mặc định.

txtSavePath

TextField

Hiển thị Sync Dir (read-only)

txtBackupPath

TextField

Hiển thị Backup Dir (read-only)

lblDriveConflictWarning

Label

Cảnh báo khi Sync và Backup cùng ổ đĩa. Ẩn mặc định.

 Khi restore đang chạy: btnRestore, btnChooseSaveFolder, btnChooseBackupFolder, btnChooseExportFolder đều bị disable.
storageActionBox chỉ visible khi có progress đang chạy hoặc có failure cần retry.
lblDriveConflictWarning hiện khi Sync Dir và Backup Dir cùng root drive (cảnh báo, không block).

# 7. Concurrency & Thread Safety

**Cơ chế**

**Áp dụng ở**

**Mục đích**

AtomicBoolean isRunning

RestoreService

Đảm bảo chỉ 1 restore/retry chạy cùng lúc (compareAndSet)

AtomicBoolean isCancelled

RestoreService

Signal dừng sớm, kiểm tra trong vòng lặp file

synchronized methods

StorageProgress

Thread-safe counter increment từ background thread

synchronized recoveryLock

RestoreService

Cơ chế wait/notifyAll cho disk recovery flow

volatile recoveryDeferred

RestoreService

Đọc từ event listener thread, viết từ restore thread

Platform.runLater()

Controller

Cập nhật JavaFX UI từ background thread

ScheduledExecutorService

Controller

Poll progress mỗi 3 giây, shutdown khi restore xong

Thread daemon=true

Controller

Background thread không block JVM shutdown