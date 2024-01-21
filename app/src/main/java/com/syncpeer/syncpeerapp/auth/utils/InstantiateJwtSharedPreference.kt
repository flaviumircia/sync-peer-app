package com.syncpeer.syncpeerapp.auth.utils

import android.app.Activity
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class InstantiateJwtSharedPreference(activity: Activity, fileName: String) {

    private val context = activity
    private val fileName = fileName

    fun getSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}