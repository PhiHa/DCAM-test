package com.dvid.dcam.platform.storage;

import java.io.File;

interface DcamMediaValidator {
    boolean isPlayable(DcamFileType type, File file);
}
