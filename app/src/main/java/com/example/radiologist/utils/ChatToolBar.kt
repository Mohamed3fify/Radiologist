package com.example.radiologist.utils

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drchat.R
import com.example.radiologist.chatBot.ChatViewModel
import com.example.radiologist.model.Conversation
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.bg_light
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatToolBar(
    onNavigationIconClick: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()

) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    var dialogOpen by remember { mutableStateOf(false) }
    val conversationName = remember { mutableStateOf("") }
    val user = Firebase.auth.currentUser
    val conversationId = UUID.randomUUID().toString()

    DrChatTheme {
        Surface(
            shadowElevation = 4.dp,
            tonalElevation = 0.dp,
            color = if (isSystemInDarkTheme) bg_dark else bg_light,
            modifier = Modifier
        ) {
            CenterAlignedTopAppBar(
                title = {
                    val paddingSizeModifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                        .size(32.dp)
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter =  if (isSystemInDarkTheme) painterResource( R.drawable.logo_radiologist_dark ) else painterResource( R.drawable.logo_radiologist_light ) ,
                                modifier = Modifier
                                    .size(40.dp),
                                contentDescription = "App Logo",

                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Radiologist",
                                textAlign = TextAlign.Center,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSystemInDarkTheme) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigationIconClick,
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            "drawerIcon",
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { onNavigationIconClick() },
                            tint = if (isSystemInDarkTheme) Color.White else Color.Black,
                        )
                    }
                },

                colors = topAppBarColors(
        containerColor = if (isSystemInDarkTheme) bg_dark else bg_light,
        titleContentColor = Color.White
        ),

            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                       dialogOpen = true
                    },
                ) {
                    Image(
                          painter = if (isSystemInDarkTheme) painterResource(R.drawable.add) else  painterResource(R.drawable.add_black),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = {
                dialogOpen = false
            },
            title = { Text("Save a Conversation") },
            text = {
                TextField(
                    value = conversationName.value,
                    onValueChange = { conversationName.value = it },
                    placeholder = { Text("Conversation Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                       // chatViewModel.addConversation()
                       dialogOpen = false

                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dialogOpen = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true,)
@Composable
fun AppBarPreview() {

    ChatToolBar(
        onNavigationIconClick = {},
        chatViewModel = viewModel()
    )

}