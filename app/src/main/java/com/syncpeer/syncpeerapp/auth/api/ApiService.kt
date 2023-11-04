package com.syncpeer.syncpeerapp.auth.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/authenticate")
    suspend fun loginUser(@Body request: AuthRequestResponse.LoginRequest): Response<AuthRequestResponse.Response>

    @POST("api/v1/auth/register")
    suspend fun registerUser(@Body request: AuthRequestResponse.RegisterRequest): Response<AuthRequestResponse.Response>

}