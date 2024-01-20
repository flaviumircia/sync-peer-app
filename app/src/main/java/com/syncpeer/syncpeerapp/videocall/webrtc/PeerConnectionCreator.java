package com.syncpeer.syncpeerapp.videocall.webrtc;

import android.util.Log;

import com.google.gson.Gson;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationManager;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.PeerConnectionObserver;

import org.java_websocket.client.WebSocketClient;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

public class PeerConnectionCreator implements RenegotiationMediator {
    private final PeerConnection.RTCConfiguration rtcConfiguration;
    private final String TAG;
    private final PeerConnectionFactory peerConnectionFactory;
    private final WebSocketClient webSocket;
    private final String senderEmail;
    private final RenegotiationManager renegotiationManager;
    private String receiverEmail;

    public PeerConnectionCreator(String tag,
                                 PeerConnection.RTCConfiguration rtcConfiguration,
                                 PeerConnectionFactory peerConnectionFactory,
                                 WebSocketClient webSocket,
                                 String senderEmail,
                                 String receiverEmail,
                                 RenegotiationManager renegotiationManager) {
        this.TAG = tag;
        this.rtcConfiguration = rtcConfiguration;
        this.peerConnectionFactory = peerConnectionFactory;
        this.webSocket = webSocket;
        this.senderEmail = senderEmail;
        this.renegotiationManager = renegotiationManager;
        this.renegotiationManager.registerComponent(this);
        this.receiverEmail = receiverEmail;
    }

    public PeerConnection createPeerConnection() {


        return peerConnectionFactory.createPeerConnection(rtcConfiguration, new PeerConnectionObserver(TAG + ":PeerConnectionFactory") {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                super.onSignalingChange(signalingState);
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                super.onIceConnectionReceivingChange(b);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                super.onIceGatheringChange(iceGatheringState);

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                sendIceCandidate(iceCandidate);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                super.onIceCandidatesRemoved(iceCandidates);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                super.onRemoveStream(mediaStream);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);
            }

            @Override
            public void onRenegotiationNeeded() {
                super.onRenegotiationNeeded();
            }
        });

    }
    public void sendIceCandidate(IceCandidate iceCandidate) {
        var gson = new Gson();
        if (senderEmail != null && receiverEmail != null) {
            var iceCandidateWrapper = new IceCandidateDto(senderEmail, receiverEmail, iceCandidate);

            Log.d("WebRTCManager", "onIceCandidate: " + iceCandidateWrapper);

            WebSocketOperations.Companion.send(webSocket,
                    gson.toJson(iceCandidateWrapper),
                    BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.ICE_OFFER);
        }
    }

    @Override
    public void updateStatus(Boolean status) {
        Log.d(TAG, "updateStatus: " + status);
    }
}
