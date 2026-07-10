# Agile Hierarchy - Epic vs Feature vs User Story vs Task

**Page ID**: 27165134  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27165134

---


# Agile Hierarchy

# Epic - Feature  - User Story  - Task

## Mục tiêu tài liệu

Tài liệu này giúp team hiểu rõ hierarchy trong Agile/Product Development nhằm:

Chuẩn hóa backlog structure

Giảm confusion khi tạo Jira ticket

Giúp PM breakdown requirement đúng cách

Giúp Dev hiểu business context

Tăng khả năng planning và tracking

# Big Picture

textwide760# 1. EPIC

## EPIC là gì?

Epic là một initiative lớn hoặc business goal lớn.

Epic thường:

Có phạm vi lớn

Kéo dài nhiều sprint

Bao gồm nhiều feature

Mang giá trị business lớn

## EPIC trả lời câu hỏi gì?

👉 WHY are we building this?

(Tại sao chúng ta làm việc này?)

## Ví dụ EPIC

### Authentication System Revamp

Hoặc:

Payment System

Notification System

User Onboarding Experience

Subscription Management

## Đặc điểm của EPIC

Tiêu chí

Mô tả

Scope

Rất lớn

Timeline

Nhiều sprint

Owner

PM / Product Owner

Focus

Business Goal

Detail Level

High-level

# 2. FEATURE

## FEATURE là gì?

Feature là capability hoặc functionality cụ thể bên trong Epic.

Feature:

Deliver business value

Có thể release độc lập

Thường kéo dài 1 hoặc nhiều sprint

## FEATURE trả lời câu hỏi gì?

👉 WHAT are we building?

(Chúng ta đang xây cái gì?)

## Ví dụ FEATURE

Epic:
Authentication System

Feature:

Google Login

Forgot Password

Email Verification

Two-factor Authentication

## Đặc điểm của FEATURE

Tiêu chí

Mô tả

Scope

Medium

Timeline

1 hoặc nhiều sprint

Owner

PM

Focus

Capability

Detail Level

Medium

# 3. USER STORY

## USER STORY là gì?

User Story mô tả nhu cầu cụ thể của user.

Story cần:

nhỏ

rõ ràng

implementable

measurable

## USER STORY trả lời câu hỏi gì?

👉 HOW will the user use this?

(User sẽ dùng feature như thế nào?)

## Structure chuẩn của User Story

textwide760## Ví dụ USER STORY

textwide760## Đặc điểm USER STORY

Tiêu chí

Mô tả

Scope

Small

Timeline

Trong 1 sprint

Owner

PM + Team

Focus

User Need

Detail Level

High

# 4. TASK / SUBTASK

## TASK là gì?

Task là công việc kỹ thuật cụ thể để implement User Story.

Task thường được assign cho:

Backend Dev

Frontend Dev

Mobile Dev

QA

## TASK trả lời câu hỏi gì?

👉 WHAT EXACTLY needs to be implemented?

(Cần implement cụ thể cái gì?)

## Ví dụ TASK

### Backend

Create Google OAuth API

Validate access token

Create session

### Frontend

Add Google Login button

Handle redirect flow

Error handling UI

### QA

Test login success

Test invalid token

Regression login flow

## Đặc điểm TASK

Tiêu chí

Mô tả

Scope

Very Small

Timeline

1-3 ngày

Owner

Dev / QA

Focus

Execution

Detail Level

Very Detailed

# Ví dụ thực tế hoàn chỉnh

# EPIC

textwide760⬇

# FEATURE

textwide760⬇

# USER STORY

textwide760⬇

# TASKS

textwide760# Hierarchy đề xuất cho Jira

textwide760# Cách áp dụng cho team hiện tại

## Confluence

Dùng cho:

PRD

Business Requirement

Product Documentation

Workflow

Engineering Guideline

## Jira

Dùng cho:

Epic

Story

Task

Bug

Sprint Tracking

Delivery Management

# Ví dụ Jira Structure thực tế

## EPIC

User Authentication

### STORY

Google Login

#### SUBTASK

Backend API

Frontend UI

Mobile Integration

QA Testing

# Sai lầm phổ biến của startup nhỏ

❌ Dùng Epic như task lớn

❌ Không có hierarchy

❌ Story quá technical

❌ Không có business context

❌ Task quá lớn để estimate

# Best Practice cho team Product nhỏ

✅ Epic = Business Initiative

✅ Story = User Value

✅ Task = Technical Execution

✅ Mỗi Story fit trong 1 sprint

✅ Mỗi Task fit trong vài ngày

# Key Takeaway

Level

Focus

Question

Epic

Business Goal

WHY

Feature / Story

Product Capability

WHAT

Task

Technical Execution

HOW

# Recommended Workflow

Business Idea
&rarr; PRD
&rarr; Epic
&rarr; Story
&rarr; Task
&rarr; Development
&rarr; QA
&rarr; Release

# Kết luận

Việc chuẩn hóa Agile hierarchy sẽ giúp:

Backlog rõ ràng hơn

Dễ planning hơn

Dễ estimate hơn

Dễ tracking progress hơn

Giảm confusion giữa PM / Dev / QA

Scale team dễ dàng hơn