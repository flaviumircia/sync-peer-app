package com.syncpeer.syncpeerapp.auth.compose

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.syncpeer.syncpeerapp.R

@Composable
fun CircleWithImage() {
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val pickMedia = pickMediaFromGallery(selectedImageUri)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(256.dp)
                .background(color = Color.White, shape = CircleShape)
                .clip(CircleShape) // Apply circle shape clipping
                .clickable {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType = "image/*")))
                },
            contentAlignment = Alignment.Center
        ) {
            // Use the selectedImageUri value to load the image, or provide a default image if no image is selected
            val painter = if (selectedImageUri.value != null) {
                rememberImagePainter(selectedImageUri.value)
            } else {
                painterResource(id = R.drawable.account_icon)
            }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(256.dp), // Adjust the size as needed
                contentScale = ContentScale.Crop // Scale the image to fill the container while preserving aspect ratio
            )
        }
    }
}

@Composable
private fun pickMediaFromGallery(selectedImageUri: MutableState<Uri?>): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri.value = uri
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    return pickMedia
}
