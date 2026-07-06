# Location

Trạng thái: planned feature, chưa implement.

Feature này dành cho các nghiệp vụ liên quan đến vị trí/GPS ở mức nghiệp vụ của app.

Không đặt trực tiếp Android `LocationManager` ở đây. Android-specific implementation nên nằm trong `platform/`.

Khi bắt đầu implement, thêm code theo cấu trúc:

```text
location/
├── domain/
├── application/
│   ├── usecase/
│   └── port/
└── presentation/     nếu feature có UI state riêng
```

