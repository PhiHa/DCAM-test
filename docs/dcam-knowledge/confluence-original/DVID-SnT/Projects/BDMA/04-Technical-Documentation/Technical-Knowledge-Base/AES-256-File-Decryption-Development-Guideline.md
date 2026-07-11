# AES-256 File Decryption Development Guideline

**Page ID**: 27197752  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/27197752

---


# AES-256 File Decryption Development Guideline

## Mục tiêu

Tài liệu này mô tả cơ chế giải mã file đang được sử dụng trong hệ thống nhằm:

Giúp Developer hiểu encryption/decryption flow

Đảm bảo maintain compatibility với file cũ

Chuẩn hóa implementation giữa các module

Giảm rủi ro khi refactor hoặc mở rộng hệ thống

Hỗ trợ onboarding developer mới

# Scope sử dụng trong hệ thống hiện tại

## Current System Scope

Chức năng AES-256-CTR hiện tại được sử dụng cho:

### Bodycam File Synchronization System

Cụ thể:

File video/media được đồng bộ từ thiết bị Bodycam lên hệ thống

File được lưu ở trạng thái encrypted

Hệ thống backend/client sẽ thực hiện decrypt trước khi xử lý hoặc playback

Mục tiêu chính:

bảo vệ dữ liệu trong quá trình đồng bộ

tránh truy cập trực tiếp file raw

maintain compatibility với thiết bị hiện tại

## Important Notes

⚠ Đây là implementation phục vụ compatibility với hệ thống Bodycam hiện tại.

⚠ Không thay đổi encryption flow nếu chưa review impact tới:

thiết bị Bodycam hiện tại

file đã đồng bộ trước đó

hệ thống playback/export

backward compatibility

# Tổng quan kỹ thuật

## Encryption Algorithm

Hệ thống hiện tại sử dụng:

```
AES-256-CTR
```

Thông tin chính:

Thành phần

Giá trị

Cipher

AES-256-CTR

Crypto Library

OpenSSL EVP

Key Length

256 bit (32 bytes)

IV Length

16 bytes

Key Derivation

SHA256(password)

IV Strategy

Fixed IV = 0

Padding

Không sử dụng

# Encryption / Decryption Flow

Bodycam Device
    ↓
Encrypted Media File
    ↓
Upload / Synchronization
    ↓
Backend / Client
    ↓
SHA256(password)
    ↓
AES-256-CTR Decryption
    ↓
Playable Media File
# Key Derivation

## Current Implementation

Password được hash bằng SHA256 để tạo AES key.

### Example

Input Password:
123456

SHA256(password)
↓
32-byte AES key
# IV Handling

## Current Behavior

Hệ thống hiện tại sử dụng fixed IV:

```
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
```

Implementation:

```
unsigned char iv[16] = { 0 };
```

# IMPORTANT NOTES

## Compatibility Requirement

⚠ Không thay đổi IV handling đối với file legacy.

Nếu thay đổi:

File cũ sẽ không decrypt được

Media file từ Bodycam cũ có thể playback fail

Mất backward compatibility

# OpenSSL EVP Flow

## Decryption Process

EVP_DecryptInit_ex()
EVP_DecryptUpdate()
EVP_DecryptFinal_ex()
# File Processing Strategy

## Streaming Mode

File được đọc theo block để hỗ trợ file media lớn.

### Buffer Size

```
256 * 1024
```

### Advantages

Không load toàn bộ file vào RAM

Hỗ trợ video/media file lớn

Memory efficient

Phù hợp cho bodycam synchronization flow

# Current Limitations

## 1. Fixed IV

Current implementation sử dụng IV cố định.

### Security Risk

Không an toàn nếu encrypt nhiều file với cùng key

Có thể bị phân tích stream pattern

### Current Decision

Giữ nguyên để đảm bảo compatibility với hệ thống Bodycam hiện tại.

## 2. No Integrity Check

AES-CTR không tự verify:

Sai password

File bị tamper

Corrupted file

### Current Behavior

Nếu password sai:

Decrypt vẫn chạy thành công

Output file có thể corrupted

Video/media playback có thể lỗi hoặc fail

## 3. SHA256 Direct Hashing

Current implementation:

```
SHA256(password)
```

### Limitation

Không có salt

Không chống brute-force tốt như PBKDF2/Argon2

# Development Rules

## DO

✅ Maintain compatibility với file Bodycam cũ

✅ Sử dụng OpenSSL EVP API

✅ Streaming file processing

✅ Handle large media file efficiently

✅ Handle file open/write errors

✅ Document mọi thay đổi liên quan crypto

✅ Review kỹ trước khi modify encryption flow

## DO NOT

❌ Không tự implement AES algorithm

❌ Không thay đổi IV logic của file legacy

❌ Không thay đổi file format mà không review impact

❌ Không refactor crypto flow mà thiếu regression test

❌ Không break compatibility với file media đã đồng bộ

# Future Improvement Recommendation

## Recommended Modern Approach

Nếu build version mới trong tương lai:

Current

Recommended

SHA256(password)

PBKDF2 / Argon2

Fixed IV

Random IV

AES-CTR

AES-256-GCM

No Integrity Check

Authentication Tag

# Recommended Future File Format

```
[16-byte IV][Encrypted Data][Authentication Tag]
```

# Testing Requirement

## Required Test Cases

### Functional Test

Correct password decrypt success

Wrong password handling

Empty file decrypt

Large video file decrypt

Binary media file decrypt

## Compatibility Test

Legacy Bodycam files must still work

Regression testing after crypto changes

Existing playback flow must still work

## Performance Test

Large video file performance

Memory usage

Multi-file synchronization

Concurrent decrypt handling

# Code Review Checklist

Before merge:

- Compatibility impact checked
- Playback flow validated
- File corruption risk reviewed
- Error handling validated
- Large media file tested
- Wrong password tested
- Regression test completed
- Documentation updated

# Key Takeaways

Current implementation ưu tiên compatibility hơn security modernization.

Không thay đổi crypto flow nếu chưa review đầy đủ impact.

Mọi thay đổi liên quan encryption/decryption cần regression test kỹ.

Crypto feature cần document đầy đủ để tránh maintain risk.

Đây là core compatibility component của hệ thống Bodycam synchronization.

# Related Components

AES256FileDecode

OpenSSL EVP

Bodycam Synchronization Module

Media Playback Flow

File Import/Export Flow

# References

OpenSSL EVP Documentation

AES-256-CTR Standard

Internal Encryption Compatibility Rules

Bodycam Synchronization Architecture