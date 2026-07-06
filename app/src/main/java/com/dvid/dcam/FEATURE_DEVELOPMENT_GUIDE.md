# Hướng dẫn phát triển feature DCAM

Tài liệu này viết cho người mới/intern. Mục tiêu là khi nhận một task, mọi
người biết file nên đặt ở đâu và logic nên nằm ở lớp nào, không phải đoán ý đồ
kiến trúc.

Đọc thêm: [ARCHITECTURE.md](ARCHITECTURE.md).

## 1. Nhớ nhanh trong 30 giây

```text
domain ← application ← presentation/app/platform
```

- `domain`: dữ liệu/quy tắc thuần Java.
- `application/usecase`: workflow nghiệp vụ.
- `application/port`: interface mà application cần để gọi repository/hardware/provider.
- `presentation`: ViewModel/UI state của feature, nếu feature có màn hình riêng.
- `platform`: Android, CameraX, MediaRecorder, filesystem, Room, WorkManager.
- `app`: Activity, navigation, cross-feature ViewModel, composition root.

## 2. Cây package của một feature

```text
feature/<name>/
├── domain/
├── application/
│   ├── usecase/
│   ├── port/
│   └── repository/        chỉ dùng khi implementation thuần application/core
└── presentation/          chỉ dùng khi feature có UI state/ViewModel riêng
```

Không cần feature nào cũng đủ mọi folder. Nhưng đã có loại file nào thì đặt
đúng chỗ đó.

Không tạo `interfaces/`, `classes/`, `implementations/`, `services/`,
`adapter/in`, `adapter/out`, `port/in`, hoặc `port/out` trong source hiện tại.

## 3. Chọn vị trí file

| Câu hỏi | Đặt ở đâu |
|---|---|
| Đây là enum/entity/value object/rule thuần Java? | `feature/<name>/domain` |
| Đây là workflow như start recording, browse media, đổi ngôn ngữ? | `feature/<name>/application/usecase` |
| Đây là interface để use case gọi camera, audio, storage, repository, logging? | `feature/<name>/application/port` hoặc `core/<capability>/application/port` |
| Đây là repository implementation thuần Java, không Android SDK? | `application/repository` |
| Đây là UI state/ViewModel riêng của feature? | `feature/<name>/presentation` |
| Đây là Android/CameraX/MediaRecorder/filesystem/Room/WorkManager code? | `platform/<capability>` |
| Đây là wiring chọn implementation thật? | `app/AppComposition` |
| Đây là Activity/navigation/cross-feature shell? | `app` |

## 4. Khi nào tạo interface?

Team chọn convention chặt:

1. Use case public phải là interface.
2. Implementation của use case phải là `*UseCaseImpl`.
3. Capability boundary trong `application/port` cũng là interface.
4. Concrete implementation của project interface phải kết thúc bằng `Impl`.

Tạo interface khi có boundary thật:

- UI hoặc hardware router cần gọi workflow application;
- application cần gọi Android/hardware/provider mà không phụ thuộc SDK cụ thể;
- cần fake để unit test workflow;
- repository là contract giữa use case và data source;
- có khả năng đổi implementation như CameraX/vendor/fake/local/remote.

Không tạo interface cho:

- model/enum/value object;
- helper nhỏ;
- class chỉ có một responsibility nội bộ, không cần fake;
- feature chưa có requirement.

Nếu chưa trả lời được “ai gọi interface này?” và “implementation nào làm thật?”,
thì khoan tạo.

## 5. Use case interface

Use case là API đi vào application.

```java
package com.dvid.dcam.feature.capture.application.usecase;

public interface VideoRecordingUseCase {
    void toggleVideo();
    void startVideo();
    void startSos();
    void stopRecording();
    void toggleSos();
}
```

Implementation:

```java
package com.dvid.dcam.feature.capture.application.usecase;

import com.dvid.dcam.feature.capture.application.port.CameraGateway;

public final class VideoRecordingUseCaseImpl implements VideoRecordingUseCase {
    private final CameraGateway camera;

    public VideoRecordingUseCaseImpl(CameraGateway camera) {
        this.camera = camera;
    }

    @Override public void startVideo() {
        camera.startVideo();
    }
}
```

Một use case interface có thể gom nhiều operation cùng một nghiệp vụ nhỏ.
Ví dụ `VideoRecordingUseCase` gom start/stop/toggle video và SOS vì đều là
video-recording workflow. Đừng gom bừa `Media + Device + Settings` vào một
interface lớn.

## 6. Interface trong `application/port`

Đây là API application cần để gọi ra ngoài. Folder tên `port` vì theo
Clean/Hexagonal, nhưng **class name không dùng `Port`**.

Ví dụ:

```java
package com.dvid.dcam.feature.capture.application.port;

public interface CameraGateway {
    void takePhoto();
    void startVideo();
    void startSos();
    void stopRecording();
}
```

Implementation nằm ở platform nếu dùng Android/framework:

```java
package com.dvid.dcam.platform.camera;

import com.dvid.dcam.feature.capture.application.port.CameraGateway;

public final class CameraXCameraGatewayImpl implements CameraGateway {
    // CameraX implementation
}
```

Tên nên theo capability, không theo chữ `Port`:

| Nên dùng | Không dùng |
|---|---|
| `CameraGateway` | `CameraPort` |
| `AudioRecorder` | `AudioPort` |
| `MediaOpener` | `MediaOpenPort` |
| `LanguagePreferenceStore` | `LanguagePreferencePort` |
| `ConfigurationSource` | `ConfigurationSourcePort` |
| `LogSink` | `LogPort` |

`Repository` giữ nguyên vì đã là boundary quen thuộc: `MediaRepository`,
`DeviceRepository`, `ConfigurationRepository`.

