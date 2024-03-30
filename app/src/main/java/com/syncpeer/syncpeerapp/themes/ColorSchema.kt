package com.syncpeer.syncpeerapp.themes

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.syncpeer.syncpeerapp.R

class ColorSchema(private val context:Context){
    val primary = ContextCompat.getColor(context,R.color.purple_200) // Corresponds to @color/purple_200
    val primaryVariant = ContextCompat.getColor(context,R.color.purple_700) // Corresponds to @color/purple_700
    val secondary = Color.White // Corresponds to @color/white
    val secondaryVariant = ContextCompat.getColor(context,R.color.deep_blue) // Corresponds to @color/teal_200
    val background = ContextCompat.getColor(context, R.color.deep_blue) // Corresponds to @color/deep_blue
    val surface = ContextCompat.getColor(context,R.color.transparent) // Corresponds to @color/deep_blue
    val error = Color(0xFFA3001E)
    val onPrimary = Color.White // Corresponds to @color/black
    val onSecondary = Color.Black // Corresponds to @color/black
    val onBackground = Color.White // Corresponds to @color/white
    val onSurface = Color.White // Corresponds to @color/white
    val onError = Color.White
    val isLight = false // Define whether the theme is light or dark
}