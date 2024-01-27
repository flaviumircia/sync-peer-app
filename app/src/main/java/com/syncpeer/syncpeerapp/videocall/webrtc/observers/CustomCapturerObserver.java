package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.CapturerObserver;
import org.webrtc.VideoFrame;

import java.nio.ByteBuffer;


public class CustomCapturerObserver implements CapturerObserver {
    private final String TAG;
    private final VideoSinkObserver videoSinkObserver;

    public CustomCapturerObserver(String tag, VideoSinkObserver videoSinkObserver) {
        TAG = tag;
        this.videoSinkObserver = videoSinkObserver;
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
        Log.d(TAG, "onFrameCaptured: " + videoFrame.toString());
        videoSinkObserver.onFrame(videoFrame);
    }

    private void logBuffer(ByteBuffer buffer){
        while (buffer.hasRemaining()) {
            int byteValue = buffer.get() & 0xFF;  // Convert to unsigned byte value
            Log.d(TAG, "onData: " + byteValue);

        }
    }

}
