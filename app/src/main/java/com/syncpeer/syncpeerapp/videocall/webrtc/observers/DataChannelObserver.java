package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import org.webrtc.DataChannel;

public class DataChannelObserver implements DataChannel.Observer {
    private final String TAG;
    private final DataChannel dataChannel;
    public DataChannelObserver(String tag, DataChannel dataChannel) {
        TAG = tag;
        this.dataChannel = dataChannel;
    }

    @Override
    public void onBufferedAmountChange(long l) {
        Log.d(TAG, "onBufferedAmountChange: " + l);
    }

    @Override
    public void onStateChange() {
        Log.d("DataChannel", "onStateChange: " + dataChannel.state());
    }

    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        Log.d("DataChannel", "onMessage: " + buffer.data.get());

    }
}
