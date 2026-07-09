# PRD Template Standard

**Page ID**: 27164818  
**Version**: 1  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27164818

---


# PRD Template Standard

## Mục tiêu

Tài liệu này định nghĩa template chuẩn để viết PRD (Product Requirement Document) cho team Product và Development.

Mục tiêu:

Chuẩn hóa cách mô tả requirement

Giảm misunderstanding giữa Business, PM, Dev và QA

Tăng khả năng estimate và planning

Giảm scope creep

Tăng khả năng predict timeline và release

# Khi nào cần viết PRD?

PRD cần được tạo khi:

Có feature mới

Có major enhancement

Có business flow mới

Có integration mới

Có thay đổi ảnh hưởng nhiều module

Không bắt buộc viết PRD đầy đủ cho:

Minor bug

UI fix nhỏ

Hotfix production

# Cấu trúc PRD chuẩn

## 1. Overview

Mô tả ngắn gọn feature hoặc initiative.

### Ví dụ

Tính năng đăng nhập bằng Google cho web application.

## 2. Business Objective

Mục tiêu business của feature.

### Ví dụ

Tăng conversion onboarding

Giảm friction khi đăng nhập

Tăng retention user

## 3. Problem Statement

Mô tả vấn đề hiện tại.

### Ví dụ

Người dùng phải tạo account thủ công dẫn đến tỷ lệ drop cao trong onboarding flow.

## 4. Success Metrics

Xác định feature thành công như thế nào.

### Ví dụ

Increase login completion rate +20%

Reduce onboarding time

Increase DAU retention

## 5. Scope

### In Scope

Google OAuth login

Auto create account

Session management

### Out of Scope

Facebook login

Apple login

Multi-device session sync

## 6. User Flow

Mô tả flow người dùng.

### Ví dụ

User click Login with Google

Redirect Google OAuth

User approve permission

System authenticate account

Redirect dashboard

## 7. Functional Requirements

Mô tả logic xử lý chi tiết.

### Ví dụ

Hệ thống tạo account mới nếu email chưa tồn tại

Nếu account đã tồn tại thì login trực tiếp

Token hết hạn sau X giờ

Error message hiển thị khi OAuth fail

## 8. UI/UX Reference

Đính kèm:

Figma

Wireframe

Mockup

Prototype

## 9. Edge Cases

Liệt kê các trường hợp đặc biệt.

### Ví dụ

User cancel OAuth

Google account disabled

Duplicate email

Network timeout

## 10. Technical Notes

Dành cho thông tin kỹ thuật.

### Ví dụ

API integration

Database impact

Third-party dependency

Security concern

Migration plan

## 11. QA Notes

Thông tin dành cho QA.

### Ví dụ

Regression impact

Test scenarios

Browser compatibility

Mobile responsive testing

## 12. Release Plan

Kế hoạch release.

### Ví dụ

Deploy staging

Internal UAT

Production rollout

Monitoring after release

# PRD Template Mẫu

# PRD - [Feature Name]

## 1. Overview

[Mô tả feature]

## 2. Business Objective

[Mục tiêu business]

## 3. Problem Statement

[Vấn đề hiện tại]

## 4. Success Metrics

Metric 1

Metric 2

## 5. Scope

### In Scope

Item 1

Item 2

### Out of Scope

Item X

Item Y

## 6. User Flow

Step 1

Step 2

Step 3

## 7. Functional Requirements

Requirement 1

Requirement 2

Requirement 3

## 8. UI/UX Reference

[Figma / Mockup]

## 9. Edge Cases

Edge case 1

Edge case 2

## 10. Technical Notes

[Technical information]

## 11. QA Notes

[Test scenarios / regression impact]

## 12. Release Plan

[Staging / Production rollout]

# Workflow đề xuất

PRD Flow:

Business Idea
&rarr; PM viết PRD
&rarr; Review với Dev
&rarr; QA review test impact
&rarr; Approval
&rarr; Breakdown Jira Epic/Story
&rarr; Sprint Planning
&rarr; Development
&rarr; QA
&rarr; Release

# Kết luận

PRD giúp team:

Align business và technical

Chuẩn hóa requirement

Giảm misunderstanding

Tăng khả năng planning

Giảm rework

Tăng hiệu quả delivery