package com.syncpeer.syncpeerapp.videocall.webrtc;

import com.google.gson.Gson;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

public class WebRtcSender {

    private final PeerConnection peerConnection;

    public WebRtcSender(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }
    public void sendOffer(String destinationMail, String email, WebSocketClient webSocket){
        peerConnection.createOffer(new SessionDescriptionObserver("CreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                peerConnection.setLocalDescription(new SessionDescriptionObserver("LocalPeerConnectionSetLocalDescription"), sessionDescription);

                var sdpOffer = new SdpOfferDto(destinationMail, email, sessionDescription);
                Gson gsonObjectMapper = new Gson();
                String sdpSession = gsonObjectMapper.toJson(sdpOffer);

                WebSocketOperations.Companion.subscribe(webSocket,
                        BuildConfig.SIGNALING_SERVER_OFFER_SDP_TOPIC
                                + "/"
                                + sdpOffer.getSource());

                WebSocketOperations.Companion.send(webSocket,
                        sdpSession,
                        BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.OFFER);
            }
        }, new MediaConstraints());

    }
}
