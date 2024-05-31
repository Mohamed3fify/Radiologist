package com.example.radiologist.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
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
import com.example.radiologist.ui.theme.main_app_light
import com.example.radiologist.ui.theme.txt_input_dark

@Composable
fun DrawerHeader(userData: UserData) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
    )
    {
        Row(
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
                    contentDescription = "default img",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(80.dp)
                        .padding(8.dp),
                    tint = contentColor
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
                    color = contentColor
                )
            } else {
                appUser?.firstName?.let { firstName ->
                    Text(
                        text = firstName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
            if (userData.email != null) {
                Text(
                    text = userData.email,
                    fontSize = 14.sp,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun HistoryNavigation(
    selected: Boolean,
    onHistoryClicked: () -> Unit,
) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val background = if (selected) {
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onHistoryClicked() }
            .clip(CircleShape)
            .then(background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 6.dp, top = 12.dp, bottom = 16.dp)
                .size(24.dp),
            tint = contentColor,
        )

        Text(
            text = "History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun Settings(selected: Boolean, onSignOut: () -> Unit) {
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val background = if (selected) {
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .clickable { onSignOut() }
                .clip(CircleShape)
                .then(background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = "Sign out",
                color = Color.Red,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )

        }
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        thickness = 1.dp
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrawerPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        DividerItem()
        HistoryNavigation(selected = false) {}
    }

}