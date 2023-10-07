package com.syncpeer.syncpeerapp.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.syncpeer.syncpeerapp.BuildConfig
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.login_api.ApiService
import com.syncpeer.syncpeerapp.auth.viewmodels.LoginViewModel
import com.syncpeer.syncpeerapp.databinding.ActivityLoginBinding
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.viewModelLogin = viewModel  // Set the view model variable
        viewModel.apiService = retrofit.create(ApiService::class.java)

    }

}