# Streaming

Trạng thái: planned feature, chưa implement.

Feature này dành cho nghiệp vụ streaming/PTT/livestream ở mức app.

Protocol, SDK hoặc service implementation cụ thể nên nằm trong `platform/` và được gọi qua port.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
streaming/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

