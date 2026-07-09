# Module Media Viewer

**Page ID**: 27197712  
**Version**: 3  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27197712

---


# 1. Tổng quan module

Module Media Viewer là tính năng xem và phát lại file ảnh / video trực tiếp trong ứng dụng BDMA. Người dùng mở viewer từ danh sách file đã đồng bộ; viewer cho phép duyệt qua từng file, xem metadata, toạ độ GPS và phát video với đầy đủ điều khiển playback.

 

**Phạm vi hỗ trợ:**

Ảnh tĩnh: file ảnh JPG (type = "image").

Video: MP4 thông thường (type = "video") và file IMP từ bodycam (type = "IMP").

Metadata GPS: đọc từ EXIF cho ảnh, đọc từ metadata track XML trong MP4 cho video.

 

# 2. Kiến trúc & Cấu trúc file

 

## 2.1 Sơ đồ package

**Package**

**Nội dung**

modules/media/controllers

MediaViewerController - controller FXML duy nhất của màn hình

modules/media/services

MediaViewerService - quản lý Stage; MediaMetadataService - đọc GPS

modules/media/readers

ImageMetadataReader, VideoMetadataReader, interface MediaMetadataReader

modules/media/dtos

GpsCoordinate, GpsPoint

resources/fxml/common/media

media-viewer.fxml - layout toàn bộ màn hình

resources/css/common/media

media-viewer.css – css của phần media

 

## 2.2 Luồng khởi tạo

Khi người dùng double-click file từ danh sách, code gọi MediaViewerService.open(allFiles, clickedFile, ownerStage). Service lọc danh sách chỉ giữ file viewable (ảnh hoặc video), tính index của file được click, sau đó:

Nếu Stage chưa tồn tại: load FXML qua ViewLoader, tạo Scene, áp theme, gắn owner, hiển thị Stage mới.

Nếu Stage đã mở: gọi setMedia trực tiếp trên controller đang chạy để chuyển file – không tạo lại Stage.

Sau khi Stage hiển thị, controller nhận danh sách file và index bắt đầu qua setMedia().

Controller gọi callback onLoadFile để điều phối load ảnh hoặc video tuỳ type.

 

# 3. Mô tả chi tiết từng class

## 3.1 MediaViewerService

**Package: **com.app.common.modules.media.services

**Scope: **Spring Singleton.

 

Service đóng vai trò orchestrator: tạo và quản lý vòng đời của Stage viewer (singleton stage pattern – chỉ một cửa sổ viewer tồn tại tại một thời điểm). Các trách nhiệm chính:

Lọc danh sách file có thể xem (isViewable).

Khởi tạo Stage lần đầu (initStage): load FXML, áp CSS, áp theme, set owner/modality.

Tái sử dụng Stage: nếu Stage đã tồn tại chỉ cập nhật media, không tạo lại.

Dispatch load đúng loại: loadImage hoặc loadVideo trên controller.

Lắng nghe ThemeChangedEvent và LanguageChangedEvent để cập nhật UI khi cần.

Giải phóng tài nguyên khi Stage đóng (cleanup controller, null stage/controller).

 

**Method**

**Mô tả**

open(allFiles, clicked, owner)

Entry point công khai. Lọc file, tính index, mở/cập nhật Stage.

close()

Đóng Stage, cleanup tài nguyên.

isViewable(type)

Kiểm tra type có phải image hoặc video/IMP không.

onThemeChanged(event)

@EventListener – áp lại theme khi người dùng đổi theme.

onLanguageChanged(event)

@EventListener – gọi refreshLocalizedText() trên controller.

 

## 3.2 MediaViewerController

**Package: **com.app.common.modules.media.controllers

**Scope: **Spring Prototype (mỗi lần load FXML tạo instance mới).

 

Controller quản lý toàn bộ UI của cửa sổ viewer. Layout gồm hai vùng chính: panel detail bên trái (220px cố định) và vùng media bên phải (grow ALWAYS). Vùng media dùng StackPane để overlay 3 trạng thái: ScrollPane ảnh, StackPane video, và label loading/error.

 

