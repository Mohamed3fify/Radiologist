package com.example.drchat.logIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.drchat.chatBot.ChatBotActivity
import com.example.drchat.register.RegisterActivity
import com.example.drchat.ui.theme.DrChatTheme
import com.example.drchat.ui.theme.Grey
import com.example.drchat.utils.ChatAuthButton
import com.example.drchat.utils.ChatAuthTextField
import com.example.drchat.utils.LoadingDialog
import com.example.drchat.utils.Toolbar


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                loginContent {

                }
            }
        }
    }
}

@Composable
fun loginContent(viewModel: LoginViewModel = viewModel(), onFinish: () -> Unit) {
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    var showLoginSuccessDialog by remember { mutableStateOf(false) }


    fun String.getIcon(): ImageVector {
        return when (this) {
            "first name" -> Icons.Filled.AccountCircle
            "email" -> Icons.Filled.Email
            "Password" -> Icons.Filled.Lock
            else -> Icons.Default.AccountCircle
        }
    }

    Scaffold(topBar = {
        Toolbar(title = stringResource(id = R.string.login))

    })
    { paddingValues ->
        paddingValues

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(Grey),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.fillMaxHeight(0.15F)) // Add padding below ChatToolbar
            Box {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App logo",
                    //contentScale = ContentScale.Crop, // Adjust content scale as needed
                    modifier = Modifier.size(120.dp)

                )
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.20F))
            Text(
                text = "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 25.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
            ChatAuthTextField(
                state = viewModel.emailState,
                error = viewModel.emailErrorState.value,
                label = stringResource(R.string.email) to stringResource(R.string.email).getIcon()
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChatAuthTextField(
                state = viewModel.passwordState,
                error = viewModel.passwordErrorState.value,
                label = stringResource(R.string.password) to stringResource(R.string.password).getIcon(),
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChatAuthButton(title = stringResource(id = R.string.login)) {
                viewModel.login()
            }
            Spacer(modifier = Modifier.height(13.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Text(
                    text = "Don't have an account ?",
                    color = Color.White,
                    fontSize = 17.sp
                    //modifier = Modifier.padding(start = 8.dp)
                )

                // Spacer(modifier = Modifier.width(16.dp)) // Add space between text and button

                TextButton(
                    onClick = {
                        viewModel.navigateToRegister()
                    },
                    // modifier = Modifier.padding(1.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        color = Color.Red,
                        fontSize = 17.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }


        }
    }
    TriggerEvents(event = viewModel.events.value) {
            onFinish()
    }

    LoadingDialog(isLoading = viewModel.isLoading)
    if (loginSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetEvent()
                showLoginSuccessDialog = false

                },
            title = { Text("Login Successful") },
            text = { Text("You have successfully logged in.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetEvent()
                        viewModel.resetLoginSuccesss()
                        onFinish()
                        showLoginSuccessDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun TriggerEvents(
    event: LoginEvent,
    viewModel: LoginViewModel = viewModel(),
    onFinish: () -> Unit
) {
    val context = LocalContext.current


    when (event) {
        LoginEvent.Idle -> {}

        is LoginEvent.LoginFailed -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetEvent() },
                title = { Text("Login Failed") },
                text = { Text("Email or Password is incorrect , try again or create a new account ") },

                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.navigateToRegister()
                        }
                    ) {
                        Text("Create Account")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.resetEvent()
                            onFinish()
                        }
                    ) {
                        Text("Try again ")
                    }
                }
            )
        }

        is LoginEvent.NavigateToChatBot -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            context.startActivity(intent)
            onFinish()
        }

        LoginEvent.NavigateToRegister -> {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
            viewModel.resetEvent()
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun LoginPreview() {
    loginContent {

    }
}
