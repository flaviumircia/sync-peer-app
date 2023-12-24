package com.syncpeer.syncpeerapp.videocall.webrtc

import com.syncpeer.syncpeerapp.videocall.callback.MessageHolder
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WebSocketClient(uri: String) {

    val serverUri = URI(uri)
    private var webSocketClient: WebSocketClient? = null
    fun connectToServer() {
        val webSocketClient: WebSocketClient = object : WebSocketClient(serverUri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                val stompConnectFrame = "CONNECT\naccept-version:1.0\nhost:${uri}\n\n\u0000"
                send(stompConnectFrame)
            }

            override fun onMessage(message: String) {

                // Handle received messages here
                println("Received message: $message")
                MessageHolder.isCaller = !message.contains("{\"type\":\"offer\"")

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                println("Connection closed. Code: $code, Reason: $reason")
            }

            override fun onError(ex: Exception) {
                println("Error: ${ex.message}")
            }

        }
        this.webSocketClient = webSocketClient
        webSocketClient.connectBlocking()

    }


    fun send(jsonMessage: String, destination: String) {
        val stompSendFrame =
            "SEND\ndestination:${destination}\ncontent-type:application/json\n\n${jsonMessage}\u0000"
        webSocketClient?.send(stompSendFrame)
    }

    fun subscribe(topicName: String) {
        val stompSubscribeFrame = "SUBSCRIBE\nid:0\ndestination:${topicName}\n\n\u0000"
        webSocketClient?.send(stompSubscribeFrame)
    }

    fun unsubscribe(topicName: String) {
        val stompUnsubscribeFrame = "UNSUBSCRIBE\ndestination:${topicName}\n\n\u0000"
        webSocketClient?.send(stompUnsubscribeFrame)
    }

}