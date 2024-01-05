package com.syncpeer.syncpeerapp.videocall.webrtc;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.auth.utils.Constants;
import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;
import com.syncpeer.syncpeerapp.videocall.utils.SdpOfferDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.AddIceObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.PeerConnectionObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.SessionDescriptionObserver;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PeerToPeerConnectionEstablishment {

    private final PeerConnectionFactory peerConnectionFactory;
    private final Context context;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private WebSocketClient webSocket;
    private String email;
    private String receivedEmail = "";
    private PeerConnection peerConnection;
    private Boolean isCaller = false;
    private PeerConnection remotePeerConnection;
    private DataChannel dataChannel;
    private WebRtcSender localWebRtcSender;

    public PeerToPeerConnectionEstablishment(Context context,String email) {

        this.context = context;
        this.email = email;

        var rootEglBase = EglBase.create();
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory
                .InitializationOptions
                .builder(context)
                .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        this.peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory();

    }

    public void initializeWebRTC() {

        List<PeerConnection.IceServer> iceServers = new ArrayList<>(
                List.of(
                        PeerConnection
                                .IceServer
                                .builder("stun:stun.l.google.com:19302")
                                .createIceServer()
                ));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        this.peerConnection = createPeerConnection(rtcConfig, "PeerConnection");
        this.remotePeerConnection = createPeerConnection(rtcConfig, "RemotePeerConnection");

        videoSource = peerConnectionFactory.createVideoSource(createVideoCapturer().isScreencast());

        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack", videoSource);
        peerConnection.addTrack(localVideoTrack);
        try {
            this.webSocket = getWebSocket(remotePeerConnection, peerConnection);
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebSocketOperations.Companion.subscribe(webSocket, BuildConfig.SIGNALING_SERVER_OFFER_SDP_TOPIC + "/" + email);
        WebSocketOperations.Companion.subscribe(webSocket, BuildConfig.SIGNALING_SERVER_SEND_ICE_TOPIC + "/" + email);
    }

    public void startTheExchangeTo(String destinationMail) {

        this.receivedEmail = destinationMail;

        //TODO: This isCaller thing is very buggy change logic
        isCaller = true;

//        localWebRtcSender.sendOffer(destinationMail,email,webSocket);
        peerConnection.createOffer(new SessionDescriptionObserver("CreateOffer") {
            @Override
            public void onCreateFailure(String s) {
                super.onCreateFailure(s);
            }

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                // Set local description
                var sdpOffer = new SdpOfferDto(destinationMail, email, sessionDescription);
                peerConnection.setLocalDescription(new SessionDescriptionObserver("LocalPeerConnectionSetLocalDescription"), sessionDescription);
                // Send SDP offer
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

    @Nullable
    private PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, String TAG) {
        return peerConnectionFactory.createPeerConnection(rtcConfig, new PeerConnectionObserver(TAG + ":PeerConnectionFactory") {
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


    private void sendIceCandidate(IceCandidate iceCandidate) {
        var gson = new Gson();

        var iceCandidateWrapper = new IceCandidateDto(email, receivedEmail, iceCandidate);
        Log.d("WebRTCManager", "onIceCandidate: " + iceCandidateWrapper);

        WebSocketOperations.Companion.send(webSocket,
                gson.toJson(iceCandidateWrapper),
                BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.ICE_OFFER);
    }

    private void sendAnswer(PeerConnection remotePeer, SdpOfferDto remote) {

        receivedEmail = remote.getSource();
        remotePeer.setRemoteDescription(new SessionDescriptionObserver("RemotePeerConnectionSetRemoteDescription") {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                remotePeer.createAnswer(new SessionDescriptionObserver("RemotePeerCreateAnswer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);

                        var sdpOffer = new SdpOfferDto(remote.getSource(),
                                remote.getDestination(),
                                sessionDescription);

                        remotePeer.setLocalDescription(new SessionDescriptionObserver("RemotePeerConnectionSetLocalDescription"), sessionDescription);

                        Gson gsonObjectMapper = new Gson();
                        String sdpSession = gsonObjectMapper.toJson(sdpOffer);

                        WebSocketOperations.Companion.send(webSocket,
                                sdpSession,
                                BuildConfig.SIGNALING_SERVER_SEND_ENDPOINT + BuildConfig.OFFER);
                    }
                }, new MediaConstraints());
            }

        }, remote.getSdpMessage());


    }

    @NonNull
    private WebSocketClient getWebSocket(PeerConnection remotePeerConnection, PeerConnection
            localPeerConnection) throws URISyntaxException, InterruptedException {
        var web = new WebSocketClient(new URI(BuildConfig.SIGNALING_SERVER)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                var stompConnectFrame = "CONNECT\naccept-version:1.0\nhost:${uri}\n\n\u0000";
                send(stompConnectFrame);
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocketClient ", "onMessage: " + message);

                Gson gson = new Gson();

                JsonReader reader = new JsonReader(new StringReader(
                        message.split("\n\n")[1]
                                .replace("\u0000", ""))
                );

                reader.setLenient(true);

                if (message.contains("OFFER")) {
                    sendAnswer(remotePeerConnection, gson.fromJson(reader, SdpOfferDto.class));
                }
                if (message.contains("ANSWER")) {
                    SdpOfferDto temp = gson.fromJson(reader, SdpOfferDto.class);
                    localPeerConnection.setRemoteDescription(new SessionDescriptionObserver("setSenderAnswer"), temp.getSdpMessage());

                }
                if (message.contains(BuildConfig.SIGNALING_SERVER_SEND_ICE_TOPIC + "/" + email)) {

                    IceCandidateDto iceCandidateDto = gson.fromJson(reader, IceCandidateDto.class);

                    if (isCaller) {
                        localPeerConnection
                                .addIceCandidate(
                                        iceCandidateDto.getIceCandidate(),
                                        new AddIceObserver("LocalPeerConnection", iceCandidateDto)
                                );
                    } else {
                        remotePeerConnection
                                .addIceCandidate(
                                        iceCandidateDto.getIceCandidate(),
                                        new AddIceObserver("RemotePeerConnection", iceCandidateDto)
                                );
                    }
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };
        web.connectBlocking();

        return web;
    }

    // Method to create video capturer (camera)
    private VideoCapturer createVideoCapturer() {
        // Get the list of available cameras using Camera2Enumerator
        Camera2Enumerator enumerator = new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();

        // Select the front or back camera (or any specific camera)
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                // Use the front-facing camera
                return createCameraCapturer(enumerator, deviceName);
            }
        }

        return null; // Handle case where no suitable camera is found
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator, String deviceName) {
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CameraTexture", null);

        return enumerator.createCapturer(deviceName, new CameraVideoCapturer.CameraEventsHandler() {
            @Override
            public void onCameraError(String s) {

            }

            @Override
            public void onCameraDisconnected() {

            }

            @Override
            public void onCameraFreezed(String s) {

            }

            @Override
            public void onCameraOpening(String s) {

            }

            @Override
            public void onFirstFrameAvailable() {

            }

            @Override
            public void onCameraClosed() {

            }

            // Implement capturer observer methods as needed
        });
    }

    public void sendMessageToPeer(String message) {
        DataChannel.Init dataChannelInit = new DataChannel.Init();
        dataChannel = peerConnection.createDataChannel("dataChannel", dataChannelInit);
        dataChannel.registerObserver(new DataChannel.Observer() {

            @Override
            public void onBufferedAmountChange(long l) {
                // Handle buffered amount change, if needed
            }

            @Override
            public void onStateChange() {
                Log.d("DataChannel", "onStateChange: " + dataChannel.state());
                if (dataChannel.state().equals(DataChannel.State.OPEN)) {
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                    DataChannel.Buffer data = new DataChannel.Buffer(buffer, false);
                    dataChannel.send(data);
                }
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Log.d("DataChannel", "onMessage: " + buffer.data.get());
            }
        });
    }
    // Other methods for managing WebRTC components
}

