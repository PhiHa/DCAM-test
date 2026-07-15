package com.dvid.dcam.feature.settings.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import com.dvid.dcam.core.input.domain.ButtonRole;
import com.dvid.dcam.platform.input.DeveloperHardwareButtonSettings;
import com.dvid.dcam.platform.input.HardwareButtonLayout;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Developer-only presentation for editing physical button bindings. */
public final class DeveloperButtonBindingsScreen {
    private final Context context;
    private final DeveloperHardwareButtonSettings settings;
    private final SettingsControlRenderer renderer;
    private final Supplier<HardwareButtonLayout> applyCurrent;
    private final BooleanSupplier hasDefaults;
    private final Supplier<HardwareButtonLayout> resetDefaults;
    private final Consumer<HardwareButtonLayout> onLayoutApplied;
    private final Consumer<String> showNotice;
    private final KeyEventConsole keyEventConsole = new KeyEventConsole(12);
    private TextView consoleView;

    public DeveloperButtonBindingsScreen(
            Context context,
            DeveloperHardwareButtonSettings settings,
            Supplier<HardwareButtonLayout> applyCurrent,
            BooleanSupplier hasDefaults,
            Supplier<HardwareButtonLayout> resetDefaults,
            Consumer<HardwareButtonLayout> onLayoutApplied,
            Consumer<String> showNotice) {
        this.context = context;
        this.settings = settings;
        this.renderer = new SettingsControlRenderer(context);
        this.applyCurrent = applyCurrent;
        this.hasDefaults = hasDefaults;
        this.resetDefaults = resetDefaults;
        this.onLayoutApplied = onLayoutApplied;
        this.showNotice = showNotice;
    }

