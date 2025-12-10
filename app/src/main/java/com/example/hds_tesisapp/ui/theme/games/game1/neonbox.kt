package com.example.hds_tesisapp.ui.theme.games.game1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NeonBox(num: String) {
    Box(
        modifier = Modifier
            .size(110.dp, 90.dp)
            .background(
                Color(0xFF0B2545),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                4.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color.Cyan,
                        Color(0xFF00BFFF),
                        Color(0xFF66FFFF)
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .shadow(12.dp, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = num,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF80FFFF)
        )
    }
}
