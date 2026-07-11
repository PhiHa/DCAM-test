# Weekly Team Meeting 6.1

**Page ID**: 34078721  
**Version**: 2  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/34078721

---


# Weekly Team Meeting 6.1

# 1. Tổng kết công việc tuần qua

Thành viên / Nhóm

Done

Blocker

Next plan

Việt Anh

Hoàn thiện chức năng hiển thị thiết bị trước đăng nhập và đồng bộ trạng thái login. Qua test hiện tại, chức năng đã đăng nhập/hiển thị được.

Cần tiếp tục kiểm tra lại luồng đồng bộ trạng thái login.

Đẩy lại code/PR để đồng nghiệp test lại.

Việt Anh

Triển khai chức năng restore từng file missing thay vì restore toàn bộ. Khi bật Media View hoặc export có thể restore từng file đơn lẻ.

Cần hoàn thiện và đưa vào review/test.

Hoàn thiện task restore file đơn lẻ và đẩy PR/code review.

Phi

Đã đẩy code liên quan đến UI để test và nhờ đồng nghiệp test lại.

Chưa rõ còn issue UI nào tồn đọng sau test.

Fix comment review nếu có.

Phi

Thay đổi phương án xử lý khi mất database: bỏ trigger lưu thay đổi, chuyển sang copy trực tiếp file database làm nguồn restore khi phát hiện database cũ bị xóa/thiếu.

Phương án trigger trước đó dễ gây lỗi và rollback. Phương án copy file cần tiếp tục theo dõi.

Theo dõi/kiểm tra lại phương án restore database mới để đảm bảo an toàn dữ liệu.

Phi

Triển khai cảnh báo khi tắt app có tiến trình đang chạy: OK để tắt app, Cancel để tiếp tục tiến trình. Đã đưa code vào code review.

Cần Việt Anh xem/test lại.

Việt Anh kiểm tra code review và test chức năng này.

Việt Anh & Phi

Đang hoàn thiện các task hiện có.

Cần tiếp tục nắm quy trình release để hỗ trợ team.

Sau khi hoàn thiện task hiện tại, cùng tìm hiểu quy trình tạo release để hỗ trợ Nhân khi cần.

Nhân

Chuẩn bị tạo bản release.

Cần xác nhận: version, scope release, tài liệu đi kèm.

Thứ Hai tuần sau tạo bản release và chuẩn bị tài liệu release.

# 2. Blockers

ID

Blocker

Ảnh hưởng

Hướng xử lý

BLK-001

Whitelist vẫn chưa lọc được hết thiết bị không hợp lệ từ phía client.

Có nguy cơ sync/backup nhầm thiết bị lạ hoặc không hợp lệ.

Tiếp tục kiểm tra logic whitelist và test với dữ liệu thiết bị thực tế.

BLK-002

Phương án kỹ thuật ban đầu dùng trigger để lưu thay đổi vào bảng mới dễ gây lỗi và rollback.

Có thể ảnh hưởng tới độ ổn định khi xử lý database.

Đã chuyển sang phương án copy file database; cần theo dõi thêm sau khi test.

# 3. Decisions

## DEC-001 - Release phải có tài liệu đi kèm

Trường

Nội dung

Quyết định

Khi tạo bản release cho khách hàng, cần có tài liệu release đi kèm.

Tài liệu liên quan

Release Notes, mô tả phần mềm, thông tin thay đổi chính.

Owner

PM / Nhân

Trạng thái

Agreed

## DEC-002 - Release chuyển sang QA/kiểm thử sau khi tạo

Trường

Nội dung

Quyết định

Sau khi tạo release, bản release sẽ được chuyển sang phòng QA/kiểm thử.

Hướng xử lý nếu có lỗi

Nếu cần hotfix thì xử lý tiếp theo quy trình hotfix/release.

Owner

PM / Nhân

Trạng thái

Agreed

# 4. Action Items

Owner

Action Item

Thời hạn

Trạng thái

Việt Anh

Đẩy lại code để test chức năng hiển thị thiết bị trước đăng nhập và đồng bộ trạng thái login.

Tuần tới

Open

Phi

Đẩy lại code cho chức năng restore file đơn lẻ và nhờ review.

Tuần tới

Open

Việt Anh

Test/kiểm tra code review liên quan cảnh báo tắt app khi có tiến trình đang chạy.

Tuần tới

Open

Việt Anh & Phi

Hoàn thiện các task hiện có.

Tuần tới

Open

Việt Anh & Phi

Tìm hiểu quy trình tạo release để hỗ trợ Nhân khi cần.

Sau khi hoàn thiện task hiện có

Open

Nhân

Tạo bản release vào thứ Hai tuần sau.

Thứ Hai tuần sau

Open

Nhân

Chuẩn bị tài liệu release gồm release notes và mô tả phần mềm.

Trước khi chuyển QA

Open

# 5. Risks

ID

Rủi ro

Mức độ

Hành động giảm thiểu

RISK-001

Whitelist chưa loại được hết thiết bị không hợp lệ từ phía client.

High

Tiếp tục test với thiết bị thực tế, bổ sung rule lọc thiết bị, ghi nhận các case thiết bị lạ.

RISK-002

Phương án restore database mới chuyển từ trigger sang copy file, vẫn cần kiểm chứng thêm.

Medium

Theo dõi trong quá trình test, bổ sung test case mất database/xóa database/restore database.

RISK-003

Quy trình release chưa chuẩn hóa đầy đủ tài liệu đi kèm.

Medium

Hoàn thiện Release Notes Template, Release Checklist và quy định release phải có tài liệu trước khi chuyển QA.