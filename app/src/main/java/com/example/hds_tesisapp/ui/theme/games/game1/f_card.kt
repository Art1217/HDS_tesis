package com.example.hds_tesisapp.ui.theme.games.game1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FuturisticCard(text: String) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(70.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF10233F), Color(0xFF0A1A2E))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 3.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.Cyan, Color(0xFF00E5FF))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(10.dp, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(6.dp)
        )
    }
}
