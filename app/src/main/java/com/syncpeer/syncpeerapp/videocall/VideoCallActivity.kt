package com.syncpeer.syncpeerapp.videocall

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.videocall.webrtc.PeerToPeerConnectionEstablishment
import com.syncpeer.syncpeerapp.videocall.webrtc.events.MessageEvent
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator
import com.syncpeer.syncpeerapp.videocall.webrtc.observers.RendererEventsObserver
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class VideoCallActivity : AppCompatActivity() {
    private var peerToPeerConnectionEstablishment: PeerToPeerConnectionEstablishment? = null
    private lateinit var sendingText:String
    private var isGranted:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootEglBase = EglBase.create()
        setContentView(R.layout.activity_video_call)
        val surfaceViewRenderer = findViewById<SurfaceViewRenderer>(R.id.surfaceViewRenderer)
        surfaceViewRenderer.init(rootEglBase.eglBaseContext,RendererEventsObserver("RendererEventsObserver"))
        surfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        surfaceViewRenderer.setZOrderMediaOverlay(false)
        surfaceViewRenderer.setEnableHardwareScaler(true)
        surfaceViewRenderer.setMirror(true)
        surfaceViewRenderer.visibility = View.VISIBLE


        val email = applicationContext
            .getSharedPreferences(Constants.USER_EMAIL, MODE_PRIVATE)
            .getString(Constants.USER_EMAIL, null)

        val destinationMail = intent.getStringExtra("destination_mail")
        this.sendingText = "Send Message to Peer $destinationMail"
        val cameraPermission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            isGranted = true
        } else {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission), 100)
        }
        if(isGranted) {
            peerToPeerConnectionEstablishment = PeerToPeerConnectionEstablishment(
                applicationContext,
                email,
                destinationMail,
                DestinationEmailMediator(),
                surfaceViewRenderer,
                rootEglBase
            )

            val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
            val task = Runnable {
                peerToPeerConnectionEstablishment?.initializePeerConnections()
            }
            val future = executor.scheduleAtFixedRate(task, 2, 5, TimeUnit.SECONDS)
        }
        EventBus.getDefault().register(this)
        val composeView = findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            MainScreen()
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isGranted = true
            } else {
                Toast.makeText(this,"NU E BINE",Toast.LENGTH_LONG).show()
                onDestroy()
            }
        }
    }
    @Composable
    @Preview
    fun MainScreen() {
        var messageToSend by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            TextField(
                value = messageToSend,
                onValueChange = { messageToSend = it },
                label = { Text("Type your message here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Button(
                onClick = {
                    peerToPeerConnectionEstablishment?.sendMessageToPeer(messageToSend)
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 40.dp,
                        end = 40.dp
                    )
                    .height(40.dp),
            ) {
                Text(text = sendingText)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: MessageEvent) {
        val message = event.message
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun unregister() {
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregister()
        peerToPeerConnectionEstablishment?.unregister()
    }
}