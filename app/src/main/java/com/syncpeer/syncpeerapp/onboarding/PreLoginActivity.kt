package com.syncpeer.syncpeerapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.LoginActivity
import com.syncpeer.syncpeerapp.auth.RegisterActivity
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton

class PreLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_login)

        val connectButton = findViewById<ThemedButton>(R.id.connectButton)
        val registerButton = findViewById<ThemedButton>(R.id.registerButton)

        connectButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}