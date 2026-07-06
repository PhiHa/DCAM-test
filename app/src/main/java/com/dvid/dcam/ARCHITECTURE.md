# Kiến trúc source Android DCAM

DCAM sử dụng kiến trúc **feature-first** trong một Gradle module Android `:app`.
Cây package thể hiện feature nào sở hữu nghiệp vụ, đồng thời giữ luồng dependency
MVVM/UseCase/Repository/Service Adapter theo Android Development Standard.

Hướng dẫn thực hành từng bước dành cho thành viên mới và intern nằm tại
[FEATURE_DEVELOPMENT_GUIDE.md](FEATURE_DEVELOPMENT_GUIDE.md).

## 1. Cấu trúc tổng thể

```text
com.dvid.dcam/
├── app/                         Điểm khởi động và shell phối hợp nhiều feature
│   ├── MainActivity
│   ├── AppComposition          Chọn và nối concrete adapter
│   ├── navigation/
│   └── presentation/           State/ViewModel dùng qua nhiều feature
├── feature/                     Các khả năng của sản phẩm (vertical slice)
│   ├── capture/
│   │   ├── application/        Command và capture use case
│   │   ├── domain/             Model, repository/service contract
│   │   └── data/               Repository implementation/coordination
│   ├── media/                  Workflow và state duyệt media
│   ├── device/                 Workflow và contract trạng thái thiết bị
│   └── */domain/               Contract scaffold cho khả năng tương lai
├── platform/                    Android, hardware, filesystem và provider code
│   ├── camera/, audio/, recording/
│   ├── storage/, database/, config/
│   ├── device/, input/, permission/
│   └── logging/
└── core/                        Contract/model nhỏ thực sự dùng chung
    ├── config/
    └── logging/
```

Ý nghĩa của bốn package cấp cao:

- `app`: Android entry point, navigation, presentation dùng qua nhiều feature và
  dependency composition.
- `feature`: hành vi/nghiệp vụ của sản phẩm, ví dụ capture, media hoặc device.
- `platform`: cách Android, hardware hoặc provider SDK thực hiện một contract.
- `core`: primitive/contract ổn định được nhiều feature thực sự dùng chung.

## 2. Hướng dependency

```text
app ────────────────→ feature ─────→ core
 │                       ↑
 └──── lựa chọn ───→ platform ─────→ core
```

Quy tắc bắt buộc:

- `app` được phép nối feature với platform. `MainActivity` render UI và xử lý
  lifecycle; `AppComposition` là nơi chọn concrete repository/adapter.
- `feature/*/application`, `feature/*/domain` và `feature/*/data` không được
  import Android, AndroidX, CameraX, Room, WorkManager, `app` hoặc `platform`.
- `platform` implement contract của feature/core bằng Android hoặc provider API,
  nhưng không phụ thuộc ngược vào `app`.
- `core` không phụ thuộc `app`, `feature`, `platform`, Android hoặc provider SDK.
- Presentation có thể dùng AndroidX ViewModel/LiveData nhưng không gọi platform
  adapter trực tiếp.

`LayerDependencyTest` kiểm tra các quy tắc này và từ chối production Java class
nằm ngoài bốn package cấp cao `app`, `feature`, `platform`, `core`.

## 3. Luồng chuẩn bên trong feature

```text
View/Activity
    ↓ quan sát state và gửi action
ViewModel
    ↓ gọi
UseCase
    ↓ phụ thuộc
Repository
    ↓ phụ thuộc
Service/Provider contract
    ↑ được implement bởi
Platform adapter
```

- View/Activity chỉ render và forward input.
- ViewModel giữ UI state và gọi use case.
- UseCase chứa workflow, precondition và business decision.
- Repository phối hợp các domain contract và map dữ liệu/lỗi.
- Domain contract mô tả feature cần gì, không mô tả SDK làm bằng cách nào.
- Platform adapter gọi Android/vendor API, xử lý threading và dịch lỗi thô.

## 4. Vì sao `domain` chứa model, repository và service cùng nhau?

