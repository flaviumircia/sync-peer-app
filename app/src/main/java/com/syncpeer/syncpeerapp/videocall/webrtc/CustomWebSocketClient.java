package com.syncpeer.syncpeerapp.videocall.webrtc;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.Component;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.AddIceObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.StringReader;
import java.net.URI;

public class CustomWebSocketClient extends WebSocketClient implements Component {
    private final String TAG;
    private final DestinationEmailMediator destinationEmailMediator;
    private PeerConnection peerConnection;
    private PeerConnection remotePeerConnection;
    private Gson gson;
    private boolean isCaller;
    private String iceTopicSubscribeUrl;

    public CustomWebSocketClient(URI serverUri, String tag, Boolean isCaller, String iceTopicSubscribeUrl, DestinationEmailMediator destinationEmailMediator) {
        super(serverUri);
        this.TAG = tag;
        this.iceTopicSubscribeUrl = iceTopicSubscribeUrl;
        this.destinationEmailMediator = destinationEmailMediator;
        this.destinationEmailMediator.registerComponent(this);
        this.gson = new Gson();
        this.isCaller = isCaller;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen HTTP_STATUS: " + handshakedata.getHttpStatus());
        Log.d(TAG, "onOpen HTTP_STATUS_MESSAGE: " + handshakedata.getHttpStatusMessage());
        var stompConnectFrame = "CONNECT\naccept-version:1.0\nhost:${uri}\n\n\u0000";
        send(stompConnectFrame);
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: " + message);
        var reader = parseIncomingMessages(message);
        if (message.contains("OFFER") && remotePeerConnection != null) {
            sendAnswer(remotePeerConnection, gson.fromJson(reader, SdpOfferDto.class), this);
        }
        if (message.contains("ANSWER") && peerConnection != null) {
            SdpOfferDto temp = gson.fromJson(reader, SdpOfferDto.class);
            peerConnection.setRemoteDescription(new SessionDescriptionObserver("setSenderAnswer"), temp.getSdpMessage());
        }
        if (message.contains(iceTopicSubscribeUrl)) {
            IceCandidateDto iceCandidateDto = gson.fromJson(reader, IceCandidateDto.class);
            PeerConnection connection = isCaller ? peerConnection : remotePeerConnection;
            String connectionType = isCaller ? "LocalPeerConnection" : "RemotePeerConnection";
            if (connection != null)
                connection.addIceCandidate(
                        iceCandidateDto.getIceCandidate(),
                        new AddIceObserver(connectionType, iceCandidateDto));
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    private void sendAnswer(PeerConnection remotePeer, SdpOfferDto remote, WebSocketClient webSocket) {
        destinationEmailMediator.setDestinationEmailShared(remote.getSource());
        remotePeer.setRemoteDescription(new SessionDescriptionObserver("RemotePeerConnectionSetRemoteDescription") {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                remotePeer.createAnswer(new SessionDescriptionObserver("RemotePeerCreateAnswer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                        remotePeer.setLocalDescription(new SessionDescriptionObserver("RemotePeerConnectionSetLocalDescription"),
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
                }, new MediaConstraints());
            }

        }, remote.getSdpMessage());


    }

    private JsonReader parseIncomingMessages(String message) {

        JsonReader reader = new JsonReader(
                new StringReader(
                        message.split("\n\n")[1] //split the incoming message by the two \n of the websocket
                                .replace("\u0000", "") // replace the last null character with ""
                )
        );
        reader.setLenient(true);
        return reader;
    }

    @Override
    public void update(String value) {
        Log.d(TAG, "update: " + value);
    }

    public void setPeerConnection(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }

    public void setRemotePeerConnection(PeerConnection remotePeerConnection) {
        this.remotePeerConnection = remotePeerConnection;
    }
}
