package com.syncpeer.syncpeerapp.videocall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.syncpeer.syncpeerapp.BuildConfig
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.auth.utils.InstantiateJwtSharedPreference
import com.syncpeer.syncpeerapp.videocall.callback.MessageHolder
import com.syncpeer.syncpeerapp.videocall.webrtc.WebRtcManager
import com.syncpeer.syncpeerapp.videocall.webrtc.WebSocketClient
import kotlinx.coroutines.*


class HomeActivity : AppCompatActivity() {
    private var webSocketClient: WebSocketClient? = null
    private var isCaller = true;
    private val scope = CoroutineScope(Dispatchers.Default) // Create a CoroutineScope

    init{
        observeMessageChanges()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val sharedPreferences =
            InstantiateJwtSharedPreference(this, Constants.JWT_FILE_NAME).getSharedPreferences()
        val tempJWT = sharedPreferences.getString(Constants.SHARED_PREFERENCES_JWT_NAME, null)
        this.webSocketClient = WebSocketClient(BuildConfig.SIGNALING_SERVER)

    setContent {
            MainScreen()
        }
    }
    private fun observeMessageChanges(){
        //TODO: Draw the schema for the logic of send/receive SDP exchange
        scope.launch {
            while (isActive) {
                delay(500)
                if(!MessageHolder.isCaller){
                    val webRtcManager = WebRtcManager(applicationContext,webSocketClient,false)
                    webRtcManager.initializeWebRTC()
                }
            }
        }
    }
    @Composable
    @Preview
    fun MainScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    // Usage:
                    val webRtcManager = WebRtcManager(applicationContext,webSocketClient,true)
                    webRtcManager.initializeWebRTC()
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
                Text(text = "Call!")
            }
        }
    }

}