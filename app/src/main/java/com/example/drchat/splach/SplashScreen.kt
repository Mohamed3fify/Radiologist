package com.example.drchat.splach

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drchat.Constants
import com.example.drchat.R
import com.example.drchat.chatBot.ChatBotActivity
import com.example.drchat.logIn.LoginActivity
import com.example.drchat.ui.theme.DrChatTheme
import com.example.drchat.ui.theme.Grey

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                SplachContect {
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplachContect(viewModel: SplachViewModel = viewModel(), onFinish: () -> Unit) {

    LaunchedEffect(key1 = Unit) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewModel.navigate()
            }, 1000
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App logo"
        )
    }
    TriggerEvents(event = viewModel.event.value) {
        onFinish()
    }
}

@Composable
fun TriggerEvents(
    event: SplachEvent,
    viewModel: SplachViewModel = viewModel(),
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    when (event) {
        SplachEvent.Idle -> {}
        is SplachEvent.NavigateToChatBot -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            intent.putExtra(Constants.USER_KEY, event.user)
            context.startActivity(intent)
            onFinish()
        }

        SplachEvent.NavigateToLogin -> {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            onFinish()
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SplachPreview() {
    SplachContect {}
}

