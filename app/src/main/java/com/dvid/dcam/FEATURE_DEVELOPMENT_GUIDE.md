# Hướng dẫn phát triển feature DCAM

Tài liệu này dành cho thành viên mới và intern. Mục tiêu là giúp trả lời nhanh
ba câu hỏi khi nhận một task:

1. File mới phải đặt ở đâu?
2. Logic nào thuộc file/layer nào?
3. Cần kiểm tra gì trước khi tạo pull request?

Đọc [ARCHITECTURE.md](ARCHITECTURE.md) trước để hiểu sơ đồ tổng thể. Tài liệu
này tập trung vào cách áp dụng kiến trúc đó trong công việc hằng ngày.

## 1. Mô hình cần nhớ

DCAM tổ chức source theo **feature trước, layer sau**.

```text
app → feature → core
  └→ platform → feature/core contracts
```

Luồng xử lý chuẩn bên trong một feature:

```text
View/Activity
    ↓ quan sát state và gửi action
ViewModel
    ↓ gọi
UseCase
    ↓ phụ thuộc
Repository
    ↓ phụ thuộc
Domain contract
    ↑ được implement bởi
Platform adapter
```

Giải thích ngắn:

- `app`: khởi động ứng dụng, navigation và nối các dependency.
- `feature`: hành vi của sản phẩm, ví dụ capture, media, device.
- `platform`: code nói chuyện trực tiếp với Android, CameraX, Room, file hoặc SDK.
- `core`: contract/model nhỏ được nhiều feature thật sự sử dụng chung.

MVVM chỉ mô tả phần UI (`View → ViewModel → state`). Clean Architecture mô tả
hướng dependency của toàn ứng dụng. Hai khái niệm này bổ sung cho nhau.
Trong tài liệu sản phẩm, `MVP` có nghĩa là **Minimum Viable Product**, không phải
Model-View-Presenter.

## 2. Chọn thư mục trong 30 giây

| Câu hỏi | Nếu câu trả lời là “có” | Đặt tại |
|---|---|---|
| Code này render màn hình hoặc tạo UI state riêng của feature? | UI-specific | `feature/<name>/presentation` |
| Code này mô tả một workflow hoặc business rule? | Application logic | `feature/<name>/application` |
| Code này là model, repository interface hoặc capability contract không biết Android? | Domain logic | `feature/<name>/domain` |
| Code này phối hợp repository/domain contract và map dữ liệu, nhưng không gọi Android SDK? | Feature data | `feature/<name>/data` |
| Code này import `android.*`, CameraX, Room, WorkManager hoặc vendor SDK? | Platform implementation | `platform/<capability>` |
| Code này điều hướng hoặc phối hợp nhiều feature? | App shell | `app/navigation` hoặc `app/presentation` |
| Code này tạo/chọn concrete implementation? | Dependency wiring | `app/AppComposition.java` |
| Code này được nhiều feature sử dụng và không phụ thuộc feature nào? | Shared primitive/contract | `core` |

Nếu vẫn chưa chắc, đừng chọn `core` theo phản xạ. Hãy đặt code vào feature đang
sở hữu nghiệp vụ trước. Chỉ chuyển sang `core` khi có ít nhất hai feature thật
sự cần cùng một abstraction ổn định.

## 3. Trách nhiệm của từng layer

### `presentation`: màn hình cần hiển thị gì?

Thường chứa:

- `*ViewModel`
- `*UiState` hoặc state phục vụ riêng cho UI
- mapping từ domain result sang text/trạng thái hiển thị

Được phép:

- dùng AndroidX ViewModel, LiveData;
- gọi use case;
- tạo immutable UI state;
- xử lý action đơn giản như chọn tab hoặc yêu cầu refresh.

Không được:

- gọi CameraX, Room, filesystem, Retrofit hoặc vendor SDK;
- tự quyết định đường dẫn lưu file;
- chứa workflow dài hoặc business policy;
- import class trong `platform`.

Ví dụ hiện tại:

```text
feature/media/presentation/MediaBrowserState.java
app/presentation/MainViewModel.java
```

`MainViewModel` nằm trong `app` vì hiện tại nó phối hợp capture, media, device và
navigation. Khi một màn hình có lifecycle/hành vi độc lập, ưu tiên tách
ViewModel riêng vào `feature/<name>/presentation`.

### `application`: hệ thống phải làm gì?

Thường chứa:

- `*UseCase`
- command/application action
- workflow, precondition và thứ tự các bước

Được phép:

