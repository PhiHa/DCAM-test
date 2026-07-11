# Báo cáo đánh giá BDMA-124 & PR #120

**Page ID**: 38371332  
**Version**: 1  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/38371332

---


# BÁO CÁO ĐÁNH GIÁ TASK BDMA-124 & PR #120

**Ngày:** 17/06/2026 | **Người đánh giá:** Rovo AI Assistant | **Dự án:** BDMA

## PHẦN 1: ĐÁNH GIÁ TASK BDMA-124

**Task:** [BDMA-124](https://ducviet.atlassian.net/browse/BDMA-124) — Display specifications of device when connecting

### ✅ Điểm tốt

**Acceptance Criteria rõ ràng:** 6 tiêu chí cụ thể, có thể kiểm tra được (battery %, charging status, storage usage, cảnh báo pin < 20%, cảnh báo storage > 80%, tự động cập nhật).

**Có UI mockup đính kèm**, giúp dev hiểu rõ expected output.

**Story points hợp lý** (3 points) cho scope công việc này.

**Collaboration tốt:** Có trao đổi review qua lại giữa Dinh Nhan và lavietanh.utc, đang ở vòng review thứ 2-3 trên PR #120.

### ⚠️ Điểm cần lưu ý

**Trễ deadline 5 ngày:** Due date là 12/06, hôm nay đã 17/06 mà vẫn đang ở trạng thái Code Review. Task cũng đã bị kéo dài từ Sprint 3 (Display Data) sang Sprint 4 (Release Management).

**PR review kéo dài:** Vòng review đầu tiên từ ngày 12/06, đến hôm nay 17/06 vẫn còn đang sửa qua lại. Điều này có thể cho thấy scope ban đầu chưa được align kỹ giữa dev và reviewer, hoặc có vấn đề kỹ thuật phát sinh.

**Thiếu một số thông tin:** Không có time estimate, không có labels, và description chưa đề cập đến edge cases (ví dụ: thiết bị mất kết nối thì hiển thị trạng thái gì? Refresh interval bao lâu?).

### 💡 Gợi ý

Kiểm tra trực tiếp trên PR #120 xem còn bao nhiêu comment chưa resolve, và cân nhắc cập nhật lại due date cho phù hợp với thực tế. Nếu PR sắp merge thì cũng nên plan luôn cho bước testing.

## PHẦN 2: ĐÁNH GIÁ CHI TIẾT PR #120

**PR:** [#120 — Display specifications of device when connecting](https://github.com/DucVietTech/bdma/pull/120)  
**Tác giả:** vietanh1910 (lavietanh.utc)### 🔴 Lỗi nghiêm trọng (cần fix ngay)

**Logic Error — Nhầm điều kiện Battery/Storage:** Trong `buildSpecificationLines`, code dùng `info.isLowBattery()` để apply style `max-storage` cho storage icon → **sai logic**. Phải là `info.isMaxUsage()`:

// SAI
if (info != null && info.isLowBattery()) {
    storageIcon.getStyleClass().add("max-storage");
}
// ĐÚNG
if (info != null && info.isMaxUsage()) {
    storageIcon.getStyleClass().add("max-storage");
}
### 🟡 Rủi ro NullPointerException (5 chỗ)

Vị trí

Vấn đề

`isConnected()`

So sánh `status == DeviceStatus.CONNECTED` không null-safe → nên dùng `DeviceStatus.CONNECTED.equals(status)`

`DeviceClickHandler`

Không check `onRequestSync` null trước khi gán handler

`formatProgress()`

Không check `progress` null trước khi gọi `.total()`, `.passed()`

`parseBytesToKiB()`

`value` có thể null → NPE khi chia, catch `NumberFormatException` không đúng loại exception

`getDeviceSpecInfo()`

Gọi nhiều external service (`adbClient`, `driveLetterMapper`, `massStorageService`) không có try-catch

### 🟡 Vấn đề Performance

`refreshSpecification()` gọi `adbClient.getBatteryInfo()` **tuần tự** cho từng device → nếu nhiều thiết bị sẽ rất chậm. Reviewer đề xuất dùng `ExecutorService` để gọi song song.

### 🔵 Code Quality & CSS (7 comments)

**CSS trùng lặp:** 5 badge classes có cùng properties, chỉ khác màu → nên tách ra class chung `.device-cell-badge`

**CSS hover trùng:** `.device-card:hover` và `.device-card.connected:hover` giống nhau → gộp lại

**Hardcoded colors:** Badge colors dùng giá trị cứng thay vì dùng semantic variables

**Sai cú pháp JavaFX CSS:** `-fx-background-color: -app-bg` → phải dùng `var(--app-bg)`

**Type safety:** `batteryLevel`, `storageUsePercent` lưu dạng `String` thay vì `Integer`

**Silent failure:** `parsePercent()` nuốt exception không log

### ⚠️ Merge Conflicts chưa giải quyết

Có conflict ở **6 files** quan trọng:

`DeviceManagementController.java`

`AdminLayoutController.java`

`DashboardController.java`

`AdbClient.java`

`DeviceListState.java`

`GuestDashboardController.java`

### 📊 Tổng kết

Mức độ

Số lượng

🔴 Bug nghiêm trọng

1

🟡 Rủi ro runtime (NPE, Performance)

6

🔵 Code quality / CSS

7

⚠️ Merge conflicts

6 files

**Tổng review comments**

**~14**

### 🎯 Hành động đề xuất (theo thứ tự ưu tiên)

Fix bug logic Battery/Storage trong `buildSpecificationLines`

Thêm null-checks cho 5 điểm NPE đã xác định

Resolve merge conflicts ở 6 files

Cân nhắc dùng `ExecutorService` cho `refreshSpecification()`

Refactor CSS trùng lặp và sửa cú pháp JavaFX CSS

Đổi `batteryLevel`/`storageUsePercent` từ `String` sang `Integer`

**Đánh giá:** PR này **chưa sẵn sàng merge**. Lỗi logic Battery/Storage là critical bug sẽ ảnh hưởng trực tiếp đến UX. Cộng thêm 5 điểm có thể gây NPE và merge conflicts chưa resolve, dev cần ít nhất **1-2 ngày nữa** để fix và đưa lên review lại.