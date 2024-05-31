package com.example.radiologist.utils


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.radiologist.ui.theme.bg_dark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String,
    onNavigationIconClick: (() -> Unit)? = null
) {

    TopAppBar(title = {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val titleColor = if (isSystemInDarkTheme) Color.White else Color.Black
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme) Color.Transparent else Color.Transparent)
                .padding(end = 50.dp),
                verticalAlignment = Alignment.CenterVertically

        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                fontSize = 20.sp,
                color = titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }, navigationIcon = {
        if (onNavigationIconClick != null)
            Icon(
                painter = painterResource(id = R.drawable.back_icon_white),
                contentDescription = stringResource(id = R.string.icon_back),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(40.dp)
                    .clickable {
                        onNavigationIconClick()
                    }
                    .padding(8.dp),
                tint = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
          },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.Transparent

        )

    )
}

@Preview
@Composable
fun ChatToolbarPreview() {
    Toolbar(title = "login")
}