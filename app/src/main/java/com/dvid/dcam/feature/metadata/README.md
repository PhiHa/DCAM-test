# Metadata

Trạng thái: planned feature, chưa implement.

Feature này dành cho nghiệp vụ metadata của media, ví dụ thông tin file, checksum, đánh dấu nghiệp vụ hoặc dữ liệu bổ sung đi kèm ảnh/video/audio.

Không tạo dummy interface/class. Khi có use case thật thì mới thêm Java source.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
metadata/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

