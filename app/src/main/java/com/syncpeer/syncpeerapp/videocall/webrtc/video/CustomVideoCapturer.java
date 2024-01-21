package com.syncpeer.syncpeerapp.videocall.webrtc.video;

import android.util.Log;

import org.webrtc.CameraVideoCapturer;

public class CustomVideoCapturer implements CameraVideoCapturer.CameraEventsHandler {
    private final String TAG;

    public CustomVideoCapturer(String tag) {
        TAG = tag;
    }

    @Override
    public void onCameraError(String errorDescription) {
        Log.e(TAG, "onCameraError: " + errorDescription);
    }

    @Override
    public void onCameraDisconnected() {
        Log.i(TAG, "onCameraDisconnected");
    }

    @Override
    public void onCameraFreezed(String errorDescription) {
        Log.e(TAG, "onCameraFreezed: " + errorDescription);
    }

    @Override
    public void onCameraOpening(String cameraName) {
        Log.i(TAG, "onCameraOpening: " + cameraName);
    }

    @Override
    public void onFirstFrameAvailable() {
        Log.i(TAG, "onFirstFrameAvailable");
    }

    @Override
    public void onCameraClosed() {
        Log.i(TAG, "onCameraClosed");
    }
}
