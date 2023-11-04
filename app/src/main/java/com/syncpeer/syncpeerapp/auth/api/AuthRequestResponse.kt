package com.syncpeer.syncpeerapp.auth.api

class AuthRequestResponse {

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class RegisterRequest(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    )

    data class Response(
        val token: String,
        val isAlreadyTaken: Boolean
        // Other response properties
    )

}