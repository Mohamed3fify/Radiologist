package com.example.drchat.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.drchat.ui.theme.DrChatTheme
import com.example.drchat.ui.theme.Grey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatToolBar() {
    DrChatTheme {
        Surface(
            shadowElevation = 4.dp,
            tonalElevation = 0.dp,
        ) {
            CenterAlignedTopAppBar(
                title = {
                    val paddingSizeModifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .size(32.dp)
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.logo),
                                modifier = paddingSizeModifier.then(Modifier.clip(RoundedCornerShape(6.dp))),
                                contentDescription = "App Logo"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dr Chat",
                                textAlign = TextAlign.Center,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                },

                /*navigationIcon = {
                    IconButton(
                        onClick = {
                            onClickMenu()
                        },
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            "backIcon",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },*/

                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Grey,
                    titleContentColor = Color.White,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    ChatToolBar(

    )
}