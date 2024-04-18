package com.example.drchat.logIn


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.drchat.logIn.google.GoogleAuthUiClient
import com.example.drchat.register.RegisterActivity
import com.example.drchat.ui.theme.DrChatTheme
import com.example.drchat.ui.theme.Grey
import com.example.drchat.utils.ChatAuthButton
import com.example.drchat.utils.ChatAuthTextField
import com.example.drchat.utils.GoogleSignInButton
import com.example.drchat.utils.LoadingDialog
import com.example.drchat.utils.Toolbar
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
                loginContent{
                }
            }
        }
    }
}

@Composable
fun loginContent(
    viewModel: LoginViewModel   = viewModel(),
    onFinish: () -> Unit
) {

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
        chatBotLauncher.launch(chatBotIntent)
    }
    val isLoading = remember { mutableStateOf(false) }

    val launcher  =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)

            try {
                
                account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                Firebase.auth.signInWithCredential(credential).addOnCompleteListener{signInTask ->
                    isLoading.value =true
                    if (signInTask.isSuccessful) {
                        // Sign-in with Google successful, now launch the chatbot activity
                        account = GoogleSignIn.getLastSignedInAccount(context)
                        launchChatBotActivity()

                    } else {
                        Log.e("TAG", "Google Sign in Failed")
                    }
                }

            } catch (e : ApiException){
                Log.w("TAG" , "Google Sign in Failed" , e)

            }
        }
    DisposableEffect(key1 = loginSuccess) {
        if (loginSuccess) {
            isLoading.value = false
        }
        onDispose { }
    }
    LoadingDialog(isLoading)

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

            Spacer(modifier = Modifier.fillMaxHeight(0.10F)) // Add padding below ChatToolbar
            Box {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(90.dp)

                )
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.10F))
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
                )
                TextButton(
                    onClick = {
                        viewModel.navigateToRegister()
                    },
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.White, thickness = 1.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "or",
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.White, thickness = 1.dp)

        }
            GoogleSignInButton(
                state = state,
                onClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
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
    loginContent{

    }
}