### 3.2.1 Quản lý trạng thái UI

**Trạng thái**

**Hiển thị gì**

Loading

lblLoading hiện, scrollPane và videoPane ẩn, statusBar trắng.

Image

scrollPane hiện, videoPane ẩn, các tool zoom/rotate hiện.

Video

videoPane hiện, scrollPane ẩn, tool zoom/rotate ẩn.

Error

lblError hiện với thông báo lỗi, các pane khác ẩn.

 

### 3.2.2 Load ảnh (loadImage)

Chạy trên background thread (ExecutorService single-thread). Dùng FolderManagerService để resolve đường dẫn tuyệt đối từ syncedPath. Tạo JavaFX Image với background loading (tham số thứ 3 = true), lắng nghe progressProperty để chuyển sang onImageReady khi tải xong. Sau khi ảnh sẵn sàng:

Gắn Image vào ImageView.

Gọi fitImageToPane() để scale ảnh vừa pane.

Cập nhật status bar và detail panel.

Đọc GPS từ EXIF trên background thread, cập nhật label detailGps.

 

### 3.2.3 Load video (loadVideo)

Resolve path trên background thread. Tạo MediaPlayer trên FX thread (bắt buộc vì MediaPlayer là JavaFX object). Đồng thời submit job đọc GPS timeline trên background thread. Sau khi MediaPlayer ready (setOnReady):

Tự động play, nút btnPlayPause hiện ký hiệu Pause.

Cập nhật dimension từ media metadata thực tế.

VideoPane nhận focus để nhận phím tắt.

 

### 3.2.4 Điều khiển video

**Tính năng**

**Chi tiết**

Play/Pause

Nút btnPlayPause + click vào vùng video. Trạng thái toggle giữa ▶ và ⏸

.

Seek slider

Kéo videoSlider để seek. Dùng flag sliderDragging tránh feedback loop khi currentTimeProperty cập nhật.

Seek nhanh

Nút &minus;30s, &minus;5s, +5s, +30s và phím &larr; &rarr; trên bàn phím (5 giây mỗi lần).

Tốc độ phát

ComboBox: 0.25x, 0.5x, 0.75x, 1x, 1.25x, 1.5x, 2x. Set mediaPlayer.setRate().

Âm lượng

Slider volumeSlider bind trực tiếp vào mediaPlayer.volumeProperty().

GPS theo thời gian

Mỗi tick currentTimeProperty gọi updateGpsForTime(): tìm GpsPoint gần nhất theo thời gian và hiển thị toạ độ.

Kết thúc video

Tự seek về đầu, pause, slider reset về 0.

 

### 3.2.5 Zoom & Rotate ảnh

Hai chế độ zoom:

[object Object] (mặc định): fitImageToPane() scale ảnh theo tỉ lệ nhỏ nhất giữa width/height của pane. Tự động re-fit khi resize cửa sổ.

[object Object]: tắt khi nhấn nút Zoom In/Out hoặc Ctrl+Scroll. applyZoom(factor) nhân zoomFactor với factor và set FitWidth/FitHeight tuyệt đối.

Rotate dùng Transform Rotate quanh tâm của ImageView. Mỗi click xoay ±90 độ, tích luỹ vào biến rotation.

 

### 3.2.6 Cleanup tài nguyên

Method cleanup() được gọi khi Stage đóng:

stopCurrentMedia(): unbind volume, stop và dispose MediaPlayer, remove tất cả ChangeListener trên boundsProperty/widthProperty/heightProperty của videoPane.

executor.shutdownNow(): dừng background thread.

 

## 3.3 MediaMetadataService

**Package: **com.app.common.modules.media.services

Service facade điều phối đến reader phù hợp dựa trên type:

 

**Method**

**Type được xử lý**

**Delegate đến**

readGps(path, type)

"image"

