package com.dvid.dcam.feature.capture.domain;

import com.dvid.dcam.core.config.DcamConfig;

/** Audio capability boundary. PTT will use its own service contract. */
public interface AudioService {
    String toggle(DcamConfig config);
    void release();
}
