package com.dvid.dcam;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import com.dvid.dcam.app.AppState;
import com.dvid.dcam.app.DcamActions;
import com.dvid.dcam.app.Screen;
import com.dvid.dcam.audio.AudioRecorder;
import com.dvid.dcam.camera.CameraActions;
import com.dvid.dcam.camera.CameraPreview;
import com.dvid.dcam.camera.CameraState;
import com.dvid.dcam.camera.RecordingMode;
import com.dvid.dcam.camera.VideoActions;
import com.dvid.dcam.config.CsonConfigStore;
import com.dvid.dcam.config.DcamConfig;
import com.dvid.dcam.device.AndroidDeviceInfoProvider;
import com.dvid.dcam.device.DeviceInfo;
import com.dvid.dcam.device.DeviceInfoProvider;
import com.dvid.dcam.input.HardwareButtonHandler;
import com.dvid.dcam.logging.DcamLogger;
import com.dvid.dcam.permissions.DcamPermissions;
import com.dvid.dcam.storage.DcamFileName;
import com.dvid.dcam.storage.DcamFileType;
import com.dvid.dcam.storage.DcamMediaOutput;
import com.dvid.dcam.storage.DcamMediaOutputFactory;
import com.dvid.dcam.storage.DcamStorage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MainActivity extends ComponentActivity {
    private static final int PERMISSIONS_REQUEST = 100;
    private final DateTimeFormatter clock = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private FrameLayout root;
    private DcamStorage storage;
    private DcamMediaOutput mediaOutput;
    private AudioRecorder audioRecorder;
    private AppState state;
    private CameraPreview cameraPreview;
    private TextView recordingBadge;
    private TextView fileName;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceInfoProvider deviceInfoProvider = new AndroidDeviceInfoProvider(this);
        DeviceInfo deviceInfo = deviceInfoProvider.read();
        DcamLogger.init(this, deviceInfo);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        requestPermissions(DcamPermissions.runtime(), PERMISSIONS_REQUEST);
        storage = DcamStorage.from(this);
        mediaOutput = new DcamMediaOutputFactory(storage);
        String hardwareId = deviceInfo.getHardwareId();
        DcamConfig config;
        try { config = new CsonConfigStore(storage.configsFile(), hardwareId).load(); }
        catch (Exception error) { DcamLogger.w("Using default config", error); config = DcamConfig.defaults(hardwareId); }
        DcamLogger.setCamId(config.getAccountUserId());
        CameraState camera = new CameraState(RecordingMode.IDLE, null, null);
        state = new AppState(config, camera);
        audioRecorder = new AudioRecorder(this, mediaOutput);
        cameraPreview = new CameraPreview(this, this, config, mediaOutput);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        setContentView(root);
        connectActions();
        showCamera();
    }

    private void connectActions() {
        DcamActions.takePhoto = () -> CameraActions.takePhoto.run();
        DcamActions.toggleVideo = () -> VideoActions.toggleVideo.run();
        DcamActions.startVideo = () -> VideoActions.startVideo.run();
        DcamActions.stopVideo = () -> VideoActions.stopVideo.run();
        DcamActions.toggleAudio = () -> audioRecorder.toggle(state.getConfig());
        DcamActions.startSos = () -> {
            VideoActions.startSos.run();
            LocalDateTime at = LocalDateTime.now();
            DcamConfig config = state.getConfig();
            state.setCamera(new CameraState(RecordingMode.SOS,
                    DcamFileName.build(DcamFileType.SOS, config.getAccountUserId(), config.getPoliceUserId(), at,
                            config.isVideoEncrypted()), System.currentTimeMillis()));
            updateStatus();
        };
        DcamActions.toggleSos = () -> {
            if (state.getCamera().getMode() == RecordingMode.SOS) {
                DcamActions.stopVideo.run();
                state.setCamera(new CameraState(RecordingMode.IDLE, null, null));
                updateStatus();
            } else DcamActions.startSos.run();
        };
    }

    private void showCamera() {
        state.setScreen(Screen.CAMERA);
        root.removeAllViews();
        LinearLayout page = column();
        page.setPadding(dp(16), dp(12), dp(16), dp(12));
        page.addView(statusHeader());
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        previewParams.setMargins(0, dp(16), 0, dp(16));
        if (cameraPreview.getParent() instanceof ViewGroup) ((ViewGroup) cameraPreview.getParent()).removeView(cameraPreview);
        page.addView(cameraPreview, previewParams);
        page.addView(statusFooter());
        root.addView(page, match());
    }

    private View statusHeader() {
        LinearLayout header = column();
        LinearLayout ids = row();
        ids.addView(label("CAM " + state.getConfig().getAccountUserId(), true), weighted());
        TextView user = label("USER " + state.getConfig().getPoliceUserId(), true); user.setGravity(Gravity.END);
        ids.addView(user, weighted()); header.addView(ids);
        LinearLayout status = row();
        recordingBadge = label("READY", true); status.addView(recordingBadge, weighted());
        TextView battery = label("BAT 83%   GPS ●", false); battery.setGravity(Gravity.END); status.addView(battery, weighted());
        header.addView(status);
        fileName = label("", false); fileName.setTextColor(Color.LTGRAY); header.addView(fileName);
        updateStatus();
        return header;
    }

    private View statusFooter() {
        LinearLayout footer = row();
        footer.addView(label("FREE 42GB", false), weighted());
        TextView capture = label("CAPTURE", true); capture.setGravity(Gravity.CENTER); capture.setOnClickListener(v -> DcamActions.takePhoto.run());
        footer.addView(capture, weighted());
        TextView date = label(clock.format(LocalDateTime.now()), false); date.setGravity(Gravity.END); footer.addView(date, weighted());
        return footer;
    }

    private void updateStatus() {
        if (recordingBadge == null || fileName == null) return;
        RecordingMode mode = state.getCamera().getMode();
        String text = mode == RecordingMode.IDLE ? "READY" : mode == RecordingMode.SOS ? "SOS ●" : mode.name() + " ●";
        recordingBadge.setText(text); recordingBadge.setTextColor(mode == RecordingMode.IDLE ? Color.WHITE : Color.RED);
        fileName.setText(state.getCamera().getCurrentFileName() == null ? "" : state.getCamera().getCurrentFileName());
    }

    private void showMenu() {
        state.setScreen(Screen.MENU); root.removeAllViews();
        LinearLayout page = column(); page.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout bar = row(); bar.addView(label(state.getCamera().getMode() == RecordingMode.IDLE ? "READY" : "REC ●", true), weighted());
        TextView battery = label("BAT 83%", false); battery.setGravity(Gravity.END); bar.addView(battery, weighted()); page.addView(bar);
        GridLayout grid = new GridLayout(this); grid.setColumnCount(3); grid.setUseDefaultMargins(false);
        addTile(grid, "Files", Screen.FILES); addTile(grid, "Record\nSettings", Screen.RECORD_SETTINGS);
        addTile(grid, "User\nSettings", Screen.USER_SETTINGS); addTile(grid, "Server\nSettings", Screen.SERVER_SETTINGS);
        addTile(grid, "Storage\nSettings", Screen.STORAGE_SETTINGS); addTile(grid, "Device\nSettings", Screen.DEVICE_SETTINGS);
        addTile(grid, "Audio\nSettings", Screen.AUDIO_SETTINGS); addTile(grid, "Camera\nSettings", Screen.CAMERA_SETTINGS);
        addTile(grid, "About", Screen.ABOUT);
        ScrollView scroll = new ScrollView(this); scroll.addView(grid, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        page.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(page, match());
    }

    private void addTile(GridLayout grid, String title, Screen screen) {
        TextView tile = label(title, false); tile.setGravity(Gravity.CENTER); tile.setTextSize(16); tile.setBackgroundColor(Color.rgb(30, 30, 30));
        tile.setOnClickListener(v -> showPlaceholder(screen));
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(); params.width = (getResources().getDisplayMetrics().widthPixels - dp(56)) / 3; params.height = dp(116);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        grid.addView(tile, params);
    }

    private void showPlaceholder(Screen screen) {
        state.setScreen(screen); root.removeAllViews();
        TextView placeholder = label(screen.name().replace('_', ' ') + "\nTap or Back to return", false);
        placeholder.setGravity(Gravity.CENTER); placeholder.setTextSize(18); placeholder.setOnClickListener(v -> showMenu());
        root.addView(placeholder, match());
    }

    @Override public void onBackPressed() {
        if (state.getScreen() == Screen.CAMERA) showMenu();
        else if (state.getScreen() == Screen.MENU) showCamera();
        else showMenu();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return HardwareButtonHandler.onKeyDown(keyCode, event.getRepeatCount(), event.getEventTime()) || super.onKeyDown(keyCode, event);
    }

    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        return HardwareButtonHandler.onKeyUp(keyCode) || super.onKeyUp(keyCode, event);
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) cameraPreview.bindIfPermitted();
    }

    @Override protected void onDestroy() {
        DcamLogger.i("MainActivity destroyed");
        cameraPreview.release(); audioRecorder.release();
        DcamActions.takePhoto = () -> {}; DcamActions.toggleVideo = () -> {}; DcamActions.startVideo = () -> {}; DcamActions.stopVideo = () -> {}; DcamActions.toggleAudio = () -> {}; DcamActions.startSos = () -> {}; DcamActions.toggleSos = () -> {};
        super.onDestroy();
    }

    private LinearLayout column() { LinearLayout view = new LinearLayout(this); view.setOrientation(LinearLayout.VERTICAL); return view; }
    private LinearLayout row() { LinearLayout view = new LinearLayout(this); view.setOrientation(LinearLayout.HORIZONTAL); view.setGravity(Gravity.CENTER_VERTICAL); return view; }
    private TextView label(String text, boolean bold) { TextView view = new TextView(this); view.setText(text); view.setTextColor(Color.WHITE); view.setTextSize(14); if (bold) view.setTypeface(null, android.graphics.Typeface.BOLD); view.setPadding(dp(4), dp(6), dp(4), dp(6)); return view; }
    private LinearLayout.LayoutParams weighted() { return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f); }
    private FrameLayout.LayoutParams match() { return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
