package com.syncpeer.syncpeerapp.videocall.webrtc.senders;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.WebSocketOperations;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;


public class RequestSender {
    public static void createAndSendOffer(WebSocketClient webSocket,
                                          PeerConnection peerConnection,
                                          String destinationMail,
                                          String senderEmail) {

        peerConnection.createOffer(new SessionDescriptionObserver("CreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                peerConnection.setLocalDescription(new SessionDescriptionObserver("LocalPeerConnectionSetLocalDescription"),
                        sessionDescription);
                sendOffer(sessionDescription, destinationMail, senderEmail, webSocket);
            }
        }, getMediaConstraints());

    }

    public static void setDescriptionAndSendAnswer(PeerConnection remotePeer, SdpOfferDto remote, WebSocketClient webSocket) {

        remotePeer.setRemoteDescription(new SessionDescriptionObserver("RemotePeerConnectionSetRemoteDescription") {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                remotePeer.createAnswer(new SessionDescriptionObserver("RemotePeerCreateAnswer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                        var gson = new Gson();
                        remotePeer.setLocalDescription(
                                new SessionDescriptionObserver("RemotePeerConnectionSetLocalDescription"),
                                sessionDescription);

                        var sdpOffer = new SdpOfferDto(
                                remote.getSource(),
                                remote.getDestination(),
                                sessionDescription);

                        WebSocketOperations.Companion.send(
                                webSocket,
                                gson.toJson(sdpOffer),
                                BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.OFFER);
                    }
                }, getMediaConstraints());

            }

            @Override
            public void onCreateFailure(String s) {
                super.onCreateFailure(s);
            }

            @Override
            public void onSetFailure(String s) {
                super.onSetFailure(s);
            }

        }, remote.getSdpMessage());
    }

    @NonNull
    private static MediaConstraints getMediaConstraints() {
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googCodecName", "VP8"));
        return videoConstraints;
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
