package com.example.drchat.chatBot

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.drchat.R
import com.example.drchat.ui.theme.DrChatTheme
import com.example.drchat.ui.theme.Grey
import com.example.drchat.ui.theme.txt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ChatBotActivity : ComponentActivity() {
    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult<PickVisualMediaRequest, Uri>(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri?.let {
                uriState.update { uri.toString() }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(Grey)
                                        .height(70.dp)
                                        .padding(horizontal = 16.dp),
                            ) {
//                                Text(
//                                    modifier = Modifier.align(Alignment.Center),
//                                    text = "DrChat",
//                                    fontSize = 25.sp,
//                                    color = MaterialTheme.colorScheme.onPrimary
//                                )
                                Image(
                                    painter = painterResource(R.drawable.logo),
                                    contentDescription = "App logo",
                                    modifier =
                                        Modifier.size(90.dp)
                                            .align(Alignment.Center)
                                            .padding(top = 20.dp),
                                )
                            }
                        },
                    ) {
                        chatScreen(paddingValues = it)
                    }
                }
            }
        }
    }

    @Composable
    fun chatScreen(paddingValues: PaddingValues) {
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitmap()

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Grey)
                    .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom,
        ) {
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                reverseLayout = true,
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->
                    if (chat.isFromUser) {
                        userItem(
                            prompt = chat.prompt,
                            bitmap = chat.bitmap,
                        )
                    } else {
                        botItem(response = chat.prompt)
                    }
                }
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    bitmap?.let {
                        Image(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .padding(bottom = 2.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "picked image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap(),
                        )
                    }
                    Icon(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest
                                            .Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build(),
                                    )
                                },
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    textStyle =
                        TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                        ),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                        ),
                    value = chatState.prompt,
                    onValueChange = {
                        chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                    },
                    placeholder = {
                        Text(
                            text = "message",
                            color = Color.White,
                        )
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clickable {
                                chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                                uriState.update { "" }
                            },
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send prompt",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    @Composable
    fun userItem(
        prompt: String,
        bitmap: Bitmap?,
    ) {
        Column(
            modifier = Modifier.padding(start = 100.dp, bottom = 16.dp),
        ) {
            bitmap?.let {
                Image(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .padding(bottom = 2.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap(),
                )
            }

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.drchat.ui.theme.userItem)
                        .padding(16.dp),
                text = prompt,
                fontSize = 17.sp,
                color = Color.White,
            )
        }
    }

    @Composable
    fun botItem(response: String) {
        val profileImage = painterResource(R.drawable.logo)

        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 16.dp),
        ) {
            Image(
                painter = profileImage,
                contentDescription = "Profile Photo",
                modifier =
                    Modifier
                        .size(50.dp)
                        .padding(10.dp)
                        .background(Color.Transparent),
                //  contentScale = ContentScale.Crop
            )

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.drchat.ui.theme.botItem)
                        .padding(16.dp),
                text = response,
                fontSize = 17.sp,
                color = txt,
            )
        }
    }

    @Composable
    private fun getBitmap(): Bitmap? {
        val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State =
            rememberAsyncImagePainter(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .size(Size.ORIGINAL)
                        .build(),
            ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }

        return null
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun botPreview() {
}