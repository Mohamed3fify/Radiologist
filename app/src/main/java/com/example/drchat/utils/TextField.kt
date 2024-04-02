package com.example.drchat.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.drchat.ui.theme.black
import com.example.drchat.ui.theme.blue


@Composable
fun ChatAuthTextField(
    state: MutableState<String>,
    error: String?,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth(0.9F)) {


        OutlinedTextField(
            value = state.value, onValueChange = {
                state.value = it
            },
            modifier = Modifier.fillMaxWidth(.9F),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = blue,
                unfocusedIndicatorColor = black,
                errorIndicatorColor = Color.Red
            ),

            label = {
                Text(text = label , fontSize = 12.sp  , fontWeight = FontWeight.Normal)
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
        if (error != null) {
            Text(
                text = error, color = Color.Red,
                fontSize = 18.sp,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Start)
            )
        }

    }
}