- phụ thuộc repository/domain interface;
- kiểm tra điều kiện trước khi chạy;
- phối hợp nhiều thao tác thuộc cùng một feature;
- trả về domain result rõ ràng.

Không được:

- biết Activity, View hoặc Android lifecycle;
- import `android.*`, `androidx.*` hay `platform`;
- tự mở file, database hoặc SDK;
- format text dành riêng cho UI.

Tên use case nên diễn tả một hành động:

```text
TakePhotoUseCase
StartVideoUseCase
BrowseMediaUseCase
RefreshDeviceStatusUseCase
```

### `domain`: khái niệm và contract của feature là gì?

Thường chứa:

- model/value object;
- enum trạng thái;
- `*Repository` interface;
- capability contract như `CameraService` hoặc `MediaBrowserService`;
- domain event/result/error.

Domain phải là Java thuần và không biết implementation bên dưới. Nếu một class
domain cần import `Context`, `Uri`, CameraX type hoặc Room annotation thì boundary
đang sai.

Lưu ý: hậu tố `Service` trong `domain` nghĩa là **capability contract**, không
phải Android `Service`. Android service thật phải nằm trong `platform` và có tên
rõ như `RecordingForegroundService`.

Không thêm method vào interface scaffold chỉ để “chuẩn bị trước”. Method chỉ
được thêm sau khi use case, requirement và kiểu result/error đã đủ rõ.

### `data`: phối hợp contract của feature như thế nào?

Thường chứa implementation repository, ví dụ:

```text
DefaultCaptureRepository
DefaultMediaRepository
DefaultDeviceRepository
```

Repository implementation có thể:

- gọi nhiều domain service interface;
- map dữ liệu giữa service result và domain model;
- điều phối thứ tự thao tác thuộc feature;
- chuẩn hóa lỗi trước khi trả lên use case.

Repository implementation không được gọi trực tiếp Android/vendor class. Nó
phải phụ thuộc interface trong `domain`.

### `platform`: Android hoặc SDK thực hiện contract như thế nào?

Thường chứa:

- CameraX/Camera2/vendor SDK adapter;
- Android audio/location/device adapter;
- Room DAO/database/entity;
- filesystem/MediaStore;
- WorkManager, network provider và Android Service;
- permission và hardware-key routing.

Platform adapter chịu trách nhiệm:

- gọi API/SDK thật;
- tuân thủ threading/lifecycle của SDK;
- chuyển exception/status code thô thành result mà feature hiểu;
- giữ provider-specific type không rò lên feature;
- log đủ context an toàn để chẩn đoán lỗi.

Platform adapter không nên quyết định business policy. Ví dụ adapter có thể báo
“storage còn 200 MB”; use case/repository mới quyết định có cho phép record hay
không theo policy đã được duyệt.

### `app`: ứng dụng được lắp ráp như thế nào?

`app` chứa:

- Android entry point;
- navigation giữa feature;
- state/ViewModel phối hợp nhiều feature;
- `AppComposition`, nơi chọn concrete implementation.

`MainActivity` chỉ nên:

- xử lý Android lifecycle và permission launcher;
- inflate ViewBinding;
- render state;
- chuyển user input sang ViewModel/command;
- điều hướng hoặc mở Android intent thuộc UI shell.

Không tạo repository/adapter rải rác trong Activity. Mọi wiring mới phải đi qua
`AppComposition`.

## 4. Ví dụ luồng có thật: duyệt media

```text
MainActivity
    ↓ yêu cầu mở folder
MainViewModel
    ↓
BrowseMediaUseCase
    ↓
MediaRepository
    ↑ implemented by
DefaultMediaRepository
    ↓ phụ thuộc contract
MediaBrowserService
    ↑ implemented by
LocalMediaBrowserService (platform/storage)
```

Ý nghĩa:

- UI không biết folder vật lý nằm ở đâu.
- Use case không biết Android filesystem.
- Repository không phụ thuộc concrete storage adapter.
- Chỉ `LocalMediaBrowserService` chạm vào file thật.
- `AppComposition` nối repository với platform adapter.

Khi chưa rõ cách làm feature mới, hãy lần theo luồng này hoặc capture flow trước
khi tạo thêm abstraction.

## 5. Quy trình implement một feature

### Bước 1: xác nhận requirement

Trước khi code, cần biết:

- người dùng/hệ thống muốn đạt kết quả gì;
- input và output là gì;
- success, failure, unavailable có ý nghĩa gì;
- dữ liệu nào phải lưu;
- hành vi offline, lifecycle và retry;
- acceptance test nào chứng minh task hoàn thành.

