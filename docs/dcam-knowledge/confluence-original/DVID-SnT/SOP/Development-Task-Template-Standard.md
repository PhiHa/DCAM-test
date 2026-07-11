# Development Task Template Standard

**Page ID**: 27197540  
**Version**: 3  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/27197540

---


# Chuẩn hóa Task Template cho Team Development

## Mục tiêu

Tài liệu này nhằm chuẩn hóa cách tạo và quản lý task trong team phát triển phần mềm nhằm:

Giảm ambiguity giữa PM, Developer và QA

Tăng khả năng estimate chính xác

Giảm rework và bug phát sinh

Tăng khả năng predict timeline và release

Chuẩn hóa quy trình làm việc khi scale team

# Nguyên tắc chung

Mỗi task cần:

Có objective rõ ràng

Có acceptance criteria cụ thể

Có owner chịu trách nhiệm

Có estimate và priority

Có đủ thông tin để Dev và QA thực hiện mà không phụ thuộc quá nhiều vào trao đổi miệng

# Cấu trúc Task Template chuẩn

## 1. Task Title

Task title cần ngắn gọn, rõ ràng và thể hiện action cụ thể.

### Ví dụ tốt

[BE] API Login Google

[FE] Dashboard Statistics UI

[Mobile] Fix crash upload image

### Không khuyến khích

Fix login

Dashboard

API issue

## 2. Business Objective

Mô tả mục tiêu business hoặc lý do cần task này.

### Ví dụ

Người dùng cần đăng nhập nhanh bằng Google để tăng conversion onboarding.

## 3. Requirement Description

Mô tả flow và logic xử lý.

### Ví dụ User Flow

User click Login with Google

Redirect Google OAuth

Nếu account chưa tồn tại thì tạo account mới

Redirect dashboard sau khi login thành công

## 4. Acceptance Criteria

Đây là phần quan trọng nhất của task.

Task chỉ được xem là hoàn thành khi đáp ứng đầy đủ Acceptance Criteria.

### Ví dụ

User login thành công bằng Google

Không tạo duplicate account

Redirect đúng dashboard

Error handling hoạt động đúng

Session được lưu chính xác

## 5. UI/UX Reference

Đính kèm:

Figma link

Mockup

Screenshot

Prototype

## 6. Technical Note

Dành cho thông tin kỹ thuật:

API liên quan

Database impact

Third-party service

Dependency

Security concern

## 7. Estimate

Task cần estimate trước khi đưa vào sprint.

Estimate dùng Story Point ([Story Point trong Jira và cách Estimate](https://scrumpass.com/story-point-trong-jira-va-cach-estimate-story-point/) )

## 8. Priority

Định nghĩa mức độ ưu tiên:

Critical

High

Medium

Low

## 9. Assignee

Xác định owner chịu trách nhiệm chính.

## 10. QA Checklist

Ví dụ:

Test happy case

Test invalid input

Regression related feature

Responsive test

Cross-browser test

# Template mẫu

## [Task Title]

### Business Objective

[Business Goal]

### Requirement Description

[Flow và logic xử lý]

### Acceptance Criteria

- Criteria 1
- Criteria 2
- Criteria 3

### UI/UX Reference

[Figma / Mockup]

### Technical Note

[Technical information]

### Estimate

[Story Point / Time]

### Priority

[High / Medium / Low]

### Assignee

[Owner]

### QA Checklist

- Test case 1
- Test case 2
- Regression

# Quy trình phối hợp đề xuất

## PM

PM chịu trách nhiệm:

Business Objective

Requirement Description

Acceptance Criteria

Priority

## Developer

Developer chịu trách nhiệm:

Technical Note

Estimate

Technical implementation

## QA

QA chịu trách nhiệm:

Test Checklist

Regression testing

Quality verification

# Kết luận

Việc chuẩn hóa task template sẽ giúp:

Team phối hợp hiệu quả hơn

Giảm misunderstanding

Tăng tốc độ delivery

Giảm bug production

Tăng khả năng scale team trong tương lai