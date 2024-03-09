package com.syncpeer.syncpeerapp.videocall.webrtc.events;

import org.webrtc.VideoTrack;

public class TrackEvent {

    private VideoTrack videoTrack;

    public TrackEvent(VideoTrack videoTrack) {
        this.videoTrack = videoTrack;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }

}
