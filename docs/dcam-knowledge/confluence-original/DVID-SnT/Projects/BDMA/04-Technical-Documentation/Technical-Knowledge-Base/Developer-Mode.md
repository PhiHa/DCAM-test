# Developer Mode

**Page ID**: 25755735  
**Version**: 9  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/25755735

---


*Tính năng dành cho đội ngũ kỹ thuật thao tác trực tiếp tại chỗ/remote. Hoặc hướng dẫn khách hàng thao tác khi cần cập nhật dữ liệu.

Luồng: 
Login với tài khoản Dev &rarr; Hiển thị dialog TOTP &rarr; Xác thực &rarr; Trang Developer

Chức năng:
- Import file sql mã hoá
- Export script sql thành file mã hoá
- …

Mô tả:

Tài khoản Dev:
- Là một record đặc biệt trong bảng users. Tồn tại mặc định từ đầu, không thể tạo/xoá
- Không hiển thị trong màn hình quản lý user, không thể tạo user trùng với tài khoản dev
- Username/Password: dev / dev (đơn giản, dễ nhớ)

Bảo mật:
- Vì mật khẩu có thể bị ghi lại ở máy client nên sẽ không dựa vào nó. TOTP sẽ là cơ chế chính để xác thực truy cập. Mỗi khi tài khoản Dev đăng nhập đều phải cung cấp thêm mã OTP.
- App sẽ có 1 hệ thống generate OTP từ secretKey + thời gian trên máy (không cần internet). Dev nhập secretKey vào các app 2FA trên điện thoại cá nhân (như Google Authenticator, Authy,…) sẽ có được OTP.
- secretKey sẽ được set cứng trong app, tránh việc quản lý hàng ngàn TOTP khác nhau ứng với mỗi client, đội ngũ dev sẽ tự bảo quản key này
- secretKey sẽ được truyền vào khi build (thông qua github secret, tránh để lộ trong source code)
- OTP môi trường release:

......

- OTP môi trường IDE/local test:

......

- Sau 5 phút dev session sẽ hết hạn, cần cung cấp OTP để tiếp tục hoặc buộc phải đăng xuất:

Tự động hiện sau mỗi 5 phút

- Các chức năng nâng cao (tạo bản vá, đổi mật khẩu,..) cũng sẽ được bảo vệ bằng OTP (mở khoá chức năng cũng sẽ reset đếm ngược của session về lại 5p)

Giao diện:
- Gồm tab import và export sql.

Tab import:
- Cho phép select file.
- Giải mã và validate nếu đúng sql hợp lệ thì tiến hành áp dụng vào db của máy.

Màn hình Nhập bản vá

Tab export:
- Được bảo vệ bằng TOTP.
- Gồm panel cho phép tạo SQL theo các kịch bản đơn giản, text area để nhập SQL thủ công, nút nạp SQL cho phép load nội dung file SQL lên trình soạn thảo, và nút Tạo bản vá để xuất file vá đã mã hoá, sẵn sàng giao tới khách hàng.
- Syntax lệnh sql sẽ được kiểm tra liên tục, chỉ cho phép export nếu hợp lệ.

Màn hình Tạo bản vá