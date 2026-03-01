package com.example.hds_tesisapp.ui.theme.games.game7

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

// ─── Helpers ──────────────────────────────────────────────────────────────────

fun Context.findGame7Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame7Activity()
    else -> null
}

// ─── Defaults ─────────────────────────────────────────────────────────────────

private const val DEFAULT_CURRENT      = 5
private const val DEFAULT_TARGET       = 12
private const val DEFAULT_PLUS_USES    = 3
private const val DEFAULT_MINUS_USES   = 4
private const val DEFAULT_MULTIPLY_USES = 1

// ─── Main Screen ──────────────────────────────────────────────────────────────

@Composable
fun Game7Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findGame7Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // ── Mutable state ─────────────────────────────────────────────────────────
    var currentValue    by remember { mutableStateOf(DEFAULT_CURRENT) }
    val targetValue     = DEFAULT_TARGET   // Fixed target for this round

    var plusOneUses     by remember { mutableStateOf(DEFAULT_PLUS_USES) }
    var minusOneUses    by remember { mutableStateOf(DEFAULT_MINUS_USES) }
    var multiplyTwoUses by remember { mutableStateOf(DEFAULT_MULTIPLY_USES) }

    val isWon = currentValue == targetValue

    // ── Palette ───────────────────────────────────────────────────────────────
    val bgColor       = Color(0xFFF5F5F5)
    val cardColor     = Color.White
    val accentGreen   = Color(0xFF43A047)
    val accentRed     = Color(0xFFE53935)
    val accentBlue    = Color(0xFF1E88E5)
    val accentPurple  = Color(0xFF8E24AA)
    val textDark      = Color(0xFF212121)
    val textMuted     = Color(0xFF757575)

    // ── Layout ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // ── TITLE (top center) ────────────────────────────────────────────────
        Text(
            text = "CAMBIA EL VALOR",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accentBlue,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
        )

        // ── LINA (observer, left) ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .size(width = 80.dp, height = 120.dp)
                .background(Color(0xFF5C6BC0), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFF9FA8DA), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("LINA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // ── CENTER CARD ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(420.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Header: Current value + Target ─────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current value (top-left of card)
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Valor actual", fontSize = 11.sp, color = textMuted)
                        Text(
                            text = currentValue.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isWon) accentGreen else accentBlue
                        )
                    }

                    // Arrow
                    Text("→", fontSize = 28.sp, color = textMuted)

                    // Target (highlighted, center-right)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Objetivo", fontSize = 11.sp, color = textMuted)
                        Text(
                            text = targetValue.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = accentPurple
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                // ── Victory banner ──────────────────────────────────────────
                if (isWon) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Text(
                            text = "🏆 ¡Victoria! ¡Llegaste a $targetValue!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentGreen,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }
                }

                // ── Operation buttons ───────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // +1
                    OperationButton(
                        label = "+1",
                        uses = plusOneUses,
                        color = accentGreen,
                        modifier = Modifier.weight(1f),
                        enabled = plusOneUses > 0 && !isWon
                    ) {
                        currentValue += 1
                        plusOneUses--
                    }

                    // -1
                    OperationButton(
                        label = "-1",
                        uses = minusOneUses,
                        color = accentRed,
                        modifier = Modifier.weight(1f),
                        enabled = minusOneUses > 0 && !isWon
                    ) {
                        currentValue -= 1
                        minusOneUses--
                    }

                    // ×2
                    OperationButton(
                        label = "×2",
                        uses = multiplyTwoUses,
                        color = accentPurple,
                        modifier = Modifier.weight(1f),
                        enabled = multiplyTwoUses > 0 && !isWon
                    ) {
                        currentValue *= 2
                        multiplyTwoUses--
                    }
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                // ── Restart / Continue buttons ──────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Reiniciar
                    OutlinedButton(
                        onClick = {
                            currentValue     = DEFAULT_CURRENT
                            plusOneUses      = DEFAULT_PLUS_USES
                            minusOneUses     = DEFAULT_MINUS_USES
                            multiplyTwoUses  = DEFAULT_MULTIPLY_USES
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔄 Reiniciar", fontWeight = FontWeight.Bold)
                    }

                    // Continuar (only visible on win)
                    if (isWon) {
                        Button(
                            onClick = { onLevelComplete() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentGreen)
                        ) {
                            Text("Continuar →", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ─── Reusable operation button ────────────────────────────────────────────────

@Composable
fun OperationButton(
    label: String,
    uses: Int,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color(0xFFBDBDBD)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "$uses usos restantes",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}
