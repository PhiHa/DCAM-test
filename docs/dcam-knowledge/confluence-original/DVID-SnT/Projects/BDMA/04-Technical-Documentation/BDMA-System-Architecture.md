# BDMA System Architecture

**Page ID**: 25264261  
**Version**: 5  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/25264261

---


# BDMA System Architecture

# 🏗️ System Overview

BDMA (Bodycam Data Management Application) là hệ thống quản lý dữ liệu BodyCam hoạt động trên nền tảng Windows Desktop.

Kiến trúc hệ thống được thiết kế theo hướng:

Modular architecture

Operational reliability first

Secure-by-default

Maintainable codebase

Extensible device integration

AI-readable operational data

Mục tiêu của hệ thống:

Đồng bộ dữ liệu BodyCam ổn định

Quản lý dữ liệu tập trung

Bảo vệ dữ liệu và phân quyền truy cập

Hỗ trợ monitoring và backup

Tạo nền tảng cho operational analytics và AI-assisted workflow trong tương lai

# 🧱 High-Level Architecture

textwide760# 📦 Core Modules

Module

Purpose

Owner

Notes

Device Detection

Phát hiện thiết bị BodyCam kết nối USB

Core System

Theo dõi external drive

Device Recognition

Nhận diện BodyCam hợp lệ

Core System

Kiểm tra cấu trúc dữ liệu/API

Sync Engine

Đồng bộ dữ liệu từ BodyCam

Core System

Retry và integrity validation

Data Indexing

Lập index dữ liệu video/hình ảnh

Backend

Tối ưu search/filter

Secure Storage

Quản lý và bảo vệ dữ liệu

Backend

Hidden folder + permission control

Backup Manager

Tạo và quản lý backup

Backend

Hidden folder + permission control + checksum

User Management

Quản lý user và phân quyền

Application Layer

CRUD + folder permission

Media Viewer

Hiển thị video/hình ảnh

UI Layer

Playback + preview

Reporting Engine

Thống kê và export dữ liệu

Application Layer

Excel export

System Monitor

Giám sát storage/backup/version

Monitoring Layer

Warning & operational visibility

Update Manager

Kiểm tra và cập nhật phiên bản

Infrastructure Layer

Auto update workflow

Settings Manager

Quản lý user settings

Application Layer

Theme/language/storage config

# 🔄 System Flow

## Main Business Flow

textwide760## Data Flow

textwide760# 🧩 Integrations

Integration

Purpose

Protocol/API

Notes

BodyCam Vendor API

Lấy thông tin thiết bị và metadata

Vendor API

Có dependency vào vendor

SQLite

Local metadata database

SQL

Lightweight local storage

Windows USB Interface

Detect external device

Windows API

USB monitoring

ADB (Android Debug Bridge)

Giao tiếp với thiết bị Android

ADB protocol (TCP/USB)

Requires USB debugging enabled on device

Map Services

Hiển thị vị trí dữ liệu GPS

Third-party API

Non-commercial usage

Loggly

Logging & monitoring

API

Operational logging

GitHub Actions

CI/CD — build, package, release

GitHub REST API / YAML

Triggers on `cicd` / `develop` branch

NSIS

Build Windows installer (.exe)

NSIS scripting

Wraps jpackage app-image

Jira

Delivery & issue tracking

Atlassian API

OSd workflow

Confluence

Documentation & knowledge

Atlassian API

Operational memory

# 🗄️ Database Overview

## Main Entities

Entity

Purpose

Notes

User

Quản lý user hệ thống

Authentication & permissions

User Config

Các config cá nhân với từng user

|  
User Activation History

Log lịch sử active/deactive user

|  
Recent User

Danh sách các user đăng nhập gần nhất trên máy

Chỉ lưu tối đa 5 user

BodyCam Model Whitelist

Danh sách model được xác thực

|  
BodyCam Model Whitelist Rule

Danh sách các rule để xác định thiết bị thuộc model đã được xác thực

|  
BodyCam Validated Device