Nếu contract còn `TBD`, không tự phát minh schema, trạng thái, encryption hoặc
quyền sở hữu dữ liệu. Ghi rõ blocker/open decision trong task hoặc design.

### Bước 2: xác định feature sở hữu hành vi

Ví dụ:

- chụp/record/SOS → `feature/capture`;
- duyệt media → `feature/media`;
- trạng thái thiết bị → `feature/device`;
- location/GPS → `feature/location`;
- metadata → `feature/metadata`.

Không tạo feature mới chỉ vì có một class helper.

### Bước 3: bắt đầu từ domain contract và use case cần thiết

Chỉ tạo model/interface mà use case hiện tại thật sự cần. Tránh tạo đủ bốn folder
và hàng loạt interface rỗng để cây thư mục trông “đẹp”.

### Bước 4: viết repository/data coordination

Repository phụ thuộc domain contract. Dùng fake implementation để test workflow
trước khi nối Android SDK nếu có thể.

### Bước 5: viết platform adapter

Đặt mọi Android/provider import vào `platform`. Map callback, exception và
threading ngay tại boundary này.

### Bước 6: viết presentation

Tạo state bất biến, ViewModel và screen cần thiết. ViewModel chỉ gọi use case và
map kết quả sang state.

### Bước 7: wiring trong `AppComposition`

Chọn concrete adapter và truyền nó vào repository/ViewModel factory. Không dùng
singleton/global mutable registry để nối feature.

### Bước 8: thêm test và chạy verification

Test package nên mirror production package:

```text
src/main/java/com/dvid/dcam/feature/media/...
src/test/java/com/dvid/dcam/feature/media/...
```

