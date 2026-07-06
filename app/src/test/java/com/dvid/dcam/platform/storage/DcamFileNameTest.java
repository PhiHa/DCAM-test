package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DcamFileNameTest {
    @Test public void buildsSosEncryptedName() {
        assertEquals("DSJ_36NCC009910_000000_20260619_100324_SOS_enc.mp4",
                DcamFileName.build(DcamFileType.SOS, "36NCC009910", "000000",
                        LocalDateTime.of(2026, 6, 19, 10, 3, 24), true));
    }
}