Metadata thiết bị đã được xác thực

Device identification

Media File

Video/hình ảnh đồng bộ. Lưu thông tin các file đã đồng bộ hoặc backup

Indexed searchable asset

Restore Failures

Lưu lại các file restore lỗi để retry bằng tay

|  
App Config

System configuration

Persistent configuration

## Data Storage Strategy

Dữ liệu media lưu trong protected local storage

Metadata lưu trong SQLite/PostgreSQL

File index hỗ trợ search/filter nhanh

Backup archive sử dụng checksum để xác định tính toàn vẹn của  file

Database và storage được tách biệt để tối ưu maintainability

# 🔐 Security & Access Control

## Security Principles

Secure-by-default: Xác thực thiết bị được đồng bộ vào hệ thống.

Least privilege access: USER chỉ thấy file, device của mình, ADMIN thấy tất cả.

Protected local storage: Windows ACL deny.

Protected backup storage: Windows ACL deny và Checksum verification.

Operational audit visibility: Hiển thị số lượng file đồng bộ, restore, export khi thành công, thất bại.

## Access Control

Area

Strategy

Authentication

Local user authentication

Authorization

File level permission

Data Protection

Windows ACL deny

Backup Security

Windows ACL deny and Checksum verification

Operational Logging

Sync  & backup activity logging

## Security Risks

Data corruption during sync

Unauthorized storage access

Device firmware inconsistency

Backup leakage risk

Dependency on vendor API structure

# 📡 Logging & Monitoring

Tool

Purpose

Notes

Loggly

Application logging

Error & operational monitoring

System Monitor

Storage/backup/version monitoring

Local monitoring layer

Sync History

Track sync operation

Operational visibility

Warning Engine

Generate operational alerts

Preventive monitoring

## Monitoring Strategy

Theo dõi storage usage

Cảnh báo backup quá hạn                                                                                                                                                                                                                                         

Theo dõi sync failure

Theo dõi software version outdated

Logging các operational event quan trọng

# 🚀 Deployment & Release

## Deployment Flow

textwide760## CI/CD Direction

Stage

Purpose

Build

Automated build process

Static Analysis

SonarQube & code quality

Testing

QA & validation

Packaging

Windows release package

Release

Controlled deployment

Rollback

Recovery from failed update

# ⚠️ Technical Risks

Risk

Impact

Device firmware variability

Sync inconsistency

Large media storage growth

Performance degradation

Interrupted sync process

Data corruption

Vendor API dependency

Compatibility issue

Monolithic growth

Maintainability risk

Weak operational logging

Hard incident investigation

# 📌 Technical Decisions

Decision

Reason

Impact

Java Desktop Application

Cross-team maintainability

Stable long-term development

SQLite + PostgreSQL

Local + scalable metadata strategy

Flexible deployment

Local Protected Storage

Data security requirement

Higher operational control

Auto Sync Workflow

Reduce manual operation

Faster operational workflow

Modular Architecture Direction

Avoid monolithic growth

Easier scaling & maintenance

Monitoring-first mindset

Improve operational reliability

Better incident visibility

# 🧠 AI & Operational Architecture Direction

BDMA được định hướng phát triển theo mô hình:

textwide760Hệ thống sẽ dần hỗ trợ:

AI-assisted search

Operational analytics

Automated reporting

Intelligent monitoring

Sync failure analysis

Knowledge-driven operation

Điều này yêu cầu:

Structured operational data

Consistent workflow

Logging & monitoring maturity

Documentation-driven development

Decision logging

# 🔗 Related Documents

BDMA Product Vision

BDMA SRS V1.0

Jira Workflow Standard

Release SOP

Decision Log

Sprint Reports

Technical Knowledge Base

# 💡 Engineering Notes

Ưu tiên reliability trước feature expansion

Giảm technical debt sớm để tránh architecture degradation

Theo dõi storage scalability ngay từ đầu dự án

Chuẩn hóa operational logging để hỗ trợ AI-assisted monitoring trong tương lai