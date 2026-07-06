# Update

Trạng thái: planned feature, chưa implement.

Feature này dành cho nghiệp vụ cập nhật app/firmware/config ở mức app.

Chi tiết tải file, cài đặt, hoặc gọi Android/package API nên nằm trong `platform/` và được gọi qua port.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
update/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