Chạy tối thiểu:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
.\gradlew.bat lintDebug
```

Feature liên quan camera, permission, lifecycle, Room migration hoặc hardware
phải có thêm instrumentation/real-device evidence phù hợp. Unit test pass không
chứng minh feature hoạt động đúng trên BodyCamera.

## 6. Naming convention

Tên package viết thường và ưu tiên danh từ số ít theo capability, ví dụ
`camera`, `device`, `permission`, `storage`. Các tên mô tả hoạt động như
`logging` và `recording` không phải dạng số nhiều. Tính nhất quán quan trọng hơn
việc cố biến mọi tên thành cùng một dạng ngữ pháp.

| Loại class | Quy ước | Ví dụ |
|---|---|---|
| UI state | `*UiState` hoặc tên state rõ nghĩa | `MainUiState`, `MediaBrowserState` |
| ViewModel | `*ViewModel` | `MainViewModel` |
| Use case | động từ + đối tượng + `UseCase` | `TakePhotoUseCase` |
| Repository interface | `*Repository` | `MediaRepository` |
| Repository implementation | `Default*Repository` | `DefaultMediaRepository` |
| Domain capability | `*Service`, `*Provider` hoặc `*Gateway` theo ADR/convention của feature | `CameraService` |
| Android adapter | `Android*`, `CameraX*` hoặc tên provider rõ ràng | `AndroidDeviceInfoProvider` |
| Android Service | `*ForegroundService` hoặc tên lifecycle rõ ràng | `RecordingForegroundService` |
| Immutable domain value | danh từ nghiệp vụ | `DeviceStatus`, `MediaEntry` |

Tên phải mô tả trách nhiệm. Tránh các tên chung chung như `Utils`, `Manager`,
`Helper`, `CommonService` hoặc `DataHandler`. Nếu không thể đặt tên rõ, class có
thể đang làm quá nhiều việc.

Android resource nên có prefix theo mục đích/feature:

```text
screen_camera.xml
screen_file_explorer.xml
item_media_entry.xml
```

Không hardcode user-facing text trong Java nếu text thuộc UI ổn định; đặt nó vào
`res/values/strings.xml`.

## 7. Dependency giữa các feature

Mặc định, một feature không import trực tiếp implementation của feature khác.

Khi feature A cần dữ liệu/hành vi của feature B:

1. kiểm tra xem `app` có thể phối hợp hai use case hay không;
2. nếu đó là contract dùng chung thật sự, cân nhắc đưa abstraction nhỏ vào
   `core` sau review;
3. không import `feature/b/data/Default...` từ feature A;
4. không đưa cả feature vào `core` để né dependency rule.

Ví dụ không nên làm:

```java
// Trong feature/metadata
new DefaultCaptureRepository(...);
```

Việc chọn `DefaultCaptureRepository` thuộc `AppComposition`, không thuộc metadata.

## 8. Threading và lifecycle

| Loại công việc | Cách xử lý |
|---|---|
| UI update | Main thread qua ViewModel/LiveData |
| File, checksum, parsing | `ExecutorService` hoặc executor được thiết kế rõ |
| Room/database | Room/database executor |
| WorkManager task | WorkManager-managed execution |
| Stateful vendor/hardware command | `HandlerThread` hoặc serialized thread theo SDK |
| CameraX callback | Theo executor/lifecycle contract của CameraX |

Không block main thread. Không tự tạo thread pool chung cho command hardware có
thứ tự nhạy cảm. Không giữ Activity/View trong object sống lâu hơn lifecycle.

## 9. State, result và error

- UI state nên bất biến.
- Trạng thái phải đủ rõ: idle, loading/preparing, active, completed, error.
- Platform exception/status code phải được map tại adapter/repository boundary.
- Không đẩy raw vendor exception qua nhiều layer.
- Error message cho người dùng và diagnostic detail cho log là hai mục đích khác
  nhau; không hiển thị secret/path nhạy cảm lên UI.
- Optional capability như GPS/cloud không được làm hỏng capture nếu requirement
  nói core flow phải tiếp tục offline.

## 10. Những lỗi tổ chức code thường gặp

| Không nên | Nên làm |
|---|---|
| Gọi CameraX trực tiếp từ ViewModel | Tạo domain contract và platform adapter |
| Đọc/ghi file trong Activity | Đi qua use case/repository/storage contract |
| Đặt mọi helper vào `core` | Giữ helper trong feature sở hữu nó |
| Repository import concrete Android adapter | Repository phụ thuộc domain interface |
| Platform adapter quyết định business policy | Adapter báo capability/result; use case quyết định policy |
| Tạo interface rỗng “để dành” | Chỉ tạo signature từ requirement/use case đã duyệt |
| Một ViewModel quản lý mọi feature mãi mãi | Tách feature ViewModel khi màn hình đủ độc lập |
| Catch exception rồi bỏ qua | Map lỗi và log context an toàn |
| Chỉ test happy path | Test success, failure, unavailable và boundary cases |

## 11. Checklist trước pull request

### Vị trí và dependency

- [ ] File nằm trong feature/capability thực sự sở hữu nó.
- [ ] Production code chỉ thuộc `app`, `feature`, `platform` hoặc `core`.
- [ ] Feature application/domain/data không import Android hoặc `platform`.
- [ ] App presentation không gọi platform adapter trực tiếp.
- [ ] Repository phụ thuộc interface, không phụ thuộc concrete SDK adapter.
- [ ] Wiring concrete implementation chỉ nằm trong `AppComposition`.

### Logic và chất lượng

- [ ] Activity/View chỉ render state và forward action.
- [ ] ViewModel không chứa file/SDK/business workflow.
- [ ] Use case mô tả workflow rõ ràng.
- [ ] Platform error được map, không rò raw provider type lên trên.
- [ ] Long-running work không block UI thread.
- [ ] Không log secret hoặc dữ liệu nhạy cảm không cần thiết.
- [ ] Không biến prototype storage/schema thành contract khi design còn TBD.

### Test và evidence

- [ ] Có unit test cho success và failure quan trọng.
- [ ] Test package mirror production package.
- [ ] Architecture tests pass.
- [ ] `testDebugUnitTest assembleDebug` pass.
- [ ] `lintDebug` không có error mới.
- [ ] Có real-device/integration evidence nếu task liên quan hardware, BDMA hoặc
      acceptance target không thể chứng minh bằng unit test.

## 12. Khi nào cần hỏi senior/reviewer trước khi code tiếp?

Hỏi khi task yêu cầu một trong các quyết định sau nhưng tài liệu chưa chốt:

- schema metadata hoặc DCAM–BDMA Data Contract;
- folder/filename/status trở thành public compatibility contract;
- encryption, key ownership hoặc dữ liệu nhạy cảm;
- xóa/ghi dữ liệu qua boundary DCAM–BDMA;
- thay đổi recording lifecycle/process-death ownership;
- thêm vendor SDK, cloud provider hoặc Gradle module;
- tạo dependency trực tiếp giữa hai feature;
- đưa một abstraction mới vào `core`;
- thay đổi API/domain contract đang được feature khác dùng.

Mục tiêu không phải tạo nhiều layer nhất có thể. Mục tiêu là để một developer
khác có thể tìm đúng code, hiểu đúng trách nhiệm và thay implementation mà không
làm vỡ business flow.
