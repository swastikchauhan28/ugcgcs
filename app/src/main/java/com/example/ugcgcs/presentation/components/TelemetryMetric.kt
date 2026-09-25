package com.example.ugcgcs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TelemetryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF101A22))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, color = Color(0xFF91A5B4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color(0xFFE7F0F5), fontSize = 14.sp, fontFamily = FontFamily.Monospace)
    }
}
