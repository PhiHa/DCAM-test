# Module Sync & Backup

**Page ID**: 29032450  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/29032450

---


# 1. Tổng quan hệ thống

Module Sync & Backup là trung tâm xử lý dữ liệu camera hành trình trong ứng dụng BDMA. Module gồm hai pipeline độc lập chạy song song:

Data Sync Pipeline – kéo file từ thiết bị camera về máy chủ qua ADB.

Data Backup Pipeline – sao chép file đã sync sang ổ đĩa backup.

Hai pipeline phối hợp với nhau: backup worker chờ sync hoàn thành trước khi xử lý, đảm bảo tính nhất quán dữ liệu.

## 1.1 Sơ đồ luồng dữ liệu tổng thể

**Bước**

**Thành phần**

**Mô tả**

1

DeviceTracker

Phát hiện thiết bị kết nối/ngắt qua ADB, publish DeviceEvent

2

DeviceValidationService

Xác thực device qua whitelist, đọc cameraId từ table validated_device. 

3

AdminLayoutController

Nhận DeviceEvent, hiển thị dialog xác nhận, gọi registerDeviceForSync()

4

DeviceSyncQueue

Thêm SyncContext vào hàng đợi, cập nhật DeviceMiniStatus

5

DataSyncWorker

Lấy entry từ queue, kéo file qua ADB, gọi DataSyncService.saveFile()

6

DataSyncService

Insert DB (status=SYNCED), gọi DataBackupService.enqueue()

7

DataBackupQueue

Thêm localPath vào hàng đợi backup

8

DataBackupWorker

Copy file sang backupDir, gọi DataBackupService.markBackup()

9

FileRepository

Update DB: status=BACKEDUP, backed_up_path, backed_up_at

# 2. Data Sync Pipeline

## 2.1 Các thành phần chính

**Class / Interface**

**Package**

**Vai trò**

DataSyncRunner

modules/datasync

Quản lý lifecycle của sync worker thread và device tracker thread

DataSyncWorker

modules/datasync/workers

Runnable chính: lấy SyncContext từ queue, thực hiện sync từng file

DeviceSyncQueue

modules/datasync/queues

BlockingQueue<Entry> với dedup bằng ConcurrentHashSet

DataSyncService

modules/datasync/services

Business logic: resolve userId/deviceId, insert DB, enqueue backup

SyncContext

common/dtos

Record chứa thông tin session + device cho một lần sync

DeviceMiniStatus

common/services

Theo dõi trạng thái IDLE/QUEUED/SYNCING/COMPLETED cho từng camera

FileSyncCompletedEvent

modules/datasync/events

Event publish sau mỗi file sync thành công

## 2.2 DataSyncRunner – Quản lý Thread

DataSyncRunner là điểm khởi động và dừng an toàn cho toàn bộ sync pipeline.

**Method**

**Mô tả**

startSyncWorker()

Khởi động sync-worker thread. Chờ shuttingDown=false trước khi start (tránh race condition logout nhanh).

startDeviceTracker()

Khởi động device-tracker thread. Gọi startSyncWorker() trước.

resetForLogout()

Interrupt tracker thread, gọi worker.requestShutdown(), chờ join 5s trong background thread, force interrupt nếu quá timeout. Set shuttingDown=false sau khi xong.

shutdown() [@PreDestroy]

Interrupt cả hai thread khi Spring context đóng.

**Lưu ý quan trọng: **startSyncWorker() được gọi từ LoginService.onLoginSuccess(). DataSyncRunner không implement CommandLineRunner để tránh auto-sync khi chưa đăng nhập.

## 2.3 DeviceSyncQueue – Hàng đợi thiết bị

**Field / Method**

**Kiểu**

**Mô tả**

queue

LinkedBlockingQueue<Entry>

Hàng đợi blocking, thread-safe

inQueue

ConcurrentHashSet<String>

Dedup bằng cameraId để tránh enqueue trùng

currentCameraId

volatile String

Camera đang được sync (null nếu rảnh)

add(SyncContext)

boolean

Thêm vào queue, trả về false nếu đã tồn tại hoặc đang sync

take()

Entry

Blocking take, set currentCameraId

done(cameraId)

void

Xóa currentCameraId, gọi deviceMiniStatus.markDone()

remove(cameraId)

void

Xóa khỏi queue khi device ngắt kết nối, markCancelled()

clearAll()

void

