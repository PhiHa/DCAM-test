package com.dvid.dcam.feature.settings.presentation;

import java.util.List;

/** Immutable control description for one settings row. */
public final class SettingItem {
    public enum Type {
        TEXT,
        CHECKBOX,
        CHOICE,
        SLIDER,
        RADIO
    }

    private final SettingId id;
    private final Type type;
    private final String label;
    private final String value;
    private final List<String> options;
    private final int selectedIndex;
    private final boolean checked;
    private final int min;
    private final int max;
    private final int numberValue;
    private final String unit;

    private SettingItem(
            SettingId id,
            Type type,
            String label,
            String value,
            List<String> options,
            int selectedIndex,
            boolean checked,
            int min,
            int max,
            int numberValue,
            String unit) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.value = value;
        this.options = options == null ? List.of() : List.copyOf(options);
        this.selectedIndex = selectedIndex;
        this.checked = checked;
        this.min = min;
        this.max = max;
        this.numberValue = numberValue;
        this.unit = unit;
    }

    public static SettingItem text(String label, String value) {
        return new SettingItem(null, Type.TEXT, label, value, null, 0, false, 0, 0, 0, null);
    }

    public static SettingItem checkbox(SettingId id, String label, boolean checked) {
        return new SettingItem(id, Type.CHECKBOX, label, null, null, 0, checked, 0, 0, 0, null);
    }

    public static SettingItem choice(
            SettingId id, String label, List<String> options, int selectedIndex) {
        return new SettingItem(id, Type.CHOICE, label, null, options, selectedIndex, false, 0, 0, 0, null);
    }

    public static SettingItem slider(
            SettingId id, String label, int min, int max, int value, String unit) {
        return new SettingItem(id, Type.SLIDER, label, null, null, 0, false, min, max, value, unit);
    }

    public static SettingItem radio(
            SettingId id, String label, List<String> options, int selectedIndex) {
        return new SettingItem(id, Type.RADIO, label, null, options, selectedIndex, false, 0, 0, 0, null);
    }

    public SettingId getId() { return id; }
    public Type getType() { return type; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public List<String> getOptions() { return options; }
    public int getSelectedIndex() { return selectedIndex; }
    public boolean isChecked() { return checked; }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getNumberValue() { return numberValue; }
    public String getUnit() { return unit; }
}
