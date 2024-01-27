package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.RendererCommon;

public class RendererEventsObserver implements RendererCommon.RendererEvents {
    private final String TAG;

    public RendererEventsObserver(String tag) {
        TAG = tag;

    }

    @Override
    public void onFirstFrameRendered() {
        Log.d(TAG, "onFirstFrameRendered: ");
    }

    @Override
    public void onFrameResolutionChanged(int i, int i1, int i2) {
        Log.d(TAG, "onFirstFrameRendered:"+i+","+i1+","+i2);

    }
}
