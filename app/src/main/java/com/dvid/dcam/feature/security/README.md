# Security

Trạng thái: planned feature, chưa implement.

Feature này dành cho nghiệp vụ bảo mật ở mức app, ví dụ quyền truy cập nghiệp vụ, mã hoá theo policy, hoặc kiểm tra điều kiện an toàn trước khi thao tác.

Chi tiết Android/framework hoặc thư viện mã hoá cụ thể nên nằm ở `platform/` và được gọi qua `application/port`.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
security/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

