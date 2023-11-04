package com.syncpeer.syncpeerapp.videocall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.auth.utils.InstantiateJwtSharedPreference

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val sharedPreferences = InstantiateJwtSharedPreference(this,Constants.JWT_FILE_NAME).getSharedPreferences()
        val tempJWT = sharedPreferences.getString(Constants.SHARED_PREFERENCES_JWT_NAME,null)
        val textView: TextView = findViewById(R.id.textView)
        textView.text = tempJWT.toString()
    }
}