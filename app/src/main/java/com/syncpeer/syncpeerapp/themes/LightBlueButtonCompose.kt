package com.syncpeer.syncpeerapp.themes

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LightBlueButton(context: Context, text: String, onClick: () -> Unit) {
    val colorSchema = ColorSchema(context)
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(colorSchema.primary),
            contentColor = Color.White
        ),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
    ) {
        Text(style = StandardFonts.getDescriptionHintStyle(),text = text)
    }
}
