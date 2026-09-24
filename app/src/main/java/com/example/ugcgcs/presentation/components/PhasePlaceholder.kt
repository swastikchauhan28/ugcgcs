package com.example.ugcgcs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PhasePlaceholder(title: String, phase: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A1015)).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title.uppercase(), color = Color(0xFFE7F0F5), fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Scheduled for $phase", color = Color(0xFF91A5B4), modifier = Modifier.padding(top = 8.dp))
    }
}
