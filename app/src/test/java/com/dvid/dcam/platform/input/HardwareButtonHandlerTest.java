package com.dvid.dcam.platform.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.view.KeyEvent;
import com.dvid.dcam.feature.capture.application.usecase.AudioRecordingUseCase;
import com.dvid.dcam.feature.capture.application.usecase.PhotoCaptureUseCase;
import com.dvid.dcam.feature.capture.application.usecase.VideoRecordingUseCase;
import org.junit.jupiter.api.Test;

public class HardwareButtonHandlerTest {
    @Test public void cameraKeyTakesPhoto() {
        FakePhotoCaptureUseCaseImpl photos = new FakePhotoCaptureUseCaseImpl();
        HardwareButtonRouter router = router(photos, new FakeVideoRecordingUseCaseImpl());
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_CAMERA, 0, 0L));
        assertEquals(1, photos.photos);
    }

    @Test public void f2TakesPhoto() {
        FakePhotoCaptureUseCaseImpl photos = new FakePhotoCaptureUseCaseImpl();
        HardwareButtonRouter router = router(photos, new FakeVideoRecordingUseCaseImpl());
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F2, 0, 0L));
        assertEquals(1, photos.photos);
    }

    @Test public void f10StartsOnDownAndStopsOnUp() {
        FakeVideoRecordingUseCaseImpl videos = new FakeVideoRecordingUseCaseImpl();
        HardwareButtonRouter router = router(new FakePhotoCaptureUseCaseImpl(), videos);
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F10, 0, 0L));
        assertTrue(router.onKeyUp(KeyEvent.KEYCODE_F10));
        assertEquals(1, videos.starts);
        assertEquals(1, videos.stops);
    }

    @Test public void f7LongPressTogglesSosOnceAfterThreeSeconds() {
        FakeVideoRecordingUseCaseImpl videos = new FakeVideoRecordingUseCaseImpl();
        HardwareButtonRouter router = router(new FakePhotoCaptureUseCaseImpl(), videos);
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F7, 0, 1000L));
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F7, 1, 3999L));
        assertEquals(0, videos.sosToggles);
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F7, 2, 4000L));
        assertTrue(router.onKeyDown(KeyEvent.KEYCODE_F7, 3, 4500L));
        assertTrue(router.onKeyUp(KeyEvent.KEYCODE_F7));
        assertEquals(1, videos.sosToggles);
    }

    @Test public void unknownKeyIgnored() {
        HardwareButtonRouter router = router(new FakePhotoCaptureUseCaseImpl(), new FakeVideoRecordingUseCaseImpl());
        assertFalse(router.onKeyDown(KeyEvent.KEYCODE_A, 0, 0L));
    }

    private static HardwareButtonRouter router(
            FakePhotoCaptureUseCaseImpl photos, FakeVideoRecordingUseCaseImpl videos) {
        return new HardwareButtonRouter(photos, videos, new FakeAudioRecordingUseCaseImpl());
    }

    private static final class FakePhotoCaptureUseCaseImpl implements PhotoCaptureUseCase {
        int photos;

        @Override public void takePhoto() { photos++; }
    }

    private static final class FakeVideoRecordingUseCaseImpl implements VideoRecordingUseCase {
        int starts;
        int stops;
        int sosToggles;

        @Override public void toggleVideo() {}
        @Override public void startVideo() { starts++; }
        @Override public void startSos() {}
        @Override public void stopRecording() { stops++; }
        @Override public void toggleSos() { sosToggles++; }
    }

    private static final class FakeAudioRecordingUseCaseImpl implements AudioRecordingUseCase {
        @Override public String toggleAudio() { return null; }
    }
}
