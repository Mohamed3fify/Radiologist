package com.example.drchat.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drchat.R
import com.example.drchat.ui.theme.Grey
import com.example.drchat.ui.theme.blue
import com.example.drchat.ui.theme.light_blue
import com.example.drchat.ui.theme.main_app
import com.example.drchat.ui.theme.shadowColor

@Composable
fun ChatAuthButton(
    modifier: Modifier = Modifier,
    title: String,
    textStyle: TextStyle = TextStyle(fontSize = 18.sp),
    isEnabled: Boolean = true, onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .size(60.dp)
            .padding(horizontal = 35.dp)
    )
    {
    Button(
        modifier = if (isEnabled) modifier.fillMaxWidth(0.9F) else modifier
            .fillMaxWidth(0.9F)
            .shadow(
                elevation = 5.dp,
                ambientColor =shadowColor,
            ),
        onClick = { onClick() },
        contentPadding = PaddingValues(horizontal = 36.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) main_app else Color.White,
            contentColor = if (isEnabled) Color.White else Grey
        )
    ) {
        Text(text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
               .padding(horizontal = 85.dp)

        )
        Spacer(modifier = Modifier.weight(1F))

     }
   }
}

@Composable
fun CreateButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = light_blue, contentColor = Color.Black)
    ) {
        Text(
            text = stringResource(R.string.create),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatAuthButtonPreview() {
    ChatAuthButton(title = "Login") {

    }
}
