package com.syncpeer.syncpeerapp.videocall.webrtc.events;

public class OnRenegotiationEvent {
    Boolean isNeeded;
    public OnRenegotiationEvent(Boolean value) {
        this.isNeeded = value;
    }

    public Boolean getNeeded() {        return isNeeded;
    }
}
