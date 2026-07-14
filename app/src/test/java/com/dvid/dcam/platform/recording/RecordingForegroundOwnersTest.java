package com.dvid.dcam.platform.recording;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class RecordingForegroundOwnersTest {
    @Test void stoppingOneRecordingKeepsOtherOwnerActive() {
        RecordingForegroundOwners owners = new RecordingForegroundOwners();
        owners.startVideo();
        owners.startAudio();

        owners.stopVideo();
        assertTrue(owners.isActive());
        assertFalse(owners.hasVideo());
        assertTrue(owners.hasAudio());

        owners.stopAudio();
        assertFalse(owners.isActive());
    }

    @Test void stoppingAudioKeepsVideoCameraAndMicrophoneOwnership() {
        RecordingForegroundOwners owners = new RecordingForegroundOwners();
        owners.startAudio();
        owners.startVideo();

        owners.stopAudio();
        assertTrue(owners.isActive());
        assertTrue(owners.hasVideo());
        assertFalse(owners.hasAudio());
    }
}
