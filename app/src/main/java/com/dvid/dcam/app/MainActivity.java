package com.dvid.dcam.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import com.dvid.dcam.R;
import com.dvid.dcam.app.navigation.MainScreen;
import com.dvid.dcam.app.presentation.MainUiState;
import com.dvid.dcam.app.presentation.MainViewModel;
import com.dvid.dcam.app.presentation.MainViewModelFactory;
import com.dvid.dcam.core.config.domain.DcamConfig;
import com.dvid.dcam.databinding.ActivityMainBinding;
import com.dvid.dcam.databinding.ItemMediaEntryBinding;
import com.dvid.dcam.databinding.ScreenCameraBinding;
import com.dvid.dcam.databinding.ScreenFileExplorerBinding;
import com.dvid.dcam.databinding.ScreenMenuBinding;
import com.dvid.dcam.databinding.ScreenSettingsDetailBinding;
import com.dvid.dcam.feature.capture.domain.RecordingMode;
import com.dvid.dcam.feature.device.domain.CapabilityStatus;
import com.dvid.dcam.feature.device.domain.DeviceStatus;
import com.dvid.dcam.feature.media.application.usecase.OpenMediaUseCase;
import com.dvid.dcam.feature.media.domain.MediaEntry;
import com.dvid.dcam.feature.settings.application.usecase.LanguageSettingsUseCase;
import com.dvid.dcam.feature.settings.domain.AppLanguage;
import com.dvid.dcam.platform.config.AndroidLanguagePreferenceStoreImpl;
import com.dvid.dcam.platform.input.HardwareButtonRouter;
import com.dvid.dcam.platform.logging.DcamLogger;
import com.dvid.dcam.platform.permission.DcamPermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Android entry point and ViewBinding presentation shell. */
public final class MainActivity extends ComponentActivity {
    private final DateTimeFormatter clock = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private FrameLayout root;
    private AppComposition.CaptureRuntime captureRuntime;
    private MainViewModel viewModel;
    private HardwareButtonRouter hardwareButtons;
    private OpenMediaUseCase openMedia;
    private LanguageSettingsUseCase languageSettings;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private MainUiState latestState;
    private MainScreen renderedScreen;
    private ScreenCameraBinding cameraScreen;
    private ScreenFileExplorerBinding fileExplorerScreen;
    private ScreenMenuBinding menuScreen;

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AndroidLanguagePreferenceStoreImpl.localizedContext(newBase));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComposition composition = AppComposition.create(this);
        languageSettings = composition.languageSettingsUseCase();

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.contentRoot;
        setContentView(binding.getRoot());

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (captureRuntime != null) captureRuntime.camera().bindIfPermitted();
                });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { navigateBack(); }
        });

        permissionLauncher.launch(DcamPermissions.runtime());
        captureRuntime = composition.createCaptureRuntime(this);
        openMedia = composition.createOpenMediaUseCase(this);
        viewModel = new ViewModelProvider(
                this, new MainViewModelFactory(
                        composition.config(), composition.initialDeviceStatus(),
                        captureRuntime.photoCapture(), captureRuntime.videoRecording(),
                        captureRuntime.audioRecording(), captureRuntime.captureEvents(),
                        composition.refreshDeviceStatusUseCase(), composition.browseMediaUseCase()))
                .get(MainViewModel.class);
        hardwareButtons = composition.createHardwareButtonRouter(
                captureRuntime.photoCapture(), captureRuntime.videoRecording(), captureRuntime.audioRecording());
        viewModel.state().observe(this, this::render);
    }

    @Override protected void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.refreshDeviceStatus();
    }

    private void render(MainUiState state) {
        latestState = state;
        if (renderedScreen != state.getScreen()) {
            renderedScreen = state.getScreen();
            if (state.getScreen() == MainScreen.CAMERA) renderCamera();
            else if (state.getScreen() == MainScreen.MENU) renderMenu();
            else if (state.getScreen() == MainScreen.FILES) renderFileExplorer();
            else renderSettingsDetail(state.getScreen());
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
        cameraScreen.captureAction.setOnClickListener(view -> viewModel.takePhoto());
        if (captureRuntime.camera().getParent() instanceof ViewGroup) {
            ((ViewGroup) captureRuntime.camera().getParent()).removeView(captureRuntime.camera());
        }
        cameraScreen.previewContainer.addView(captureRuntime.camera(), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(cameraScreen.getRoot());
    }

    private void renderMenu() {
        clearScreenBindings();
        root.removeAllViews();
        menuScreen = ScreenMenuBinding.inflate(getLayoutInflater(), root, false);
        menuScreen.files.setOnClickListener(view -> viewModel.show(MainScreen.FILES));
        menuScreen.recordSettings.setOnClickListener(view -> viewModel.show(MainScreen.RECORD_SETTINGS));
        menuScreen.cameraSettings.setOnClickListener(view -> viewModel.show(MainScreen.CAMERA_SETTINGS));
        menuScreen.videoStreamSettings.setOnClickListener(
                view -> viewModel.show(MainScreen.VIDEO_STREAM_SETTINGS));
        menuScreen.audioSettings.setOnClickListener(view -> viewModel.show(MainScreen.AUDIO_SETTINGS));
        menuScreen.storageSettings.setOnClickListener(view -> viewModel.show(MainScreen.STORAGE_SETTINGS));
        menuScreen.gpsSettings.setOnClickListener(view -> viewModel.show(MainScreen.GPS_SETTINGS));
        menuScreen.deviceSettings.setOnClickListener(view -> viewModel.show(MainScreen.DEVICE_SETTINGS));
        menuScreen.userSettings.setOnClickListener(view -> viewModel.show(MainScreen.USER_SETTINGS));
        menuScreen.serverSettings.setOnClickListener(view -> viewModel.show(MainScreen.SERVER_SETTINGS));
        menuScreen.transferSettings.setOnClickListener(view -> viewModel.show(MainScreen.TRANSFER_SETTINGS));
        menuScreen.about.setOnClickListener(view -> viewModel.show(MainScreen.ABOUT));
        root.addView(menuScreen.getRoot());
    }

    private void renderFileExplorer() {
        clearScreenBindings();
        root.removeAllViews();
        fileExplorerScreen = ScreenFileExplorerBinding.inflate(getLayoutInflater(), root, false);
        fileExplorerScreen.upAction.setOnClickListener(view -> viewModel.navigateMediaUp());
        root.addView(fileExplorerScreen.getRoot());
    }

    private void renderSettingsDetail(MainScreen screen) {
        clearScreenBindings();
        root.removeAllViews();
        ScreenSettingsDetailBinding detail = ScreenSettingsDetailBinding.inflate(
                getLayoutInflater(), root, false);
        detail.title.setText(settingsTitle(screen));
        for (String item : getResources().getStringArray(settingsItems(screen))) {
            addSettingRow(detail.settingsList, "\u2022 " + item, null);
        }
        if (screen == MainScreen.DEVICE_SETTINGS) renderLanguageSettings(detail.settingsList);
        root.addView(detail.getRoot());
    }

    private void renderLanguageSettings(LinearLayout settingsList) {
        if (languageSettings == null) return;
        AppLanguage current = languageSettings.currentLanguage();
        addSectionHeading(settingsList, getString(R.string.language_section_title));
        for (AppLanguage language : languageSettings.supportedLanguages()) {
            boolean selected = language == current;
            String label = (selected ? "\u2713 " : "  ") + languageName(language);
            addSettingRow(settingsList, label, view -> changeLanguage(language));
        }
    }

    private void addSettingRow(
            LinearLayout settingsList, String text, View.OnClickListener clickListener) {
        TextView row = (TextView) getLayoutInflater().inflate(
                R.layout.item_setting_row, settingsList, false);
        row.setText(text);
        if (clickListener != null) {
            row.setClickable(true);
            row.setFocusable(true);
            row.setOnClickListener(clickListener);
        }
        settingsList.addView(row);
    }

    private void addSectionHeading(LinearLayout settingsList, String text) {
        TextView heading = (TextView) getLayoutInflater().inflate(
                R.layout.item_setting_section_heading, settingsList, false);
        heading.setText(text);
        settingsList.addView(heading);
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
        String recording = recordingText(state.getCapture().getMode());
        String device = deviceText(state.getDeviceStatus());
        if (cameraScreen != null) {
            boolean idle = state.getCapture().getMode() == RecordingMode.IDLE;
            cameraScreen.recordingBadge.setText(recording);
            cameraScreen.recordingBadge.setTextColor(idle ? Color.WHITE : Color.RED);
            String currentFile = state.getCapture().getCurrentFileName();
            cameraScreen.fileName.setText(currentFile == null ? "" : currentFile);
            cameraScreen.statusMessage.setText(state.getMessage() == null ? "" : state.getMessage());
            cameraScreen.deviceStatus.setText(device);
            cameraScreen.storageStatus.setText(storageText(state.getDeviceStatus()));
            cameraScreen.currentTime.setText(clock.format(LocalDateTime.now()));
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

    private static String recordingText(RecordingMode mode) {
        if (mode == RecordingMode.IDLE) return "READY";
        return mode.name() + " \u25CF";
    }

    private static String deviceText(DeviceStatus status) {
        String battery = status.getBatteryPercent() < 0 ? "BAT ?" : "BAT " + status.getBatteryPercent() + "%";
        return battery + " \u00B7 GPS " + gpsText(status.getGpsStatus());
    }

    private static String gpsText(CapabilityStatus status) {
        switch (status) {
            case AVAILABLE: return "ON";
            case DISABLED: return "OFF";
            case UNAVAILABLE: return "N/A";
            default: return "?";
        }
    }

    private static String storageText(DeviceStatus status) {
        long bytes = status.getAvailableStorageBytes();
        if (bytes < 0) return "FREE ?";
        double gib = bytes / (1024d * 1024d * 1024d);
        return String.format(Locale.ROOT, "FREE %.1f GB", gib);
    }

    private void navigateBack() {
        MainScreen screen = latestState == null ? MainScreen.CAMERA : latestState.getScreen();
        if (screen == MainScreen.CAMERA) viewModel.show(MainScreen.MENU);
        else if (screen == MainScreen.MENU) viewModel.show(MainScreen.CAMERA);
        else if (screen == MainScreen.FILES && viewModel.navigateMediaUp()) return;
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
        if (viewModel != null) viewModel.onCapturePlatformReleased();
        if (captureRuntime != null) captureRuntime.release();
        super.onDestroy();
    }

    private void clearScreenBindings() {
        cameraScreen = null;
        fileExplorerScreen = null;
        menuScreen = null;
    }
}
