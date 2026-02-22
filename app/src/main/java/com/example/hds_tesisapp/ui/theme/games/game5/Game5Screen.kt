package com.example.hds_tesisapp.ui.theme.games.game5

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

// Helper (safe to redeclare in separate package)
fun Context.findGame5Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame5Activity()
    else -> null
}

// ─── Game Data ─────────────────────────────────────────────────────────────

data class Game5Round(
    val number: Int,
    val condition: String,
    val leftLabel: String,   // e.g. "Puerta Izquierda"
    val rightLabel: String,  // e.g. "Puerta Derecha"
    // true = LEFT door is correct, false = RIGHT door is correct
    val correctIsLeft: Boolean
)

val game5Rounds = listOf(
    Game5Round(
        number = 15,
        condition = "Si el número en el cofre es PAR,\nve por la Puerta Izquierda.\nSi es IMPAR, ve por la Puerta Derecha.",
        leftLabel = "PUERTA\nIZQUIERDA",
        rightLabel = "PUERTA\nDERECHA",
        correctIsLeft = false  // 15 is odd → Right door
    ),
    Game5Round(
        number = 8,
        condition = "Si el número en el cofre es PAR,\nve por la Puerta Izquierda.\nSi es IMPAR, ve por la Puerta Derecha.",
        leftLabel = "PUERTA\nIZQUIERDA",
        rightLabel = "PUERTA\nDERECHA",
        correctIsLeft = true   // 8 is even → Left door
    ),
    Game5Round(
        number = 21,
        condition = "Si el número en el cofre es PAR,\nve por la Puerta Izquierda.\nSi es IMPAR, ve por la Puerta Derecha.",
        leftLabel = "PUERTA\nIZQUIERDA",
        rightLabel = "PUERTA\nDERECHA",
        correctIsLeft = false  // 21 is odd → Right door
    )
)

// ─── Screen ─────────────────────────────────────────────────────────────────

@Composable
fun Game5Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current

    // Force Landscape
    val activity = remember { context.findGame5Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var roundIndex by remember { mutableStateOf(0) }
    val round = game5Rounds[roundIndex]

    // Selected door: null = none, true = Left, false = Right
    var selectedLeft by remember { mutableStateOf<Boolean?>(null) }
    var isGameWon by remember { mutableStateOf(false) }

    // Colors for selection
    val leftBorderColor = when {
        selectedLeft == true -> Color(0xFFFFEB3B)  // Yellow when selected
        else -> Color(0xFF78909C)
    }
    val rightBorderColor = when {
        selectedLeft == false -> Color(0xFFFFEB3B)
        else -> Color(0xFF78909C)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF37474F)) // Dark blue-grey (matches screenshot)
    ) {

        // ── TOP TITLE ────────────────────────────────────────────────────────
        Text(
            text = "¿PAR O IMPAR?",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFF9C4),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        // ── LINA CHARACTER (Left) ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp)
                .size(width = 90.dp, height = 140.dp)
                .background(Color(0xFF5C6BC0), RoundedCornerShape(12.dp)) // Indigo placeholder
                .border(3.dp, Color(0xFF9FA8DA), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LINA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // ── CENTER CONTENT ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 140.dp, end = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── DOORS ROW ─────────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Door Box
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 90.dp)
                        .background(
                            if (selectedLeft == true) Color(0xFF5C6BC0) else Color(0xFF455A64),
                            RoundedCornerShape(12.dp)
                        )
                        .border(3.dp, leftBorderColor, RoundedCornerShape(12.dp))
                        .clickable { selectedLeft = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = round.leftLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Right Door Box
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 90.dp)
                        .background(
                            if (selectedLeft == false) Color(0xFF5C6BC0) else Color(0xFF455A64),
                            RoundedCornerShape(12.dp)
                        )
                        .border(3.dp, rightBorderColor, RoundedCornerShape(12.dp))
                        .clickable { selectedLeft = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = round.rightLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── CHEST / NUMBER BOX ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 70.dp)
                    .background(Color(0xFF8D6E63), RoundedCornerShape(10.dp)) // Brown chest
                    .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp)), // Gold border
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📦",
                        fontSize = 18.sp
                    )
                    Text(
                        text = round.number.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                }
            }
        }

        // ── CONDITION BOX (Speech Bubble, Bottom-Right of center) ────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp, start = 160.dp, end = 160.dp)
                .fillMaxWidth(0.55f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFB0BEC5), RoundedCornerShape(16.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = round.condition,
                color = Color(0xFF37474F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }

        // ── COMPROBAR BUTTON (Top Right) ──────────────────────────────────────
        Button(
            onClick = {
                when {
                    selectedLeft == null -> {
                        Toast.makeText(context, "Elige una puerta primero.", Toast.LENGTH_SHORT).show()
                    }
                    selectedLeft == round.correctIsLeft -> {
                        // Correct
                        if (roundIndex < game5Rounds.lastIndex) {
                            roundIndex++
                            selectedLeft = null // Reset selection for next round
                        } else {
                            isGameWon = true
                        }
                    }
                    else -> {
                        Toast.makeText(context, "¡Incorrecto! Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                        selectedLeft = null // Reset selection
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding()
                .padding(16.dp)
                .zIndex(10f)
        ) {
            Text("COMPROBAR")
        }

        // ── WIN MESSAGE ───────────────────────────────────────────────────────
        if (isGameWon) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable { onLevelComplete() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡EXCELENTE!",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFEB3B)
                    )
                    Text(
                        text = "Toca para continuar",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
