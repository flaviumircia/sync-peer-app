package com.syncpeer.syncpeerapp.videocall.webrtc;


import android.content.Context;
import android.util.Log;

import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.OnRenegotiationEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.TrackEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.Component;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationManager;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.VideoSinkObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.senders.OfferSender;
import com.syncpeer.syncpeerapp.videocall.webrtc.video.VideoCapturerCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeerToPeerConnectionEstablishment implements Component, RenegotiationMediator {

    private final String TAG = "PeerToPeerConnectionEstablishment";
    private final PeerConnectionFactory peerConnectionFactory;
    private final Context context;
    private final DestinationEmailMediator destinationEmailMediator;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;
    private CustomWebSocketClient webSocket;
    private final String email;
    private String destinationEmail;
    private PeerConnection peerConnection;
    private PeerConnection remotePeerConnection;
    private DataChannel dataChannel;
    private RenegotiationManager renegotiationManager;
    private Deque<String> messages;
    private SurfaceViewRenderer localVideoView;
    private SurfaceViewRenderer remoteVideoView;
    private EglBase rootEglBase;
    private Boolean once = false;

    public PeerToPeerConnectionEstablishment(Context context,
                                             String email,
                                             String destinationEmail,
                                             DestinationEmailMediator destinationEmailMediator,
                                             SurfaceViewRenderer localVideoView,
                                             SurfaceViewRenderer remoteVideoView,
                                             EglBase rootEglBase) throws URISyntaxException, InterruptedException {
        EventBus.getDefault().register(this);

        this.context = context;
        this.rootEglBase = rootEglBase;
        this.email = email;
        this.localVideoView = localVideoView;
        this.remoteVideoView = remoteVideoView;
        this.destinationEmail = destinationEmail;
        this.destinationEmailMediator = destinationEmailMediator;
        this.destinationEmailMediator.registerComponent(this);
        this.destinationEmailMediator.setDestinationEmailShared(destinationEmail);
        this.peerConnectionFactory = initializePeerConnectionFactory(context);

        this.renegotiationManager = new RenegotiationManager();
        this.renegotiationManager.registerComponent(this);
        this.messages = new LinkedList<>();
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

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable task1 = this::renderFramesToLocalVideoView;
        Runnable task2 = this::renderFramesToRemoteVideoView;

        executorService.schedule(task1, 1, TimeUnit.SECONDS);
        executorService.schedule(task2, 2, TimeUnit.SECONDS);

        executorService.shutdown();
    }

    private void renderFramesToLocalVideoView() {

        if (localVideoView != null && localVideoView.isAttachedToWindow()) {
            VideoSinkObserver videoSinkObserver = new VideoSinkObserver("VideoSinkObserver", localVideoView);

            this.videoCapturer = new VideoCapturerCreator(context, videoSinkObserver, rootEglBase).createVideoCapturer();
            var widthPixels = (int) (120 * context.getResources().getDisplayMetrics().density);
            var heightPixels = (int) (160 * context.getResources().getDisplayMetrics().density);
            // Create video source and track
            this.videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
            this.localVideoTrack = peerConnectionFactory.createVideoTrack("localVideoTrack" + email, videoSource);
            this.localVideoTrack.setEnabled(true);

// Create a MediaStream with a label
            MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream");
// Add the local video track to the media stream
            mediaStream.addTrack(localVideoTrack);

// Create an observer to handle video rendering (assuming videoSinkObserver is a valid implementation)
            localVideoTrack.addSink(videoSinkObserver);

// Add the local video track to the PeerConnection
            RtpTransceiver transceiver = peerConnection.addTransceiver(localVideoTrack);
            Log.d(TAG, "renderFramesToLocalVideoView: " + transceiver.getDirection().name());
            localVideoView.post(() -> {
                Log.d(TAG, "initializePeerConnections:" + localVideoView.isEnabled());
                videoCapturer.startCapture(widthPixels, heightPixels, 24);
            });


        }

    }

    private void renderFramesToRemoteVideoView() {

        if (remoteVideoView != null && remoteVideoView.isAttachedToWindow() && remoteVideoTrack != null) {
            VideoSinkObserver videoSinkObserver = new VideoSinkObserver("REMOTE", remoteVideoView);

//            VideoSinkObserver videoSinkObserver = new VideoSinkObserver("RemoteVideoSinkObserver", remoteVideoView);
//TODO: THIS IS BULLSHIT, WATCH THE onAddStream from the remote peerConnection and if you have a mediastream call this function with
            // renderFramesToVideoView(mediaStream)
            //TODO:
            // Iterate through transceivers in the remotePeerConnection
            remoteVideoTrack.enabled();
            // Assuming you have a method to handle the remote track
            handleRemoteVideoTrack(remoteVideoTrack);
            // Add the remoteVideoView as a sink to the remoteVideoTrack
            remoteVideoTrack.addSink(videoSinkObserver);
            once = true;

        }

    }


    private void handleRemoteVideoTrack(VideoTrack remoteVideoTrack) {
        Log.d(TAG, "handleRemoteVideoTrack: " + remoteVideoTrack.id());
    }

    public void sendMessageToPeer(String message) {
        messages.offer(message);

        var executor = Executors.newScheduledThreadPool(1);

        var renegotiateTask = new Runnable() {
            @Override
            public void run() {
                sendMessageViaDataChannel(messages.peek());
            }
        };
        var callback = executor.scheduleAtFixedRate(renegotiateTask, 0, 1, TimeUnit.SECONDS);

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void renegotiate(OnRenegotiationEvent event) {
        if (event.getNeeded()) {
            OfferSender.createAndSendOffer(webSocket, peerConnection, destinationEmail, email);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void receiveRemoteVideoTrack(TrackEvent event) {
        this.remoteVideoTrack = event.getVideoTrack();
    }

    private void sendMessageViaDataChannel(String message) {
        DataChannel.Init dataChannelInit = new DataChannel.Init();
        this.dataChannel = peerConnection.createDataChannel("dataChannel", dataChannelInit);
        dataChannel.registerObserver(new DataChannel.Observer() {

            @Override
            public void onBufferedAmountChange(long l) {
                // Handle buffered amount change, if needed
            }

            @Override
            public void onStateChange() {
                Log.d("DataChannel", "onStateChange: " + dataChannel.state());
                if (dataChannel != null && dataChannel.state().equals(DataChannel.State.OPEN)) {
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

    public void unregister() throws InterruptedException {
        EventBus.getDefault().unregister(this);
        if (videoCapturer != null) {
            videoCapturer.stopCapture();
            videoCapturer.dispose();
        }
    }
}

