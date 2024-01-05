package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SessionDescriptionObserver implements SdpObserver {
    private String tag;

    public SessionDescriptionObserver(String tag) {
        this.tag = tag;
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // Implementation when creating an SDP offer or answer is successful
        // For example, log success or perform additional actions
        Log.d(tag, "onCreateSuccess: " + sessionDescription.description);
    }

    @Override
    public void onSetSuccess() {
        // Implementation when setting local or remote description is successful
        // For example, log success or perform additional actions
        Log.d(tag, "onSetSuccess");
    }

    @Override
    public void onCreateFailure(String s) {
        // Implementation when creating an SDP offer or answer fails
        // For example, log failure or perform error handling
        Log.e(tag, "onCreateFailure: " + s);
    }

    @Override
    public void onSetFailure(String s) {
        // Implementation when setting local or remote description fails
        // For example, log failure or perform error handling
        Log.e(tag, "onSetFailure: " + s);
    }

    // Other methods from the SdpObserver interface
}

