package com.syncpeer.syncpeerapp.auth.viewmodels

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.syncpeer.syncpeerapp.auth.login_api.ApiService
import com.syncpeer.syncpeerapp.auth.login_api.LoginRequestResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    val emailEditText = ObservableField<String>()
    val passwordEditText = ObservableField<String>()
    val loginResponseLiveData = MutableLiveData<LoginRequestResponse.LoginResponse>()
    lateinit var apiService:ApiService
//TODO: CHANGE THIS
    @OptIn(DelicateCoroutinesApi::class)
    fun login() {
        GlobalScope.launch {
                val response = apiService.loginUser(LoginRequestResponse.LoginRequest(emailEditText.get().toString(),passwordEditText.get().toString()))
                if (response.isSuccessful) {
                    loginResponseLiveData.value = response.body()
                } else {
                    Log.d("AUTH COIN", "login: "+ response.message().toString())
                }
            }
        }


}