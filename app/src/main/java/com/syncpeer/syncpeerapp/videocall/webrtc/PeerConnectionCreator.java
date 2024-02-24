package com.syncpeer.syncpeerapp.videocall.webrtc;

import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.syncpeer.syncpeerapp.BuildConfig;
import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.MessageEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.TrackEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationManager;
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.RenegotiationMediator;
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.PeerConnectionObserver;
import com.syncpeer.syncpeerapp.videocall.webrtc.senders.OfferSender;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.VideoTrack;

import java.util.List;

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
        //TODO: Here we have a method called onTrack that is triggered when a remote Track is added
        //TODO: I receive the on track but I think i need to add that track to the remoteVideoView to have it
        boolean[] isCompleted = new boolean[1];
        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, new PeerConnectionObserver(TAG + ":PeerConnectionFactory") {
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
                if(iceGatheringState== PeerConnection.IceGatheringState.COMPLETE){
                    isCompleted[0] = true;
                }
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
            public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
                for (MediaStream mediaStream : mediaStreams) {
                    List<VideoTrack> videoTracks = mediaStream.videoTracks;

                    for (VideoTrack videoTrack : videoTracks) {
                        Log.d(TAG, "onAddTrack1: " + videoTrack);

                        // Render the video track on a SurfaceViewRenderer
                        break;
                    }
                }
                if(receiver.track().kind().equals("video"))
                {
                    VideoTrack videoTrack = (VideoTrack) receiver.track();
                    EventBus.getDefault().post(new TrackEvent(videoTrack));
                }

                if(mediaStreams.length==0)
                    Log.e(TAG, "No MediaStream associated were found!" + receiver.track().id() +"," + receiver.track().state()+","+receiver.track().enabled()+","+receiver.track().kind());
                super.onAddTrack(receiver, mediaStreams);
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


            @Override
            public void onTrack(RtpTransceiver transceiver) {
                // Check if the received transceiver contains a VideoTrack
//                MediaStreamTrack mediaStreamTrack = transceiver.getReceiver().track();
//
//                if (mediaStreamTrack instanceof VideoTrack videoTrack) {
//                    // Now you can use the VideoTrack as needed, for example, add it to a SurfaceViewRenderer
////                    EventBus.getDefault().post(new TrackEvent(videoTrack));
//
//                }
                //TODO: if this doesn't work, check the local video track id and check if the remote one is the same
                // on the caller side.
                // If yes then the problem is purely on the rendering side (capturing the frames somehow)
                super.onTrack(transceiver);
            }
        });
        if (isCompleted[0]){
            OfferSender.createAndSendOffer(webSocket, peerConnection, senderEmail, receiverEmail);
            isCompleted[0]=false;
        }
        return peerConnection;
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
