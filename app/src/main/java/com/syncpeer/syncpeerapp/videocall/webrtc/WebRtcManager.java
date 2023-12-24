package com.syncpeer.syncpeerapp.videocall.webrtc;


import android.content.Context;
import android.util.Log;

import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.utils.JsonSerializer;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebRtcManager {

    private PeerConnectionFactory peerConnectionFactory;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private Context context;
    private WebSocketClient webSocket;
    private Boolean isCaller;

    public WebRtcManager(Context context, WebSocketClient webSocketClient, Boolean isCaller) {
        this.context = context;
        this.webSocket = webSocketClient;
        this.isCaller = isCaller;
    }

    public void initializeWebRTC() {
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions()
        );
        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {
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
        VideoCapturer videoCapturer = createVideoCapturer();
        assert videoCapturer != null;
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());

        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack", videoSource);

        assert peerConnection != null;
        peerConnection.addTrack(localVideoTrack);

        // Create an offer and set it as local description
        peerConnection.createOffer(new CustomSdpObserver("createOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                // Set local description
                peerConnection.setLocalDescription(new CustomSdpObserver("setLocalDesc"), sessionDescription);

                // Convert SDP offer/answer to JSON or string
                String sdpString = sessionDescription.description;


                // Send SDP offer/answer via WebSocket
                if (webSocket != null) {
                    webSocket.connectToServer();
                    if (isCaller) {
                        Map<String, String> jsonMap = new HashMap<>();
                        jsonMap.put("type", "offer");
                        jsonMap.put("id", "5");
                        jsonMap.put("sdp", sdpString);
                        String sdpSession = JsonSerializer.mapToString(jsonMap);
                        Log.d("WebRTCManager", "onCreateSuccess: " + sdpSession);
                        webSocket.subscribe(BuildConfig.SIGNALING_SERVER_OFFER_SDP_TOPIC);
                        webSocket.send(sdpSession, BuildConfig.SIGNALING_SERVER_STOMP_SEND);
                    } else {
                        Map<String, String> jsonMap = new HashMap<>();
                        jsonMap.put("type", "answer");
                        jsonMap.put("id", "5");
                        jsonMap.put("sdp", sdpString);
                        String sdpSession = JsonSerializer.mapToString(jsonMap);
                        Log.d("WebRTCManager", "onCreateSuccess: " + sdpSession);
                        webSocket.subscribe(BuildConfig.SIGNALING_SERVER_ANSWER_SDP_TOPIC);
                        webSocket.send(sdpSession, BuildConfig.SIGNALING_SERVER_STOMP_RECEIVE);
                    }

                }

            }
        }, new MediaConstraints());

    }
//    public void initializeWebRTC(Context context) {
//        // Create and initialize PeerConnectionFactory
//        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions());
//        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();
//
//        // Create and initialize video capturer (for example, camera)
//        videoCapturer = createVideoCapturer();
//
//        // Create video source
//        EglBase.Context eglContext = EglBase.create().getEglBaseContext();
//        videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
//
//
//        // Create video track
//        localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack", videoSource);
//    }

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