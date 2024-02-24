package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class VideoSinkObserver implements VideoSink {

    private final String TAG;
    private final SurfaceViewRenderer surfaceViewRenderer;

    public VideoSinkObserver(String tag, SurfaceViewRenderer surfaceViewRenderer) {
        TAG = tag;
        this.surfaceViewRenderer = surfaceViewRenderer;
    }
    @Override
    public void onFrame(VideoFrame videoFrame) {
        if(TAG.equals("REMOTE"))
            Log.d(TAG, "Frame received in VideoSinkObserver ");
        surfaceViewRenderer.onFrame(videoFrame);
    }

}
