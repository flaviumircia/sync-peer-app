package com.syncpeer.syncpeerapp.videocall.webrtc.video;

import android.content.Context;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

public class VideoCapturerCreator {

    private final Context context;

    public VideoCapturerCreator(Context context){
        this.context = context;
    }

    public VideoCapturer createVideoCapturer() {
        // Get the list of available cameras using Camera2Enumerator
        Camera2Enumerator enumerator = new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();

        // Select the front or back camera (or any specific camera)
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                // Use the front-facing camera
                return createCameraCapturer(enumerator, deviceName);
            }
        }

        return null; // Handle case where no suitable camera is found
    }
    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator, String deviceName) {
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CameraTexture", null);

        return enumerator.createCapturer(deviceName, new CustomVideoCapturer("VideoCapturer"));
    }
}
