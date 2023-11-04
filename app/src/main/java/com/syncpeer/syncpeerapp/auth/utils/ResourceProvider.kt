package com.syncpeer.syncpeerapp.auth.utils

import android.content.res.Resources

object ResourceProvider {
    private var resources: Resources? = null

    fun initialize(resources: Resources) {
        this.resources = resources
    }

    fun getString(resId: Int): String {
        return resources?.getString(resId) ?: ""
    }
}