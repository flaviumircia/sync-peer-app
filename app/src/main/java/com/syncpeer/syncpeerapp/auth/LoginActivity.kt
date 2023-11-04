package com.syncpeer.syncpeerapp.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.syncpeer.syncpeerapp.BuildConfig
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.api.ApiService
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.auth.utils.InstantiateJwtSharedPreference
import com.syncpeer.syncpeerapp.auth.viewmodels.LoginViewModel
import com.syncpeer.syncpeerapp.databinding.ActivityLoginBinding
import com.syncpeer.syncpeerapp.videocall.HomeActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private fun storeJwtToSharedPreference(loginActivity: Activity, jwt: String) {

        val sharedPreference = InstantiateJwtSharedPreference(
            loginActivity,
            Constants.JWT_FILE_NAME
        ).getSharedPreferences()

        sharedPreference.edit().putString(Constants.SHARED_PREFERENCES_JWT_NAME, jwt)
    }

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

        viewModel.hasNetworkFailed.observe(this) { message ->
            message?.let {
                if (it)
                    Toast.makeText(
                        this,
                        getString(R.string.there_is_a_network_error_please_check_your_connection_and_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        // check if the jwt came back empty
        viewModel.jwt.observe(this) { message ->
            message?.let {

                if (it.contentEquals("") || it == null)
                    Toast.makeText(
                        this,
                        getString(R.string.wrong_email_or_password),
                        Toast.LENGTH_SHORT
                    ).show()

                else {
                    storeJwtToSharedPreference(this, it)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}