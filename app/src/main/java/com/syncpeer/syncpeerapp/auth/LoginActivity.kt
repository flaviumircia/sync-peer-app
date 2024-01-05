package com.syncpeer.syncpeerapp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private fun storeJwtToSharedPreference(loginActivity: Activity, jwt: String) {

        val sharedPreference = InstantiateJwtSharedPreference(
            loginActivity,
            Constants.JWT_FILE_NAME
        ).getSharedPreferences()

        getSharedPreferences(Constants.USER_EMAIL, Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.USER_EMAIL,decodePayloadJwt(jwt))
            .apply()

        sharedPreference.edit().putString(Constants.SHARED_PREFERENCES_JWT_NAME, jwt).apply()
    }
    private fun decodePayloadJwt(jwt: String): String? {

        // Decode the JWT
        val jwtParts = jwt.split(".")
        val jwtPayload = jwtParts[1]

        // Decode the JWT payload (Base64)
        val decodedBytes = Base64.decode(jwtPayload,Base64.DEFAULT)
        val decodedString = String(decodedBytes)
        val jsonObject = JSONObject(decodedString)
        return jsonObject.optString("sub")
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