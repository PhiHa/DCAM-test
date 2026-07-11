# Device Management Tab Specification

**Page ID**: 33357826  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/33357826

---


Device Management Tab Specification

# 1. Mục tiêu

Bổ sung tab Quản lý thiết bị bên cạnh tab Quản lý người dùng trong màn hình quản trị. Tab này dùng để xem danh sách thiết bị BodyCam, đổi tên hiển thị và ẩn/hiện thiết bị cùng dữ liệu liên quan.

# 2. Phạm vi chức năng

Chức năng

Mô tả

Tab Quản lý thiết bị

Thêm tab mới trong màn hình quản trị, đặt cạnh tab Quản lý người dùng.

Danh sách thiết bị

Hiển thị các thiết bị BodyCam đã được hệ thống ghi nhận.

Đổi tên hiển thị

Cho phép đổi Display Name, không thay đổi Device ID gốc.

Show/Hide thiết bị

Cho phép ẩn hoặc hiện thiết bị trong hệ thống.

Ẩn/hiện dữ liệu liên quan

Khi thiết bị bị ẩn, dữ liệu liên quan cũng bị ẩn khỏi các màn hình người dùng thông thường.

# 3. Gợi ý hiển thị danh sách

Danh sách thiết bị có thể hiển thị dạng bảng hoặc card, tối thiểu gồm các trường sau:

Trường

Ý nghĩa

Device ID

Mã định danh gốc của thiết bị.

Display Name

Tên hiển thị do người dùng đặt.

Visibility

Shown / Hidden.

Last Sync

Thời điểm đồng bộ gần nhất nếu có.

Actions

Rename, Hide, Show.

# 4. Quy tắc dữ liệu

·       Device ID không được thay đổi.

·       Đổi tên chỉ ảnh hưởng Display Name trên giao diện.

·       Hide là ẩn mềm, không xóa thiết bị và không xóa dữ liệu.

·       Thiết bị bị Hidden không hiển thị với user thông thường.

·       Dữ liệu của thiết bị Hidden không hiển thị mặc định trong MediaView, File List và Search.

·       Admin vẫn có thể xem thiết bị Hidden trong tab Quản lý thiết bị nếu có filter phù hợp.

·       Show lại thiết bị sẽ khôi phục hiển thị thiết bị và dữ liệu liên quan.

# 5. Trạng thái rỗng

Nếu chưa có thiết bị nào được ghi nhận, hiển thị:

Chưa có thiết bị nào được ghi nhận.

# 6. Acceptance Criteria

·       Tab Quản lý thiết bị được hiển thị cạnh tab Quản lý người dùng.

·       Danh sách thiết bị BodyCam được hiển thị trong tab Quản lý thiết bị.

·       Người dùng có thể đổi tên hiển thị thiết bị.

·       Device ID gốc không bị thay đổi khi đổi Display Name.

·       Người dùng có thể Hide và Show lại thiết bị.

·       Thiết bị Hidden và dữ liệu liên quan không hiển thị với user thông thường.

·       Show lại thiết bị khôi phục hiển thị dữ liệu liên quan.

·       Không xóa dữ liệu khi Hide thiết bị.

·       Tab Quản lý người dùng vẫn hoạt động bình thường.

# 7. Ghi chú mở rộng

Tính năng này là nền tảng cho Device Management Module. Các chức năng có thể mở rộng sau: hiển thị pin, trạng thái sạc, bộ nhớ, trạng thái đồng bộ và Device Health Score.