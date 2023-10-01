package com.syncpeer.syncpeerapp.onboarding.adapters

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.syncpeer.syncpeerapp.R

@BindingAdapter("app:icon")
fun setButtonIcon(button: MaterialButton, iconResId: Int) {
    val context = button.context

    val iconDrawable = if(iconResId!=0)
            AppCompatResources.getDrawable(context, iconResId)
        else
            null

    button.icon = iconDrawable

    val color = ContextCompat.getColor(context, R.color.light_blue)
    val mode = PorterDuff.Mode.SRC_IN
    val colorFilter = PorterDuffColorFilter(color, mode)

    if (iconDrawable != null) {
        iconDrawable.colorFilter = colorFilter
    }

}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}