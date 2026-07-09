# Sprint Meeting 4.1

**Page ID**: 6225923  
**Version**: 6  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/6225923

---


##  Date

 

##  Participants

[DVID-S&T](https://quyendt3k4.atlassian.net/people/team/8d961dda-ffe7-46c5-859b-624513b1235b) (  )

Type @ name to mention a person and add them as a meeting attendee - they will be notified

##  Goals

**Done** - Tuần này team đã làm được những gì?

**What's next **- Tuần tới team sẽ làm gì?

**Reslove** - Các thành viên trong nhóm có vướng mắc gì cần tháo gỡ không?

 Brainstorm

Type /whiteboard to create an interactive canvas for icebreakers, brainstorms, diagramming, retros, and more
##  Discussion topics

**Topic**

**Presenter**

**Notes**

**Done **

 

Hoàn thành code base của app gồm: 
 - Build và đóng gói thành file .exe đẩy lên releasse. 
 - Hoàn thành chức năng cơ bản login.
 - Hoàn thành chức năng tự động update app khi có bản releasse mới
 - Hoàn thành chức năng ghi lại log và đẩy log lên loggly.
Chức năng User Setting: 
 - Hoàn thành phần setting thư mục lưu trữ back up và hạn chế quyền truy cập từ ngoài app vào 2 chức năng này. 
 - Hoàn thành chức năng setting có xóa dữ liệu của BodyCam sau khi chuyển dữ liệu vào máy qua app không? ( Chỉ có setting chưa có chức năng áp dụng setting này). 
 - Hoàn thành module user để admin quản lý tài khoản. 
 - Có setting ngôn ngữ theme theo tài khoản đăng nhập.

|   

Thiết lập môi trường làm việc. Tham gia các nhóm quản lý tiến độ.

Thiết kế sơ lược database để cùng th��o luận, thống nhất các đối tượng cần lưu trữ quản lý và cách đặt tên các field.

Cải thiện UX/UI: live search, live validate, chuyển các setting vào một popup, sửa các lỗi hiển thị, thêm các thông báo lỗi cho các trường hợp còn thiếu.

Mã hoá database, thêm các điều kiện validate username & mật khẩu khi thêm/sửa user. 

Tối ưu source code, fix các warning của SonarQube. Gom các chức năng tạo user mới, edit user cũ, xem thông tin các nhân vào cùng một luồng xử lý để giảm thiểu trùng lặp code. 

Get deviceID theo MachineGuid của thiết bị (chỉ thay đổi khi user cài lại hệ điều hành). Fix lỗi không xoá được thư mục tmp khi tắt phần mềm, fix lỗi các dòng log sớm khi app mới chạy không có kèm deviceID.

**What's next**

 

Test chéo lại các chức năng đã hoàn thành trên môi trường thực tế

Trao đổi phối hợp làm việc nhóm với thành viên mới trong team (nếua có)

Tìm hiểu sản phẩm phần cứng CameraBody thực tế để hoàn thiện knowledge

**Reslove**

 

Cần lấy deviceId là thông tin phần cứng ít thay đổi của máy tính để tránh mất thông tin khi xóa dữ liệu.

Cần mã hóa các thư mục config, db để bảo mật thông tin app.

Khi xây dựng app cần build có các tùy chọn xóa data khi gỡ cài đặt.

Khi build ci/cd thì cần chia ra các bản thử nghiệm thì chỉ để build thành atifact để tải về không tạo bản releasse luôn. Khi bản thử nghiệm test ok thì mới đẩy lên thành bản releasse.

Tất cả các folder data, backup đều cần hạn chế quyền truy cập. Và khi thay đổi thư mục lưu thì vẫn giữ nguyên trạng thái hạn chế quyền truy cập với các file cũ để tránh người dùng thay đổi để vượt qua.

|   

Nên dành ít thời gian đầu để cùng thảo luận từ các chức năng chính của yêu cầu dự án
+ Thiết kế sơ bộ ra danh sách các màn hình cần có (không cần đẹp, chỉ cần bố cục tổng quan, các button, các field nào cần phải có) tránh việc mỗi người code mỗi ý rồi sau này lại phải xoá bớt để tổng hợp, chỉnh sửa lại. 
(vd: Màn hình setting của app sẽ gồm những mục setting nào? Tất cả setting của app gom chung vào 1 trang setting lớn hay sẽ chia nhỏ theo từng chức năng, mỗi trang chức năng sẽ có 1 nút setting nhỏ riêng? theme/language nằm ở 1 nút setting nhỏ riêng hay gom chung với các setting đường dẫn?..v..v…)

+ Lập ra các flow hiển thị/xử lý của mỗi chức năng (vd: từ homepage thì bấm đâu để sang màn hình backup, ở trang backup thì màn hình backup sẽ gồm những field nào, bấm nút backup sẽ hiển thị gì, nhảy tới màn hình nào) để lúc code sẽ trôi chảy hơn, tránh xung đột ý tưởng khi teamwork chung trên cùng 1 màn hình.

##  Action items

2
87618c380c3e
incomplete

##  Decisions

Type /decision to record the decisions you make in this meeting:

7547aafc-ce18-4d83-949e-973cc72224fa46561588-dfd9-4abe-a9af-e4fd3f290cd4DECIDED
 Related info

Type /database to create a database of related information, meetings, or assets