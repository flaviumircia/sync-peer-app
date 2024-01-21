package com.syncpeer.syncpeerapp.videocall

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.syncpeer.syncpeerapp.R
import com.syncpeer.syncpeerapp.auth.utils.Constants
import com.syncpeer.syncpeerapp.videocall.users.SelectUserDto
import com.syncpeer.syncpeerapp.videocall.users.UserCard

class HomeActivity : AppCompatActivity() {
    private var list: List<SelectUserDto>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val email = applicationContext
            .getSharedPreferences(Constants.USER_EMAIL, MODE_PRIVATE)
            .getString(Constants.USER_EMAIL, null)
        val listOfUsers = listOf(
            SelectUserDto(
                0,
                "test_d@gmail.com",
                "test",
                R.drawable.check_icon
            ),
            SelectUserDto(
                1,
                "test@gmail.com",
                "test2",
                R.drawable.check_icon
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
        list.let {
            if (it != null) {
                allUsers(
                    userList = it
                )
            }
        }
    }


    @Composable
    fun allUsers(userList: List<SelectUserDto>) {
        Scaffold(bottomBar = {}) {
            it.calculateBottomPadding()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
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
                            "\uD83C\uDF3FCall any user you want!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
                items(userList) { user ->
                    UserCard(
                        name = user.name,
                        lastMessage = user.lastMessage,
                        image = user.imageRes,
                        onClick = {
                            val intent = Intent(this@HomeActivity, VideoCallActivity::class.java)
                            intent.putExtra("destination_mail", user.name)
                            startActivity(intent)
                        })

                }
            }
        }

    }
}