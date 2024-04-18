package com.example.drchat.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R

@Composable
fun AppDrawer() {
    Column {
        DrawerHeader()
        DividerrItem()
        DrawerBody()
        DividerrItem()
        DrawerBottom()
    }
}
@Composable
fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            //.background(color = Grey)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(60.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 20.dp))
        Text(
            text = "Dr Chat",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
@Composable
fun DrawerBody() {
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
                //fontFamily = FontWeight.SemiBold,
                color = Color.Black
            )
        }
        Divider(color = Color.Black, thickness = .5.dp) // Divider below "New Chat" button

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

        }

    }
}
@Composable
fun DrawerBottom() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "User's Application Name",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "user@example.com",
            fontSize = 14.sp,
            color = Color.Black
        )
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
@Preview(showBackground = true , showSystemUi = true )
@Composable
fun PreviewAppDrawer() {
    AppDrawer()
}