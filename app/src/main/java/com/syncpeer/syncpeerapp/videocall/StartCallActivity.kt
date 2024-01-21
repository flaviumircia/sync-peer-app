package com.syncpeer.syncpeerapp.videocall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syncpeer.syncpeerapp.R
import org.webrtc.SurfaceViewRenderer

class StartCallActivity : AppCompatActivity() {
    private lateinit var remoteViewRenderer: SurfaceViewRenderer
    private lateinit var localViewRenderer: SurfaceViewRenderer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_call)

        remoteViewRenderer = findViewById(R.id.remotePeerVideoView)
        localViewRenderer = findViewById(R.id.localPeerVideoView)

    }
}