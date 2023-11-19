package com.syncpeer.syncpeerapp.videocall.websocket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
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
}