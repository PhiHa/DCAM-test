package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DcamAudioFileNameTest {
    @Test public void buildsAudioName() {
        assertEquals("DCAM_CAM001_000000_20260619_100324.aac",
                DcamFileName.build(DcamFileType.AUDIO, "CAM001", "000000",
                        LocalDateTime.of(2026, 6, 19, 10, 3, 24), false));
    }
}
