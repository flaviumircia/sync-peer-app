package com.syncpeer.syncpeerapp.onboarding.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton

@BindingAdapter("app:icon")
fun setButtonIcon(button: MaterialButton, iconResId: Int) {
    button.setIconResource(iconResId)
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}