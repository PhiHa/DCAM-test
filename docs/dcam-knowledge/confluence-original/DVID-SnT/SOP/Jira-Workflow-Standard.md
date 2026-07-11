# Jira Workflow Standard

**Page ID**: 25460748  
**Version**: 2  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/25460748

---


# Jira Workflow Standard

## 🎯 Goal

Chuẩn hóa trạng thái task để:

Team dễ phối hợp

PM dễ tracking

CEO dễ theo dõi tiến độ

AI dễ reasoning

Giảm task bị mơ hồ hoặc quên trạng thái

# 🧭 Standard Workflow

Backlog
→ To Do
→ In Progress
→ Code Review
→ Testing
→ Done
# 📌 Workflow Definition

## 1. Backlog

Task mới tạo nhưng chưa sẵn sàng để thực hiện.

### Điều kiện

Requirement chưa rõ

Chưa estimate

Chưa prioritize

Chưa assign

### Không nên

Dev tự kéo task từ đây để làm

## 2. To Do

Task đã sẵn sàng để bắt đầu làm.

### Điều kiện

Requirement rõ

Có acceptance criteria

Có assignee

Có priority

Đã estimate cơ bản

### Output mong muốn

Dev có thể bắt đầu làm ngay

## 3. In Progress

Task đang được thực hiện.

### Điều kiện

Dev đang coding / xử lý

Có update tiến độ nếu blocker kéo dài

### Rules

Một dev không nên giữ quá nhiều task In Progress

Nếu blocked lâu cần report trong weekly meeting

## 4. Code Review

Task đã code xong và đang chờ review.

### Điều kiện

Pull request đã tạo

Build pass

Self-review cơ bản

### Mục tiêu

Review logic

Review coding standard

Review maintainability

## 5. Testing

Task đang được test hoặc verify.

### Điều kiện

Code review pass

Deploy lên môi trường test/staging

### Bao gồm

QA test

PM verify

UAT cơ bản

Regression check

## 6. Done

Task hoàn thành hoàn toàn.

### DONE khi

Code merged

Test pass

Không còn blocker

Jira updated

Documentation updated nếu cần

### Không nên

Move Done khi chưa verify

Move Done chỉ vì code xong

# 🚨 Workflow Rules

## General Rules

Task phải có owner rõ ràng

Task phải có priority

Không để task In Progress quá lâu

Không skip workflow step

Bug production cần tạo riêng issue

# 📊 Recommended Metrics

Open tasks

Blocked tasks

Sprint completion

Reopened bugs

Average review time

Average testing time

# 💡 PM Notes

## Mục tiêu của workflow

Workflow dùng để:

tăng visibility

tăng consistency

giảm chaos

improve delivery quality

KHÔNG dùng để:

micro-manage dev

tracking quá mức

tạo process phức tạp