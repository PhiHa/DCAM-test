package com.dvid.dcam.feature.capture.application.port;

import com.dvid.dcam.core.config.domain.DcamConfig;

/** Audio capability required by application workflows. */
public interface AudioRecorder {
    String toggle(DcamConfig config);
    void release();
}