Xóa toàn bộ queue khi logout

## 2.4 SyncContext – Dữ liệu context của một lần sync

SyncContext là Java record, được tạo một lần khi enqueue và không thay đổi trong suốt quá trình sync:

**Field**

**Ý nghĩa**

username

Username của người dùng đang đăng nhập lúc enqueue

isAdmin

true nếu là ADMIN (có thể sync file của mọi user trên camera)

saveDir

Thư mục sync đích – chụp tại thời điểm enqueue, không bị ảnh hưởng nếu settings thay đổi giữa chừng

autoDelete

Có xóa file trên device sau khi sync thành công không

deviceName

Tên hiển thị của thiết bị

hardwareId

Serial vật lý của device, dùng cho ADB

cameraId

Camera ID logic từ config.cson, dùng cho DB và queue

**canSync(fileUserName, fileCameraId): **ADMIN có thể sync tất cả file cùng cameraId; USER chỉ sync file có username khớp với session.

## 2.5 DataSyncService – Business Logic

**Method**

**Mô tả**

resolveDeviceId(cameraId)

Tìm device trong DB theo cameraId. Trả về null nếu chưa đăng ký (không tự tạo).

resolveUserId(username)

Tìm user trong DB. Nếu ADMIN và chưa có, tự tạo user mới với password mặc định (DEFAULT_SYNC_USER_HASH).

saveFile(userId, deviceId, fileName, localPath, info)

Insert vào bảng files (ON CONFLICT DO UPDATE theo name), gọi DataBackupService.enqueue(localPath).

loadSyncedFiles(deviceId)

Load danh sách file đã sync cho device để dedup trong memory.

setOnUserAutoCreated(listener)

Callback để notify UserManagementController khi có user mới được auto-create.

 

# 3. Data Backup Pipeline

## 3.1 Các thành phần chính

**Class**

**Package**

**Vai trò**

DataBackupRunner

modules/databackup

Quản lý lifecycle của backup worker thread

DataBackupWorker

modules/databackup/workers

Runnable: xử lý từng file trong backup queue

DataBackupQueue

modules/databackup/queues

LinkedBlockingQueue<String> với Set dedup theo localPath

DataBackupService

modules/databackup/services

Enqueue, markBackup(), recoverPendingBackups(), scheduled recovery

FileBackupCompletedEvent

modules/databackup/events

Event publish sau mỗi file backup thành công

## 3.2 DataBackupRunner – Quản lý Thread

Cấu trúc tương tự DataSyncRunner với cùng pattern shuttingDown để tránh race condition logout/login nhanh.

**Method**

**Mô tả**

startBackupWorker()

Khởi động backup-worker thread. Gọi từ LoginService.onLoginSuccess() sau startSyncWorker().

resetForLogout()

Gọi worker.requestShutdown(), join 5s trong background thread, force interrupt nếu timeout.

shutdown() [@PreDestroy]

Interrupt thread khi Spring context đóng.

## 3.3 DataBackupWorker – Logic xử lý backup

### 3.3.1 Luồng xử lý chính

Lấy nonDriveLetterSyncedPath từ DataBackupQueue.take() (blocking).

Gọi waitForSyncToComplete() – sleep 5s loop cho đến khi DeviceMiniStatus.isAnySyncActive() = false.

Kiểm tra shouldSkipDueToDeferred() – nếu backupRecoveryDeferred=true thì markDeferred và bỏ qua.

Gọi processBackup() để thực sự copy file.

### 3.3.2 processBackup() – Các bước kiểm tra trước khi copy

**Bước**

**Method**

**Hành động nếu thất bại**

1

isBackupDirConfigured()

Log warn, skip file

2

checkBackupDirAccessible()

Publish StorageUnavailableEvent(BACKUP, DRIVE_UNAVAILABLE), waitForBackupDirRecovery()

3

checkSufficientSpace()

Publish StorageUnavailableEvent(BACKUP, LOW_SPACE), waitForBackupDirRecovery()

4

performBackup()

Copy file, update DB, publish FileBackupCompletedEvent

 

### 3.3.3 waitForBackupDirRecovery() – Cơ chế chờ phục hồi

Khi backup dir không khả dụng, worker block tại synchronized(recoveryLock) { recoveryLock.wait() }. Có hai kết quả có thể xảy ra:

**Event nhận được**

**Kết quả**

StorageRestoredEvent(BACKUP)

