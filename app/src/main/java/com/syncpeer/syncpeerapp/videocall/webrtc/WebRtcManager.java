package com.syncpeer.syncpeerapp.videocall.webrtc;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.syncpeer.syncpeerapp.BuildConfig;

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
import java.util.ArrayList;
import java.util.List;

public class WebRtcManager {

    private PeerConnectionFactory peerConnectionFactory;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private final Context context;
    private WebSocketClient webSocket;

    public WebRtcManager(Context context) {
        this.context = context;
    }

    public void initializeWebRTC() {

        VideoSource videoSource = null;
        var rootEglBase = EglBase.create();
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory
                .InitializationOptions
                .builder(context)
                .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory();


        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        PeerConnection peerConnection = getWebRTCManager(rtcConfig);

        VideoCapturer videoCapturer = createVideoCapturer();

        if (videoCapturer != null)
            videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());

        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack", videoSource);

        if (peerConnection != null) {
            peerConnection.addTrack(localVideoTrack);

            try {
                PeerConnection remotePeerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver());
                this.webSocket = getWebSocket(remotePeerConnection, peerConnection);
            } catch (URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Create an offer and set it as local description
            peerConnection.createOffer(new CustomSdpObserver("OFFER") {
                @Override
                public void onCreateFailure(String s) {
                    super.onCreateFailure(s);
                    Log.d("CreateOffer", "onCreateFailure: " + s);
                }

                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    super.onCreateSuccess(sessionDescription);
                    // Set local description
                    peerConnection.setLocalDescription(new CustomSdpObserver("setLocalDesc"), sessionDescription);
                    // Send SDP offer/answer via WebSocket
                    sendSdpSession(
                            5L,
                            sessionDescription,
                            BuildConfig.SIGNALING_SERVER_OFFER_SDP_TOPIC,
                            BuildConfig.SIGNALING_SERVER_STOMP_SEND);
                }
            }, new MediaConstraints());
        }

    }

    @Nullable
    private PeerConnection getWebRTCManager(PeerConnection.RTCConfiguration rtcConfig) {
        return peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {
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
                Log.d("WebRTCManager", "onIceCandidate: " + iceCandidate.sdp);

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

    private void sendAnswer(PeerConnection peerConnection, SessionDescription remote) {

        SessionDescription remoteOfferSessionDescription = new SessionDescription(SessionDescription.Type.OFFER, remote.description);
        peerConnection.setRemoteDescription(new CustomSdpObserver("setRemoteDesc") {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                Log.d("Remote", "onSetSuccess");

                // Proceed to create an answer after successfully setting the remote description
                peerConnection.createAnswer(new CustomSdpObserver("ANSWER") {
                    @Override
                    public void onCreateFailure(String s) {
                        super.onCreateFailure(s);
                        Log.d("ANSWER", "onCreateFailure: " + s);
                    }

                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);
                        // Set local description
                        peerConnection.setLocalDescription(new CustomSdpObserver("ANSWER"), sessionDescription);
                        // Send SDP offer/answer via WebSocket
                        sendSdpSession(
                                5L,
                                sessionDescription,
                                BuildConfig.SIGNALING_SERVER_ANSWER_SDP_TOPIC,
                                BuildConfig.SIGNALING_SERVER_STOMP_RECEIVE);
                    }
                }, new MediaConstraints());
            }

            @Override
            public void onSetFailure(String s) {
                super.onSetFailure(s);
                Log.d("Remote", "onSetFailure: " + s);
            }
        }, remoteOfferSessionDescription);
    }

    @NonNull
    private WebSocketClient getWebSocket(PeerConnection remotePeerConnection, PeerConnection localPeerConnection) throws URISyntaxException, InterruptedException {
        var web = new WebSocketClient(new URI(BuildConfig.SIGNALING_SERVER)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                var stompConnectFrame = "CONNECT\naccept-version:1.0\nhost:${uri}\n\n\u0000";
                send(stompConnectFrame);
            }

            @Override
            public void onMessage(String message) {

                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(message.split("\n\n")[1].replace("\u0000", "")));
                reader.setLenient(true);

                if (message.contains("OFFER"))
                    sendAnswer(remotePeerConnection, gson.fromJson(reader, SessionDescription.class));
                else if (message.contains("ANSWER"))
                    localPeerConnection.setRemoteDescription(new CustomSdpObserver("setSenderAnswer"), gson.fromJson(reader, SessionDescription.class));

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

    private void sendSdpSession(Long id, SessionDescription sessionDescription, String signalingServerOfferSdpTopic, String signalingServerStompSend) {
        Gson gsonObjectMapper = new Gson();

        String sdpSession = gsonObjectMapper.toJson(sessionDescription);

        Log.d("WebRTCManager", "onCreateSuccess: " + sdpSession);

        WebSocketOperations.Companion.subscribe(webSocket, signalingServerOfferSdpTopic);
        WebSocketOperations.Companion.send(webSocket, sdpSession, signalingServerStompSend);
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


    // Other methods for managing WebRTC components
}