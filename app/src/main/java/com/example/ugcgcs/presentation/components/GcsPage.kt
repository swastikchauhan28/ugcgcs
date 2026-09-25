package com.example.ugcgcs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val panel = Color(0xFF101A22)
private val outline = Color(0xFF29404D)
private val muted = Color(0xFF91A5B4)

@Composable
fun PageHeading(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().background(panel).padding(horizontal = 16.dp, vertical = 13.dp)) {
        Text(title.uppercase(), color = Color(0xFFE7F0F5), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(subtitle, color = muted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
fun GcsSection(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth().border(1.dp, outline).background(panel)) {
        Text(title.uppercase(), color = muted, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp))
        HorizontalDivider(color = outline)
        content()
    }
}

@Composable
fun GcsKeyValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(label.uppercase(), color = muted, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Text(value, color = Color(0xFFE7F0F5), fontFamily = FontFamily.Monospace, fontSize = 13.sp)
    }
}
