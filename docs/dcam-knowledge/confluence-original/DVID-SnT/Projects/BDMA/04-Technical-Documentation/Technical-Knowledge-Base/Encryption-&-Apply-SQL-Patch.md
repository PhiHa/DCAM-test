# Encryption & Apply SQL Patch

**Page ID**: 27165291  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/27165291

---


# 1. Tổng quan Module

Module cung cấp cơ chế mã hóa file SQL (.sql) thành file nhị phân (.sql.enc) bằng thuật toán AES-256-GCM, sau đó giải mã và thực thi an toàn lên database SQLite trong ứng dụng BDMA. Thiết kế đảm bảo mỗi patch chỉ được apply đúng một lần duy nhất, ngay cả trong trường hợp chạy đồng thời.

## 1.1. Mục tiêu thiết kế

Bảo vệ nội dung SQL patch tránh bị đọc, sửa đổi hoặc tái sử dụng bởi bên không có thẩm quyền

Đảm bảo tính toàn vẹn (integrity): file bị tamper sẽ không decrypt được

Ngăn chặn apply trùng lặp (idempotency) ở hai tầng: pre-check trong bộ nhớ và UNIQUE constraint trên DB

Chỉ cho phép câu lệnh INSERT và UPDATE – không cho phép DROP, DELETE, ALTER, v.v.

Key không bao giờ lưu trong source code hay config file – luôn đến từ biến môi trường

## 1.2. Các class chính

**Class / Interface**

**Package**

**Vai trò**

PatchCryptoService

com.app.common.services

Mã hóa (encrypt) và giải mã + validate (decryptAndValidate)

PatchApplyService

com.app.common.services

Điều phối: decrypt → execute SQL → ghi record, có @Transactional

PatchApplyRepository

com.app.common.repositories

Truy vấn / ghi bảng patch_apply

PatchApply

com.app.common.models

Model tương ứng bảng patch_apply

# 2. Cấu trúc File .sql.enc

File được mã hóa theo định dạng nhị phân BDP v2. Bố cục byte như sau:

**Offset**

**Độ dài**

**Trường**

**Mô tả**

0

4 bytes

Magic

0x42 0x44 0x50 0x02  ("BDP\x02") – định danh file

4

16 bytes

Patch ID

UUID sinh ngẫu nhiên theo thời gian chạy, mỗi lần mã hóa tạo ra một UUID khác nhau để định danh file patch.

20

12 bytes

IV

Initialization Vector ngẫu nhiên (SecureRandom) cho AES-GCM

32

N bytes

Ciphertext + GCM Tag

Nội dung SQL sau khi mã hóa + 16 bytes authentication tag

*Toàn bộ phần Header [0..31] được dùng làm AAD (Additional Authenticated Data). Bất kỳ thay đổi nào lên header hoặc ciphertext đều khiến GCM tag verification thất bại.*

## 2.1. Quy tắc đặt tên file

File plaintext:  patch_001_mo_ta.sql   →   File mã hóa:  patch_001_mo_ta.sql.enc

Quy trình encrypt luôn thêm đuôi .enc, không xóa đuôi gốc, tránh ghi đè file plaintext.

 

# 3. Luồng Xử lý

## 3.1. Mã hóa (Encrypt)

Thực hiện bởi PatchCryptoService.encrypt(Path sqlFile):

**Validate đầu vào – **kiểm tra file tồn tại, là regular file, không vượt 10 MB

**Parse & validate SQL – **dùng JSQLParser, chỉ chấp nhận INSERT / UPDATE

**Sinh Patch ID – **UUID v5 từ SHA-1 của nội dung file (deterministic)

**Sinh IV ngẫu nhiên – **12 bytes từ SecureRandom

**Load master key – **đọc từ property patch.master.key, clear khỏi heap sau dùng

**Mã hóa AES-256-GCM – **plaintext + AAD (header) → ciphertext + GCM tag

**Ghi file .sql.enc – **layout: [magic][patchId][iv][ciphertext+tag]

## 3.2. Apply Patch (Decrypt + Execute)

Thực hiện bởi PatchApplyService.apply(Path encFile) với @Transactional:

**decryptAndValidate() – **validate magic bytes, đọc Patch ID

**Tầng 1 – pre-check DB – **existsByPatchId(): nếu đã apply → ném AppException ngay, KHÔNG decrypt

10.  **Giải mã AES-256-GCM – **xác minh GCM tag + AAD; thất bại → AEADBadTagException

11.  **Parse & validate SQL – **JSQLParser kiểm tra lần 2 sau decrypt

12.  **Execute từng câu lệnh – **jdbcTemplate.execute() cho mỗi INSERT / UPDATE

13.  **Tầng 2 – ghi record – **INSERT patch_apply; UNIQUE constraint bắt race condition

# 4. Schema Database

## 4.1. Bảng patch_apply

**Cột**

**Kiểu**

**Ràng buộc**

**Mô tả**

