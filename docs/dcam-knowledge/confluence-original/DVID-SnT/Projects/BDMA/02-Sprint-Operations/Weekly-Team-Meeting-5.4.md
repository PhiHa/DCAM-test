# Weekly Team Meeting 5.4

**Page ID**: 30539808  
**Version**: 1  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/30539808

---


# Weekly Team Meeting 5.4

**Ngày họp:** 30/05/2026

**Chủ trì:** PM

**Thành viên tham gia:** PM, Việt Anh, Phi, Hoàn, Nhân

## 1. Tổng kết công việc tuần qua

Thành viên

Công việc hoàn thành

Khó khăn / Vướng mắc

Kế hoạch tuần tới

Việt Anh

Onboarding dự án, tìm hiểu source code và quy trình. Thực hiện chức năng hiển thị thiết bị trước đăng nhập.

Đồng bộ trạng thái thiết bị giữa Login/Logout và trạng thái xác thực.

Hoàn thiện chức năng, xử lý đồng bộ trạng thái và submit Code Review.

Phi

Điều chỉnh UI Media View. Chỉnh kích thước button, border, màu sắc. Bổ sung xử lý lỗi Database và khởi động lại ứng dụng.

Chưa quen CDS UI Framework. Đang nghiên cứu cơ chế mã hóa/giải mã dữ liệu.

Tiếp tục nghiên cứu mã hóa và chuẩn bị triển khai task mới.

Hoàn

Hoàn thành chức năng tạo file database mã hóa. Hoàn thiện tài liệu build, release và Media Sync. Hỗ trợ onboarding thành viên mới.

Không có.

Hoàn tất bàn giao trước khi nghỉ việc.

Nhân

Rework Loggly Module. Triển khai DevMode, OTP, Patch Management và SQL Tool.

Không có.

Tiếp tục hoàn thiện DevMode và các tính năng quản trị.

## 2. Khó khăn và trở ngại

Thành viên

Vấn đề

Hướng xử lý

Việt Anh

Chưa nắm rõ luồng đồng bộ trạng thái thiết bị.

Tiếp tục tìm hiểu hệ thống, trao đổi với các thành viên cũ.

Phi

Tiếp cận CDS Framework và cơ chế mã hóa còn chậm.

Tăng cường trao đổi, nghiên cứu tài liệu kỹ thuật.

## 3. Quyết định

### DEC-001 – Cập nhật Jira Workflow

Cho phép chuyển trạng thái từ **Testing** về **In Progress** khi phát hiện lỗi trong quá trình kiểm thử.

**Workflow mục tiêu:** To Do → In Progress → Code Review → Testing → Done. Testing → In Progress khi phát hiện lỗi.

**Owner:** PM

**Trạng thái:** Planned

## 4. Action Items

Owner

Action Item

Thời hạn

PM

Cập nhật Jira Workflow hỗ trợ Testing → In Progress

Sprint 5.5

PM

Đảm bảo hoàn tất bàn giao kiến thức từ Hoàn

Trước khi nghỉ việc

Việt Anh

Hoàn thiện chức năng hiển thị thiết bị trước đăng nhập

Sprint 5.5

Phi

Hoàn thành nghiên cứu cơ chế mã hóa/giải mã

Sprint 5.5

Nhân

Hoàn thiện DevMode

Sprint 5.5

## 5. Rủi ro

ID

Rủi ro

Mức độ

Hành động

RISK-001

Hoàn nghỉ việc dẫn tới nguy cơ mất tri thức dự án

Medium

Hoàn tất bàn giao tài liệu, kiến thức và ownership các module liên quan.