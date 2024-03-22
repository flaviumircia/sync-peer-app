package com.syncpeer.syncpeerapp.videocall

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.themes.SyncPeerAppTheme
import com.syncpeer.syncpeerapp.videocall.users.CreateCard
import com.syncpeer.syncpeerapp.videocall.users.SelectUserDto
import com.syncpeer.syncpeerapp.videocall.users.UserCard

class HomeActivity : AppCompatActivity() {
    private var list: List<SelectUserDto>? = null
    private lateinit var profile: SelectUserDto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val email = applicationContext
            .getSharedPreferences(Constants.USER_EMAIL, MODE_PRIVATE)
            .getString(Constants.USER_EMAIL, null)
        profile = SelectUserDto(0, "Test Nickname", "", R.drawable.account_icon)
        val listOfUsers = listOf(
            SelectUserDto(
                0,
                "test_d@gmail.com",
                "test",
                R.drawable.account_icon
            ),
            SelectUserDto(
                1,
                "test@gmail.com",
                "test2",
                R.drawable.account_icon
            )
        )
        list = listOfUsers.filter { user -> user.name != email }
        setContent {
            SelectUserScreen()
        }
    }


    @Composable
    @Preview
    fun SelectUserScreen() {
        SyncPeerAppTheme {
            list.let {
                if (it != null)
                    RecentConversations(userList = it)
            }
        }

    }


    @Composable
    fun RecentConversations(userList: List<SelectUserDto>) {
        Scaffold(bottomBar = {}) {
            it.calculateBottomPadding()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CreateCard(
                    profile.imageRes,
                    90,
                    6,
                    Color.LightGray,
                    profile.name,
                    ""
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 100.dp),
                contentPadding = PaddingValues(16.dp)
            ) {

                item {
                    CreateHeadline()

                }
                items(userList) { user ->
                    UserCard(
                        name = user.name,
                        lastMessage = user.lastMessage,
                        image = user.imageRes,
                        onClick = {
                            startActivityOnClick(user = user)
                        })

                }
            }
        }

    }

    private fun startActivityOnClick(user: SelectUserDto) {
        val intent = Intent(this@HomeActivity, VideoCallActivity::class.java)
        intent.putExtra("destination_mail", user.name)
        startActivity(intent)
    }

    @Composable
    private fun CreateHeadline() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 25.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                "Recent Conversations",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = MaterialTheme.colorScheme.onBackground)
                .padding(horizontal = 16.dp)
        )
    }


}