# UI Design

**Page ID**: 27132122  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27132122

---


# 1. Tổng quan hệ thống CSS

Toàn bộ giao diện BDMA được xây dựng theo kiến trúc CSS phân lớp (layered CSS). Mỗi màn hình nhận đúng các lớp CSS cần thiết, không thừa, không thiếu. Thứ tự load có ảnh hưởng trực tiếp đến độ ưu tiên override.

## 1.1 Kiến trúc phân lớp

Hệ thống có 4 lớp CSS được load theo thứ tự từ thấp đến cao (lớp sau override lớp trước):

**Lớp**

**File**

**Mô tả**

1 — Base

/css/base.css

Reset, typography, component class chung toàn app (.btn-primary, .input, .form, .icon-btn, v.v.)

2 — Theme

/css/theme-dark.css hoặc theme-light.css

Toàn bộ CSS variable màu sắc (-card-bg, -text-color, -primary, v.v.) và override màu theo theme

3 — Admin

/css/admin.css

Layout shell admin: header, tab bar, status bar, storage bar, filter bar, pagination, context menu, dashboard panel

4 — Module

/css/admin/<tên-module>.css

Style cục bộ cho từng màn hình (dashboard, file-list-panel, user-settings-dialog, v.v.)

*⚠ File-list-panel.fxml tự khai báo stylesheet inline qua thuộc tính stylesheets="@/css/admin/file-list-panel.css" thay vì qua CssLoader. Đây là ngoại lệ duy nhất trong codebase.*

## 1.2 CssLoader — Cơ chế nạp CSS

Class CssLoader (com.app.common.helpers.CssLoader) là điểm tập trung duy nhất điều phối việc gắn/gỡ CSS vào Scene. Không nên gọi scene.getStylesheets() trực tiếp từ nơi khác.

**Phương thức**

**Khi nào gọi & hành vi**

applyBase(scene)

Gắn base.css nếu chưa có. Gọi một lần khi tạo Scene chính.

applyAdmin(scene)

Gỡ login.css, gắn admin.css. Gọi khi chuyển sang màn hình admin.

applyLogin(scene)

Gỡ admin.css và mọi CSS trong /css/admin/ hoặc /css/user/, gắn auth/login.css. Gọi khi về trang login.

applyModule(scene, fxml)

Gỡ toàn bộ module CSS cũ, rebuild đúng thứ tự base &rarr; theme &rarr; admin &rarr; module CSS. Gọi mỗi lần navigate sang module mới.

applyDialog(scene, fxml)

Gắn base + theme + module CSS cho cửa sổ dialog độc lập. Gọi khi tạo Stage mới qua DialogHelper.

resolveCssPath(fxml)

Utility nội bộ: thay /fxml/ thành /css/ và .fxml thành .css để tìm module CSS tương ứng.

*⚠ applyModule() luôn rebuild lại toàn bộ stylesheet list theo đúng thứ tự. Không dùng addIfAbsent() cho module CSS vì cần đảm bảo thứ tự ưu tiên.*

## 1.3 CSS Variables — Bảng màu

Toàn bộ màu sắc trong BDMA được định nghĩa dưới dạng CSS variable trên .root. Không hardcode màu HEX trực tiếp trong component CSS — luôn dùng variable để theme switching hoạt động đúng.

## 1.4 ThemeManager — Cơ chế đổi theme

ThemeManager (com.app.common.modules.theme.ThemeManager) quản lý trạng thái theme hiện tại và apply vào Scene. Khi người dùng đổi theme:

ThemeManager.setTheme(theme) được gọi, publish ThemeChangedEvent qua Spring ApplicationEventPublisher.

AdminLayoutController (và các controller khác) lắng nghe @EventListener để gọi ThemeManager.apply(scene).

DialogHelper lắng nghe ThemeChangedEvent để apply theme cho tất cả dialog đang mở, đồng thời gọi controller.onThemeChanged() nếu controller implement method đó (ví dụ: UserEditFormController cập nhật icon peek-password theo theme).

*⚠ Theme được persist vào UserConfig (SQLite). Khi khởi động lại app, theme cũ được khôi phục trước khi render bất kỳ màn hình nào.*

# 2. Admin Layout

## 2.1 Cấu trúc FXML

