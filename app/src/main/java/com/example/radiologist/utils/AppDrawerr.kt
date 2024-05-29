package com.example.radiologist.utils


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.drchat.R
import com.example.radiologist.logIn.google.UserData
import com.example.radiologist.model.DataUtils.appUser
@Composable
fun DrawerHeader(
    userData: UserData,
    darkTheme: MutableState<Boolean>,
    onThemeToggle: () -> Unit
) {
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
                        Icon(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(80.dp)
                                .padding(8.dp),
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    IconButton(
                        onClick = {  onThemeToggle.invoke() },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = if (darkTheme.value) Icons.Default.LightMode else Icons.Default.DarkMode,
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
                } else{
                    appUser?.firstName?.let { firstName ->
                        Text(
                            text = firstName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
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
fun DrawerBody(
    onChatClicked: (String) -> Unit,
    onNewChatClicked: () -> Unit,
) { Column {} }

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

@Composable
fun HistoryNavigation(
    icon: ImageVector? = null,
    text:  () ->Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
    ){
            Icon(
                imageVector = Icons.Filled.HistoryToggleOff,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, bottom = 16.dp)
                    .size(25.dp),
                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
            )

        TextButton(
            onClick = text
        ) {
            Text(
                text = "History",
                fontSize = 20.sp,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                modifier = Modifier.padding(start = 4.dp)

            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DrawerPreview(){
  //  DrawerHeader(userData = UserData("" , "Mohamed" , "MH@email.com" , ""))
   // Spacer(modifier = Modifier.height(20.dp))
  DividerrItem()
    DrawerBody(
        onChatClicked = {},
        onNewChatClicked = {},



    )
}