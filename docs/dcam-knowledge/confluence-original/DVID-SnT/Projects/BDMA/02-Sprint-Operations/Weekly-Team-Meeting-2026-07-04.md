# Weekly Team Meeting 2026-07-04

**Page ID**: 47808589  
**Version**: 2  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47808589

---


# Weekly Team Meeting – 2026-07-04

Item

Details

Project

BDMA

Meeting Type

Weekly Team Meeting

Date

2026-07-04

Participants

PM / Quyền, Việt Anh, Phi, Đức, Nhân

Prepared By

PM / Quyền

Main Focus

BDMA release follow-up, blockers review, transition plan to Android Phase 2

# 1. Completed Tasks

Member

Completed Work

Notes

Việt Anh

Hoàn thiện task hiển thị vị trí theo video.

Đã hoàn tất các bước và đẩy lên bản release gần nhất.

Việt Anh

Bắt đầu tìm hiểu dự án mới Android.

Đã tìm hiểu trong khoảng 2 ngày, hiện chưa có kết quả cụ thể.

Phi

Hoàn thiện test và tìm lỗi ở phần hiển thị map.

Hiện chưa gặp vấn đề lớn.

Đức

Hoàn thiện một số vấn đề phát sinh liên quan đến marker.

Đồng thời hoàn thiện phần tài liệu.

Nhân

Review chất lượng, fix bug và chuẩn bị cho code review.

Cần tiếp tục theo dõi chất lượng sau build.

**Summary:**  
Dự án BDMA đã phát hành **3 tuần cập nhật liên tiếp**, cho thấy team duy trì được nhịp release đều đặn. Tuy nhiên, cần tiếp tục kiểm soát chất lượng sau mỗi bản build để tránh lỗi phát sinh muộn.

# 2. Blockers của Đức

Blocker

Description

Impact

Yêu cầu ban đầu chưa rõ

Một số yêu cầu lúc đầu chưa được xác nhận đầy đủ, dẫn đến phát sinh vấn đề khi thực hiện.

Gây rework và kéo dài thời gian hoàn thành task.

Thiết kế ban đầu chưa đủ chi tiết

Nhiều vấn đề chỉ được phát hiện sau khi đã triển khai xong.

Làm tăng effort sửa đổi và kiểm tra lại.

Thiếu đồng bộ giao diện

Một số màn hình/giao diện chưa đồng nhất, trạng thái UI cần được đồng bộ tốt hơn.

Có thể ảnh hưởng trải nghiệm người dùng và phát sinh lỗi UI.

Scope/impact chưa được đánh giá đầy đủ

Phát sinh nhiều hơn so với dự kiến ban đầu.

Task kéo dài, khó kiểm soát timeline.

Thiếu kinh nghiệm xử lý vấn đề phát sinh

Một số vấn đề mất nhiều thời gian hơn để phân tích và giải quyết.

Làm giảm tốc độ xử lý task.

Test chưa đủ sớm/kỹ

Một vài phần chưa được test kỹ từ đầu nên phải sửa muộn.

Tài liệu hoàn thiện trễ, task bị kéo dài.

**PM Note:**  
Các blocker trên cho thấy cần cải thiện bước **clarify requirement**, **impact analysis**, **UI consistency review** và **testing checklist** trước khi xác nhận task hoàn thành.

# 3. Next Plan cả team

Priority

Plan

Owner / Team

High

Bắt đầu chuyển sang giai đoạn 2 của dự án Android từ tuần sau.

Cả team

High

Tiếp tục tìm hiểu kiến thức Android và kiến trúc dự án mới.

Thành viên được phân công

High

Duy trì bảo trì BDMA đang chạy; nếu BDMA phát sinh lỗi thì ưu tiên xử lý trước.

Cả team

Medium

Tiếp tục hoàn thiện tài liệu còn thiếu và fix các phần phát sinh.

Đức / Nhân / Team

Medium

Sau khi sửa code và build ra bản mới, cần test tối thiểu 24 giờ không có lỗi trước khi công nhận hoàn thành.

Dev + QA

