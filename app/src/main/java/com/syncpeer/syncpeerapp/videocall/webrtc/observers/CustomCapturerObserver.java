package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.CapturerObserver;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSource;


public class CustomCapturerObserver implements CapturerObserver {
    private final String TAG;
    private final VideoSinkObserver videoSinkObserver;
    private final VideoSource videoSource;

    public CustomCapturerObserver(String tag, VideoSinkObserver videoSinkObserver, VideoSource videoSource) {
        TAG = tag;
        this.videoSinkObserver = videoSinkObserver;
        this.videoSource = videoSource;
    }

    @Override
    public void onCapturerStarted(boolean success) {
        Log.d(TAG, "onCapturerStarted: " + success);
    }

    @Override
    public void onCapturerStopped() {
        Log.d(TAG, "onCapturerStopped");
    }

    @Override
    public void onFrameCaptured(VideoFrame videoFrame) {
//        Log.d(TAG, "onFrameCaptured: " + videoFrame.toString());
        videoSinkObserver.onFrame(videoFrame);
        videoSource.getCapturerObserver().onFrameCaptured(videoFrame);
    }

}
