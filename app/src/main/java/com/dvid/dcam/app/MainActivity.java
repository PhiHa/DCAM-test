package com.dvid.dcam.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import com.dvid.dcam.R;
import com.dvid.dcam.app.feature.DeveloperFeatureToggles;
import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.app.presentation.MainMenuModel;
import com.dvid.dcam.app.presentation.MainMenuTile;
import com.dvid.dcam.app.presentation.MainUiState;
import com.dvid.dcam.app.presentation.MainViewModel;
import com.dvid.dcam.app.presentation.MainViewModelFactory;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.core.feature.domain.FeatureGate;
import com.dvid.dcam.databinding.ActivityMainBinding;
import com.dvid.dcam.databinding.ItemMediaEntryBinding;
import com.dvid.dcam.databinding.ScreenCameraBinding;
import com.dvid.dcam.databinding.ScreenDeveloperUsersBinding;
import com.dvid.dcam.databinding.ScreenFileExplorerBinding;
import com.dvid.dcam.databinding.ScreenLoginBinding;
import com.dvid.dcam.databinding.ScreenMenuBinding;
import com.dvid.dcam.databinding.ScreenSettingsDetailBinding;
import com.dvid.dcam.feature.auth.domain.UserProvisioningRequest;
import com.dvid.dcam.feature.auth.domain.UserSource;
import com.dvid.dcam.feature.capture.domain.RecordingMode;
import com.dvid.dcam.feature.media.application.usecase.OpenMediaUseCase;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import com.dvid.dcam.feature.settings.application.usecase.LanguageSettingsUseCase;
import com.dvid.dcam.feature.settings.application.usecase.MediaEncryptionSettingsUseCase;
import com.dvid.dcam.feature.settings.domain.AppLanguage;
import com.dvid.dcam.feature.settings.presentation.DemoSettingsState;
import com.dvid.dcam.feature.settings.presentation.SettingId;
import com.dvid.dcam.feature.settings.presentation.SettingItem;
import com.dvid.dcam.feature.settings.presentation.SettingsControlRenderer;
import com.dvid.dcam.feature.settings.presentation.SettingsScreenModel;
import com.dvid.dcam.feature.settings.presentation.SettingsSection;
import com.dvid.dcam.platform.config.AndroidLanguagePreferenceStoreImpl;
import com.dvid.dcam.platform.device.DcamKioskController;
import com.dvid.dcam.platform.input.HardwareButtonRouter;
import com.dvid.dcam.platform.logging.DcamLogger;
import com.dvid.dcam.platform.permission.DcamPermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Android entry point and ViewBinding presentation shell. */
public final class MainActivity extends ComponentActivity {
    private static final int DEV_MODE_UNLOCK_TAPS = 7;
    private static final long DEV_MODE_UNLOCK_WINDOW_MS = 5_000L;
    private static final float RECORDING_BADGE_IDLE_ALPHA = 0.35f;
    private static final float RECORDING_BADGE_ACTIVE_ALPHA = 1f;
    private final DateTimeFormatter clock = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Handler cameraClock = new Handler(Looper.getMainLooper());
    private final Runnable cameraClockTick = new Runnable() {
        @Override public void run() {
            updateCameraClock();
            cameraClock.postDelayed(this, 1_000L);
        }
    };
    private FrameLayout root;
    private AppComposition composition;
    private AppComposition.CaptureRuntime captureRuntime;
    private MainViewModel viewModel;
    private HardwareButtonRouter hardwareButtons;
    private OpenMediaUseCase openMedia;
    private LanguageSettingsUseCase languageSettings;
    private DeveloperFeatureToggles developerFeatureToggles;
    private MediaEncryptionSettingsUseCase mediaEncryptionSettings;
    private SettingsControlRenderer settingsRenderer;
    private DemoSettingsState demoSettings;
    private MainMenuModel menuModel;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private DcamKioskController kioskController;
    private MainUiState latestState;
    private MainScreen renderedScreen;
    private ScreenCameraBinding cameraScreen;
    private ScreenLoginBinding loginScreen;
    private ScreenDeveloperUsersBinding developerUsersScreen;
    private ScreenFileExplorerBinding fileExplorerScreen;
    private ScreenMenuBinding menuScreen;
    private AppLanguage[] renderedLanguages = new AppLanguage[0];
    private int devModeTapCount;
    private long devModeTapWindowStartedAtMs;

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AndroidLanguagePreferenceStoreImpl.localizedContext(newBase));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        composition = AppComposition.create(this);
        languageSettings = composition.languageSettingsUseCase();
        developerFeatureToggles = composition.developerFeatureToggles();
        mediaEncryptionSettings = composition.mediaEncryptionSettingsUseCase();
        settingsRenderer = new SettingsControlRenderer(this);
        demoSettings = new DemoSettingsState(mediaEncryptionSettings.isMediaEncryptionEnabled());
        menuModel = new MainMenuModel();
        kioskController = new DcamKioskController(this);
        kioskController.applyActiveKioskPolicy();

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.contentRoot;
        setContentView(binding.getRoot());

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (captureRuntime != null) captureRuntime.bindCameraIfPermitted();
                });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { navigateBack(); }
        });

        if (!DcamPermissions.allRuntimeGranted(this)) {
            permissionLauncher.launch(DcamPermissions.runtime());
        }
        captureRuntime = composition.createCaptureRuntime(this);
        openMedia = composition.createOpenMediaUseCase(this);
        viewModel = new ViewModelProvider(
                this, new MainViewModelFactory(
                        composition.config(), composition.initialDeviceStatus(),
                        composition.refreshDeviceStatusUseCase(), composition.browseMediaUseCase(),
                        composition.authenticateOperatorUseCase(),
                        composition.operatorSessionUseCase(),
                        composition.manageOperatorUsersUseCase()))
                .get(MainViewModel.class);
        viewModel.bindCaptureEvents(captureRuntime.captureEvents());
        hardwareButtons = composition.createHardwareButtonRouter(
                captureRuntime.photoCapture(), captureRuntime.videoRecording(), captureRuntime.audioRecording());
        viewModel.state().observe(this, this::render);
    }

    @Override protected void onResume() {
        super.onResume();
        if (kioskController != null) {
            kioskController.applyActiveKioskPolicy();
            kioskController.enterLockTaskIfAllowed(this);
        }
        if (viewModel != null) viewModel.refreshDeviceStatus();
        cameraClock.removeCallbacks(cameraClockTick);
        cameraClock.post(cameraClockTick);
    }

    @Override protected void onPause() {
        cameraClock.removeCallbacks(cameraClockTick);
        super.onPause();
    }

    private void render(MainUiState state) {
        latestState = state;
        MainScreen screen = safeScreen(state.getScreen());
        if (screen != state.getScreen()) {
            viewModel.show(screen);
            return;
        }
        if (renderedScreen != screen) {
            renderedScreen = screen;
            if (screen == MainScreen.LOGIN) renderLogin();
            else if (screen == MainScreen.CAMERA) renderCamera();
            else if (screen == MainScreen.MENU) renderMenu();
            else if (screen == MainScreen.FILES) renderFileExplorer();
            else if (screen == MainScreen.DEVELOPER_USERS) renderDeveloperUsers();
            else renderSettingsDetail(screen);
        }
        updateStatus(state);
    }

    private void renderCamera() {
        clearScreenBindings();
        root.removeAllViews();
        cameraScreen = ScreenCameraBinding.inflate(getLayoutInflater(), root, false);
        DcamConfig config = viewModel.getConfig();
        cameraScreen.accountId.setText("CAM " + config.getAccountUserId());
        cameraScreen.operatorId.setText("USER " + config.getPoliceUserId());
        updateGpsStatusLine();
        bindFeatureAction(
                cameraScreen.captureAction,
                FeatureGate.IMAGE_CAPTURE,
                () -> captureRuntime.photoCapture().takePhoto());
        if (captureRuntime.cameraPreview().getParent() instanceof ViewGroup) {
            ((ViewGroup) captureRuntime.cameraPreview().getParent()).removeView(captureRuntime.cameraPreview());
        }
        cameraScreen.previewContainer.addView(captureRuntime.cameraPreview(), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(cameraScreen.getRoot());
        updateCameraClock();
    }

    private void renderLogin() {
        clearScreenBindings();
        root.removeAllViews();
        loginScreen = ScreenLoginBinding.inflate(getLayoutInflater(), root, false);
        View.OnClickListener login = view ->
                viewModel.loginPassword(loginScreen.password.getText().toString());
        loginScreen.loginAction.setOnClickListener(login);
        loginScreen.password.setOnEditorActionListener((view, actionId, event) -> {
            login.onClick(view);
            return true;
        });
        root.addView(loginScreen.getRoot());
    }

    private void renderMenu() {
        clearScreenBindings();
        root.removeAllViews();
        menuScreen = ScreenMenuBinding.inflate(getLayoutInflater(), root, false);
        List<View> visibleTiles = new ArrayList<>();
        for (MainMenuTile tile : menuModel.visibleTiles(this::isMenuTileVisible)) {
            View tileView = menuScreen.getRoot().findViewById(tile.getViewId());
            tileView.setVisibility(View.VISIBLE);
            tileView.setOnClickListener(view -> viewModel.show(tile.getScreen()));
            visibleTiles.add(tileView);
        }
        layoutVisibleMenuTiles(visibleTiles);
        root.addView(menuScreen.getRoot());
    }

    private void layoutVisibleMenuTiles(List<View> visibleTiles) {
        GridLayout grid = menuScreen.settingsGrid;
        grid.removeAllViews();
        grid.setColumnCount(Math.max(1, Math.min(3, visibleTiles.size())));
        for (View tile : visibleTiles) {
            grid.addView(tile, menuTileLayoutParams());
        }
    }

    private GridLayout.LayoutParams menuTileLayoutParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(124);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        params.setMargins(dp(6), dp(6), dp(6), dp(6));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void renderFileExplorer() {
        clearScreenBindings();
        root.removeAllViews();
        fileExplorerScreen = ScreenFileExplorerBinding.inflate(getLayoutInflater(), root, false);
        fileExplorerScreen.upAction.setOnClickListener(view -> viewModel.navigateMediaUp());
        root.addView(fileExplorerScreen.getRoot());
    }

    private void renderDeveloperUsers() {
        clearScreenBindings();
        root.removeAllViews();
        developerUsersScreen = ScreenDeveloperUsersBinding.inflate(getLayoutInflater(), root, false);
        developerUsersScreen.saveAction.setOnClickListener(view -> viewModel.provisionUser(
                new UserProvisioningRequest(
                        developerUsersScreen.userId.getText().toString(),
                        developerUsersScreen.loginName.getText().toString(),
                        developerUsersScreen.displayName.getText().toString(),
                        developerUsersScreen.password.getText().toString(),
                        UserSource.DEVELOPER)));
        root.addView(developerUsersScreen.getRoot());
    }

    private void renderSettingsDetail(MainScreen screen) {
        clearScreenBindings();
        root.removeAllViews();
        ScreenSettingsDetailBinding detail = ScreenSettingsDetailBinding.inflate(
                getLayoutInflater(), root, false);
        detail.title.setText(settingsTitle(screen));
        if (screen == MainScreen.ABOUT) {
            detail.title.setClickable(true);
            detail.title.setFocusable(true);
            detail.title.setOnClickListener(view -> handleAboutSecretTap());
        }
        renderSettingsControls(screen, detail.settingsList);
        if (screen == MainScreen.DEVELOPER_SETTINGS) {
            Button users = new Button(this);
            users.setText(R.string.manage_developer_users);
            users.setOnClickListener(view -> viewModel.show(MainScreen.DEVELOPER_USERS));
            detail.settingsList.addView(users);
        }
        root.addView(detail.getRoot());
    }

    private void renderSettingsControls(MainScreen screen, LinearLayout settingsList) {
        settingsRenderer.render(
                settingsList, settingsModel(screen),
                this::selectSetting, this::updateNumberSetting, this::updateBooleanSetting,
                this::performSettingAction);
    }

    private SettingsScreenModel settingsModel(MainScreen screen) {
        if (screen == MainScreen.DEVELOPER_SETTINGS) {
            return developerFeatureToggles.developerSettings();
        }
        SettingsScreenModel model;
        if (screen == MainScreen.RECORD_SETTINGS) model = demoSettings.recording();
        else if (screen == MainScreen.STORAGE_SETTINGS) model = demoSettings.storage();
        else if (screen == MainScreen.USER_SETTINGS) {
            demoSettings.setVideoEncryptionEnabled(mediaEncryptionSettings.isMediaEncryptionEnabled());
            model = demoSettings.security();
        }
        else if (screen == MainScreen.DEVICE_SETTINGS) model = withLanguage(demoSettings.device());
        else model = demoSettings.readOnly(visibleReadOnlySettings(screen));
        return filterUnavailableSettings(screen, model);
    }

    private String[] visibleReadOnlySettings(MainScreen screen) {
        String[] labels = getResources().getStringArray(settingsItems(screen));
        List<String> visible = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (isReadOnlySettingVisible(screen, i)) visible.add(labels[i]);
        }
        return visible.toArray(new String[0]);
    }

    private SettingsScreenModel filterUnavailableSettings(MainScreen screen, SettingsScreenModel model) {
        List<SettingsSection> sections = new ArrayList<>();
        for (SettingsSection section : model.getSections()) {
            List<SettingItem> visible = new ArrayList<>();
            for (SettingItem item : section.getItems()) {
                if (isSettingVisible(screen, item)) visible.add(item);
            }
            if (!visible.isEmpty()) sections.add(new SettingsSection(section.getTitle(), visible));
        }
        return new SettingsScreenModel(sections);
    }

    private boolean isSettingVisible(MainScreen screen, SettingItem item) {
        for (FeatureGate gate : requiredGatesForSetting(screen, item)) {
            if (!developerFeatureToggles.isEnabled(gate)) return false;
        }
        return true;
    }

    private boolean isReadOnlySettingVisible(MainScreen screen, int index) {
        for (FeatureGate gate : requiredGatesForReadOnlySetting(screen, index)) {
            if (!developerFeatureToggles.isEnabled(gate)) return false;
        }
        return true;
    }

    private static FeatureGate[] requiredGatesForSetting(MainScreen screen, SettingItem item) {
        if (item.getId() == SettingId.LANGUAGE) return noGates();
        switch (screen) {
            case RECORD_SETTINGS:
                return gates(FeatureGate.RECORDING_SETTINGS, FeatureGate.VIDEO_CAPTURE);
            case STORAGE_SETTINGS:
                return gates(FeatureGate.STORAGE_SETTINGS);
            case USER_SETTINGS:
                return gates(FeatureGate.SECURITY_SETTINGS);
            case DEVICE_SETTINGS:
                return gates(FeatureGate.DEVICE_SETTINGS);
            default:
                return noGates();
        }
    }

    private static FeatureGate[] requiredGatesForReadOnlySetting(MainScreen screen, int index) {
        switch (screen) {
            case CAMERA_SETTINGS:
                return index == 1 || index == 3 || index == 6
                        ? gates(FeatureGate.CAMERA_SETTINGS, FeatureGate.IMAGE_CAPTURE)
                        : gates(FeatureGate.CAMERA_SETTINGS);
            case VIDEO_STREAM_SETTINGS:
                return gates(FeatureGate.VIDEO_STREAMING);
            case AUDIO_SETTINGS:
                return gates(FeatureGate.AUDIO_SETTINGS, FeatureGate.AUDIO_CAPTURE);
            case GPS_SETTINGS:
                return gates(FeatureGate.GPS);
            case SERVER_SETTINGS:
                return gates(FeatureGate.CLOUD_SETTINGS);
            case TRANSFER_SETTINGS:
                return index < 4
                        ? gates(FeatureGate.VIDEO_STREAMING)
                        : gates(FeatureGate.TRANSFER);
            case ABOUT:
                return noGates();
            default:
                return noGates();
        }
    }

    private static FeatureGate[] gates(FeatureGate... gates) {
        return gates;
    }

    private static FeatureGate[] noGates() {
        return new FeatureGate[0];
    }

    private static boolean hasSettings(SettingsScreenModel model) {
        for (SettingsSection section : model.getSections()) {
            if (!section.getItems().isEmpty()) return true;
        }
        return false;
    }

    private boolean isMenuTileVisible(MainScreen screen, FeatureGate gate) {
        if (screen == MainScreen.FILES) {
            return gate == null || developerFeatureToggles.isEnabled(gate);
        }
        if (isSettingsScreen(screen)) return hasSettings(settingsModel(screen));
        return gate == null || developerFeatureToggles.isEnabled(gate);
    }

    private static boolean isSettingsScreen(MainScreen screen) {
        switch (screen) {
            case RECORD_SETTINGS:
            case CAMERA_SETTINGS:
            case VIDEO_STREAM_SETTINGS:
            case AUDIO_SETTINGS:
            case STORAGE_SETTINGS:
            case GPS_SETTINGS:
            case DEVICE_SETTINGS:
            case USER_SETTINGS:
            case SERVER_SETTINGS:
            case TRANSFER_SETTINGS:
            case ABOUT:
                return true;
            default:
                return false;
        }
    }

    private SettingsScreenModel withLanguage(SettingsScreenModel base) {
        List<SettingsSection> sections = new ArrayList<>(base.getSections());
        sections.add(languageSection());
        return new SettingsScreenModel(sections);
    }

    private SettingsSection languageSection() {
        if (languageSettings == null) {
            renderedLanguages = new AppLanguage[0];
            return new SettingsSection(getString(R.string.language_section_title),
                    List.of(SettingItem.text(getString(R.string.language_section_title), "Unavailable")));
        }
        AppLanguage current = languageSettings.currentLanguage();
        renderedLanguages = languageSettings.supportedLanguages();
        List<String> labels = new ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < renderedLanguages.length; i++) {
            labels.add(languageName(renderedLanguages[i]));
            if (renderedLanguages[i] == current) selectedIndex = i;
        }
        return new SettingsSection(getString(R.string.language_section_title),
                List.of(SettingItem.choice(SettingId.LANGUAGE,
                        getString(R.string.language_section_title), labels, selectedIndex)));
    }

    private void selectSetting(SettingId id, int selectedIndex) {
        if (id == SettingId.LANGUAGE) {
            if (selectedIndex >= 0 && selectedIndex < renderedLanguages.length) {
                changeLanguage(renderedLanguages[selectedIndex]);
            }
            return;
        }
        demoSettings.select(id, selectedIndex);
    }

    private void updateNumberSetting(SettingId id, int value) {
        demoSettings.updateNumber(id, value);
    }

    private void updateBooleanSetting(SettingId id, boolean checked) {
        if (developerFeatureToggles.setEnabled(id, checked)) return;
        demoSettings.updateBoolean(id, checked);
        if (id == SettingId.ENCRYPT_VIDEO_FILES && mediaEncryptionSettings != null) {
            mediaEncryptionSettings.setMediaEncryptionEnabled(checked);
        }
    }

    private void performSettingAction(SettingId id) {
        if (id == SettingId.LOGOUT) {
            viewModel.logout();
            return;
        }
        if (id == SettingId.CHANGE_OPERATOR_ID || id == SettingId.CHANGE_OPERATOR_PASSWORD) {
            Toast.makeText(this, R.string.account_change_pending, Toast.LENGTH_SHORT).show();
            return;
        }
        throw new IllegalArgumentException("Setting " + id + " is not an action");
    }

    private void changeLanguage(AppLanguage language) {
        if (languageSettings == null || language == languageSettings.currentLanguage()) return;
        languageSettings.changeLanguage(language);
        Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show();
        recreate();
    }

    private String languageName(AppLanguage language) {
        switch (language) {
            case SYSTEM: return getString(R.string.language_system);
            case ENGLISH: return getString(R.string.language_english);
            case VIETNAMESE: return getString(R.string.language_vietnamese);
            default: throw new IllegalArgumentException("Unsupported language " + language);
        }
    }

    private void handleAboutSecretTap() {
        long now = System.currentTimeMillis();
        if (now - devModeTapWindowStartedAtMs > DEV_MODE_UNLOCK_WINDOW_MS) {
            devModeTapWindowStartedAtMs = now;
            devModeTapCount = 0;
        }
        devModeTapCount++;
        if (devModeTapCount < DEV_MODE_UNLOCK_TAPS) return;

        devModeTapCount = 0;
        devModeTapWindowStartedAtMs = 0L;
        Toast.makeText(this, R.string.developer_mode_unlocked, Toast.LENGTH_SHORT).show();
        viewModel.show(MainScreen.DEVELOPER_SETTINGS);
    }

    private MainScreen safeScreen(MainScreen screen) {
        if (screen == MainScreen.LOGIN || screen == MainScreen.DEVELOPER_USERS) return screen;
        return menuModel.safeScreen(screen, this::isMenuTileVisible);
    }

    private void bindFeatureAction(View view, FeatureGate feature, Runnable action) {
        boolean enabled = developerFeatureToggles.isEnabled(feature);
        view.setVisibility(enabled ? View.VISIBLE : View.GONE);
        view.setOnClickListener(enabled
                ? ignored -> developerFeatureToggles.runIfEnabled(feature, action)
                : null);
    }

    private static int settingsTitle(MainScreen screen) {
        switch (screen) {
            case RECORD_SETTINGS: return R.string.record_settings;
            case CAMERA_SETTINGS: return R.string.camera_settings_short;
            case VIDEO_STREAM_SETTINGS: return R.string.video_stream_settings;
            case AUDIO_SETTINGS: return R.string.audio_settings;
            case STORAGE_SETTINGS: return R.string.storage_settings;
            case GPS_SETTINGS: return R.string.gps_settings;
            case DEVICE_SETTINGS: return R.string.device_settings_short;
            case USER_SETTINGS: return R.string.security_settings;
            case SERVER_SETTINGS: return R.string.network_settings;
            case TRANSFER_SETTINGS: return R.string.transfer_settings;
            case ABOUT: return R.string.about;
            case DEVELOPER_SETTINGS: return R.string.developer_mode;
            case DEVELOPER_USERS: return R.string.developer_users;
            default: throw new IllegalArgumentException("No settings title for " + screen);
        }
    }

    private static int settingsItems(MainScreen screen) {
        switch (screen) {
            case RECORD_SETTINGS: return R.array.record_settings_items;
            case CAMERA_SETTINGS: return R.array.camera_settings_items;
            case VIDEO_STREAM_SETTINGS: return R.array.video_stream_settings_items;
            case AUDIO_SETTINGS: return R.array.audio_settings_items;
            case STORAGE_SETTINGS: return R.array.storage_settings_items;
            case GPS_SETTINGS: return R.array.gps_settings_items;
            case DEVICE_SETTINGS: return R.array.device_settings_items;
            case USER_SETTINGS: return R.array.security_settings_items;
            case SERVER_SETTINGS: return R.array.network_settings_items;
            case TRANSFER_SETTINGS: return R.array.transfer_settings_items;
            case ABOUT: return R.array.about_settings_items;
            default: throw new IllegalArgumentException("No settings list for " + screen);
        }
    }

    private void updateStatus(MainUiState state) {
        if (cameraScreen != null) {
            boolean idle = state.getCapture().getMode() == RecordingMode.IDLE;
            cameraScreen.recordingBadge.setAlpha(idle
                    ? RECORDING_BADGE_IDLE_ALPHA : RECORDING_BADGE_ACTIVE_ALPHA);
            updateGpsStatusLine();
            if (state.getOperatorSession() != null) {
                cameraScreen.operatorId.setText("USER " + state.getOperatorSession().getFileUserId());
            } else {
                cameraScreen.operatorId.setText("USER " + viewModel.getConfig().getPoliceUserId());
            }
            updateCameraClock();
        }
        if (loginScreen != null) {
            loginScreen.loginAction.setEnabled(!state.isAuthenticationBusy());
            loginScreen.password.setEnabled(!state.isAuthenticationBusy());
            loginScreen.status.setText(state.isAuthenticationBusy()
                    ? "Loading..."
                    : state.getMessage() == null ? "" : state.getMessage());
        }
        if (developerUsersScreen != null) {
            developerUsersScreen.saveAction.setEnabled(!state.isAuthenticationBusy());
            developerUsersScreen.status.setText(state.isAuthenticationBusy()
                    ? "Saving..."
                    : state.getMessage() == null ? "" : state.getMessage());
        }
        if (fileExplorerScreen != null) updateFileExplorer(state);
    }

    private void updateFileExplorer(MainUiState state) {
        fileExplorerScreen.entries.removeAllViews();
        String path = state.getMediaBrowser().getRelativePath();
        fileExplorerScreen.path.setText(path.isEmpty()
                ? getString(R.string.media_root) : getString(R.string.media_root) + " / " + path);
        fileExplorerScreen.upAction.setVisibility(path.isEmpty() ? View.GONE : View.VISIBLE);
        if (state.getMediaBrowser().isLoading()) {
            fileExplorerScreen.status.setText(R.string.media_loading);
            return;
        }
        if (state.getMediaBrowser().getError() != null) {
            fileExplorerScreen.status.setText(state.getMediaBrowser().getError());
            return;
        }
        if (state.getMediaBrowser().getEntries().isEmpty()) {
            fileExplorerScreen.status.setText(R.string.media_empty);
            return;
        }
        fileExplorerScreen.status.setText("");
        for (MediaEntry entry : state.getMediaBrowser().getEntries()) {
            ItemMediaEntryBinding row = ItemMediaEntryBinding.inflate(
                    getLayoutInflater(), fileExplorerScreen.entries, false);
            row.icon.setImageResource(entry.isDirectory()
                    ? R.drawable.ic_settings_files : R.drawable.ic_media_file);
            row.name.setText(entry.getName());
            row.details.setText(entry.isDirectory()
                    ? getString(R.string.media_folder) : formatFileSize(entry.getSizeBytes()));
            row.getRoot().setOnClickListener(view -> {
                if (entry.isDirectory()) viewModel.openMediaFolder(entry.getRelativePath());
                else openMediaFile(entry);
            });
            fileExplorerScreen.entries.addView(row.getRoot());
        }
    }

    private void openMediaFile(MediaEntry entry) {
        if (openMedia == null || !openMedia.execute(entry)) {
            Toast.makeText(this, R.string.media_open_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024L) return bytes + " B";
        if (bytes < 1024L * 1024L) return String.format(Locale.ROOT, "%.1f KB", bytes / 1024d);
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.1f MB", bytes / (1024d * 1024d));
        }
        return String.format(Locale.ROOT, "%.1f GB", bytes / (1024d * 1024d * 1024d));
    }

    private void updateCameraClock() {
        if (cameraScreen == null) return;
        cameraScreen.currentTime.setText(clock.format(LocalDateTime.now()));
        MainUiState state = latestState;
        cameraScreen.recordingTimer.setText(recordingDurationText(state));
    }

    private static String recordingDurationText(MainUiState state) {
        if (state == null || state.getCapture().getMode() == RecordingMode.IDLE) return "00:00:00";
        Long startedAtMillis = state.getCapture().getStartedAtMillis();
        if (startedAtMillis == null) return "00:00:00";
        long elapsedMs = Math.max(0L, System.currentTimeMillis() - startedAtMillis);
        long totalSeconds = elapsedMs / 1_000L;
        long hours = totalSeconds / 3_600L;
        long minutes = (totalSeconds % 3_600L) / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static String gpsCoordinatesText() {
        return "000.00 000.00";
    }

    private void updateGpsStatusLine() {
        if (cameraScreen == null) return;
        boolean enabled = developerFeatureToggles.isEnabled(FeatureGate.GPS);
        cameraScreen.gpsStatus.setVisibility(enabled ? View.VISIBLE : View.GONE);
        cameraScreen.gpsStatus.setText(enabled ? gpsCoordinatesText() : "");
    }

    private void navigateBack() {
        MainScreen screen = latestState == null ? MainScreen.CAMERA : latestState.getScreen();
        if (screen == MainScreen.LOGIN) return;
        if (screen == MainScreen.CAMERA) viewModel.show(MainScreen.MENU);
        else if (screen == MainScreen.MENU) viewModel.show(MainScreen.CAMERA);
        else if (screen == MainScreen.FILES && viewModel.navigateMediaUp()) return;
        else if (screen == MainScreen.DEVELOPER_USERS) viewModel.show(MainScreen.DEVELOPER_SETTINGS);
        else viewModel.show(MainScreen.MENU);
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return hardwareButtons.onKeyDown(keyCode, event.getRepeatCount(), event.getEventTime())
                || super.onKeyDown(keyCode, event);
    }

    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        return hardwareButtons.onKeyUp(keyCode) || super.onKeyUp(keyCode, event);
    }

    @Override protected void onDestroy() {
        DcamLogger.i("MainActivity destroyed");
        cameraClock.removeCallbacks(cameraClockTick);
        if (viewModel != null && captureRuntime != null) {
            viewModel.onCapturePlatformReleased(captureRuntime.captureEvents());
            viewModel.unbindCaptureEvents(captureRuntime.captureEvents());
        }
        if (captureRuntime != null) captureRuntime.release();
        super.onDestroy();
    }

    private void clearScreenBindings() {
        cameraScreen = null;
        loginScreen = null;
        developerUsersScreen = null;
        fileExplorerScreen = null;
        menuScreen = null;
    }
}
