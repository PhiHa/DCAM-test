package com.dvid.dcam.platform.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DcamAudioFileNameTest {
    @Test public void buildsAudioName() {
        assertEquals("DSJ_36NCC009910_000000_20260619_100324.m4a",
                DcamFileName.build(DcamFileType.AUDIO, "36NCC009910", "000000",
                        LocalDateTime.of(2026, 6, 19, 10, 3, 24), false));
    }
}