Package được nhóm theo **trách nhiệm và độ gắn kết**, không nhóm theo cú pháp Java
như `class`, `interface` hoặc `enum`.

Ví dụ `feature/capture/domain` chứa:

```text
CaptureRepository.java          Repository contract
CameraService.java              Camera capability contract
AudioService.java               Audio capability contract
CaptureEventListener.java       Domain event contract
CaptureState.java               Domain value/model
RecordingMode.java              Domain enum/value
```

Các file trên cùng mô tả ngôn ngữ và boundary của capture nên ở cùng package là
bình thường. Interface và implementation không bị trộn lẫn:

```text
CaptureRepository
    ← DefaultCaptureRepository       feature/capture/data

CameraService
    ← CameraPreview                  platform/camera

AudioService
    ← AudioRecorder                  platform/audio
```

Không tạo các package `interfaces/`, `classes/`, `enums/` hoặc
`implementations/` chỉ để phân loại loại file. Cách đó làm cây source sâu hơn
nhưng không tạo thêm dependency boundary.

Chỉ tách package con khi có một nhóm trách nhiệm riêng, ổn định và đủ lớn để
việc tách giúp tìm code dễ hơn. Khoảng 10–15 file liên quan là một tín hiệu để
review, không phải luật cứng. Không tạo folder rỗng chỉ để cây package đối xứng.

## 5. Quy ước tên package

- Package viết thường.
- Ưu tiên danh từ số ít theo capability: `camera`, `device`, `permission`,
  `storage`, không dùng `permissions` chỉ vì có nhiều quyền runtime.
- Tên mô tả hoạt động như `logging` và `recording` không phải dạng số nhiều.
- Tên package thể hiện trách nhiệm, không thể hiện loại Java (`interfaces`,
  `classes`) hoặc tên framework (`camerax`) trừ khi đó là adapter riêng rõ ràng.
- Tính nhất quán trong codebase quan trọng hơn việc cố ép mọi tên về cùng một
  dạng ngữ pháp.

Hậu tố `Service` trong `domain` nghĩa là capability contract, không phải Android
`Service`. Android service thật nằm trong `platform` và có tên rõ lifecycle, ví
dụ `RecordingForegroundService`.

## 6. MVVM, Clean Architecture và MVP có xung đột không?

Không. Ba thuật ngữ nói về ba phạm vi khác nhau:

- **MVVM** tổ chức presentation: View → ViewModel → observable state.
- **Clean Architecture** định hướng dependency của toàn ứng dụng. DCAM áp dụng
  theo hướng thực dụng, không cố sao chép mọi layer của textbook.
- **MVP** trong tài liệu sản phẩm nghĩa là **Minimum Viable Product**, không phải
  Model-View-Presenter.

Vì vậy DCAM có thể dùng MVVM trong presentation, Clean Architecture cho boundary
và vẫn triển khai phạm vi sản phẩm MVP mà không có xung đột.

## 7. Boundary chuyển tiếp hiện tại

- `MainViewModel` vẫn phối hợp capture, media, device status và navigation. Tách
  thành feature ViewModel khi các màn hình có lifecycle/hành vi độc lập.
- `DcamMediaOutput` vẫn lộ CameraX output type nhưng chỉ bên trong `platform`.
  Không truyền type này vào feature hoặc core.
- `CameraPreview` vừa là View vừa là `CameraService` implementation. Cần tách
  preview khỏi capture driver trước khi làm recovery hoặc vendor SDK phức tạp.
- `RecordingForegroundService` chỉ cung cấp foreground visibility/process
  priority; CameraX adapter vẫn sở hữu active recording. Process-death recovery
  cần design riêng.
- Các empty contract cho cloud, location, metadata, security, streaming và
  update chỉ giữ tên boundary đã dự kiến. Chúng không chứng minh feature đã được
  implement và không được thêm method khi requirement/design còn TBD.
- Dự án vẫn có một Gradle module `:app`; dependency được enforce bằng test ở mức
  source, chưa được compiler enforce giữa nhiều build module.
