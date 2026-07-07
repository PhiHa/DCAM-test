package com.dvid.dcam.platform.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;

/** CameraX preview surface and camera-facing status text. */
@SuppressLint("ViewConstructor")
public final class CameraXPreviewView extends FrameLayout {
    private final PreviewView previewView;
    private final TextView message;

    public CameraXPreviewView(Context context) {
        super(context);
        setBackgroundColor(Color.rgb(17, 17, 17));

        previewView = new PreviewView(context);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
        addView(previewView, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        message = new TextView(context);
        message.setTextColor(Color.WHITE);
        message.setGravity(Gravity.CENTER);
        message.setPadding(12, 12, 12, 12);
        addView(message, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
    }

    Preview.SurfaceProvider surfaceProvider() {
        return previewView.getSurfaceProvider();
    }

    void showPermissionRequired() {
        showMessage(getContext().getString(com.dvid.dcam.R.string.camera_permission_required));
    }

    void clearMessage() {
        showMessage("");
    }

    void showSaved(String fileName) {
        showMessage(getContext().getString(com.dvid.dcam.R.string.media_saved, fileName));
    }

    void showRecording(String fileName) {
        showMessage(getContext().getString(com.dvid.dcam.R.string.recording_file, fileName));
    }

    void showFinalized(String text) {
        showMessage(text);
    }

    void showError(String text) {
        message.setTextColor(Color.RED);
        message.setText(text == null ? "Camera failed" : text);
    }

    private void showMessage(String text) {
        message.setTextColor(Color.WHITE);
        message.setText(text);
    }
}
