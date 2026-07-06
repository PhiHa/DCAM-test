# Storage

Trạng thái: planned feature, chưa implement.

Feature này dành cho nghiệp vụ storage ở mức app, ví dụ chính sách dung lượng, cleanup policy, hoặc quy tắc lưu trữ theo nghiệp vụ.

Filesystem/MediaStore implementation cụ thể nên nằm ở `platform/storage`.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
storage/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

