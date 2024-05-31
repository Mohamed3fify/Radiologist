package com.example.radiologist.logIn


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.radiologist.register.RegisterActivity
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.main_app
import com.example.radiologist.utils.ChatAuthButton
import com.example.radiologist.utils.ChatAuthTextField
import com.example.radiologist.utils.GoogleSignInButton
import com.example.radiologist.utils.LoadingDialog
import com.example.radiologist.utils.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                loginContent(onLogInSuccess = {finishAffinity()}){
                }
            }
        }
    }
}

@Composable
fun loginContent(
    viewModel: LoginViewModel   = viewModel(),
    onLogInSuccess: () -> Unit,
    onFinish: () -> Unit
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    var showLoginSuccessDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val context  = LocalContext.current
    val token  = stringResource(id = R.string.google_client_id)
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context,gso)
    var account : GoogleSignInAccount? by remember { mutableStateOf(null) }
    val chatBotLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    fun launchChatBotActivity() {
        val chatBotIntent = Intent(context, ChatBotActivity::class.java)
        chatBotIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        chatBotLauncher.launch(chatBotIntent)
        (context as Activity).finishAffinity()
            showLoginSuccessDialog = true
    }
    val isLoading = remember { mutableStateOf(false) }

    val launcher  =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                Firebase.auth.signInWithCredential(credential).addOnCompleteListener{signInTask ->

                    if (signInTask.isSuccessful) {
                        account = GoogleSignIn.getLastSignedInAccount(context)
                        launchChatBotActivity()
                        Toast.makeText(context, "Sign in successfully", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.e("TAG", "Google Sign in Failed")
                        Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()

                    }
                }

            } catch (e : ApiException){
                Log.w("TAG" , "Google Sign in Failed" , e)

            }

        }
    LoadingDialog(isLoading)

    Scaffold(topBar = {
        Toolbar(title = "")

    })
    { paddingValues ->
        paddingValues

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) bg_dark else Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
      Image(
          painter = painterResource(if (isSystemInDarkTheme) R.drawable.logo_radiologist_dark else R.drawable.logo_radiologist_light),
          contentDescription = null ,
          modifier =
          Modifier
              .size(90.dp)
              .padding(top = 20.dp)
      )
            Spacer(modifier = Modifier.fillMaxHeight(0.1F))
            Text(
                text = "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme) Color.White else Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))
            ChatAuthTextField(
                state = viewModel.emailState,
                error = viewModel.emailErrorState.value,
                hint = stringResource(R.string.email) ,
                icon = Icons.Filled.Email,
            )
            Spacer(modifier = Modifier.height(15.dp))
            ChatAuthTextField(
                state = viewModel.passwordState,
                error = viewModel.passwordErrorState.value,
                hint = stringResource(R.string.password) ,
                icon = Icons.Filled.Lock,
                isPassword = true,
            )
            Spacer(modifier = Modifier.height(15.dp))
            ChatAuthButton(title = stringResource(id = R.string.login)) {
                viewModel.login()
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "or",
                color = if (isSystemInDarkTheme) Color.White else Color.Black,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GoogleSignInButton(
                state = state,
                onClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Text(
                    text = "Don't have an account ?",
                    color = if (isSystemInDarkTheme) Color.White else Color.Black,
                    fontSize = 17.sp
                )
                TextButton(
                    onClick = {
                        viewModel.navigateToRegister()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        color = main_app,
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
                        viewModel.resetLoginSuccess()
                        onFinish()
                        showLoginSuccessDialog = false
                    },
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
                        },
                    ) {
                        Text("Create Account")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.resetEvent()
                            onFinish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Try again ")
                    }
                }

            )

        }

        is LoginEvent.LoginFailedEmailNotVerified -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetEvent() },
                title = { Text("Login Failed") },
                text = { Text("Check inbox and verify your email ") },

                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetEvent()
                            onFinish()
                        }
                    ) {
                        Text("ok")
                    }
                }

            )
        }

        is LoginEvent.NavigateToChatBot -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            (context as Activity).finishAffinity()
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
    loginContent(onLogInSuccess = {}){}
}
