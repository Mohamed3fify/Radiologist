package com.example.radiologist.chatBot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.drchat.R
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.history.HistoryActivity
import com.example.radiologist.logIn.LoginActivity
import com.example.radiologist.logIn.google.GoogleAuthUiClient
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.Grey
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.bg_light
import com.example.radiologist.ui.theme.bot_msg_light
import com.example.radiologist.ui.theme.main_app_light
import com.example.radiologist.ui.theme.txt
import com.example.radiologist.ui.theme.txt_input_dark
import com.example.radiologist.ui.theme.user_txt_dark
import com.example.radiologist.utils.BotTypingIndicator
import com.example.radiologist.utils.ChatInputTextField
import com.example.radiologist.utils.ChatToolBar
import com.example.radiologist.utils.DividerrItem
import com.example.radiologist.utils.DrawerBody
import com.example.radiologist.utils.DrawerBottom
import com.example.radiologist.utils.DrawerHeader
import com.example.radiologist.utils.HistoryNavigation
import com.example.radiologist.utils.NavigationItem
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

        val googleAuthUiClient by lazy {
            GoogleAuthUiClient(
                context = applicationContext,
                oneTapClient = Identity.getSignInClient(applicationContext)
            )
        }
        setContent {
            DrChatTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    color = Grey,
                ) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current
                    val darkTheme = remember { mutableStateOf(false) }

                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                FirebaseUtils.getGoogleSignInUser()
                                    ?.let { DrawerHeader(
                                        userData = it ,
                                        darkTheme = darkTheme,
                                        onThemeToggle = { darkTheme.value = !darkTheme.value }
                                    ) }
                                DividerrItem()
                                DrawerBody(
                                    onChatClicked = {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    onNewChatClicked = {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },

                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                HistoryNavigation(
                                    text = {
                                        lifecycleScope.launch {
                                            val intent = Intent(context, HistoryActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                                DividerrItem()
                                FirebaseUtils.getGoogleSignInUser()
                                    ?.let {
                                        DrawerBottom(
                                            onSignOut = {
                                                lifecycleScope.launch {
                                                    googleAuthUiClient.signOut()
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Sign out",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    val intent =
                                                        Intent(context, LoginActivity::class.java)
                                                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                   context.startActivity(intent)
                                                    (context as Activity).finishAffinity()

                                                }
                                            }
                                        )
                                    }
                            }

                        }, drawerState = drawerState
                    )
                    {
                        Scaffold(topBar = {
                            Column {
                                ChatToolBar(
                                    onNavigationIconClick = { scope.launch { drawerState.open() } },
                                )
                                DividerrItem()
                            }
                        })
                        {
                            chatScreen(paddingValues = it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun chatScreen(paddingValues: PaddingValues ) {
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitmap()

        val isSystemInDarkTheme = isSystemInDarkTheme()


        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) bg_dark else bg_light)
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
                            onImageClick ={ }
                        )
                    } else {
                        botItem(
                            response = chat.prompt
                        )
                    }
                }
            }

            if (chatState.isTyping) {
                BotTypingIndicator()
            }
            DividerrItem()
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 4.dp, end = 4.dp)
                    .background(if (isSystemInDarkTheme) bg_dark else Color.White),
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

                }
                Spacer(modifier = Modifier.width(8.dp))

                ChatInputTextField(
                    modifier = Modifier.weight(1f),
                    text = chatState.prompt,
                    onTextChanged = { newPrompt ->
                        chatViewModel.onEvent(
                            ChatUiEvent.UpdatePrompt(
                                newPrompt
                            )
                        )
                    },
                    onImagePickerClicked = {

                        imagePicker.launch(
                            PickVisualMediaRequest
                                .Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                .build(),
                        )
                    },
                    onSendClicked = {
                        val currentTime = System.currentTimeMillis()
                        val conversationId = chatState.chatList.firstOrNull { !it.isFromUser }?.conversationId ?: ""

                        chatViewModel.onEvent(
                            ChatUiEvent.SendPrompt(
                                chatState.prompt,
                                bitmap,
                                conversationId,
                                currentTime
                            )
                        )

                        uriState.update { "" }

                    }
                )
            }
        }
    }

    @Composable
    fun userItem(
        prompt: String,
        bitmap: Bitmap?,
        onImageClick: (Bitmap?) -> Unit,
    ) {

        Column(
            modifier = Modifier
                .padding(start = 100.dp, bottom = 16.dp)
                .padding(top = 16.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = if (isSystemInDarkTheme()) user_txt_dark else main_app_light,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(shape = RoundedCornerShape(12.dp))
            ) {

            bitmap?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))

                ) {
                    Image(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .padding(bottom = 2.dp, top = 15.dp, start = 15.dp, end = 15.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onImageClick(bitmap) },
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                        bitmap = it.asImageBitmap(),

                    )
                }
            }

            SelectionContainer {
                Text(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = if (isSystemInDarkTheme()) user_txt_dark else main_app_light)
                        .padding(16.dp),
                    text = prompt,
                    fontSize = 17.sp,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }

    @Composable
    fun botItem(response: String) {
        val profileImage = painterResource(if (isSystemInDarkTheme()) R.drawable.logo_radiologist_dark else R.drawable.logo_radiologist_light)


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
            )

            SelectionContainer {
                Text(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSystemInDarkTheme()) txt_input_dark else bot_msg_light)
                        .padding(16.dp),
                    text = response,
                    fontSize = 17.sp,
                    color = txt,
                    overflow = TextOverflow.Ellipsis
                )
            }

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
    @Composable
    fun displayClickedImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentDescription = "clicked image",
                contentScale = ContentScale.Crop,
                bitmap = bitmap.asImageBitmap(),
            )
        }
    }
}



@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ChatPreview() {
  DrChatTheme {

  }
}


