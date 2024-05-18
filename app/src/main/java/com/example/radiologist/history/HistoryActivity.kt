package com.example.radiologist.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.radiologist.model.Conversation
import com.example.radiologist.ui.theme.DrChatTheme
import com.example.radiologist.ui.theme.main_app_light
import com.example.radiologist.ui.theme.txt_input_dark
import com.example.radiologist.utils.DividerrItem
import com.example.radiologist.utils.Toolbar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.radiologist.chatBot.ChatBotActivity
import com.example.radiologist.model.Constants
import com.example.radiologist.ui.theme.bg_dark
import com.example.radiologist.ui.theme.bg_light
import java.util.UUID

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrChatTheme {
                HistoryContent() {
                    finish()
                }

            }
        }
    }
}

@Composable
fun HistoryContent(
    viewModel: HistoryViewModel = viewModel(),
    onFinish: () -> Unit,
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val conversationNameState = mutableStateOf("")


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
                RecyclerConversationsItem(viewModel , )
        }
    }
    TriggerEvent( event = viewModel.events.value)
}
@Composable
fun RecyclerConversationsItem(
    viewModel: HistoryViewModel = viewModel(),

) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getConversationsFromFirestore()
    }
    Column {
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
    name: String?="",
    icon: ImageVector = Icons.Filled.Delete,
    selected : Boolean?=null,
    conversation: Conversation,
    viewModel: HistoryViewModel = viewModel()
) {
    Surface {

    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            //  .clip(CircleShape)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isSystemInDarkTheme()) txt_input_dark else main_app_light)
            .clickable(onClick = { viewModel.navigateToChatScreen(conversation) }),
        verticalAlignment = Alignment.CenterVertically,

        ) {

        Text(
            name!!,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected == true) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,

            )


    }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            icon,
            tint =  Color.Red,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .size(25.dp),
            contentDescription = null,
        )
  }
}

@Composable
fun TriggerEvent(
    event: HistoryEvent,
    viewModel: HistoryViewModel = viewModel(),

    ) {
    val context = LocalContext.current

    when (event) {
        HistoryEvent.Idle -> {}

        is HistoryEvent.NavigateToChatScreen -> {
            val intent = Intent(context, ChatBotActivity::class.java)
            intent.putExtra(Constants.CONVERSATION_KEY, event.conversation.id)

            context.startActivity(intent)
            viewModel.resetEventState()
        }
    }
}
@Preview(showBackground = false ,)
@Composable
fun HistoryPreview() {
    HistoryContent() {}
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