package com.example.radiologist.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.radiologist.chatBot.ChatViewModel
import com.example.radiologist.logIn.google.UserData


@Composable
fun DrawerHeader(userData: UserData) {
    Column (
         modifier = Modifier
             .fillMaxWidth()
             .padding(16.dp)
       )
            {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (userData.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = "profile picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val defaultImageUrl = "https://example.com/default_profile_picture.jpg"
                        AsyncImage(
                            model = defaultImageUrl,
                            contentDescription = "default profile picture",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    IconButton(
                        onClick = {  },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = if (isSystemInDarkTheme()) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Mode"
                        )
                    }
                }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (userData.userName != null) {
                    Text(
                        text = userData.userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
                if (userData.email != null){
                    Text(
                        text = userData.email,
                        fontSize = 14.sp,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
               }
                Spacer(modifier = Modifier.height(20.dp))
            }
    }
}

@Composable
private fun ColumnScope.HistoryConversations(
    onChatClicked: (String) -> Unit,
    deleteConversation: (String) -> Unit,
    deleteMessages: (String) -> Unit,
    currentConversationState: String,

    ) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .weight(1f, false),
    ) {

    }
}
@Composable
fun DrawerBody() {
    val viewModel: ChatViewModel = viewModel()

    val conversations by viewModel.conversations.collectAsState(emptyList())

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AddBox,
                contentDescription = "Add Icon",
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "New Chat",
                fontSize = 24.sp,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
        Divider(color = Color.Black, thickness = .5.dp)

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

        }
    }
}

@Composable
fun DrawerBottom(onSignOut: () ->Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick = onSignOut
        ) {
            Text(
                text = "Sign out" ,
                color = Color.Red,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}


    @Composable
    fun DividerrItem(modifier: Modifier = Modifier) {
        Divider(
            modifier = modifier,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            thickness = 1.dp
        )
    }

@Preview(showBackground = true)
@Composable
fun DrawerPreview(){
    DrawerHeader(userData = UserData("" , "Mohamed" , "MH@email.com" , ""))
}