id

INTEGER

PK AUTOINCREMENT

Khóa chính tự tăng

patch_id

TEXT

NOT NULL UNIQUE

UUID v5 của patch – cột UNIQUE là tầng bảo vệ thứ 2

file_name

TEXT

NOT NULL

Tên file .sql.enc đã apply

applied_at

TEXT

NOT NULL

Thời điểm apply (giờ local)

*Bảng này chỉ ghi, không bao giờ xóa. Đây là audit log bất biến của quá trình apply patch.*

# 5. Cấu hình Master Key

## 5.1. Cấu hình trong application.properties

# application.properties

patch.master.key=${PATCH_MASTER_KEY}

Ứng dụng đọc key từ biến môi trường PATCH_MASTER_KEY thông qua Spring property binding. Không hardcode giá trị trực tiếp.

## 5.3. Truyền key khi chạy local (development)

# Windows PowerShell

$env:PATCH_MASTER_KEY="<64-char-hex>"

./gradlew bootRun

# Windows Command Prompt

set PATCH_MASTER_KEY=<64-char-hex>

gradlew.bat bootRun

# Linux / macOS

PATCH_MASTER_KEY=<64-char-hex> ./gradlew bootRun

# 6. Setup Key trên GitHub Actions

## 6.1. Thêm Secret vào Repository

Thực hiện các bước sau trên [http://GitHub.com](http://GitHub.com) :

1.  Mở repository trên GitHub → vào **Settings**

2.  Chọn **Secrets and variables** → **Actions**

3.  Click **New repository secret**

4.  Điền vào form:

◦       Name:   PATCH_MASTER_KEY

◦       Secret: <chuỗi hex 64 ký tự sinh ở bước 5.1>

5.  Click **Add secret**

## 6.2. Sử dụng trong workflow (build.yml)

Key được truyền vào build qua hai bước chính:

**Bước Build shadow JAR:**

name: Build shadow JAR

  env:

    APP_VERSION: ${{ env.APP_VERSION }}

    LOGGLY_TOKEN: ${{ secrets.LOGGLY_TOKEN }}

    PATCH_MASTER_KEY: ${{ secrets.PATCH_MASTER_KEY }}

  run: ./gradlew clean shadowJar

**Bước Build app-image (jpackage):**

name: Build app-image

  env:

    LOGGLY_TOKEN: ${{ secrets.LOGGLY_TOKEN }}

    PATCH_MASTER_KEY: ${{ secrets.PATCH_MASTER_KEY }}

  run: |

    jpackage \

      --java-options "-DPATCH_MASTER_KEY=$env:PATCH_MASTER_KEY" \

      ...

 

Khi jpackage khởi động JVM, -DPATCH_MASTER_KEY được set thành system property. Spring Boot đọc nó qua ${PATCH_MASTER_KEY} trong application.properties.

## 6.3. Xác nhận Secret đã được cấu hình

**Kiểm tra**

**Kết quả mong đợi**

Build shadowJar thành công

Không có lỗi 'Master patch key is not configured'

jpackage hoàn thành

File .exe xuất hiện trong build/installer/

Upload artifact / Release

File .exe tải về và chạy được bình thường

# 7. Hướng dẫn Tạo và Apply Patch

## 7.1. Quy trình tạo file patch

1.  **Viết file SQL** chứa các câu INSERT / UPDATE cần thiết:

-- patch_20250526_add_white_list.sql

INSERT INTO model_whitelist (id, model_name, is_active) VALUES ('body_camera_lloo', 'LLOo', 1);
INSERT INTO model_whitelist_rule (whitelist_id, prop_key, expected_value) VALUES('body_camera_lloo', 'ro.product.model', 'LLOo');
INSERT INTO model_whitelist_rule (whitelist_id, prop_key, expected_value) VALUES('body_camera_lloo', 'ro.product.device', 'k70v12_64_k419LLOo');
INSERT INTO model_whitelist_rule (whitelist_id, prop_key, expected_value) VALUES('body_camera_lloo', 'ro.board.platform', 'mt70701LLOo');

 

2.  **Gọi PatchCryptoService.encrypt()** để sinh file .sql.enc:

Gọi từ `AdminSettingsDialogController` đã tạo button `btnEncryptPatch` ở dev mode

Click button `btnEncryptPatch` xong chọn file patch_20250526_add_white_list.sql

 

3.  **Phân phối file .sql.enc** đến môi trường target. KHÔNG phân phối file .sql gốc.

4.  **Gọi PatchApplyService.apply()** trên máy target (cùng PATCH_MASTER_KEY):

Gọi từ `AdminSettingsDialogController` đã tạo button `btnApplyPatch`ở dev mode

Click button `btnApplyPatch`xong chọn file patch_20250526_add_white_list.sql.enc

*File .sql.enc có thể gửi qua mạng, lưu vào git, hoặc bundle trong installer mà không lo lộ nội dung. Key không đi kèm file.*