## 7. Ví dụ luồng capture đủ layer

```text
UI button / hardware key
    ↓
VideoRecordingUseCase                  application/usecase interface
    ↓
VideoRecordingUseCaseImpl              application/usecase implementation
    ↓
CameraGateway                          application/port interface
    ↑
CameraXCameraGatewayImpl               platform/camera implementation
    ↓
CameraX                                framework thật
```

Đọc dòng trên hơi ngược một chút:

- Flow chạy runtime: UI → use case → gateway → CameraX.
- Dependency source code: implementation ngoài phụ thuộc interface trong.

Đó là “dependency rule”.

## 8. Ví dụ đổi ngôn ngữ

```text
MainActivity
    ↓
LanguageSettingsUseCase
    ↓
LanguageSettingsUseCaseImpl
    ↓
LanguagePreferenceStore
    ↑
AndroidLanguagePreferenceStoreImpl
    ↓
SharedPreferences / LocaleManager
```

Use case không biết `SharedPreferences`. UI không tự ghi preference. Platform
implementation mới biết Android lưu và apply locale như thế nào.

## 9. Naming convention

| Loại | Quy ước | Ví dụ |
|---|---|---|
| Use case interface | `*UseCase` | `BrowseMediaUseCase` |
| Use case implementation | `*UseCaseImpl` | `BrowseMediaUseCaseImpl` |
| Repository interface | `*Repository` | `MediaRepository` |
| Repository implementation | provider + `*RepositoryImpl` | `LocalMediaRepositoryImpl` |
| Capability interface | tên capability, không `Port` | `CameraGateway`, `LogSink` |
| Platform implementation | provider/framework + capability + `Impl` | `CameraXCameraGatewayImpl` |
| Test fake | `Fake*Impl` nếu implement interface | `FakeVideoRecordingUseCaseImpl` |
| UI state | `*State` hoặc `*UiState` | `MediaBrowserState`, `MainUiState` |
| ViewModel | `*ViewModel` | `MainViewModel` |

Không dùng `I` prefix kiểu `ICameraGateway`. Không dùng `Manager`, `Helper`,
`Utils`, `Service` nếu có tên trách nhiệm rõ hơn.

## 10. Checklist implement feature

### Bước 1: hiểu requirement

Trước khi code, phải biết:

- user/action nào trigger;
- output thành công là gì;
- lỗi/unavailable xử lý sao;
- có cần offline không;
- có cần persist không;
- có cần BDMA/Data Contract không.

### Bước 2: tạo domain nếu cần

Chỉ tạo domain model phục vụ requirement hiện tại. Không tạo model “để sau này”.

### Bước 3: tạo use case

Tạo interface `*UseCase` và implementation `*UseCaseImpl` trong
`application/usecase`.

Use case nên chứa workflow:

- kiểm tra precondition;
- quyết định thứ tự gọi;
- gọi repository/gateway/store;
- map result về dạng application hiểu.

### Bước 4: tạo port nếu use case cần gọi ra ngoài

Nếu use case cần Android/hardware/filesystem/provider, tạo interface trong
`application/port`. Đặt tên theo capability.

### Bước 5: tạo implementation

- Android/SDK/filesystem/Room thật → `platform/<capability>`.
- Pure repository implementation → `application/repository`.
- Tất cả class implement project interface → `Impl`.

### Bước 6: wiring trong `AppComposition`

Không `new CameraX...` trong ViewModel/use case. Nối graph tại
`AppComposition`.

### Bước 7: test

- Unit test use case bằng fake interface.
- Test platform/repository boundary nếu có logic quan trọng.
- Chạy architecture test để bắt dependency sai.
- Với camera/audio/storage thật, cần real-device/instrumentation sau.

## 11. Không đặt logic ở đâu?

| Logic | Không nên đặt | Nên đặt |
|---|---|---|
| Start/stop/toggle recording policy | Activity/ViewModel/platform | `VideoRecordingUseCaseImpl` |
| Tạo file bằng CameraX/MediaStore | Use case/domain | `platform/storage` hoặc `platform/camera` |
| Parse config/fallback default | Activity | `ConfigurationRepositoryImpl` |
| Lưu language preference Android | Use case | `AndroidLanguagePreferenceStoreImpl` |
| Hiển thị row/menu/text | Use case/domain | Activity/ViewModel/presentation |
| Map exception SDK thành trạng thái app | UI/domain | Platform implementation boundary |

## 12. Checklist trước PR

- [ ] Domain không import Android/SDK/platform.
- [ ] Use case nằm trong `application/usecase`.
- [ ] Use case có interface và `Impl`.
- [ ] Interface gọi ra ngoài nằm trong `application/port`.
- [ ] Capability interface không dùng hậu tố `Port`.
- [ ] Implementation của project interface kết thúc bằng `Impl`.
- [ ] Android/CameraX/MediaRecorder/filesystem/Room code nằm trong `platform`.
- [ ] Wiring concrete implementation chỉ nằm trong `AppComposition`.
- [ ] Không tạo interface/feature rỗng để placeholder.
- [ ] Architecture tests pass.
- [ ] `test` và `assembleDebug` pass trước khi handoff.

## 13. Khi nào hỏi reviewer/senior?

Hỏi trước khi:

- thêm public interface mới mà chưa rõ requirement;
- tạo dependency trực tiếp giữa hai feature;
- đưa code vào `core`;
- thay storage/metadata/BDMA contract;
- thêm vendor SDK/cloud provider/update mechanism;
- thay recording owner/process-death recovery;
- đổi convention package hoặc naming.

Kiến trúc tốt không phải là nhiều file. Kiến trúc tốt là nhìn vào file biết
logic thuộc ai, boundary ở đâu, và đổi implementation nào sẽ không làm vỡ rule
bên trong.
