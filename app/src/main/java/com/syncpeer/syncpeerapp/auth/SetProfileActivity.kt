package com.syncpeer.syncpeerapp.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.compose.CircleWithImage
import com.syncpeer.syncpeerapp.themes.DescriptionText
import com.syncpeer.syncpeerapp.themes.HeadlineText
import com.syncpeer.syncpeerapp.themes.LightBlueButton
import com.syncpeer.syncpeerapp.themes.SyncPeerAppTheme

class SetProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SyncPeerAppTheme(applicationContext) {
                SetProfilePicture()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetProfilePicture() {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Make the column scrollable
    ) {
        HeadlineText(text = "Choose your \nprofile photo")

        CircleWithImage(defaultImage = R.drawable.account_icon)

        Spacer(modifier = Modifier.padding(top = 20.dp)) // Add spacer for spacing between image and text

        DescriptionText(text = "Tap on the above icon to change your profile photo.")

        Spacer(modifier = Modifier.padding(top = 20.dp)) // Add spacer for spacing between text and text field

        LightBlueButton(context = LocalContext.current, text = "Create Profile") {
            val intent = Intent(context, LoginActivity::class.java)
            launcher.launch(intent)
        }

    }

}
