package com.syncpeer.syncpeerapp.videocall.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun UserCard(name: String, lastMessage: String, image: Int, onClick: () -> Unit) {
    val picture_size = 80
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick.invoke() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        CreateCard(image, picture_size, 8, Color.LightGray, name, lastMessage)
    }
}