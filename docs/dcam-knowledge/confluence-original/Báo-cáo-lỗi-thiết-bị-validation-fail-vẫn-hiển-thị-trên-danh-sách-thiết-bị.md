# Báo cáo lỗi: thiết bị validation fail vẫn hiển thị trên danh sách thiết bị

**Page ID**: 32735234  
**Version**: 1  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/32735234

---


Ngày báo cáo: 04/06/2026

**Tóm tắt**

Có lỗi UI khiến thiết bị không vượt qua validation vẫn được hiển thị thành card trạng thái UNVALIDATED trong danh sách thiết bị.

Thiết bị hiển thị với cameraID rỗng, trong khi cameraID là điều kiện bắt buộc để xác định thiết bị hợp lệ. Khi double click card, dialog lưu thiết bị không hoạt động đúng vì validation result thực tế là invalid.

**Phạm vi và ảnh hưởng**

Danh sách thiết bị có thể hiển thị thiết bị không hợp lệ.

Người dùng có thể hiểu nhầm thiết bị đã qua bước xác thực.

Card thiếu cameraID nên không thể lưu thiết bị hợp lệ.

Luồng double click bị lệch: UI có card nhưng logic lưu thiết bị từ chối vì result invalid.

**Nguồn PR/commit gây lỗi**

PR: [https://github.com/DucVietTech/bdma/pull/55](https://github.com/DucVietTech/bdma/pull/55)

Tên PR: Bdma 60 restore data to backup

Branch nguồn: BDMA-60-Restore-data-to-backup

Branch đích: develop

Commit: a54388d

Thời gian commit: 2026-05-08 16:12:04 +0700

Quy mô PR: 43 files changed, +1,468 / -519.

**Chi tiết lỗi kỹ thuật:**

Trong DashboardController.java, PR #55 thêm nhánh xử lý DeviceEvent.EventType.UNVALIDATED và thêm method handleUnvalidated().

Logic hiện tại chỉ kiểm tra result == null, nhưng không kiểm tra result.isValid(). Vì vậy DeviceValidationResult tồn tại nhưng valid=false vẫn đi tiếp vào addTransientDevice(result).

addTransientDevice(result) tạo DeviceSummary với Status.UNVALIDATED và cameraID lấy từ result.getCameraId(). Khi result invalid, cameraID có thể rỗng/null, dẫn tới card rỗng cameraID xuất hiện trên UI.

**Khắc phục:**

Sửa trong DashboardController.handleUnvalidated(): đổi điều kiện từ:

if (result == null) {
    return;
}

thành:

if (result == null || !result.isValid()) {
    return;
}