package com.example.drchat.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.drchat.ui.theme.Grey
import com.example.drchat.ui.theme.blue
import com.example.drchat.ui.theme.botItem
import com.example.drchat.ui.theme.botResponse


@Composable
fun ChatAuthTextField(
    state: MutableState<String>,
    error: String?,
    label: Pair<String, ImageVector>,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,

    ) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(0.9F)) {

        OutlinedTextField(
            value = state.value,
            onValueChange = { state.value = it },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 15.dp),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 17.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = botResponse,
                unfocusedContainerColor = botResponse,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = botResponse,
                unfocusedIndicatorColor = botResponse,
                errorIndicatorColor = Color.Red,
            ),

            label = {

                Row() {
                    Icon(
                        imageVector = label.second,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label.first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        modifier = Modifier.clickable {}
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),

            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,

            trailingIcon = {
                if (isPassword) {
                    PasswordVisibilityToggle(
                        isPasswordVisible = passwordVisible,
                        onToggleClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                }
            }
        )


        if (error != null) {
            Text(
                text = error, color = Color.Red,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )
        }

    }
}

@Composable
fun PasswordVisibilityToggle(
    isPasswordVisible: Boolean,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleClick,
        modifier = modifier
    ) {
        Icon(
            painter = if (isPasswordVisible) painterResource(id = R.drawable.visibility_off) else painterResource(id = R.drawable.ic_eye_visible),
            contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
            tint = Color.White
        )
    }
}

@Composable
fun BotTypingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = botItem,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = blue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Typing...",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatInputTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onImagePickerClicked: () -> Unit,
    onSendClicked: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = botResponse,
        modifier =
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp, start = 4.dp, end = 4.dp, top = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                value = text,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(text = "Type a message...", color = Color.Gray)
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onImagePickerClicked)

            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = "Add Photo",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onSendClicked)

            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
 fun ChatInputPreview(){
    ChatInputTextField(
        text = "",
        onTextChanged = {},
        onImagePickerClicked = { },

    ) {}
}




