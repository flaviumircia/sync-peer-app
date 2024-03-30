package com.syncpeer.syncpeerapp.auth

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.compose.CircleWithImage
import com.syncpeer.syncpeerapp.themes.DescriptionText
import com.syncpeer.syncpeerapp.themes.HeadlineText
import com.syncpeer.syncpeerapp.themes.LightBlueButton
import com.syncpeer.syncpeerapp.themes.SyncPeerAppTheme

class SetProfileActivity : ComponentActivity() {

    private lateinit var context: Context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = applicationContext;


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
        }

    }

}
