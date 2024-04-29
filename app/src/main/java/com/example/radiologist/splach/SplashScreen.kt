package com.example.radiologist.splach

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.radiologist.model.Constants
import com.example.drchat.R
import com.example.radiologist.chatBot.ChatBotActivity
import com.example.radiologist.logIn.LoginActivity
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.bg_light

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
fun SplachContect(
    viewModel: SplashViewModel = viewModel(),
    onFinish: () -> Unit,
) {
    val logoVisible = remember { mutableStateOf(true) }
    LaunchedEffect(key1 = Unit) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewModel.navigate()
                logoVisible.value = false

            },
            1000,
        )
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) bg_dark else bg_light),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


            Image(
                painter = painterResource(if (isSystemInDarkTheme()) R.drawable.logo_radiologist_dark else R.drawable.logo_radiologist_light),
                contentDescription = "App logo",
            )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Radiologist" ,
            modifier = Modifier,
            fontSize = 30.sp ,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black ,
            fontWeight = FontWeight.Bold
        )

    }
    TriggerEvents(event = viewModel.event.value) {
        onFinish()
    }
}

@Composable
fun TriggerEvents(
    event: SplachEvent,
    viewModel: SplashViewModel = viewModel(),
    onFinish: () -> Unit,
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
private fun splachPreview() {
    SplachContect {}
}
