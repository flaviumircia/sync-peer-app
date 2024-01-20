package com.syncpeer.syncpeerapp.videocall.webrtc.senders;

import com.google.gson.Gson;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.WebRtcSender;
import com.syncpeer.syncpeerapp.videocall.webrtc.WebSocketOperations;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;


public class OfferSender {
    public static void createAndSendOffer(WebSocketClient webSocket,
                                          PeerConnection peerConnection,
                                          String destinationMail,
                                          String senderEmail) {
        peerConnection.createOffer(new SessionDescriptionObserver("CreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                peerConnection.setLocalDescription(new SessionDescriptionObserver("LocalPeerConnectionSetLocalDescription"), sessionDescription);
                sendOffer(sessionDescription, destinationMail, senderEmail, webSocket);
            }
        }, new MediaConstraints());
    }

    private static void sendOffer(SessionDescription sessionDescription, String destinationMail, String senderEmail, WebSocketClient webSocket) {
        var sdpOffer = new SdpOfferDto(destinationMail, senderEmail, sessionDescription);
        Gson gson = new Gson();
        WebSocketOperations.Companion.send(
                webSocket,
                gson.toJson(sdpOffer),
                BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.OFFER);
    }
}