Admin layout là BorderPane bọc trong StackPane root. Ba vùng chính:

**Vùng**

**Mô tả**

top — Header Shell

VBox .header-shell: chứa Label "BDMA" (.header-title), HBox tab buttons (.tabs-row), và HBox bên phải (.top-right-actions) gồm greeting label, nút settings (⚙), nút logout (.btn-logout).

center — Content Area

StackPane chồng 2 layer: StackPane #contentArea (.content-shell) là vùng render module, và VBox #noticeContainer (.notice-container) overlay ở dưới cùng để hiển thị toast notification.

bottom — Status Bar

HBox .status-bar: bên trái là .status-storage-row (2 progress bar storage), bên phải là HBox #warningStrip (.status-warning-strip) hiển thị cảnh báo.

## 2.2 Tab Navigation

Admin layout có 2 tab: Dashboard và User Management. Các button dùng styleClass menu-tab-btn. Active state được quản lý bằng class active-button.

## 2.3 Status Bar — Storage Progress

Storage bar hiển thị 2 ProgressBar (#pbDataStorage, #pbBackupStorage) với text overlay. CSS class được thêm động theo mức độ sử dụng:

## 2.4 Status Bar — Warning Strip

HBox #warningStrip (.status-warning-strip) hiển thị cảnh báo text. Khi có cảnh báo:

warningStrip.getStyleClass().add("status-warning-strip-active");

Class .status-warning-strip-active đổi background sang -warn-strip-bg và border sang -warn-strip-border (màu vàng nhạt). Label bên trong dùng inline style: -fx-text-fill: -warning-color.

## 2.5 Notice Container (Toast)

VBox #noticeContainer (.notice-container) là lớp overlay mouseTransparent ở trên cùng trong StackPane center. Padding bottom 30px để nổi lên trên nội dung.

AppNoticeService.bindNoticeContainer() được gọi trong initialize() để kết nối service với container này. 

## 2.6 Nút Logout

Button #btnLogout dùng styleClass btn-logout (định nghĩa trong admin.css). Khác với btn-primary/secondary vì màu nền là -danger (đỏ), không dùng -primary.

## 2.7 Nút Settings (⚙)

Button #btnSettings dùng styleClass icon-btn (định nghĩa trong base.css). Đây là pattern chung cho icon button nhỏ: background transparent, border -border-color, border-radius 6, cursor hand. Hover background -table-hover.

# 3. Màn hình Login

## 3.1 Cấu trúc FXML

StackPane root (.root) căn giữa. Bên trong là VBox .login-card (width 380–420px, padding 16 40 40 40, effect null — không có shadow card). Thứ tự từ trên xuống:

HBox chứa Button #btnSettings (icon-btn) căn phải.

Label tiêu đề (.title) — override trong login.css lên font-size 20px, bold.

VBox spacing 10: TextField #username, StackPane chứa password field layered.

Label #message (.message-error) — hidden mặc định, hiện khi login thất bại.

Button login (.login-btn).

## 3.2 Password Peek Pattern

Đây là pattern dùng lại ở nhiều chỗ (login, user-management-form). Cấu trúc StackPane:

Layer 1: TextField editable=false mouseTransparent=true (.input) — tạo nền và border.

Layer 2: HBox transparent chứa PasswordField + TextField visible=false + Button peek.

PasswordField và TextField được bind text 2 chiều. Nút peek show/hide bằng setVisible/setManaged. Icon mắt được cập nhật theo theme qua updatePasswordIcon() trong controller. Style 2 field dùng inline style: -fx-background-color: transparent; -fx-border-color: transparent để không hiển thị frame riêng.

# 4. Dashboard

## 4.1 Cấu trúc FXML

HBox root (.content) chia 2 vùng bằng Separator dọc:

VBox #devicePanel (.device-panel, HBox.hgrow=NEVER): Label tiêu đề + ListView #deviceListView.

VBox #fileListPane (.file-list-pane, HBox.hgrow=ALWAYS): padding left 18, chứa StackPane #fileListContainer để nhúng FileListController.

## 4.2 Device Panel — ListView

ListView #deviceListView (id trong CSS là #deviceListView) dùng custom cell được build programmatically trong DashboardController. Mỗi cell là một HBox (.device-cell-row) gồm:

Circle (dot) — SVG-like shape, fill thay đổi theo trạng thái.

VBox chứa 2 dòng text: tên thiết bị (.device-cell-name) và trạng thái (.device-cell-* tùy status).

Icon edit (SVG path, .edit-icon-svg) — chỉ hiện khi cell được select.

## 4.3 Màu dot và status text theo trạng thái thiết bị

**Trạng thái**

**Dot class / Fill  —  Text class / Color dark / Color light**

CONNECTED

.dot-connected (#4ade80 / #16a34a)  —  .device-cell-connected (#4ade80 / #16a34a)

QUEUED

.dot-queued (#fbbf24)  —  .device-cell-queued (#fbbf24 / #f59e0b)

SYNCING

.dot-syncing (#60a5fa)  —  .device-cell-syncing (#60a5fa / #3b82f6)

OFFLINE

.dot-offline (fill #334155, stroke #475569)  —  .device-cell-offline (#64748b / #94a3b8)

UNVALIDATED

.dot-unvalidated (#fbbf24)  —  .device-cell-unvalidated (#fbbf24 / #d97706)

Logic resolve style class trong DashboardController:

case QUEUED  -> "device-cell-queued"

case SYNCING -> "device-cell-syncing"

default      -> "device-cell-connected"  // khi isAlive=true

# 5. File List Panel

*⚠ File này tự khai báo CSS qua thuộc tính stylesheets trong thẻ VBox gốc. CssLoader không cần resolve path cho file này. Đây là trường hợp ngoại lệ duy nhất.*

## 5.1 Cấu trúc FXML

VBox root spacing 12. Từ trên xuống:

HBox .filter-bar: DatePicker từ-đến, ComboBox user/type filter, nút Reset, filler Region, lblSelectedCount + btnDrop (ẩn mặc định), btnExportSelected.

TableView #fileTable fixedCellSize 40. 8 cột: checkbox select, tên file, thiết bị, user, size, status, type, ngày.

HBox .pagination-bar: combobox page size, btnOpenQueueDialog (giữa), các nút &laquo;&lsaquo; page numbers &rsaquo;&raquo;.

## 5.2 Cột checkbox (colSelect)

Cột đặc biệt, không có text header. maxWidth và minWidth cùng = 45px. StyleClass col-select. Header checkbox dùng để select all, cell checkbox dùng để select từng dòng. Font-size 16px để checkbox đủ lớn.

## 5.3 Pagination — Active Page Button

Các nút số trang được tạo programmatically trong FileListController. Nút trang hiện tại được đánh dấu bằng CSS class btn-page-active.

# 6. User Management

## 6.1 Cấu trúc FXML

StackPane root (.content) chứa VBox spacing 12:

HBox .filter-bar: TextField search, ComboBox role, ComboBox status, nút Reset, filler, nút Add (.btn-primary).

TableView #table fixedCellSize 40. 5 cột: STT, Username, Role, Status, Action.

HBox .pagination-bar: pattern tương tự FileListPanel.

## 6.2 Cột Action — Cell Factory

Cột colAction dùng setCellFactory để render HBox chứa 2 button per row. Style class được set programmatically:

btnEdit.getStyleClass().add("btn-edit");

btnToggleActive.getStyleClass().setAll("button", "btn-delete");   // khi user active

btnToggleActive.getStyleClass().setAll("button", "btn-reactivate"); // khi user inactive

*⚠ btn-edit, btn-delete, btn-reactivate là custom classes — cần đảm bảo định nghĩa trong admin.css hoặc module CSS nếu muốn style riêng.*

## 6.3 Cột Status — Cell Factory

colStatus dùng setCellValueFactory trả về ObservableValue<String> là text trạng thái. Màu sắc text không được set qua CSS class riêng — dùng text thẳng ("Hoạt động" / "Vô hiệu hóa").

# 7. User Edit Form (Dialog)

## 7.1 Cấu trúc FXML

VBox root (.form) spacing 14. GridPane 2 cột (minWidth 150 | ALWAYS) chứa:

Row 0: Label "Username" — TextField #txtUsername (.input) hoặc Label #lblUsername (toggle theo mode).

Row 1: Label "Role" — ComboBox #cbRole hoặc Label #lblRole (toggle theo mode).

Row 2: Label password — StackPane password peek (pattern giống login).

Row 3: Label confirm — StackPane password confirm peek.

Dưới GridPane: Label #lblError (.message-error, ẩn mặc định), HBox nút Cancel + Save.

## 7.2 DialogMode — Toggle UI

Controller có enum DialogMode {CREATE, EDIT, ACCOUNT}. Tùy mode, applyMode() toggle visible/managed của các field:

**Mode**

**UI thay đổi**

CREATE

txtUsername visible (nhập mới), cbRole visible. lblTitle = "Thêm người dùng".

EDIT

lblUsername visible (readonly, hiển thị username), cbRole visible để đổi role. lblTitle = "Sửa người dùng".

ACCOUNT

lblUsername visible (readonly), cbRole ẩn — lblRole visible. Chỉ cho đổi password. lblTitle = "Tài khoản".

## 7.3 Theme-aware password icon

onThemeChanged(ThemeChangedEvent) được gọi bởi DialogHelper khi theme đổi. Method này cập nhật icon mắt trong peek button theo theme + trạng thái hiện tại (open/closed):

// Dark theme: eye-open-dark.png / eye-closed-dark.png

// Light theme: eye-open-light.png / eye-closed-light.png

# 8. Settings Dialogs

Có 3 settings dialog dùng chung cấu trúc FXML và CSS: admin-settings-dialog, user-settings-dialog, và prelogin-settings-popup (popup nhỏ ở trang login).

## 8.1 Admin & User Settings Dialog

[File: admin-settings-dialog.fxm](#)l / user-settings-dialog.fxml  •  CSS: applyDialog() = base + theme + user-settings-dialog.css

Root StackPane (.form, .dialog-container, .settings-dialog-container). Cấu trúc bên trong:

VBox .settings-dialog-body chứa ScrollPane (.settings-dialog-scroll) + Separator + VBox patch actions (admin only).

ScrollPane fitToWidth=true, hbarPolicy=NEVER — nội dung cuộn dọc, không ngang.

Bên trong ScrollPane: GridPane .settings-top-grid (65%|35%) chứa 2 vùng settings và action buttons, rồi các Separator + VBox .settings-card cho từng nhóm settings.

VBox #noticeContainer (.notice-container-dialog) overlay bottom cho toast notification.

## 8.2 Settings Top Grid

GridPane .settings-top-grid chia 65%|35%:

Cột trái (.settings-split-left): Language segment buttons (EN|VI) và Theme segment buttons (Light|Dark). Mỗi row là HBox .settings-inline-row với Label .settings-section-label .settings-inline-label (min-width 110px) + filler + HBox .settings-segment-row.

Cột phải (.settings-split-right): Button Info và Button Update (maxWidth Infinity, styleClass settings-action-btn).

 

## 8.3 Segment Buttons (Language & Theme)

Các button chọn ngôn ngữ và theme dùng pattern segment control:

**CSS class**

**Mô tả**

.settings-segment-btn

Trong base.css: background transparent, border -border-color, border-radius 6, padding 6 12.

.settings-segment-btn:hover

Background -table-hover.

.settings-segment-btn.active-button

Background -primary, text white, border -primary.

.settings-dialog-container .settings-segment-btn

Override trong user-settings-dialog.css: min/pref/max-width 100px, padding 4 10.

Active state được quản lý bởi controller qua pattern:

buttons.forEach(b -> b.getStyleClass().remove("active-button"));

activeButton.getStyleClass().add("active-button");

## 8.4 Settings Card

Mỗi nhóm cài đặt được wrap trong VBox .settings-card (padding 10 0, spacing 6). Giữa các card là Separator .settings-sep. Các card gồm:

Folder paths (admin): Save folder, Backup folder — mỗi row là HBox với Label .settings-field-label (min-width 100px) + TextField readonly + Button browse.

Export folder (admin & user): tương tự folder paths + CheckBox ask-every-time.

Auto delete (admin): CheckBox + Label mô tả.

Start with Windows (admin): CheckBox + Label mô tả.

Patch actions (admin, ngoài ScrollPane): Label title + HBox 2 button Encrypt/Apply patch.

# 9. Media Viewer

## 9.1 Cấu trúc FXML

HBox root (.media-viewer-root, prefWidth 1100, prefHeight 700) chia 2 vùng bằng Separator dọc:

VBox detail panel (minWidth=maxWidth=220px): hiển thị metadata file (tên, type, size, date, device, user, status, dimension, GPS). Gồm các VBox spacing 4: Label key (.media-viewer-detail-label) + Label value (.media-viewer-detail-value).

VBox chính (HBox.hgrow=ALWAYS): toolbar trên, content pane giữa, status bar dưới.

 

## 9.2 Content Pane — 3 trạng thái

StackPane #contentPane (.media-viewer-content) chứa 3 layer, chỉ một layer visible/managed tại một thời điểm:

**Layer**

**Mô tả**

ScrollPane (image)

pannable=true. Chứa StackPane > ImageView. Hiển thị khi file là ảnh.

StackPane #videoPane

Chứa MediaView + VBox video controls overlay (bottom). Hiển thị khi file là video.

Label #lblLoading / #lblError

Overlay text khi đang load hoặc có lỗi.

## 9.3 Video Controls

VBox .video-controls-overlay (gradient bottom to top từ -media-controls-overlay sang transparent) chứa:

Slider #videoSlider (.video-slider) — thanh tiến trình video.

HBox .video-controls-bar: các nút seek (&minus;30s, &minus;5s, +5s, +30s, play/pause) dùng .video-ctrl-btn, Label thời gian, filler, ComboBox speed (.video-speed-combo), slider volume.

**CSS class**

**Mô tả**

.video-controls-bar

Background -media-controls-bg (rgba có alpha). Padding 6 10.

.video-ctrl-btn

Background transparent, text fill -media-controls-text, border-radius 4. Hover: -media-controls-hover.

.video-slider

Pref-height 18px.

.video-speed-combo

Width 75px, height 28px, font-size 12px.

## 9.4 Toolbar Navigation

HBox .media-viewer-toolbar (sidebar-bg background, border bottom): btnPrev/btnNext (.btn-secondary), filler, zoom buttons + rotate buttons (.icon-btn), filler, Label counter (.media-viewer-counter).

## 9.5 Status Bar

HBox .media-viewer-statusbar (padding 6 12, sidebar-bg, border top): Label filename (.media-viewer-filename), filler, Label dimension/size/zoom (.media-viewer-meta, opacity 0.7).

## 9.6 Detail Panel Labels

**CSS class**

**Mô tả**

.media-viewer-detail-label

Font-size 11px, text fill -text-color, opacity 0.55 (key label mờ).

.media-viewer-detail-value

Font-size 13px, text fill -text-color.

.media-viewer-detail-title

Font-size 14px, bold, text fill -text-color.

# 10. User Info

VBox root (.content) spacing 16. Chứa Label tiêu đề (.title), GridPane .user-info-grid (2 cột: minWidth 120 | ALWAYS, hgap 14, vgap 10) hiển thị Username/Role/Status, và HBox #hboxBack (ẩn mặc định) chứa nút Back. 

# 12. Các Component & Pattern Chung

## 12.1 Button Variants

**CSS class**

**Nguồn + Mô tả**

.btn-primary

base.css + theme: Height 34px, padding 0 16, background -primary, text white. Hover: -primary-hover.

.btn-secondary

base.css + theme: Height 34px, padding 0 16, background #334155 (dark) / #e2e8f0 (light).

.btn-primary:disabled / .btn-secondary:disabled

theme: opacity 1 (không mờ nút), text #475569/#94a3b8, background xám nhạt.

.btn-primary-small

base.css: padding 1 12, background -primary, border-radius 6. Dùng cho action nhỏ.

.btn-logout

admin.css: Background -danger (đỏ), text white, min/pref-width 96px.

.icon-btn

base.css: Background transparent, border -border-color, border-radius 6, padding 4 8, cursor hand.

.settings-segment-btn

base.css: Segment control style. Override size trong dialog context.

.settings-action-btn

base.css: Tương tự icon-btn nhưng full-width trong action column.

 

## 12.2 Input Fields

**CSS class**

**Mô tả**

.input (base.css)

Border-radius 6, padding 8, pref/min-height 34px. Áp dụng cho TextField readonly trong form.

TextField, PasswordField (theme)

Background -input-bg, border -border-color, border-radius 6. Focus: -input-focus-bg, border -primary.

ComboBox (theme)

Cùng background/border với text-field. List-cell popup: background -card-bg, hover -table-hover, selected -table-selected.

DatePicker (theme)

Cùng style với text-field. Arrow button background -input-bg. Calendar popup được style đầy đủ (day-cell, spinner, v.v.).

 

## 12.3 TableView — Cấu trúc CSS

Base style của TableView được định nghĩa trong theme-dark/light.css. Module CSS có thể override:

**Selector**

**Mô tả**

.table-row-cell

Background -table-bg.

.table-row-cell:odd

Background -table-odd (zebra striping).

.table-row-cell:hover

Background -table-hover.

.table-row-cell:selected

Background -table-selected. Text-cell: white (dark) / -text-color (light).

.table-view .column-header

Background transparent, border phải -border-color.

.table-view .column-header .label

Text -text-color, bold.

 

## 12.4 CheckBox

**CSS state**

**Style (theme)**

Mặc định .box

Background -input-bg, border -border-color, border-radius 4.

:hover .box

Border -primary.

:selected .box

Background -primary, border -primary.

:selected .mark

Background white.

 

## 12.5 AlertHelper — Dialog hệ thống

AlertHelper tạo Alert với style nhất quán qua configure(). Các điểm quan trọng về CSS:

Tất cả Alert đều thêm class app-alert vào DialogPane. Style được định nghĩa trong theme-dark/light.css (.dialog-pane.app-alert).

Nút OK/primary được thêm class btn-primary qua styleButton(). Nút Cancel/secondary được thêm class btn-secondary.

Logo app được set làm graphic (ImageView 48×48) thay cho icon alert mặc định.

 

## 12.6 Context Menu

**CSS class**

**Mô tả (theme-dark/light.css)**

.context-menu

Background -card-bg, border -border-color, padding 0, background-insets 0.

.context-menu .menu-item

Text -text-color, background -card-bg, padding 8 12.

.context-menu .menu-item:hover/:focused

Background -table-hover.

 

## 12.7 DialogHelper — Quản lý Dialog

DialogHelper.createDialog() tạo Stage mới với Scene riêng. Các điểm CSS quan trọng:

CssLoader.applyDialog() được gọi để gắn base + theme + module CSS.

stage.setOnShowing gọi ThemeManager.apply(scene) để đảm bảo theme đúng khi dialog mở.

Tất cả dialog đang mở được track trong openDialogScenes. Khi ThemeChangedEvent fire, tất cả dialog được cập nhật theme đồng thời.

ESC key được bind để đóng dialog — xử lý trong scene.setOnKeyPressed.

 

 

# 13. Hướng dẫn mở rộng UI

## 13.1 Thêm màn hình module mới

Tạo file FXML trong /resources/fxml/admin/ (hoặc /fxml/user/).

Tạo CSS cùng tên trong /resources/css/admin/ (hoặc /css/user/).

CssLoader.resolveCssPath() sẽ tự tìm CSS theo đường dẫn FXML — không cần config thêm.

Gọi loadView(fxml) trong BaseLayoutController — nó sẽ tự gọi CssLoader.applyModule().

 

## 13.2 Thêm màu mới

Định nghĩa variable mới trong .root của cả theme-dark.css và theme-light.css.

Dùng variable trong component CSS — không hardcode HEX.

 

## 13.3 Thêm dialog mới

Tạo FXML + CSS trong /resources/fxml/ và /resources/css/ tương ứng.

Gọi DialogHelper.createDialog(fxml, title) để tạo Stage.

CSS được load tự động qua applyDialog(). Theme change tự động propagate.

 

## 13.4 Thêm trạng thái thiết bị mới (Dashboard)

Thêm CSS class mới .dot-<state> và .device-cell-<state> trong cả theme-dark.css và theme-light.css.

Cập nhật resolveConnectedStyle() trong DashboardController để return class tương ứng.

 

## 13.5 Quy tắc không viết CSS inline

Tránh dùng node.setStyle("-fx-...") cho style tái sử dụng. Chỉ dùng inline style cho:

Style phụ thuộc giá trị runtime (ví dụ: width tính theo số liệu từ DB).

Trường hợp ngoại lệ đã có trong codebase: transparent background cho HBox overlay trong password peek pattern.