package com.dvid.dcam.platform.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import com.dvid.dcam.core.input.domain.ButtonRole;
import com.dvid.dcam.core.input.domain.PhysicalButtonType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Developer-only fallback bindings used when no built-in device profile matches. */
public final class DeveloperHardwareButtonSettings {
    private static final String PREFERENCES = "developer_hardware_buttons";
    private static final int UNASSIGNED_KEY_CODE = -1;
    private static final int[] KEY_CODES = {
            KeyEvent.KEYCODE_F1, KeyEvent.KEYCODE_F2, KeyEvent.KEYCODE_F3,
            KeyEvent.KEYCODE_F4, KeyEvent.KEYCODE_F5, KeyEvent.KEYCODE_F6,
            KeyEvent.KEYCODE_F7, KeyEvent.KEYCODE_F8, KeyEvent.KEYCODE_F9,
            KeyEvent.KEYCODE_F10, KeyEvent.KEYCODE_F11, KeyEvent.KEYCODE_F12,
            KeyEvent.KEYCODE_CAMERA, KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_HEADSETHOOK
    };

    private final SharedPreferences preferences;
    private final List<String> keyCodeLabels;

    public DeveloperHardwareButtonSettings(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        keyCodeLabels = buildKeyCodeLabels();
    }

    public List<String> keyCodeLabels() { return keyCodeLabels; }

    public List<String> typeLabels() { return List.of("Button", "Switch"); }

    public int selectedKeyCodeIndex(ButtonRole role) {
        String saved = preferences.getString(role.name(), null);
        if (saved == null) return 0;
        int keyCode = savedKeyCode(saved);
        for (int index = 1; index < keyCodeLabels.size(); index++) {
            if (keyCodeLabels.get(index).equals(keyCodeName(keyCode))) return index;
        }
        return 0;
    }

    public int selectedTypeIndex(ButtonRole role) {
        String saved = preferences.getString(role.name(), null);
        return saved != null && saved.endsWith(PhysicalButtonType.SWITCH.name()) ? 1 : 0;
    }

    public void initializeIfEmpty(HardwareButtonLayout defaults) {
        if (!preferences.getAll().isEmpty() || defaults == null) return;
        writeDefaults(defaults);
    }

    public boolean resetToDefaults(HardwareButtonLayout defaults) {
        if (defaults == null || defaults.isEmpty()) return false;
        writeDefaults(defaults);
        return true;
    }

    private void writeDefaults(HardwareButtonLayout defaults) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        Set<ButtonRole> configuredRoles = new HashSet<>();
        for (HardwareButtonBinding binding : defaults.bindings()) {
            if (!configuredRoles.add(binding.role())) continue;
            editor.putString(binding.role().name(), binding.buttonKeyCode() + ":" + binding.type());
        }
        editor.apply();
    }

    public void selectKeyCode(ButtonRole role, int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= keyCodeLabels.size()) {
            throw new IllegalArgumentException("Invalid button key code selection " + selectedIndex);
        }
        int keyCode = selectedIndex == 0 ? UNASSIGNED_KEY_CODE : keyCodeFromLabel(keyCodeLabels.get(selectedIndex));
        PhysicalButtonType type = selectedType(role);
        SharedPreferences.Editor editor = preferences.edit();
        if (keyCode != UNASSIGNED_KEY_CODE) {
            for (ButtonRole other : ButtonRole.values()) {
                if (other != role && sameKey(preferences.getString(other.name(), null), keyCode)) {
                    editor.remove(other.name());
                }
            }
        }
        editor.putString(role.name(), keyCode + ":" + type).apply();
    }

    public void selectType(ButtonRole role, int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= typeLabels().size()) {
            throw new IllegalArgumentException("Invalid button type selection " + selectedIndex);
        }
        String saved = preferences.getString(role.name(), null);
        int keyCode = saved == null ? UNASSIGNED_KEY_CODE : savedKeyCode(saved);
        preferences.edit().putString(role.name(), keyCode + ":" + typeFromIndex(selectedIndex)).apply();
    }

    public HardwareButtonLayout loadLayout() {
        List<HardwareButtonBinding> bindings = new ArrayList<>();
        Set<Integer> usedKeyCodes = new HashSet<>();
        for (ButtonRole role : ButtonRole.values()) {
            String saved = preferences.getString(role.name(), null);
            int keyCode = saved == null ? UNASSIGNED_KEY_CODE : savedKeyCode(saved);
            if (keyCode == UNASSIGNED_KEY_CODE || !usedKeyCodes.add(keyCode)) continue;
            bindings.add(new HardwareButtonBinding(role, keyCode, selectedType(role)));
        }
        return new HardwareButtonLayout(bindings.toArray(new HardwareButtonBinding[0]));
    }

    private PhysicalButtonType selectedType(ButtonRole role) {
        return selectedTypeIndex(role) == 1 ? PhysicalButtonType.SWITCH : PhysicalButtonType.BUTTON;
    }

    private static boolean sameKey(String encoded, int keyCode) {
        return encoded != null && savedKeyCode(encoded) == keyCode;
    }

    private static List<String> buildKeyCodeLabels() {
        List<String> values = new ArrayList<>();
        values.add("Unassigned");
        for (int keyCode : KEY_CODES) {
            values.add(KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", ""));
        }
        return List.copyOf(values);
    }

    private static int savedKeyCode(String encoded) {
        int separator = encoded.indexOf(':');
        return Integer.parseInt(separator < 0 ? encoded : encoded.substring(0, separator));
    }

    private static String keyCodeName(int keyCode) {
        return keyCode == UNASSIGNED_KEY_CODE ? "Unassigned" : KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "");
    }

    private static int keyCodeFromLabel(String label) {
        for (int keyCode : KEY_CODES) {
            if (keyCodeName(keyCode).equals(label)) return keyCode;
        }
        throw new IllegalArgumentException("Unknown key code " + label);
    }

    private static PhysicalButtonType typeFromIndex(int index) {
        return index == 1 ? PhysicalButtonType.SWITCH : PhysicalButtonType.BUTTON;
    }
}
