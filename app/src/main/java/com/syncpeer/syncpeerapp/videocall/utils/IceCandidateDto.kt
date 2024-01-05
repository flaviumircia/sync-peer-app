package com.syncpeer.syncpeerapp.videocall.utils

import org.webrtc.IceCandidate

data class IceCandidateDto(val source: String, val destination:String, val iceCandidate: IceCandidate)
