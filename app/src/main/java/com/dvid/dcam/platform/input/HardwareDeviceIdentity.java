package com.dvid.dcam.platform.input;

/** Android build properties used to identify one hardware model. */
public final class HardwareDeviceIdentity {
    private final String productModel;
    private final String productDevice;
    private final String boardPlatform;

    public HardwareDeviceIdentity(String productModel, String productDevice, String boardPlatform) {
        this.productModel = clean(productModel);
        this.productDevice = clean(productDevice);
        this.boardPlatform = clean(boardPlatform);
    }

    public String productModel() { return productModel; }
    public String productDevice() { return productDevice; }
    public String boardPlatform() { return boardPlatform; }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
