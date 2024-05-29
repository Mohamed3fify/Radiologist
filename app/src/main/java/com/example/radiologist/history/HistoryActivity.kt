package com.example.radiologist.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.radiologist.model.Conversation
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.main_app_light
import com.example.radiologist.ui.theme.txt_input_dark
import com.example.radiologist.utils.DividerrItem
import com.example.radiologist.utils.Toolbar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drchat.R
import com.example.radiologist.chatBot.ChatBotActivity
import com.example.radiologist.chatBot.ChatUiEvent
import com.example.radiologist.chatBot.ChatViewModel
import com.example.radiologist.model.Constants
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.bg_light
import kotlinx.coroutines.launch

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                HistoryContent(
                    onFinish = { finish() },
                    onNavigateToChatScreen = { finishAffinity()}
                )

            }
        }
    }
}

@Composable
fun HistoryContent(
    viewModel: HistoryViewModel = viewModel(),
    onFinish: () -> Unit,
    onNavigateToChatScreen: () -> Unit,
    ) {
    val isSystemInDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            Column {

                Toolbar(
                    title = "History",
                ) { onFinish() }
                DividerrItem()
            }
        }) { paddingValues ->
        paddingValues

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) bg_dark else bg_light)
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom,
        ){
                RecyclerConversationsItem()
        }
    }
    TriggerEvent( event = viewModel.events.value){
        onNavigateToChatScreen()
    }
}
@Composable
fun RecyclerConversationsItem(
    viewModel: HistoryViewModel = viewModel(),
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getConversationsFromFirestore()
    }
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
    LazyColumn(
        modifier =
        Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        items(viewModel.conversationsList.size) { position ->

          ConversationItem(conversation = viewModel.conversationsList[position])
        }
    }
 }
}
@Composable
private fun ConversationItem(
    conversation: Conversation,
    viewModel: HistoryViewModel = viewModel(),

 ) {
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .height(56.dp)
            .clickable(
                onClick = {
                    viewModel.events.value = HistoryEvent.NavigateToChatScreen(conversation.id!!)
                }
            )
            .fillMaxWidth(),
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(12.dp),
        color = if (isSystemInDarkTheme()) txt_input_dark else main_app_light

    ) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 12.dp)
                    .background(if (isSystemInDarkTheme()) txt_input_dark else main_app_light),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {

                Text(
                    conversation.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                    Modifier
                        .padding(vertical = 10.dp)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                IconButton(
                    onClick = { showDialog = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.delete),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(25.dp),
                        contentDescription = null,
                    )
                }
            }
        }
  }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Conversation") },
            text = {
                Text(buildAnnotatedString {
                    append("Are you sure you want to delete ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(conversation.name)
                    }
                    append(" conversation?")
                })
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    viewModel.events.value = HistoryEvent.DeleteConversation(conversation.id!!)

                },
                    colors = ButtonDefaults.buttonColors(
                        Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TriggerEvent(
    event: HistoryEvent,
    viewModel: HistoryViewModel = viewModel(),
    onNavigateToChatScreen: () -> Unit,
    ) {
    val context = LocalContext.current
    when (event) {
        HistoryEvent.Idle -> {}

        is HistoryEvent.NavigateToChatScreen -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            intent.putExtra(Constants.CONVERSATION_KEY, event.conversationId)
            context.startActivity(intent)
            viewModel.resetEventState()
            (context as Activity).finishAffinity()
            onNavigateToChatScreen()
        }
        is HistoryEvent.DeleteConversation -> {
            viewModel.deleteConversation(event.conversationId)
            viewModel.resetEventState()
            Toast.makeText(context, "Conversation deleted", Toast.LENGTH_SHORT).show()

        }
    }
}
@Preview(showBackground = false ,)
@Composable
fun HistoryPreview() {
    HistoryContent(viewModel() , {} ,{})
}
@Preview(showBackground = false ,)
@Composable
fun ItemPreview() {
   /* ConversationItem(
        name = "",
        selected = true ,
        conversation = Conversation("" , userId = null , ""),
        viewModel = HistoryViewModel()
    )*/
}