**Focus for next week:**  
Team bắt đầu bước chuyển sang **Android Phase 2**, nhưng vẫn giữ BDMA ở trạng thái được bảo trì chủ động. Trong trường hợp BDMA phát sinh lỗi nghiêm trọng, ưu tiên xử lý BDMA trước để đảm bảo chất lượng sản phẩm hiện tại.

# 4. Decisions

ID

Decision

Owner

Status

DEC-001

Tuần sau bắt đầu chuyển sang giai đoạn 2 của dự án Android.

PM / Team

Agreed

DEC-002

BDMA vẫn được ưu tiên bảo trì khi có sự cố, dù team bắt đầu chuyển trọng tâm sang Android.

PM / Team

Agreed

DEC-003

Mỗi bản build sau khi sửa code cần được test tối thiểu 24 giờ không có lỗi trước khi kết luận task hoàn thành.

Dev + QA

Agreed

DEC-004

Thành viên được phân công tiếp tục học và tiếp cận kiến thức Android để chuẩn bị cho phase mới.

PM / Team

Agreed

# 5. Risks

ID

Risk

Impact

Mitigation

RISK-001

Nếu không tiếp tục tập trung bảo trì BDMA, tiến độ release và chất lượng sản phẩm có thể bị ảnh hưởng.

High

Duy trì ưu tiên xử lý lỗi BDMA khi có sự cố.

RISK-002

Yêu cầu ban đầu thiếu xác nhận rõ ràng dẫn tới rework.

High

Bắt buộc clarify requirement và xác nhận scope trước khi triển khai.

RISK-003

Thiếu đồng bộ UI/trạng thái giữa các màn hình.

Medium

Bổ sung bước review UI consistency trước khi merge/release.

RISK-004

Thiếu kinh nghiệm ở một số thành viên làm tăng thời gian xử lý vấn đề phát sinh.

Medium

Tăng chia sẻ kiến thức, review kỹ thuật và mentoring trong team.

RISK-005

Test chưa đủ sớm hoặc chưa đủ sâu khiến lỗi bị phát hiện muộn.

High

Áp dụng quy tắc test tối thiểu 24 giờ sau build trước khi xác nhận Done.

RISK-006

Nếu thành viên chỉ tập trung viết code mà không mở rộng kiến thức về kiến trúc, sản phẩm và công cụ mới, năng lực dài hạn có thể bị hạn chế.

Medium

Khuyến khích học kiến trúc, tư duy sản phẩm, kỹ năng phân tích và sử dụng công cụ hỗ trợ hiệu quả.

# 6. PM Notes

Team đã duy trì được nhịp release BDMA trong 3 tuần liên tiếp. Đây là điểm tích cực cần tiếp tục phát huy.

Tuy nhiên, các vấn đề phát sinh trong task marker/map cho thấy cần cải thiện quy trình từ requirement → design → implementation → test.

Với các task có ảnh hưởng nhiều màn hình hoặc trạng thái giao diện, cần đánh giá impact kỹ hơn trước khi bắt đầu code.

Bước chuyển sang Android Phase 2 cần được thực hiện có kiểm soát, không bỏ rơi các vấn đề vận hành của BDMA.

Tư duy cần chuyển từ “hoàn thành code” sang “hoàn thành sản phẩm có thể vận hành ổn định”.

# 7. Action Items

Owner

Action Item

Due Date

Status

PM / Team

Chuyển kế hoạch tuần sau sang Android Phase 2.

Next Week

Planned

Việt Anh

Tiếp tục tìm hiểu dự án Android và báo cáo kết quả cụ thể hơn.

Next Week

In Progress

Đức

Hoàn thiện các tài liệu còn thiếu và rút kinh nghiệm từ blocker task marker/map.

Next Week

In Progress

Nhân

Tiếp tục review chất lượng và chuẩn bị code review cho các phần đã fix.

Next Week

In Progress

Phi

Tiếp tục kiểm tra map/location và theo dõi lỗi phát sinh sau release.

Next Week

In Progress

Dev + QA

Áp dụng quy tắc test ít nhất 24 giờ sau build trước khi xác nhận task Done.

From Next Build

Planned