ImageMetadataReader.readGps()

readGps(path, type)

"video", "IMP"

VideoMetadataReader.readGps() – trả toạ độ điểm đầu tiên trong timeline

readGpsTimeline(path, type)

"video", "IMP"

VideoMetadataReader.readGpsTimeline() – trả toàn bộ danh sách GpsPoint

 

## 3.4 ImageMetadataReader

**Thư viện: **metadata-extractor (com.drewnoakes).

Đọc EXIF GPS directory từ file ảnh. Kiểm tra tồn tại của TAG_LATITUDE và TAG_LONGITUDE, sau đó lấy GeoLocation. Trả Optional.empty() nếu không có GPS hoặc GPS là (0, 0). Bắt mọi Exception và log warning thay vì throw.

 

## 3.5 VideoMetadataReader

**Thư viện: **mp4parser (com.googlecode.mp4parser).

Đọc GPS timeline từ meta track trong file MP4. Luồng xử lý:

Mở file bằng MovieCreator.build().

Duyệt tất cả Track, chỉ xử lý track có handler = "meta".

Mỗi sample của meta track được decode sang chuỗi ISO_8859_1, tìm block <msg>...</msg> bằng regex.

Trong mỗi msg, extract <Latitude> và <Longitude> bằng regex riêng, parse sang double, tạo GpsPoint với timestamp tích luỹ.

readGps() lấy điểm đầu tiên trong timeline. readGpsTimeline() trả toàn bộ danh sách.

 

## 3.6 GpsCoordinate & GpsPoint

**Record**

**Fields**

**Ghi chú**

GpsCoordinate

latitude, longitude (double)

toString() format: "21.012345° N, 105.678901° E"

GpsPoint

timeSeconds, latitude, longitude (double)

Đại diện một điểm GPS gắn với timestamp trong video. toCoordinate() chuyển sang GpsCoordinate.

 

# 4. Layout FXML (media-viewer.fxml)

File FXML định nghĩa layout dạng HBox gốc (prefWidth=1100, prefHeight=700):

 

**Vùng**

**Nội dung**

Detail Panel (VBox, 220px)

Hiển thị metadata: tên file, type, size, ngày tạo, thiết bị, user, trạng thái, kích thước, GPS. Mỗi field gồm label key + label value.

Toolbar (HBox)

Nút Prev/Next điều hướng, nhóm nút zoom (＋ － ⊡) và rotate (↺ ↻), label đếm file.

Content StackPane

ScrollPane chứa ImageView (ảnh), StackPane video với controls overlay, label loading, label error. Chỉ một trong 3 hiện tại một thời điểm.

Video Controls

Slider timeline, nút &minus;30s/&minus;5s/Play/+5s/+30s, label thời gian, ComboBox tốc độ, slider âm lượng.

Status Bar (HBox)

Tên file (trái), dimension, dung lượng, zoom level (phải).

 

# 5. Luồng điều hướng

Danh sách truyền vào viewer đã được lọc ch��� gi��� file viewable. Controller giữ currentIndex và mediaList. Khi điều hướng:

onPrev() / onNext(): cập nhật currentIndex, gọi loadCurrent().

loadCurrent(): cập nhật nav buttons (disable khi ở đầu/cuối), cập nhật counter label, reset transform (zoom, rotate, stop media), rồi gọi callback onLoadFile.

onLoadFile được set từ MediaViewerService, dispatch đến controller.loadImage() hoặc controller.loadVideo() tuỳ type.

 

# 6. Dependency & Thư viện

**Thư viện**

**Dùng ở**

**Mục đích**

metadata-extractor

ImageMetadataReader

Đọc EXIF metadata từ file ảnh

mp4parser

VideoMetadataReader

Parse MP4 container, đọc meta track GPS

JavaFX Media

MediaViewerController

Phát video: MediaPlayer, MediaView

Spring EventListener

MediaViewerService

Lắng nghe ThemeChangedEvent, LanguageChangedEvent