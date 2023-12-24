package com.syncpeer.syncpeerapp.videocall.webrtc;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class CustomSdpObserver implements SdpObserver {
    private String tag;

    public CustomSdpObserver(String tag) {
        this.tag = tag;
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // Implementation when creating an SDP offer or answer is successful
    }

    @Override
    public void onSetSuccess() {
        // Implementation when setting local or remote description is successful
    }

    @Override
    public void onCreateFailure(String s) {
        // Implementation when creating an SDP offer or answer fails
    }

    @Override
    public void onSetFailure(String s) {
        // Implementation when setting local or remote description fails
    }

    // Other methods from the SdpObserver interface
}
