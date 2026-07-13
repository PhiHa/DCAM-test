package com.dvid.dcam.platform.input;

import com.dvid.dcam.core.input.domain.ButtonRole;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/** Immutable collection of physical buttons available on one hardware layout. */
public final class HardwareButtonLayout {
    private static final HardwareButtonLayout EMPTY = new HardwareButtonLayout();
    private final Map<Integer, HardwareButtonBinding> bindingsByKeyCode;

    public HardwareButtonLayout(HardwareButtonBinding... bindings) {
        Map<Integer, HardwareButtonBinding> byKeyCode = new LinkedHashMap<>();
        if (bindings != null) {
            for (HardwareButtonBinding binding : bindings) {
                if (binding == null) throw new IllegalArgumentException("binding is required");
                if (byKeyCode.put(binding.buttonKeyCode(), binding) != null) {
                    throw new IllegalArgumentException(
                            "Duplicate buttonKeyCode " + binding.buttonKeyCode());
                }
            }
        }
        bindingsByKeyCode = Collections.unmodifiableMap(byKeyCode);
    }

    public HardwareButtonBinding findByButtonKeyCode(int buttonKeyCode) {
        return bindingsByKeyCode.get(buttonKeyCode);
    }

    public boolean has(ButtonRole role) {
        for (HardwareButtonBinding binding : bindingsByKeyCode.values()) {
            if (binding.role() == role) return true;
        }
        return false;
    }

    public boolean isEmpty() { return bindingsByKeyCode.isEmpty(); }

    public List<HardwareButtonBinding> bindings() {
        return List.copyOf(bindingsByKeyCode.values());
    }

    public static HardwareButtonLayout empty() { return EMPTY; }
}
