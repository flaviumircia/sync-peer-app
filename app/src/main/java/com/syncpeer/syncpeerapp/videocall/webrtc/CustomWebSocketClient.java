package com.syncpeer.syncpeerapp.videocall.webrtc;

import static com.syncpeer.syncpeerapp.videocall.webrtc.senders.RequestSender.setDescriptionAndSendAnswer;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.Component;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.AddIceObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.PeerConnection;

import java.io.StringReader;
import java.net.URI;

public class CustomWebSocketClient extends WebSocketClient implements Component {
    private final String TAG;
    private final DestinationEmailMediator destinationEmailMediator;
    private PeerConnection peerConnection;
    private PeerConnection remotePeerConnection;
    private final String iceTopicSubscribeUrl;

    public CustomWebSocketClient(URI serverUri, String tag, String iceTopicSubscribeUrl, DestinationEmailMediator destinationEmailMediator) {
        super(serverUri);
        this.TAG = tag;
        this.iceTopicSubscribeUrl = iceTopicSubscribeUrl;
        this.destinationEmailMediator = destinationEmailMediator;
        this.destinationEmailMediator.registerComponent(this);
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
        var gson = new Gson();
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
            remotePeerConnection.addIceCandidate(
                    iceCandidateDto.getIceCandidate(),
                    new AddIceObserver("RemotePeerConnection", iceCandidateDto));
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
        setDescriptionAndSendAnswer(remotePeer, remote, webSocket);
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
