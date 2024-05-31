package com.example.radiologist.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.radiologist.ui.theme.bot_msg_dark
import com.example.radiologist.ui.theme.bot_msg_light
import com.example.radiologist.ui.theme.main_app_light
import com.example.radiologist.ui.theme.txt_input_dark


@Composable
fun ChatAuthTextField(
    state: MutableState<String>,
    error: String?,
    hint: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,

    ) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    Column(modifier = Modifier.fillMaxWidth(0.9F)) {

        OutlinedTextField(
            value = state.value,
            onValueChange = { state.value = it },
            placeholder = { Text(hint, color = Color.Gray, fontSize = 15.sp) },
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(1F)
                .padding(horizontal = 15.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            textStyle = TextStyle(
                color = if (isSystemInDarkTheme) Color.White else Color.Black,
                fontSize = 17.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isSystemInDarkTheme) txt_input_dark else main_app_light,
                unfocusedContainerColor = if (isSystemInDarkTheme) txt_input_dark else main_app_light,
                errorContainerColor = Color.Red,
                focusedIndicatorColor = if (isSystemInDarkTheme) txt_input_dark else main_app_light,
                unfocusedIndicatorColor = if (isSystemInDarkTheme) txt_input_dark else main_app_light,
                errorIndicatorColor = Color.Red,

                ),

            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = if (isSystemInDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.size(18.dp)
                )
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
            },

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
            painter = if (isPasswordVisible) painterResource(id = R.drawable.visibility_off)
            else painterResource(id = R.drawable.ic_eye_visible),
            contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
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
            color = if (isSystemInDarkTheme()) txt_input_dark else bot_msg_light,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = if (isSystemInDarkTheme()) Color.White else Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))*/
                Text(
                    text = "Typing",
                    color = if (isSystemInDarkTheme()) Color.White else Color.White
                )
                Spacer(modifier = Modifier.width(3.dp))
                TypingIndicator(modifier = Modifier.size(20.dp))
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
        color = if (isSystemInDarkTheme()) bot_msg_dark else main_app_light,
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
                textStyle = TextStyle(
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontSize = 18.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
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
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
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
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                )

            }
        }
    }
}

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    dotSize: Dp = 4.dp,
    dotSpacing: Dp = 2.dp,
    animationDuration: Int = 250
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ), label = ""
    )

    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ), label = ""
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .offset { IntOffset(0, dot1Offset.toInt()) }
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(dotSpacing))
        Box(
            modifier = Modifier
                .size(dotSize)
                .offset { IntOffset(0, dot2Offset.toInt()) }
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(dotSpacing))
        Box(
            modifier = Modifier
                .size(dotSize)
                .offset { IntOffset(0, dot3Offset.toInt()) }
                .background(color, shape = CircleShape)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ChatInputPreview() {
    ChatInputTextField(
        text = "",
        onTextChanged = {},
        onImagePickerClicked = { },


        ) {}
    ChatInputTextField(text = "", onTextChanged = {}, onImagePickerClicked = { }) {

    }
}




