package com.syncpeer.syncpeerapp.videocall.utils

import org.webrtc.SessionDescription

data class SdpOfferDto(val destination:String, val source: String, val sdpMessage: SessionDescription)
