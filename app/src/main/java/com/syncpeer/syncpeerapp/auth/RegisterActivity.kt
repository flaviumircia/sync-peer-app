package com.syncpeer.syncpeerapp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.syncpeer.syncpeerapp.BuildConfig
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.api.ApiService
import com.syncpeer.syncpeerapp.auth.utils.ResourceProvider
import com.syncpeer.syncpeerapp.auth.viewmodels.RegisterViewModel
import com.syncpeer.syncpeerapp.databinding.ActivityRegisterBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ResourceProvider.initialize(resources)
        val viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.viewModelRegister = viewModel  // Set the view model variable
        viewModel.apiService = retrofit.create(ApiService::class.java)


        viewModel.jwt.observe(this) { message ->
            message?.let {

                if (it.contentEquals(""))
                    Toast.makeText(
                        this,
                        getString(R.string.wrong_email_or_password),
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        viewModel.isAlreadyTaken.observe(this) { message ->
            message?.let {
                if (it)
                    Toast.makeText(
                        this,
                        getString(R.string.error_the_e_mail_is_already_registered),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
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

    }
}