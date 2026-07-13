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
    private static final Option UNASSIGNED = new Option("Unassigned", -1, null);
    private static final int[] KEY_CODES = {
            KeyEvent.KEYCODE_F1, KeyEvent.KEYCODE_F2, KeyEvent.KEYCODE_F3,
            KeyEvent.KEYCODE_F4, KeyEvent.KEYCODE_F5, KeyEvent.KEYCODE_F6,
            KeyEvent.KEYCODE_F7, KeyEvent.KEYCODE_F8, KeyEvent.KEYCODE_F9,
            KeyEvent.KEYCODE_F10, KeyEvent.KEYCODE_F11, KeyEvent.KEYCODE_F12,
            KeyEvent.KEYCODE_CAMERA, KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_POWER
    };

    private final SharedPreferences preferences;
    private final List<Option> options;

    public DeveloperHardwareButtonSettings(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        options = buildOptions();
    }

    public List<String> optionLabels() {
        List<String> labels = new ArrayList<>(options.size());
        for (Option option : options) labels.add(option.label);
        return labels;
    }

    public int selectedIndex(ButtonRole role) {
        String saved = preferences.getString(role.name(), null);
        if (saved == null) return 0;
        for (int index = 1; index < options.size(); index++) {
            if (options.get(index).encoded().equals(saved)) return index;
        }
        return 0;
    }

    public void initializeIfEmpty(HardwareButtonLayout defaults) {
        if (!preferences.getAll().isEmpty() || defaults == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        Set<ButtonRole> configuredRoles = new HashSet<>();
        for (HardwareButtonBinding binding : defaults.bindings()) {
            if (!configuredRoles.add(binding.role())) continue;
            editor.putString(binding.role().name(),
                    binding.buttonKeyCode() + ":" + binding.type());
        }
        editor.apply();
    }

    public void select(ButtonRole role, int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= options.size()) {
            throw new IllegalArgumentException("Invalid button binding selection " + selectedIndex);
        }
        Option selected = options.get(selectedIndex);
        SharedPreferences.Editor editor = preferences.edit();
        if (selected == UNASSIGNED) {
            editor.remove(role.name()).apply();
            return;
        }
        for (ButtonRole other : ButtonRole.values()) {
            if (other != role && sameKey(preferences.getString(other.name(), null), selected.keyCode)) {
                editor.remove(other.name());
            }
        }
        editor.putString(role.name(), selected.encoded()).apply();
    }

    public HardwareButtonLayout loadLayout() {
        List<HardwareButtonBinding> bindings = new ArrayList<>();
        Set<Integer> usedKeyCodes = new HashSet<>();
        for (ButtonRole role : ButtonRole.values()) {
            Option selected = selectedOption(role);
            if (selected == UNASSIGNED || !usedKeyCodes.add(selected.keyCode)) continue;
            bindings.add(new HardwareButtonBinding(role, selected.keyCode, selected.type));
        }
        return new HardwareButtonLayout(bindings.toArray(new HardwareButtonBinding[0]));
    }

    private Option selectedOption(ButtonRole role) {
        return options.get(selectedIndex(role));
    }

    private static boolean sameKey(String encoded, int keyCode) {
        return encoded != null && encoded.startsWith(keyCode + ":");
    }

    private static List<Option> buildOptions() {
        List<Option> values = new ArrayList<>();
        values.add(UNASSIGNED);
        for (int keyCode : KEY_CODES) {
            String keyName = KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "");
            values.add(new Option(keyName + " / Button", keyCode, PhysicalButtonType.BUTTON));
            values.add(new Option(keyName + " / Switch", keyCode, PhysicalButtonType.SWITCH));
        }
        return List.copyOf(values);
    }

    private static final class Option {
        private final String label;
        private final int keyCode;
        private final PhysicalButtonType type;

        private Option(String label, int keyCode, PhysicalButtonType type) {
            this.label = label;
            this.keyCode = keyCode;
            this.type = type;
        }

        private String encoded() { return keyCode + ":" + type; }
    }
}
