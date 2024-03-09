package com.syncpeer.syncpeerapp.videocall.webrtc.events;

public class IceGatheringEvent {

    private Boolean isCompleted;

    public IceGatheringEvent(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }
}
