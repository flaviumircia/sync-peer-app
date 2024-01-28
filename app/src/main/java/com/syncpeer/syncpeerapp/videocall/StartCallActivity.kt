package com.syncpeer.syncpeerapp.videocall

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.RendererEventsObserver
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer

class StartCallActivity : AppCompatActivity() {
    private lateinit var remoteViewRenderer: SurfaceViewRenderer
    private lateinit var localViewRenderer: SurfaceViewRenderer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_call)

        remoteViewRenderer = findViewById(R.id.remotePeerVideoView)
        localViewRenderer = findViewById(R.id.localPeerVideoView)

//        val surfaceViewRenderer = findViewById<SurfaceViewRenderer>(R.id.localPeerVideoView)
//        surfaceViewRenderer.init(rootEglBase.eglBaseContext,
//            RendererEventsObserver("RendererEventsObserver")
//        )
//        surfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
//        surfaceViewRenderer.setZOrderMediaOverlay(false)
//        surfaceViewRenderer.setEnableHardwareScaler(true)
//        surfaceViewRenderer.setMirror(true)
//        surfaceViewRenderer.visibility = View.VISIBLE
    }
}