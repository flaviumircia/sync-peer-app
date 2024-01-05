package com.syncpeer.syncpeerapp.videocall.webrtc.observers;

import android.util.Log;

import com.syncpeer.syncpeerapp.videocall.utils.IceCandidateDto;

public class AddIceObserver implements org.webrtc.AddIceObserver {
    private final String TAG;
    private final IceCandidateDto iceCandidateDto;

    public AddIceObserver(String tag, IceCandidateDto iceCandidateDto) {
        TAG = tag;
        this.iceCandidateDto = iceCandidateDto;
    }

    @Override
    public void onAddSuccess() {
        Log.d(TAG, "onAddSuccess: " + iceCandidateDto.getIceCandidate().sdp);
    }

    @Override
    public void onAddFailure(String s) {
        Log.d(TAG, "OnAddFailure: " + s);

    }
}
