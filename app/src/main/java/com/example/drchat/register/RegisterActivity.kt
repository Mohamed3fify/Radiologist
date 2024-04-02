package com.example.drchat.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drchat.R
import com.example.drchat.chatBot.ChatBotActivity
import com.example.drchat.register.ui.theme.DrChatTheme
import com.example.drchat.utils.ChatAuthButton
import com.example.drchat.utils.ChatAuthTextField
import com.example.drchat.utils.ChatToolbar
import com.example.drchat.utils.LoadingDialog

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                RegisterContent(onRegistrationSuccess = {
                    finishAffinity()
                }) {
                    finish()
                }

            }
        }
    }
}

@Composable
fun RegisterContent(
    viewModel: RegisterViewModel = viewModel(),
    onRegistrationSuccess: () -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(topBar = {
        ChatToolbar(title = stringResource(id = R.string.register)) {
            onFinish()
        }
    }) { paddingValues ->
        paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(Modifier.fillMaxHeight(0.35F))
            ChatAuthTextField(
                state = viewModel.firstNameState,
                error = viewModel.firstNameErrorState.value,
                label = stringResource(
                    R.string.first_name
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChatAuthTextField(
                state = viewModel.emailState,
                error = viewModel.emailErrorState.value, label = stringResource(id = R.string.email)

            )
            Spacer(modifier = Modifier.height(8.dp))
            ChatAuthTextField(
                state = viewModel.passwordState,
                error = viewModel.passwordErrorState.value,
                label = stringResource(id = R.string.password),
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))
            ChatAuthButton(title = stringResource(R.string.create_account), isEnabled = false) {
                viewModel.register()
            }
        }
    }
    TriggerEvent(event = viewModel.events.value) {
        onRegistrationSuccess()
    }
    LoadingDialog(isLoading = viewModel.isLoading)
}

@Composable
fun TriggerEvent(
    event: RegisterEvent,
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current

    when (event) {
        RegisterEvent.Idle -> {}
        is RegisterEvent.NavigateToChatBot -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            context.startActivity(intent)
            onRegisterSuccess()
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterContent(onRegistrationSuccess = {}) {}
}
