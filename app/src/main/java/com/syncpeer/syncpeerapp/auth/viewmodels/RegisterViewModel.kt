package com.syncpeer.syncpeerapp.auth.viewmodels

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.api.ApiService
import com.syncpeer.syncpeerapp.auth.api.AuthRequestResponse
import com.syncpeer.syncpeerapp.auth.utils.FormValidator
import com.syncpeer.syncpeerapp.auth.utils.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    val firstNameEditText = ObservableField<String>()
    val lastNameEditText = ObservableField<String>()
    val emailEditText = ObservableField<String>()
    val passwordEditText = ObservableField<String>()
    val confirmPasswordEditText = ObservableField<String>()

    val passwordError = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()

    val hasAcceptedTOS = ObservableField<Boolean>()
    val hasNetworkFailed = MutableLiveData<Boolean>()
    val jwt = MutableLiveData<String>()
    val isAlreadyTaken = MutableLiveData<Boolean>()

    private val registerResponseLiveData = MutableLiveData<AuthRequestResponse.Response>()
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var apiService: ApiService

    fun register() {
        var isFailure = false

        if (!FormValidator.checkEmail(emailEditText.get().toString())) {
            emailError.postValue(ResourceProvider.getString(R.string.the_e_mail_address_is_not_valid))
            isFailure = true
        }

        if (!FormValidator.checkPassword(
                passwordEditText.get().toString(),
                confirmPasswordEditText.get().toString()
            )
        ) {
            passwordError.postValue(ResourceProvider.getString(R.string.passwords_should_match_and_have_at_least_10_characters))
            isFailure = true
        }

        if (isFailure)
            return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("RegisterViewModel", emailEditText.get().toString())
                val response = apiService.registerUser(
                    AuthRequestResponse.RegisterRequest(
                        firstNameEditText.get().toString(),
                        lastNameEditText.get().toString(),
                        emailEditText.get().toString(),
                        passwordEditText.get().toString()

                    )
                )
                if (response.isSuccessful) {
                    registerResponseLiveData.postValue(response.body())
                    jwt.postValue(response.body()?.token)
                } else {
                    jwt.postValue("")
                    isAlreadyTaken.postValue(response.body()?.isAlreadyTaken)
                }

            } catch (e: Exception) {
                hasNetworkFailed.postValue(true)
            }
        }
    }

}