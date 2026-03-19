package com.example.hds_tesisapp.ui.theme.games.game7

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

// ─── Helpers ──────────────────────────────────────────────────────────────────

fun Context.findGame7Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame7Activity()
    else -> null
}

// ─── Defaults ─────────────────────────────────────────────────────────────────

private const val DEFAULT_CURRENT       = 5
private const val DEFAULT_TARGET        = 12
private const val DEFAULT_PLUS_USES     = 3
private const val DEFAULT_MINUS_USES    = 4
private const val DEFAULT_MULTIPLY_USES = 1

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun Game7Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findGame7Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // ── State ─────────────────────────────────────────────────────────────────
    var currentValue    by remember { mutableStateOf(DEFAULT_CURRENT) }
    val targetValue     = DEFAULT_TARGET
    var plusUses        by remember { mutableStateOf(DEFAULT_PLUS_USES) }
    var minusUses       by remember { mutableStateOf(DEFAULT_MINUS_USES) }
    var multiplyUses    by remember { mutableStateOf(DEFAULT_MULTIPLY_USES) }

    val isWon = currentValue == targetValue

    // ── Colors ────────────────────────────────────────────────────────────────
    val bgGradient = Brush.verticalGradient(listOf(Color(0xFF1B0045), Color(0xFF3D007A)))
    val panelColor = Color(0xFF2A0060)
    val cardBg     = Color(0xFF1A0040)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // ── TITLE ─────────────────────────────────────────────────────────────
        Text(
            "⚡ CAMBIA EL VALOR ⚡",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFEB3B),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp)
        )

        // ── LINA (observer) ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
                .size(width = 70.dp, height = 100.dp)
                .background(Color(0xFF5C6BC0), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFB39DDB), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("LINA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        // ══════════════════════════════════════════════════════════════════════
        // LEFT PANEL – Operation token buttons
        // ══════════════════════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 110.dp)
                .width(260.dp)
                .wrapContentHeight()
                .background(panelColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF7B1FA2), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TokenButton(
                    symbol = "+1",
                    uses = plusUses,
                    tokenColor = Color(0xFF43A047),
                    shadowColor = Color(0xFF1B5E20),
                    enabled = plusUses > 0 && !isWon
                ) {
                    currentValue += 1
                    plusUses--
                }

                // divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                        .background(Color(0xFF7B1FA2))
                )

                TokenButton(
                    symbol = "-1",
                    uses = minusUses,
                    tokenColor = Color(0xFFE53935),
                    shadowColor = Color(0xFF7F0000),
                    enabled = minusUses > 0 && !isWon
                ) {
                    currentValue -= 1
                    minusUses--
                }

                // divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                        .background(Color(0xFF7B1FA2))
                )

                TokenButton(
                    symbol = "×2",
                    uses = multiplyUses,
                    tokenColor = Color(0xFF1E88E5),
                    shadowColor = Color(0xFF0D47A1),
                    enabled = multiplyUses > 0 && !isWon
                ) {
                    currentValue *= 2
                    multiplyUses--
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // RIGHT PANEL – Game Card (current value top, target center)
        // ══════════════════════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
                .size(width = 200.dp, height = 220.dp)
                .background(cardBg, RoundedCornerShape(20.dp))
                .border(4.dp, Color(0xFFFFEB3B), RoundedCornerShape(20.dp))
        ) {
            // ── Current value (top tag) ────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-16).dp)
                    .background(Color(0xFFFDD835), RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFFF57F17), RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = currentValue.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4A148C)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("⚡", fontSize = 14.sp)
                }
            }

            // ── X decoration (diagonal lines like the sketch) ─────────────
            // Simplified: just show the target in center very large
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isWon) "✅" else "⚡",
                        fontSize = 28.sp
                    )
                    Text(
                        text = targetValue.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWon) Color(0xFF69F0AE) else Color(0xFFFFEB3B),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isWon) "¡CORRECTO!" else "OBJETIVO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // BOTTOM BUTTONS – Reset / Continue
        // ══════════════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reset
            Box(
                modifier = Modifier
                    .background(Color(0xFF37474F), RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFF90A4AE), RoundedCornerShape(24.dp))
                    .clickable {
                        currentValue  = DEFAULT_CURRENT
                        plusUses      = DEFAULT_PLUS_USES
                        minusUses     = DEFAULT_MINUS_USES
                        multiplyUses  = DEFAULT_MULTIPLY_USES
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🔄 Reiniciar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Continue (only when won)
            if (isWon) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF00C853), RoundedCornerShape(24.dp))
                        .border(2.dp, Color(0xFF69F0AE), RoundedCornerShape(24.dp))
                        .clickable { onLevelComplete() }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Continuar →", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // WIN OVERLAY (full screen flash)
        // ══════════════════════════════════════════════════════════════════════
        if (isWon) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF00C853).copy(alpha = 0.15f))
                    .align(Alignment.Center)
            )
        }
    }
}

// ─── Token Button ─────────────────────────────────────────────────────────────

@Composable
fun TokenButton(
    symbol: String,
    uses: Int,
    tokenColor: Color,
    shadowColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Token (circle coin)
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    if (enabled) shadowColor else Color(0xFF424242),
                    CircleShape
                )
                // Offset gives a "depth / shadow" effect
                .padding(bottom = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-4).dp)
                    .background(
                        if (enabled) tokenColor else Color(0xFF616161),
                        CircleShape
                    )
                    .border(
                        3.dp,
                        if (enabled) Color.White.copy(alpha = 0.4f) else Color.Gray,
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(enabled = enabled) { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        // Uses label
        Text(
            text = "($uses usos)",
            fontSize = 10.sp,
            color = if (enabled) Color(0xFFE0E0E0) else Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
    }
}
