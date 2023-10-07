package com.syncpeer.syncpeerapp.auth.login_api

class LoginRequestResponse {


    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class LoginResponse(
        val token: String,
        // Other response properties
    )

}