backupRecoveryDeferred=false &rarr; worker tiếp tục xử lý file hiện tại

StorageRecoveryDeferredEvent(BACKUP)

backupRecoveryDeferred=true &rarr; worker bỏ qua file, markDeferred

 

## 3.4 DataBackupService – Recovery & Scheduling

**Method**

**Mô tả**

enqueue(localPath)

Gọi DataBackupQueue.add(). Dùng sau khi sync thành công.

markBackup(syncedPath, backedUpPath)

Update DB: status=BACKEDUP, backed_up_path, backed_up_at=now().

recoverPendingBackups()

Query DB lấy tất cả file status=SYNCED, re-enqueue vào backup queue. Dùng AtomicBoolean để tránh overlapping scan.

scheduledRecovery() [@Scheduled]

Chạy mỗi 1 giờ, publish StorageRestoredEvent(BACKUP) để trigger re-scan nếu không có drive event thực sự.

 

# 4. Queue Manager & Progress Tracking

## 4.1 QueueManagerService

QueueManagerService là facade tập trung cho ba tracker: SyncProgressTracker, BackupProgressTracker, ExportProgressTracker. Controller và Worker không tương tác trực tiếp với tracker mà đi qua service này.

**Nhóm**

**Methods quan trọng**

**Mô tả**

Sync

addDeviceToSyncTracker, addFileToSyncTracker, markSyncFileProcessing/Completed/Failed, markDeviceSyncCompleted

Theo dõi tiến trình sync theo device và file

Backup

addFileToBackupTracker, markBackupFileProcessing/Completed/Failed/Deferred

Theo dõi từng file backup

Export

addFileToExportTracker, markExportFile*, markExportDirectoryFinished

Theo dõi export (ngoài scope tài liệu này)

General

clearAll()

Xóa toàn bộ tracking khi logout

## 4.2 SyncProgressTracker

Lưu trữ Map<cameraId, DeviceQueueItem>, mỗi DeviceQueueItem chứa danh sách FileQueueItem. Mọi thay đổi publish QueueStatusChangedEvent để UI cập nhật real-time.

**ItemStatus**

**Ý nghĩa trong sync**

QUEUED

Device đã được add vào queue, chờ xử lý

PROCESSING

Đang sync file

COMPLETED

Sync xong, tất cả file thành công

COMPLETED_WITH_ERRORS

Sync xong nhưng có file lỗi

FAILED

File sync thất bại (có errorMessage và retryCount)

SKIPPED

File đã tồn tại, bỏ qua

## 4.3 BackupProgressTracker

Lưu trữ Map<filePath, FileQueueItem>. Đặc điểm riêng:

addFile(): nếu file đã tồn tại (re-queue sau DEFERRED), reset status về QUEUED thay vì bỏ qua.

getAllFiles(): Admin thấy tất cả file; User chỉ thấy file có username khớp (parse từ tên file theo FileInfo.parse()).

DEFERRED status: backup bị hoãn do drive không khả dụng, có errorMessage mô tả lý do.

 

# 5. Storage Health Monitoring

## 5.1 StorageHealthMonitor

Component kiểm tra khả năng truy cập của sync dir và backup dir, publish event tương ứng.

**Method**

**Mô tả**

checkStorageHealth(FolderType)

Kiểm tra accessibility. Nếu target=null kiểm tra cả hai. Bỏ qua nếu user chưa đăng nhập.

checkNow(FolderType)

Alias của checkStorageHealth, dùng cho external trigger.

**Kết quả kiểm tra**

**Event publish**

Accessible = false

StorageUnavailableEvent(target, DRIVE_UNAVAILABLE)

Accessible = true

StorageRestoredEvent(target)

## 5.2 Xử lý StorageUnavailableEvent trong AdminLayoutController

AdminLayoutController (qua StorageUnavailableEventHandler) track trạng thái storage block:

isStorageBlocked(FolderType.SYNC): nếu sync drive bị block, camera mới sẽ được thêm vào pendingStorageSyncs thay vì DeviceSyncQueue.

onStorageRestored(StorageRestoredEvent): nếu target=SYNC, gọi drainPendingSyncs() để re-enqueue tất cả camera đang chờ.

onStorageRestoreCompleted(): refresh dashboard, merge saved devices, refresh storage status UI.

## 5.3 Hiển thị Storage Status trên UI

