package com.syncpeer.syncpeerapp.videocall.webrtc.video;

import android.content.Context;

import com.syncpeer.syncpeerapp.videocall.webrtc.observers.CustomCapturerObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.VideoSinkObserver;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;

public class VideoCapturerCreator {

    private final Context context;
    private final VideoSinkObserver videoSinkObserver;
    private final EglBase rootEglBase;
    private final VideoSource videoSource;
    public VideoCapturerCreator(Context context, VideoSinkObserver videoSinkObserver, EglBase rootEglBase, VideoSource videoSource){
        this.context = context;
        this.videoSinkObserver = videoSinkObserver;
        this.rootEglBase=rootEglBase;
        this.videoSource = videoSource;
    }
    public VideoCapturer createVideoCapturer() {

        // Get the list of available cameras using Camera2Enumerator
        Camera2Enumerator enumerator = new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();

        // Select the front or back camera (or any specific camera)
        for (String deviceName : deviceNames) {
            //TODO: ONLY FOR TESTING PURPOSES REMOVE AFTER
            if (deviceName.equals("10")||enumerator.isFrontFacing(deviceName)) {
                // Use the front-facing camera
                return createCameraCapturer(enumerator, deviceName);
            }
        }

        return null; // Handle case where no suitable camera is found
    }
    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator, String deviceName) {
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CameraTexture", rootEglBase.getEglBaseContext());
        VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, new CustomVideoCapturer("VideoCapturer"));
        videoCapturer.initialize(surfaceTextureHelper,
                context,
                new CustomCapturerObserver("CapturerObserver", videoSinkObserver, videoSource));

        return videoCapturer;
    }
    public void releaseSurfaceTextureHelper(SurfaceTextureHelper surfaceTextureHelper) {
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
        }
    }
}
