package com.dvid.dcam.platform.input;

import com.dvid.dcam.core.input.domain.ButtonRole;
import com.dvid.dcam.core.input.domain.PhysicalButtonType;

/** One model-specific Android key binding. */
public final class HardwareButtonBinding {
    private final ButtonRole role;
    private final int buttonKeyCode;
    private final PhysicalButtonType type;

    public HardwareButtonBinding(ButtonRole role, int buttonKeyCode, PhysicalButtonType type) {
        if (role == null) throw new IllegalArgumentException("role is required");
        if (type == null) throw new IllegalArgumentException("type is required");
        this.role = role;
        this.buttonKeyCode = buttonKeyCode;
        this.type = type;
    }

    public ButtonRole role() { return role; }
    public int buttonKeyCode() { return buttonKeyCode; }
    public PhysicalButtonType type() { return type; }
}