AdminLayoutController.refreshStorageStatus() cập nhật ProgressBar và Label cho sync drive và backup drive:

**Trạng thái**

**CSS Class**

**Điều kiện**

Bình thường

(none)

Ratio < 75%

Cảnh báo

storage-warn

75% &le; ratio < 90%

Nghiêm trọng

storage-critical

ratio &ge; 90% hoặc drive không tồn tại

Không tìm thấy

storage-critical + ⚠

Folder/drive path không exist

# 6. Startup & Login Flow

## 6.1 LoginService.onLoginSuccess()

Được gọi ngay sau khi xác thực thành công. Thứ tự khởi tạo:

folderManager.init() – đảm bảo sync dir và backup dir tồn tại và accessible.

syncRunner.startSyncWorker() – khởi động sync-worker thread.

backupRunner.startBackupWorker() – khởi động backup-worker thread.

backupService.recoverPendingBackups() – re-enqueue file chưa backup từ DB.

## 6.2 Logout Flow (AdminLayoutController.logout())

deviceSyncQueue.clearAll() – xóa queue, markDone tất cả camera.

deviceMiniStatus.clearAll() – reset progress map.

queueManagerService.clearAll() – xóa sync/backup tracker.

backupRunner.resetForLogout() – graceful shutdown backup worker.

syncRunner.resetForLogout() – graceful shutdown sync worker và tracker.

session.clear() + MainApp.showLogin() – về màn hình đăng nhập.

Shutdown được thực hiện async trong background thread để không block UI thread.

# 7. Cấu hình quan trọng

**Key (AppConstants / application.properties)**

**Giá trị mặc định / Ý nghĩa**

dataSync.dataDir

Đường dẫn gốc của sync dir (cấu hình trong DB, key KEY_DATA_DIR)

dataBackup.backupDir

Đường dẫn gốc của backup dir (cấu hình trong DB, key KEY_BACKUP_DIR)

dataSync.isAutoDeleteAfterSync

true/false – xóa file trên camera sau khi sync

SYNC_FOLDER_NAME

sync_bdma.{21EC2020-3AEA-1069-A2DD-08002B30309D} – tên folder được bảo vệ

BACKUP_FOLDER_NAME

backup_bdma.{21EC2020-3AEA-1069-A2DD-08002B30309D} – tên folder backup

AppConstants.MAX_RETRY

3 – số lần retry tối đa cho mỗi file sync

FILE_STATUS_SYNCED

"SYNCED" – trạng thái sau khi sync thành công

FILE_STATUS_BACKEDUP

"BACKEDUP" – trạng thái sau khi backup thành công

DataBackupService @Scheduled

fixedDelay=1h – chu kỳ recovery scan tự động

# 8. Lưu ý & Quyết định thiết kế

**Vấn đề**

**Quyết định & Lý do**

Race condition logout/login nhanh

DataSyncRunner và DataBackupRunner dùng synchronized + volatile shuttingDown + wait(100ms loop) để đảm bảo worker cũ shutdown hoàn toàn trước khi start worker mới.

Backup worker chờ sync

DataBackupWorker.waitForSyncToComplete() sleep 5s loop kiểm tra DeviceMiniStatus.isAnySyncActive(). Backup chạy sau khi sync xong để tránh copy file chưa hoàn chỉnh.

Drive letter thay đổi

Tất cả path lưu DB không có drive letter. FileUtil.stripDriveLetter() dùng trước khi save, FolderManagerService resolve drive letter khi thực sự cần truy cập file.

File bị dedup

DeviceSyncQueue dùng ConcurrentHashSet inQueue; DataBackupQueue cũng dùng Set. Tránh enqueue cùng một file/camera nhiều lần.

Recovery sau mất điện/restart

LoginService gọi recoverPendingBackups() mỗi lần login. Scheduled recovery mỗi 1h đảm bảo không bỏ sót file.

Backup dir không khả dụng

Worker block tại recoveryLock.wait(). User có thể chọn: (1) cắm lại ổ đĩa &rarr; StorageRestoredEvent &rarr; tiếp tục; (2) hoãn &rarr; StorageRecoveryDeferredEvent &rarr; worker skip, đánh dấu DEFERRED.

Non-admin chỉ thấy file của mình

BackupProgressTracker.getAllFiles() filter theo username parse từ tên file (FileInfo.parse()). SyncContext.canSync() kiểm tra tương tự khi sync.