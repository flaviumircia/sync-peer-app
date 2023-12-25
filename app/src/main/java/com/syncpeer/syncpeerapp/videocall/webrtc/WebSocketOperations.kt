package com.syncpeer.syncpeerapp.videocall.webrtc

import org.java_websocket.client.WebSocketClient

class WebSocketOperations{
    companion object {
        fun send(webSocketClient: WebSocketClient, jsonMessage: String, destination: String) {

            val stompSendFrame =
                "SEND\ndestination:${destination}\ncontent-type:application/json\n\n${jsonMessage}\u0000"

            webSocketClient.send(stompSendFrame)
        }

        fun subscribe(webSocketClient: WebSocketClient, topicName: String) {

            val stompSubscribeFrame = "SUBSCRIBE\nid:0\ndestination:${topicName}\n\n\u0000"

            webSocketClient.send(stompSubscribeFrame)
        }

        fun unsubscribe(webSocketClient: WebSocketClient, topicName: String) {

            val stompUnsubscribeFrame = "UNSUBSCRIBE\ndestination:${topicName}\n\n\u0000"

            webSocketClient.send(stompUnsubscribeFrame)
        }
    }
}