# Bookmark Feature Overview

**Page ID**: 46170139  
**Version**: 8  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/46170139

---


# Bookmark Feature Overview

Item

Information

Project

BDMA Desktop

Document Type

Feature Overview / Technical Design

Version

Draft 1.0

Status

Draft

Owner

duchm245

Technical Reviewer

Tech Lead

Approver

TBD

Parent Folder

BDMA Technical Documentation

Target Audience

PM/BA, Tech Lead, Developers, QA

Last Updated

2026-07-04

Related Jira

BDMA-153

Related Documents

Jira BDMA-153 Add Bookmark Feature for Files [https://ducviet.atlassian.net/jira/software/projects/BDMA/boards/36?selectedIssue=BDMA-153](https://ducviet.atlassian.net/jira/software/projects/BDMA/boards/36?selectedIssue=BDMA-153) 

## Mục đích và phạm vi

Tài liệu này mô tả hành vi người dùng, luồng xử lý, thành phần phần mềm, thiết kế dữ liệu và các tiêu chí nghiệm thu của tính năng Bookmark trong BDMA Desktop. Tài liệu được dùng làm cơ sở chung cho quá trình phát triển, review kỹ thuật, kiểm thử và bảo trì tính năng.

### Trong phạm vi

Bookmark và unbookmark file media theo từng user.

Thao tác trên File List và Media Viewer.

Bookmark đơn lẻ, theo vùng liên tiếp và theo trang hiện tại.

Lọc danh sách chỉ hiển thị file đã bookmark.

Lưu trạng thái bookmark trong SQLite và khôi phục sau khi restart ứng dụng hoặc đăng nhập lại.

Đồng bộ trạng thái giao diện giữa các màn hình thông qua application event.

### Ngoài phạm vi

Đồng bộ bookmark với DCAM, cloud hoặc thiết bị khác.

Chia sẻ bookmark giữa các user.

Bookmark thư mục hoặc các đối tượng không phải file media.

Lưu lịch sử đầy đủ của mọi lần thay đổi bookmark.

### Giả định

User phải có session hợp lệ trước khi thay đổi bookmark.

File phải tồn tại trong database tại thời điểm tạo bookmark.

Mỗi cặp `(user_id, file_id)` chỉ có tối đa một record trong `file_bookmark`.

Batch bookmark/unbookmark chỉ tác động đến các file thuộc trang hiện tại, không phải toàn bộ kết quả tìm kiếm.

## 

Tổng quan

Tính năng Bookmark cho phép người dùng đánh dấu các file media quan trọng để truy cập nhanh sau này. Mỗi user có danh sách bookmark riêng, được lưu trữ trong SQLite và duy trì xuyên suốt các phiên làm việc (restart app, đăng nhập lại).

## Thao tác người dùng

**Màn hình**

**Thao tác**

**Mô tả**

File List (row)

Click icon bookmark

Toggle bookmark cho 1 file

File List (header)

Click checkbox header

Bookmark / unbookmark tất cả file trong trang hiện tại

File List (range)

Shift-click

Bookmark / unbookmark range file liên tiếp

File List (filter)

Bật/tắt "Bookmarked only"

Chỉ hiển thị file đã bookmark

Media Viewer (detail)

Click icon bookmark

Toggle bookmark cho file đang xem

## Acceptance Criteria

ID

Scenario

Expected result

AC-01

User click icon bookmark tại một row trong File List

File chuyển sang trạng thái bookmarked, thay đổi được lưu vào database và UI chỉ cập nhật sau khi service xử lý thành công.

AC-02

User click lại icon của file đã bookmark

File chuyển sang trạng thái unbookmarked mà không tạo thêm record trùng `(user_id, file_id)`.

AC-03

Trạng thái bookmark thay đổi tại File List hoặc Media Viewer

Màn hình còn lại phản ánh đúng trạng thái mới qua `FileBookmarkToggledEvent` nếu đang hiển thị cùng file và cùng user.

AC-04

User thực hiện bookmark/unbookmark bằng header

Thao tác chỉ áp dụng cho các file trong trang hiện tại và phát event chứa đúng danh sách file đã xử lý.

AC-05

User shift-click một range file liên tiếp

Tất cả file trong range nhận cùng trạng thái đích; thao tác không đảo trạng thái ngoài ý muốn.

AC-06

User bật bộ lọc Bookmarked only

Chỉ các file được bookmark bởi user hiện tại được hiển thị.

AC-07

User unbookmark một file khi đang bật Bookmarked only

File được loại khỏi danh sách và pagination được tính lại ngay sau khi nhận event thành công.

AC-08

Ứng dụng restart hoặc user đăng xuất rồi đăng nhập lại

Trạng thái bookmark đã lưu được khôi phục từ SQLite.

AC-09

Hai user bookmark cùng một file

Trạng thái của hai user độc lập và event của user này không cập nhật UI của user kia.

AC-10

Cùng một yêu cầu `setBookmarked(fileId, state)` được gửi nhiều lần

Kết quả cuối cùng vẫn là `state`; không xảy ra toggle ngoài ý muốn.

AC-11

Bookmark được thực hiện từ thao tác đơn hoặc batch

Media Viewer hiển thị đúng trạng thái và timestamp; nếu event không có timestamp thì đọc lại từ database.

AC-12

Không có session/user hợp lệ hoặc database update thất bại

Không publish success event và UI không hiển thị trạng thái chưa được lưu; cách thông báo lỗi cho user là **TBD**.

## Luồng chính

wide760
UI gọi `FileBookmarkService.setBookmarked(fileId, bookmarked)`

Service resolve `userId` từ `Session.getUser()`, gọi `insertIfAbsent()` hoặc `unbookmark()`.

Service publish `FileBookmarkToggledEvent` qua `ApplicationEventPublisher`.

Controller nhận event và cập nhật UI state.

## Thành phần chính

**Component**

**File**

**Responsibility**

FileBookmark

models/FileBookmark.java

Model: id, userId, fileId, createdAt, updatedAt, bookmarked (boolean)

FileBookmarkRepository

repositories/FileBookmarkRepository.java

JDBC persistence: insertIfAbsent, unbookmark, isBookmarked, findBookmarkTimestamp, bookmarkAll, unbookmarkAll, filterBookmarkedIds

FileBookmarkService

services/FileBookmarkService.java

Business logic: setBookmarked, isBookmarked, bookmarkAll, unbookmarkAll. Resolve user từ Session, publish event, return BookmarkChange

BookmarkChange

dtos/BookmarkChange.java

Record: userId, fileId, bookmarked, updatedAt — service return type, DTO (không có DB table)

FileBookmarkToggledEvent

events/FileBookmarkToggledEvent.java

Spring ApplicationEvent: userId, fileIds (List), bookmarked, updatedAt

FileService

services/FileService.java

Set bookmarkUserId vào FileFilter trước khi query

FileRepository

repositories/FileRepository.java

findByFilter() LEFT JOIN file_bookmark — conditional join khi có bookmarkUserId

FileFilter

dtos/FileFilter.java

Fields: bookmarkedOnly (Boolean), bookmarkUserId (Long)

FileView

dtos/FileView.java

Fields: bookmarked (boolean), bookmarkedAt (String)

FileListController

admin/layout/controllers/

Bookmark column, batch header, shift-click range, filter toggle, @EventListener

MediaViewerController

modules/media/controllers/

Bookmark icon + timestamp trong detail view, DataExportService export button, Session + userId guard trong onBookmarkChanged

## Thay đổi Database

### Bảng mới: `file_bookmark`

**Migration:** `V6__add_file_bookmark_table.sql`

sqlwide760### Truy vấn chính:

Set bookmarked (insertIfAbsent)**:** `ON CONFLICT (user_id, file_id) DO UPDATE SET is_bookmark=1, updated_at=excluded.updated_at` RETURNING *

Toggle (unbookmark): UPDATE `is_bookmark=0, updated_at=now` WHERE `is_bookmark=1`

Bookmark state trong file list: LEFT JOIN trong `FileRepository.findByFilter()`:

wide760trueFilter "Bookmarked only": thêm `AND b.id IS NOT NULL`

---

### // TODO Quyết định thiết kế dữ liệu

Chủ đề

Trạng thái hiện tại

Việc cần xác nhận

Soft-delete bằng `is_bookmark`

Giúp thao tác set bookmark dùng upsert và có tính idempotent; đổi lại, record unbookmarked vẫn chiếm dung lượng và mọi query phải lọc `is_bookmark = 1`. Đây không phải audit log đầy đủ vì cùng một record được cập nhật lại.

Đã xác nhận tiếp tục soft-delete

Xóa user

`ON DELETE CASCADE` tự động xóa bookmark của user.

Xác nhận đây là hành vi nghiệp vụ mong muốn.

Xóa file

`ON DELETE NO ACTION` không tạo orphan khi SQLite foreign key được bật; thao tác xóa file sẽ bị chặn nếu còn bookmark tham chiếu đến file.

**TBD:** giữ `NO ACTION`, đổi sang `CASCADE`, hay xóa bookmark trong cùng transaction trước khi xóa file. Đồng thời xác nhận `PRAGMA foreign_keys = ON`.

Timestamp

Schema hiện dùng `datetime('now', 'localtime')`. Dữ liệu có thể không nhất quán khi máy hoặc môi trường triển khai đổi timezone.

**TBD:** chuẩn hóa lưu UTC và chỉ chuyển timezone tại UI, hoặc ghi rõ lý do tiếp tục dùng local time.

Index `is_bookmark`

Column có cardinality thấp nên index đơn có thể không mang lại lợi ích đủ lớn và làm tăng write overhead.

**TBD:** kiểm tra bằng `EXPLAIN QUERY PLAN`; cân nhắc bỏ index đơn hoặc thay bằng composite/partial index theo query thực tế.

Giới hạn bookmark

Chưa có giới hạn theo user trong schema hoặc mô tả nghiệp vụ.

**TBD:** xác nhận không giới hạn hoặc định nghĩa giới hạn và hành vi khi đạt ngưỡng.

## //TODO Edge Cases và Error Handling

Tình huống

Hành vi mong đợi

Trạng thái

File bị xóa khi vẫn còn bookmark

Tuân theo delete policy được chốt trong phần quyết định dữ liệu; không để orphan bookmark.

TBD

User bị xóa

Bookmark của user được xóa theo `ON DELETE CASCADE`.

Defined; cần test integration

Bookmark một `file_id` không tồn tại

Database từ chối insert khi foreign key được bật; service không publish success event.

Defined; cần test integration

Không có session hoặc user hợp lệ

Service từ chối thao tác và UI giữ trạng thái hiện tại.

Defined; UX thông báo lỗi TBD

Double-click hoặc event liên tiếp

`setBookmarked(fileId, boolean)` bảo đảm trạng thái đích có tính idempotent.

Defined

Batch input rỗng

Không thay đổi database, không phát event gây refresh UI không cần thiết.

Cần xác nhận bằng test

Batch chứa file đã ở trạng thái đích

Không đảo trạng thái; kết quả cuối cùng đồng nhất với trạng thái được yêu cầu.

Defined

File bị xóa trong lúc đang mở Media Viewer

Viewer không tiếp tục hiển thị bookmark như thao tác thành công; cách đóng view hoặc báo lỗi là TBD.

TBD

Database update thất bại

Transaction rollback; không publish success event; UI không hiển thị trạng thái chưa persist.

Defined; UX thông báo lỗi TBD

Unbookmark làm trang hiện tại rỗng

Tính lại pagination và chuyển đến trang hợp lệ gần nhất.

Cần xác nhận bằng test

Hai màn hình cùng cập nhật một file

Event cuối cùng phản ánh trạng thái đã persist; listener phải guard theo `userId`.

Defined; cần test concurrency

Migration V6 chưa chạy hoặc thất bại

Không sử dụng tính năng với schema thiếu; startup/migration phải báo lỗi có thể chẩn đoán.

Cần xác nhận theo cơ chế migration hiện tại

Timezone hệ điều hành thay đổi

Timestamp vẫn biểu diễn cùng thời điểm nếu dữ liệu được chuẩn hóa UTC.

Phụ thuộc quyết định timestamp

## UX/UI Specification

Phần này mô tả giao diện thực tế dựa trên screenshot của BDMA Dashboard và Media Viewer.

### File List

Nút lọc `Bookmarks` nằm trên filter bar, sau `All types` và trước `Reset filters`.

Nút sử dụng icon bookmark ở bên trái label. Khi filter đang bật, icon có màu cam và danh sách chỉ còn các file đã bookmark.

Cột bookmark là cột thứ hai của bảng, nằm ngay sau cột checkbox chọn file và trước cột `Name`.

Header của cột chứa icon bookmark dạng outline, dùng cho thao tác batch trên các file của trang hiện tại.

Mỗi row hiển thị icon outline màu xám khi chưa bookmark và icon đặc màu cam khi đã bookmark.

Bookmark là trạng thái độc lập với checkbox chọn row và trạng thái selected/highlight của row.

*Figure 1 – Bookmark column, batch header and Bookmarks filter in File List.*

### Media Viewer

Trạng thái bookmark nằm trong tab `Details` của panel `MEDIA`, ở gần cuối danh sách metadata và ngay phía trên nút `Export`.

Khi file đã bookmark, UI hiển thị icon bookmark đặc màu cam cùng label `Bookmarked`.

Timestamp bookmark được hiển thị ở dòng kế tiếp theo định dạng giao diện, ví dụ `Jul 02, 2026 14:36`.

Icon và label là vùng thao tác để bookmark/unbookmark file đang mở; sau khi thay đổi thành công, trạng thái và timestamp phải được refresh tại chỗ.

*Figure 2 – Bookmark state and timestamp in the Media Viewer Details panel.*

### Trạng thái và hành vi UX

Icon outline màu xám: file chưa được bookmark.

Icon đặc màu cam: file đã được bookmark.

Nút filter `Bookmarks` có icon màu cam khi filter đang bật; khi tắt trở về trạng thái trung tính.

Header bookmark tác động đến các file trên trang hiện tại; phạm vi này cần được mô tả trong tooltip.

Filter bookmark, checkbox chọn row và row highlight là ba trạng thái độc lập, không được dùng thay thế cho nhau.

Khi unbookmark trong lúc filter `Bookmarks` đang bật, row được loại khỏi danh sách và pagination được tính lại.

Trong Media Viewer, timestamp chỉ hiển thị khi file đã bookmark; khi unbookmark thì label và timestamp được xóa hoặc chuyển về trạng thái chưa bookmark.

Disable hoặc hiển thị trạng thái đang xử lý để tránh thao tác lặp trong khi request chưa hoàn tất, nếu thao tác database không tức thời.

Khi filter `Bookmarks` không có kết quả, hiển thị empty state thay vì bảng trống không giải thích.

Icon cần có tooltip/accessible label tương ứng với hành động `Bookmark` hoặc `Remove bookmark`.

Loading state và thông báo lỗi cụ thể: **TBD**.

## // TODO Performance Considerations

Query File List phải lọc bookmark theo cả `user_id` và `is_bookmark`; không dựa vào index đơn trên `is_bookmark` nếu chưa có kết quả đo.

Dùng `EXPLAIN QUERY PLAN` trên dữ liệu gần với production để kiểm tra `LEFT JOIN`, filter Bookmarked only và pagination.

Đánh giá index dựa trên query thực tế. Candidate cần đo gồm composite index `(user_id, is_bookmark, file_id)` hoặc partial index cho record có `is_bookmark = 1`, tùy phiên bản SQLite được hỗ trợ.

Batch bookmark/unbookmark nên chạy trong một transaction và phát một batch event thay vì một event/refresh cho từng file.

Cần ghi nhận expected volume gồm số file, user và bookmark trên một installation trước khi đặt ngưỡng hiệu năng.

Ngưỡng response time và kích thước batch chấp nhận được: **TBD**, do PM/Tech Lead/QA thống nhất sau khi có baseline đo thực tế.

## Lưu ý

Bug/issue

Ảnh hưởng

Cách xử lý

Race condition do dùng `toggleBookmark()`

Các thao tác hoặc event gần nhau có thể đảo trạng thái ngoài ý muốn.

Thay bằng `setBookmarked(fileId, boolean)` có tính idempotent.

UI bookmark bị cập nhật hai lần

Controller tự cập nhật UI, sau đó event lại cập nhật lần nữa, gây trạng thái nhấp nháy hoặc sai lệch.

Chỉ cập nhật UI thông qua `FileBookmarkToggledEvent` sau khi service xử lý thành công.

Bỏ bookmark nhưng file vẫn còn trong bộ lọc Bookmarked Only

Danh sách hiển thị dữ liệu không còn đúng với DB.

Xóa file khỏi `filteredFiles` và tính lại pagination ngay khi nhận event unbookmark.

Media Viewer hiển thị trạng thái bookmark cũ

`FileView` là immutable DTO nên dữ liệu bookmark có thể stale khi trạng thái thay đổi từ màn hình khác.

Đọc trạng thái và timestamp trực tiếp từ DB khi hiển thị detail.

Event bookmark có thể cập nhật UI của sai user

Event không được kiểm tra `userId`, có thể làm UI của session hiện tại phản ánh thay đổi thuộc user khác.

Thêm guard so sánh user hiện tại với `event.userId` tại cả File List và Media Viewer.

Batch bookmark làm mất timestamp trên Media Viewer

Batch event không chứa `updatedAt`, khiến detail hiển thị dấu `—` dù file đã được bookmark.

Khi batch event không có timestamp, Media Viewer truy vấn timestamp từ DB.

Bookmark không hiển thị đúng khi xem video

Luồng chuẩn bị detail của video chưa gọi cập nhật bookmark như luồng ảnh.

Chuyển `updateBookmarkUI()` vào phần chuẩn bị detail dùng chung.

Đổi ngôn ngữ làm trạng thái bookmark không refresh

Label và định dạng timestamp vẫn giữ locale trước đó.

Refresh lại bookmark UI khi nhận sự kiện đổi ngôn ngữ.

|  |  |  

## Tài liệu liên quan

Jira BDMA-153 Add Bookmark Feature for Files [https://ducviet.atlassian.net/jira/software/projects/BDMA/boards/36?selectedIssue=BDMA-153](https://ducviet.atlassian.net/jira/software/projects/BDMA/boards/36?selectedIssue=BDMA-153)