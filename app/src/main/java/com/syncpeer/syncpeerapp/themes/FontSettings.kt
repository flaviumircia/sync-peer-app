package com.syncpeer.syncpeerapp.themes

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.syncpeer.syncpeerapp.R

object StandardFonts {
    private fun generateFontFamily(fontId: Int): FontFamily {
        val customFont =
            Font(fontId)
        return FontFamily(customFont)
    }

    fun getHeadlineStyle(): TextStyle {
        return TextStyle(
            fontFamily = generateFontFamily(R.font.montserrat_bold),
            fontSize = 36.sp, // Set the font size
        )
    }

    fun getDescriptionHintStyle(): TextStyle {
        return TextStyle(
            fontFamily = generateFontFamily(R.font.montserrat_medium),
            fontSize = 16.sp, // Set the font size
        )
    }
}