    public void render(LinearLayout parent) {
        parent.removeAllViews();
        renderer.render(parent, model(), (id, selectedIndex) -> {
            select(id, selectedIndex);
            render(parent);
        }, (id, value) -> {}, (id, checked) -> {}, id -> {});
        Button reset = new Button(context);
        reset.setText("Reset device defaults");
        reset.setOnClickListener(view -> confirmReset(parent));
        parent.addView(reset);
        renderer.section(parent, "Key event console");
        consoleView = new TextView(context);
        consoleView.setBackgroundResource(com.dvid.dcam.R.drawable.bg_setting_card);
        consoleView.setMinHeight(dp(120));
        consoleView.setPadding(dp(14), dp(10), dp(14), dp(10));
        String consoleText = keyEventConsole.text();
        consoleView.setText(consoleText.isEmpty() ? "Waiting for key events" : consoleText);
        consoleView.setTextColor(Color.WHITE);
        consoleView.setTextSize(14);
        consoleView.setTypeface(Typeface.MONOSPACE);
        parent.addView(consoleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    public boolean onKeyDown(int keyCode) {
        appendKeyEvent(keyCode, "DOWN");
        return keyCode != KeyEvent.KEYCODE_BACK;
    }

    public boolean onKeyUp(int keyCode) {
        appendKeyEvent(keyCode, "UP");
        return keyCode != KeyEvent.KEYCODE_BACK;
    }

    private SettingsScreenModel model() {
        List<String> keyCodes = settings.keyCodeLabels();
        List<String> types = settings.typeLabels();
        return new SettingsScreenModel(List.of(
                section("Record", ButtonRole.RECORD, SettingId.DEV_BUTTON_RECORD_KEY_CODE, SettingId.DEV_BUTTON_RECORD_TYPE, keyCodes, types),
                section("Important recording", ButtonRole.IMPORTANT_RECORDING, SettingId.DEV_BUTTON_IMPORTANT_RECORDING_KEY_CODE, SettingId.DEV_BUTTON_IMPORTANT_RECORDING_TYPE, keyCodes, types),
                section("Photo capture", ButtonRole.PHOTO_CAPTURE, SettingId.DEV_BUTTON_PHOTO_CAPTURE_KEY_CODE, SettingId.DEV_BUTTON_PHOTO_CAPTURE_TYPE, keyCodes, types),
                section("Audio capture", ButtonRole.AUDIO_CAPTURE, SettingId.DEV_BUTTON_AUDIO_CAPTURE_KEY_CODE, SettingId.DEV_BUTTON_AUDIO_CAPTURE_TYPE, keyCodes, types),
                section("PTT", ButtonRole.PTT, SettingId.DEV_BUTTON_PTT_KEY_CODE, SettingId.DEV_BUTTON_PTT_TYPE, keyCodes, types),
                section("SOS", ButtonRole.SOS, SettingId.DEV_BUTTON_SOS_KEY_CODE, SettingId.DEV_BUTTON_SOS_TYPE, keyCodes, types)));
    }

    private SettingsSection section(
            String title, ButtonRole role, SettingId keyCodeId, SettingId typeId,
            List<String> keyCodes, List<String> types) {
        return new SettingsSection(title, List.of(
                SettingItem.choice(keyCodeId, "Keycode", keyCodes, settings.selectedKeyCodeIndex(role)),
                SettingItem.choice(typeId, "Button type", types, settings.selectedTypeIndex(role))));
    }

    private void select(SettingId id, int selectedIndex) {
        ButtonRole role = keyCodeRole(id);
        if (role != null) settings.selectKeyCode(role, selectedIndex);
        else {
            role = typeRole(id);
            if (role == null) throw new IllegalArgumentException("Unsupported developer button setting " + id);
            settings.selectType(role, selectedIndex);
        }
        onLayoutApplied.accept(applyCurrent.get());
        showNotice.accept("Button fallback applied");
    }

    private void confirmReset(LinearLayout parent) {
        if (!hasDefaults.getAsBoolean()) {
            new AlertDialog.Builder(context)
                    .setTitle("No device defaults")
                    .setMessage("No default button preset exists for this device.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle("Reset device defaults?")
                .setMessage("Replace current button bindings with device preset?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    onLayoutApplied.accept(resetDefaults.get());
                    render(parent);
                    showNotice.accept("Device button defaults restored");
                })
                .show();
    }

    private static ButtonRole keyCodeRole(SettingId id) {
        switch (id) {
            case DEV_BUTTON_RECORD_KEY_CODE: return ButtonRole.RECORD;
            case DEV_BUTTON_IMPORTANT_RECORDING_KEY_CODE: return ButtonRole.IMPORTANT_RECORDING;
            case DEV_BUTTON_PHOTO_CAPTURE_KEY_CODE: return ButtonRole.PHOTO_CAPTURE;
            case DEV_BUTTON_AUDIO_CAPTURE_KEY_CODE: return ButtonRole.AUDIO_CAPTURE;
            case DEV_BUTTON_PTT_KEY_CODE: return ButtonRole.PTT;
            case DEV_BUTTON_SOS_KEY_CODE: return ButtonRole.SOS;
            default: return null;
        }
    }

    private static ButtonRole typeRole(SettingId id) {
        switch (id) {
            case DEV_BUTTON_RECORD_TYPE: return ButtonRole.RECORD;
            case DEV_BUTTON_IMPORTANT_RECORDING_TYPE: return ButtonRole.IMPORTANT_RECORDING;
            case DEV_BUTTON_PHOTO_CAPTURE_TYPE: return ButtonRole.PHOTO_CAPTURE;
            case DEV_BUTTON_AUDIO_CAPTURE_TYPE: return ButtonRole.AUDIO_CAPTURE;
            case DEV_BUTTON_PTT_TYPE: return ButtonRole.PTT;
            case DEV_BUTTON_SOS_TYPE: return ButtonRole.SOS;
            default: return null;
        }
    }

    private void appendKeyEvent(int keyCode, String action) {
        String keyName = KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "");
        appendKeyEvent(keyName, action);
    }

    private void appendKeyEvent(String keyName, String action) {
        String text = keyEventConsole.append(keyName, action);
        if (consoleView != null) consoleView.setText(text);
    }

    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
