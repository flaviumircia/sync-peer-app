package com.syncpeer.syncpeerapp.themes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HeadlineText(text:String){
    Text(
        text = text,
        style = StandardFonts.getHeadlineStyle(),
        modifier = Modifier.padding(30.dp),
        color = Color.White
    )
}

@Composable
fun DescriptionText(text:String){
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = StandardFonts.getDescriptionHintStyle(),
        color = Color.White,
        modifier = Modifier
            .padding(horizontal = 16.dp),
    )
}