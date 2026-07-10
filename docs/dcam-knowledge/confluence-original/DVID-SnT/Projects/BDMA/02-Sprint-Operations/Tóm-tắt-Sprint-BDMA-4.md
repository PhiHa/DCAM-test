# Tóm tắt Sprint BDMA-4

**Page ID**: 47775837  
**Version**: 1  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47775837

---


Bản tóm tắt ngắn gọn, sẵn sàng chia sẻ cho cấp quản lý về sprint vừa hoàn thành.

# Tổng quan

**Sprint:** BDMA-4 Release Management

**Thời gian:**  &rarr;  (kế hoạch), đóng thực tế 

**Mục tiêu sprint:** Phát hành chính thức phần mềm và hỗ trợ dịch vụ

**Kết quả:** Đạt mục tiêuGreen — phát hành BDMA v1.0.133 vào 

Sprint hoàn thành 18/18 issue, bao gồm 15 Task và 3 Bug; toàn bộ phạm vi cam kết đã được bàn giao.

# Thành tựu chính

**Phát hành chính thức phiên bản BDMA v1.0.133:** Hoàn thành các hạng mục trọng tâm phục vụ mục tiêu phát hành, gồm Tab Vị trí với điểm đánh dấu bản đồ, Bookmark, Quản lý thiết bị và giải mã video ngoài. Đồng thời nâng cấp pipeline CI/CD với Cloudflare R2 publishing qua [BDMA-155](https://ducviet.atlassian.net/browse/BDMA-155) để hỗ trợ quy trình phát hành ổn định hơn.

**Xử lý dứt điểm toàn bộ lỗi trong sprint:** Bao gồm lỗi phát audio/SOS mức ưu tiên cao tại [BDMA-130](https://ducviet.atlassian.net/browse/BDMA-130) và lỗi installer chặn phát hành tại [BDMA-156](https://ducviet.atlassian.net/browse/BDMA-156), giúp bảo vệ chất lượng bản phát hành cuối cùng.

# Blocker và rủi ro chính

**Lỗi installer được phát hiện muộn:** [BDMA-156](https://ducviet.atlassian.net/browse/BDMA-156) được phát hiện vào , gần cuối sprint. Nguyên nhân là quá trình cài đặt thất bại khi file `.exe` bị đổi tên. Lỗi đã được xử lý vào . **Tác động:** kéo dài thời gian kiểm tra và xác nhận bản phát hành, góp phần làm sprint đóng muộn.

**Hoàn tất hạng mục trọng yếu vào ngày đóng sprint:** Tab Vị trí tại [BDMA-136](https://ducviet.atlassian.net/browse/BDMA-136) và Bookmark tại [BDMA-153](https://ducviet.atlassian.net/browse/BDMA-153) chỉ hoàn thành vào . **Tác động:** sprint vượt kế hoạch 5 ngày trước khi có thể chốt hoàn toàn.

# Thay đổi phạm vi

**Thêm mới trong sprint:** 9 trên 18 issue được tạo sau khi sprint bắt đầu, bao gồm các hạng mục về refactor UI, cải thiện CI, luồng cập nhật ứng dụng và Bookmark. Việc bổ sung này làm tăng áp lực hoàn thành vào giai đoạn cuối sprint.

**Carry-over từ Sprint 3:** 4 hạng mục được kéo sang gồm [BDMA-86](https://ducviet.atlassian.net/browse/BDMA-86), [BDMA-85](https://ducviet.atlassian.net/browse/BDMA-85), [BDMA-88](https://ducviet.atlassian.net/browse/BDMA-88) và [BDMA-102](https://ducviet.atlassian.net/browse/BDMA-102); tất cả đều đã hoàn thành trong sprint này.

**Loại bỏ khỏi phạm vi:** Không có issue nào bị bỏ hoặc chuyển trả về backlog.

# Khuyến nghị cho sprint tiếp theo

**Khóa phạm vi sớm hơn:** Nên thiết lập thời điểm đóng băng scope từ sớm trong sprint để hạn chế việc bổ sung quá nhiều hạng mục giữa chừng và giúp dự báo tiến độ chính xác hơn.

**Dành vùng đệm cho kiểm thử phát hành:** Bổ sung 2–3 ngày hardening trước mốc kết thúc kế hoạch để kiểm thử installer, quy trình update và các tình huống cận phát hành.

# Liên kết quan trọng

[BDMA-155](https://ducviet.atlassian.net/browse/BDMA-155) — Cloudflare R2 publishing cho CI/CD

[BDMA-130](https://ducviet.atlassian.net/browse/BDMA-130) — Lỗi audio/SOS mức ưu tiên cao

[BDMA-156](https://ducviet.atlassian.net/browse/BDMA-156) — Lỗi installer chặn phát hành

[BDMA-136](https://ducviet.atlassian.net/browse/BDMA-136) — Tab Vị trí

[BDMA-153](https://ducviet.atlassian.net/browse/BDMA-153) — Bookmark

[Danh sách toàn bộ issue của sprint](https://ducviet.atlassian.net/issues/?jql=sprint%20%3D%20112%20ORDER%20BY%20created%20DESC)