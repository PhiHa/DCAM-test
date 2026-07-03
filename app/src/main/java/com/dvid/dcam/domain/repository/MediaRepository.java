package com.dvid.dcam.domain.repository;

import com.dvid.dcam.domain.model.MediaEntry;
import java.util.List;

public interface MediaRepository {
    List<MediaEntry> list(String relativePath) throws Exception;
}
