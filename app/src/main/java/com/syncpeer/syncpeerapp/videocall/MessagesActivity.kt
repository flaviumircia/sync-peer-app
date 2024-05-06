package com.syncpeer.syncpeerapp.videocall

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
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
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.videocall.webrtc.PeerToPeerConnectionEstablishment
import com.syncpeer.syncpeerapp.videocall.webrtc.mediators.DestinationEmailMediator
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

class MessagesActivity : AppCompatActivity() {
    private var peerToPeerConnectionEstablishment: PeerToPeerConnectionEstablishment? = null
    private lateinit var sendingText:String
    private var destinationMail:String?="";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_call)
        val rootEglBase = EglBase.create()
        val email = applicationContext
            .getSharedPreferences(Constants.USER_EMAIL, MODE_PRIVATE)
            .getString(Constants.USER_EMAIL, null)

        destinationMail = intent.getStringExtra("destination_mail")
        this.sendingText = "Send Message to Peer $destinationMail"

        peerToPeerConnectionEstablishment = PeerToPeerConnectionEstablishment(
            applicationContext,
            email,
            destinationMail,
            DestinationEmailMediator(),
            null,
            null,
            rootEglBase
        )

        setContent {
            MainScreen()
        }
    }
    @Composable
    @Preview
    fun MainScreen() {
        //TODO: Test if sending/receiving messages work and if the calling works
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

            // First Button
            Button(
                onClick = {
                    peerToPeerConnectionEstablishment?.sendMessageViaDataChannel(messageToSend)
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 40.dp,
                        end = 40.dp,
                        top = 8.dp // Add top padding to separate buttons
                    )
                    .height(40.dp),
            ) {
                Text(text = sendingText)
            }

            // Second Button
            Button(
                onClick = {
                    startActivityOnClick();
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 80.dp,
                        end = 80.dp
                    )
                    .height(40.dp),
            ) {
                Text(text = "CALL!")
            }
        }
    }

    private fun startActivityOnClick() {
        val intent = Intent(this@MessagesActivity, VideoCallActivity::class.java)
        intent.putExtra("destination_mail", destinationMail)
        startActivity(intent)
    }
}