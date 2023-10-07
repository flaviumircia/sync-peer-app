package com.syncpeer.syncpeerapp.auth.login_api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login") // Replace with your API endpoint
    suspend fun loginUser(@Body request: LoginRequestResponse.LoginRequest): Response<LoginRequestResponse.LoginResponse>
}