package com.syncpeer.syncpeerapp.auth.viewmodels

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.syncpeer.syncpeerapp.auth.api.ApiService
import com.syncpeer.syncpeerapp.auth.api.AuthRequestResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val emailEditText = ObservableField<String>()
    val passwordEditText = ObservableField<String>()
    private val loginResponseLiveData = MutableLiveData<AuthRequestResponse.Response>()
    val jwt = MutableLiveData<String?>()
    val hasNetworkFailed = MutableLiveData<Boolean>()

    lateinit var apiService: ApiService
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.IO)


    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.loginUser(
                    AuthRequestResponse.LoginRequest(
                        emailEditText.get().toString(),
                        passwordEditText.get().toString()
                    )
                )
                if (response.isSuccessful) {

                    loginResponseLiveData.postValue(response.body())
                    jwt.postValue(response.body()?.token)
                } else {
                    jwt.postValue("")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "login: ", e)
                hasNetworkFailed.postValue(true)
            }
        }
    }


}