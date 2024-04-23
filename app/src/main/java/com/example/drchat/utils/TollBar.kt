package com.example.drchat.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.drchat.ui.theme.Grey


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String, onNavigationIconClick: (() -> Unit)? = null) {
    TopAppBar(title = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Grey)
                .height(10.dp)
                .padding(end = 50.dp)

        ) {

        }
    }, navigationIcon = {
        if (onNavigationIconClick != null)
            Image(
                painter = painterResource(id = R.drawable.back_icon_white),
                contentDescription = stringResource(id = R.string.icon_back),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(40.dp)
                    .clickable {
                        onNavigationIconClick()
                    }
                    .padding(8.dp)
            )
    },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White

        )

    )
}

@Preview
@Composable
fun ChatToolbarPreview() {
    Toolbar(title = "login")
}