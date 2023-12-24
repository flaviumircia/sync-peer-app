package com.syncpeer.syncpeerapp.videocall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syncpeer.syncpeerapp.BuildConfig

import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.videocall.webrtc.WebRtcManager
import com.syncpeer.syncpeerapp.videocall.webrtc.WebSocketClient


class VideoCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

    }
}