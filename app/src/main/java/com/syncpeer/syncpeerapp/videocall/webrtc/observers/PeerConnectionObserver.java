package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import com.syncpeer.syncpeerapp.videocall.webrtc.events.MessageEvent;
import com.syncpeer.syncpeerapp.videocall.webrtc.events.OnRenegotiationEvent;

import org.greenrobot.eventbus.EventBus;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PeerConnectionObserver implements PeerConnection.Observer {

    private String TAG;

    public PeerConnectionObserver(String tag) {
        this.TAG = tag;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "onSignalingChange: " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "onIceConnectionChange: " + iceConnectionState);

    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        Log.d(TAG, "onIceConnectionReceivingChange: " + receiving);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "onIceGatheringChange: " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "onIceCandidate: " + iceCandidate);

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG, "onIceCandidatesRemoved");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "onAddStream: " + mediaStream);
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "onRemoveStream: " + mediaStream);
    }

    @Override
    public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
        Log.d(TAG, "onAddTrack: " + receiver.track().toString());

        PeerConnection.Observer.super.onAddTrack(receiver, mediaStreams);
    }

    @Override
    public void onTrack(RtpTransceiver transceiver) {
        Log.d(TAG, "onTrack: " + transceiver.toString());

        PeerConnection.Observer.super.onTrack(transceiver);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        dataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                Log.d(TAG+"DATACHANNEL", "onStateChange: " + dataChannel.state());

            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                if (dataChannel.state().equals(DataChannel.State.OPEN)) {
                    Log.d(TAG+"DATACHANNEL", "onMessage: ");
                    if (buffer.binary) {
                        // Handle binary data if needed
                        ByteBuffer data = buffer.data;
                        byte[] bytes = new byte[data.remaining()];
                        data.get(bytes);
                        // Process the binary data (bytes array)
                        Log.d("DataChannelRemotePeer", "Received binary data: " + Arrays.toString(bytes));
                    } else {
                        ByteBuffer data = buffer.data;
                        byte[] bytes = new byte[data.remaining()];
                        data.get(bytes);
                        String message = new String(bytes, StandardCharsets.UTF_8);
                        Log.d("DataChannelRemotePeer", "Received text message: " + message);
                        EventBus.getDefault().post(new MessageEvent(message));
                    }
                }

            }
        });

    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded");
        EventBus.getDefault().post(new OnRenegotiationEvent(true));
    }


}
