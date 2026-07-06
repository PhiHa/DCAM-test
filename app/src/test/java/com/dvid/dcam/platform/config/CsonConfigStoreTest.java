package com.dvid.dcam.platform.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.nio.file.Files;
import com.dvid.dcam.core.config.domain.DcamConfig;
import org.junit.jupiter.api.Test;

public class CsonConfigStoreTest {
    @Test public void loadsIdsAndEncryption() throws Exception {
        File file = File.createTempFile("configs", ".cson");
        Files.writeString(file.toPath(), "[video]\nfile.encryption=\"1\"\n[device]\naccount.user_id=\"CAM001\"\npolice.user_id=\"000000\"");
        DcamConfig config = new CsonConfigStore(file).load();
        assertEquals("CAM001", config.getAccountUserId());
        assertEquals("000000", config.getPoliceUserId());
        assertTrue(config.isVideoEncrypted());
    }
}
