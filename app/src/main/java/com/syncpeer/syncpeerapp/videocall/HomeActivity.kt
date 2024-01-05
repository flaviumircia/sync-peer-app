package com.syncpeer.syncpeerapp.videocall

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.videocall.webrtc.PeerToPeerConnectionEstablishment
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {
    private var peerToPeerConnectionEstablishment: PeerToPeerConnectionEstablishment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val email = applicationContext
            .getSharedPreferences(Constants.USER_EMAIL, MODE_PRIVATE)
            .getString(Constants.USER_EMAIL, null)

        peerToPeerConnectionEstablishment = PeerToPeerConnectionEstablishment(applicationContext, email)
        peerToPeerConnectionEstablishment?.initializeWebRTC()

        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        if(!email.equals("test_d@gmail.com")){
            val task = Runnable {
                peerToPeerConnectionEstablishment?.startTheExchangeTo("test_d@gmail.com")
            }
            val future = executor.scheduleAtFixedRate(task,3,5,TimeUnit.SECONDS)

        }

    setContent {
            MainScreen()
        }
    }
    @Composable
    @Preview
    fun MainScreen() {
        var messageToSend by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize(),
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
                Text(text = "Send Message to Peer!")
            }
        }
    }

}