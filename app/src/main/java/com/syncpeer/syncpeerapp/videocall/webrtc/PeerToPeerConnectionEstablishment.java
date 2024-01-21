package com.syncpeer.syncpeerapp.videocall.webrtc;


import android.content.Context;
import android.util.Log;

import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.OnRenegotiationEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.Component;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationManager;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.senders.OfferSender;
import com.syncpeer.syncpeerapp.videocall.webrtc.video.VideoCapturerCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PeerToPeerConnectionEstablishment implements Component, RenegotiationMediator {

    private final String TAG = "PeerToPeerConnectionEstablishment";
    private final PeerConnectionFactory peerConnectionFactory;
    private final Context context;
    private final DestinationEmailMediator destinationEmailMediator;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private CustomWebSocketClient webSocket;
    private final String email;
    private String destinationEmail;
    private PeerConnection peerConnection;
    private PeerConnection remotePeerConnection;
    private DataChannel dataChannel;
    private RenegotiationManager renegotiationManager;
    private Deque<String> messages;

    public PeerToPeerConnectionEstablishment(Context context, String email,String destinationEmail, DestinationEmailMediator destinationEmailMediator) throws URISyntaxException, InterruptedException {

        this.context = context;
        this.email = email;
        this.destinationEmail = destinationEmail;
        this.destinationEmailMediator = destinationEmailMediator;
        this.destinationEmailMediator.registerComponent(this);
        this.destinationEmailMediator.setDestinationEmailShared(destinationEmail);
        this.peerConnectionFactory = initializePeerConnectionFactory(context);
        this.renegotiationManager = new RenegotiationManager();
        this.renegotiationManager.registerComponent(this);
        this.messages = new LinkedList<>();

        EventBus.getDefault().register(this);

        this.webSocket = new CustomWebSocketClient(new URI(BuildConfig.SIGNALING_SERVER),
                "WebSocketClient",
                BuildConfig.SIGNALING_SERVER_SEND_ICE_TOPIC + "/" + email,
                destinationEmailMediator);

        webSocket.connectBlocking();

        //Subscribe to the ice topic and sdp topic
        WebSocketOperations.Companion.subscribe(webSocket, BuildConfig.SIGNALING_SERVER_OFFER_SDP_TOPIC + "/" + email);
        WebSocketOperations.Companion.subscribe(webSocket, BuildConfig.SIGNALING_SERVER_SEND_ICE_TOPIC + "/" + email);


    }

    private PeerConnectionFactory initializePeerConnectionFactory(Context context) {
        var rootEglBase = EglBase.create();

        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory
                .InitializationOptions
                .builder(context)
                .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());


        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory();
    }


    public void initializePeerConnections() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>(
                List.of(
                        PeerConnection
                                .IceServer
                                .builder("stun:stun.l.google.com:19302")
                                .createIceServer()
                        ,
                        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:443")
                                .setUsername("054e5048508b70f98dbbc7ba")
                                .setPassword("GKhoYikRKw1jg1s5")
                                .createIceServer()

                )
        );

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);

        this.peerConnection = new PeerConnectionCreator(
                "LocalPeerConnection",
                rtcConfig,
                peerConnectionFactory,
                webSocket,
                email,
                destinationEmailMediator.getDestinationEmailShared(),
                renegotiationManager).createPeerConnection();

        this.remotePeerConnection = new PeerConnectionCreator(
                "RemotePeerConnection",
                rtcConfig,
                peerConnectionFactory,
                webSocket,
                email,
                destinationEmailMediator.getDestinationEmailShared(),
                renegotiationManager).createPeerConnection();

        //add each peerConnection to the websocket
        this.webSocket.setPeerConnection(peerConnection);
        this.webSocket.setRemotePeerConnection(remotePeerConnection);


        videoSource = peerConnectionFactory
                .createVideoSource(
                        new VideoCapturerCreator(context)
                                .createVideoCapturer()
                                .isScreencast()
                );
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack", videoSource);
        peerConnection.addTrack(localVideoTrack);
    }

    public void sendMessageToPeer(String message) {

        DataChannel.Init dataChannelInit = new DataChannel.Init();
        dataChannel = peerConnection.createDataChannel("dataChannel", dataChannelInit);

        messages.offer(message);

        var executor = Executors.newScheduledThreadPool(1);

        var renegotiateTask = new Runnable() {
            @Override
            public void run() {
                sendMessageViaDataChannel(messages.peek());
                if (messages.isEmpty()) {
                    executor.shutdown();
                }
            }
        };
        var callback = executor.scheduleAtFixedRate(renegotiateTask, 0, 2, TimeUnit.SECONDS);

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void renegotiate(OnRenegotiationEvent event){
        if(event.getNeeded())
            OfferSender.createAndSendOffer(webSocket, peerConnection, destinationEmail, email);
    }
    private void sendMessageViaDataChannel(String message) {
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
                    messages.poll();
                }
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Log.d("DataChannel", "onMessage: " + buffer.data.get());
            }
        });
    }

    @Override
    public void update(String value) {
        this.destinationEmail = value;
        Log.d(TAG, "update: " + value);
    }

    @Override
    public void updateStatus(Boolean status) {
        Log.d(TAG, "updateStatus: " + status);
    }
    public void unregister(){
        EventBus.getDefault().unregister(this);
    }
}

