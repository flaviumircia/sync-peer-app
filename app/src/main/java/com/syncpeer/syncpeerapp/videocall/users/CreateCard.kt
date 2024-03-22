package com.syncpeer.syncpeerapp.videocall.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CreateCard(
    imageSource:Int,
    imageSize: Int,
    padding: Int,
    backgroundColor: Color,
    nameText: String,
    smallMessage: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(imageSize.dp)
                .padding(padding.dp)
                .background(backgroundColor, shape = CircleShape),
        ) {
            Image(
                painter = painterResource(id = imageSource),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Column(
            Modifier.padding(8.dp)
        ) {
            Text(
                text = nameText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if(smallMessage != ""){
                Text(
                    text = smallMessage,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

        }
    }
}