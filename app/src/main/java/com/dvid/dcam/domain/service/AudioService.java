package com.dvid.dcam.domain.service;

import com.dvid.dcam.domain.model.DcamConfig;

/** Audio capability boundary. PTT will use its own service contract. */
public interface AudioService {
    String toggle(DcamConfig config);
    void release();
}
