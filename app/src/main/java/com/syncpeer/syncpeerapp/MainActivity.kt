package com.syncpeer.syncpeerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syncpeer.syncpeerapp.onboarding.OnboardingActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }
}