package com.example.radiologist.register


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drchat.R
import com.example.radiologist.chatBot.ChatBotActivity
import com.example.radiologist.logIn.LoginActivity
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.main_app
import com.example.radiologist.utils.ChatAuthButton
import com.example.radiologist.utils.ChatAuthTextField
import com.example.radiologist.utils.CustomDialog
import com.example.radiologist.utils.LoadingDialog
import com.example.radiologist.utils.Toolbar

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
    onFinish: () -> Unit,
) {
    val accountAlreadyExists by viewModel.accountAlreadyExists.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            Toolbar(title = "" , onNavigationIconClick = onFinish)
        }) { paddingValues ->
        paddingValues

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) bg_dark else Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(if (isSystemInDarkTheme) R.drawable.logo_radiologist_dark else R.drawable.logo_radiologist_light),
                contentDescription = null,
                modifier =
                Modifier
                    .size(90.dp)
                    .padding(top = 20.dp)
            )
            Spacer(Modifier.fillMaxHeight(0.10F))
            Text(
                text = "Create an account ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme) Color.White else Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))
            ChatAuthTextField(
                state = viewModel.firstNameState,
                error = viewModel.firstNameErrorState.value,
                hint = stringResource(R.string.userName),
                icon = Icons.Filled.AccountCircle,
            )

            Spacer(modifier = Modifier.height(15.dp))
            ChatAuthTextField(
                state = viewModel.emailState,
                error = viewModel.emailErrorState.value,
                hint = stringResource(id = R.string.email),
                icon = Icons.Filled.Email,
            )

            Spacer(modifier = Modifier.height(15.dp))
            ChatAuthTextField(
                state = viewModel.passwordState,
                error = viewModel.passwordErrorState.value,
                hint = stringResource(id = R.string.password),
                icon = Icons.Filled.Lock,
                isPassword = true,

                )

            Spacer(modifier = Modifier.height(15.dp))
            ChatAuthTextField(
                state = viewModel.confirmPasswordState,
                error = viewModel.confirmPasswordErrorState.value,
                hint = stringResource(id = R.string.confirm_password),
                icon = Icons.Filled.Lock,
                isPassword = true,
            )
            Spacer(
                modifier = Modifier.height(14.dp),
            )
            ChatAuthButton(
                title = stringResource(R.string.create_account),

                ) {

                viewModel.register()

            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Text(
                    text = "Already have an account ?",
                    color = if (isSystemInDarkTheme) Color.White else Color.Black,
                    fontSize = 17.sp
                )

                TextButton(
                    onClick = {
                        viewModel.navigateToLogin()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        color = main_app,
                        fontSize = 17.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        TriggerEvent(event = viewModel.events.value) {
            onRegistrationSuccess()
        }
        LoadingDialog(isLoading = viewModel.isLoading)

        if (accountAlreadyExists) {
            CustomDialog(
                title = "Account Already Exists",
                message = "An account with this email already exists.",
                onDismiss = {
                    viewModel.resetEvent()
                    viewModel.resetAccountAlreadyExists()
                }
            )
        }

    }
}

@Composable
fun TriggerEvent(
    event: RegisterEvent,
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,

    ) {
    val context = LocalContext.current
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()

    when (event) {
        RegisterEvent.Idle -> {}
        is RegisterEvent.NavigateToChatBot -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            context.startActivity(intent)
            onRegisterSuccess()
        }

        RegisterEvent.NavigateToLogin -> {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)

            if (registrationSuccess) {
                Toast.makeText(
                    context, "Registration successful , Email verification link sent , Check your email",
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.resetEvent()
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterContent(onRegistrationSuccess = {}) {}
}
