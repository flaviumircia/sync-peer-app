package com.syncpeer.syncpeerapp.videocall.webrtc

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState

class WebSocketOperations {
    companion object {
        fun send(webSocketClient: WebSocketClient, jsonMessage: String, destination: String) {

            val stompSendFrame =
                "SEND\ndestination:${destination}\ncontent-type:application/json\n\n${jsonMessage}\u0000"
            Log.d("WebSocketOperations", "send: " + webSocketClient.readyState);
            if(webSocketClient.readyState.equals(ReadyState.OPEN))
                webSocketClient.send(stompSendFrame)
            else if(webSocketClient.readyState.equals(ReadyState.CLOSED)){
                webSocketClient.reconnectBlocking()
            }

        }

        fun subscribe(webSocketClient: WebSocketClient, topicName: String) {

            val stompSubscribeFrame = "SUBSCRIBE\nid:0\ndestination:${topicName}\n\n\u0000"
            if(webSocketClient.readyState.equals(ReadyState.OPEN))
                webSocketClient.send(stompSubscribeFrame)
            else if(webSocketClient.readyState.equals(ReadyState.CLOSED)){
                webSocketClient.reconnectBlocking()
            }
        }

        fun unsubscribe(webSocketClient: WebSocketClient, topicName: String) {

            val stompUnsubscribeFrame = "UNSUBSCRIBE\ndestination:${topicName}\n\n\u0000"
            if (webSocketClient.isOpen)
                webSocketClient.send(stompUnsubscribeFrame)
        }
    }
}