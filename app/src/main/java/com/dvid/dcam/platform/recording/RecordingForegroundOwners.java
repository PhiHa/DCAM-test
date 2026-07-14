package com.dvid.dcam.platform.recording;

final class RecordingForegroundOwners {
    private boolean video;
    private boolean audio;

    void startVideo() { video = true; }
    void startAudio() { audio = true; }
    void stopVideo() { video = false; }
    void stopAudio() { audio = false; }
    boolean hasVideo() { return video; }
    boolean hasAudio() { return audio; }
    boolean isActive() { return video || audio; }
}
