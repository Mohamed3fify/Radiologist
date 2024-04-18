package com.example.drchat.utils

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.drchat.logIn.LoginViewModel
import com.example.drchat.logIn.google.SignInState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GoogleSignInButton(
    state: SignInState,
    viewModel: LoginViewModel = viewModel(),
    onClick : () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError){
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 35.dp)
            .fillMaxWidth()
    ) {

    Surface(
        shape = RoundedCornerShape(40.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .size(60.dp)
            .clickable {
                if (state.signInError != null) {
                    Toast
                        .makeText(
                            context,
                            state.signInError,
                            Toast.LENGTH_LONG
                        )
                        .show()
                } else {
                    onClick()
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Center,
            modifier = Modifier.padding( end = 30.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google icon",
                modifier = Modifier
                    .size(35.dp)

            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Sign in With Google",
                color = Color.White,
                fontSize = 16.sp,
                style = TextStyle(fontWeight = FontWeight.SemiBold)
            )
        }
    }
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
            )
        }
  }
    LaunchedEffect(state) {
        if (state.isSignInSuccessful || state.signInError != null) {
            viewModel.resetState()
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GoogleLabelPreview(){
    GoogleSignInButton(state = SignInState() , onClick = {})

}