package com.example.hds_tesisapp.ui.theme.games.game6

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.drawWithContent
import kotlin.random.Random

// ─── Helpers ─────────────────────────────────────────────────────────────────

fun Context.findGame6Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame6Activity()
    else -> null
}

// ─── Data ─────────────────────────────────────────────────────────────────────

data class MathQuestion(
    val text: String,
    val options: List<Int>,
    val correctIndex: Int
)

fun generateQuestion(): MathQuestion {
    val a = Random.nextInt(2, 10)
    val b = Random.nextInt(2, 10)
    val answer = a * b
    val wrongs = listOf(answer - b, answer + a, answer + b).filter { it != answer && it > 0 }
    val opts = (listOf(answer) + wrongs.take(2)).shuffled()
    return MathQuestion("$a × $b = ?", opts, opts.indexOf(answer))
}

enum class TurnPhase {
    PLAYER_CHOOSE_ACTION,  // Player picks: Advance, Attack, Defend
    PLAYER_MATH_QUESTION,  // Math check before attacking
    SHOW_RESULT,           // Show what happened this turn
    ENEMY_TURN,            // Enemy attacks
    GAME_OVER_WIN,
    GAME_OVER_LOSE
}

// ─── HP Bar ──────────────────────────────────────────────────────────────────

@Composable
fun HpBar(hp: Int, maxHp: Int, color: Color, label: String) {
    val fraction = (hp.toFloat() / maxHp).coerceIn(0f, 1f)
    val barColor by animateColorAsState(
        targetValue = when {
            fraction > 0.5f -> color
            fraction > 0.25f -> Color(0xFFFFA726)
            else -> Color(0xFFEF5350)
        }, label = "hpColor"
    )
    Column(modifier = Modifier.width(150.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$hp / $maxHp", color = Color.White, fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF37474F))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(6.dp))
                    .background(barColor)
            )
        }
    }
}

// ─── Character Box ────────────────────────────────────────────────────────────

@Composable
fun CharacterBox(
    label: String,
    emoji: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    extraContent: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(width = 100.dp, height = 120.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF90A4AE), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 36.sp)
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            extraContent()
        }
    }
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun Game6Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findGame6Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // ── State ────────────────────────────────────────────────────────────────
    val maxPlayerHp = 30
    val maxEnemyHp  = 30

    var playerHp      by remember { mutableStateOf(maxPlayerHp) }
    var enemyHp       by remember { mutableStateOf(maxEnemyHp)  }
    var isDefending   by remember { mutableStateOf(false)       }
    var isAdvanced    by remember { mutableStateOf(false)       } // bonus damage next attack
    var phase         by remember { mutableStateOf(TurnPhase.PLAYER_CHOOSE_ACTION) }
    var statusMsg     by remember { mutableStateOf("¡Tu turno! Elige una acción.") }
    var question      by remember { mutableStateOf(generateQuestion()) }
    var turn          by remember { mutableStateOf(1) }

    // ── Colours ──────────────────────────────────────────────────────────────
    val bgColor      = Color(0xFF1A1A2E)
    val panelColor   = Color(0xFF16213E)
    val accentYellow = Color(0xFFFFC107)
    val accentBlue   = Color(0xFF00B4D8)

    fun doEnemyTurn() {
        val damage = if (isDefending) Random.nextInt(0, 3) else Random.nextInt(3, 8)
        playerHp  = (playerHp - damage).coerceAtLeast(0)
        isDefending = false
        statusMsg = if (damage == 0)
            "¡Bloqueaste todo el daño! 🛡️"
        else
            "El enemigo te atacó por $damage de daño."
        phase = if (playerHp <= 0) TurnPhase.GAME_OVER_LOSE else TurnPhase.SHOW_RESULT
        turn++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // ── ARENA (full background layer) ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp) // leave room for action bar
        ) {
            // Player character - left
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 48.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HpBar(playerHp, maxPlayerHp, accentBlue, "Tom Atom")
                    Spacer(Modifier.height(8.dp))
                    CharacterBox(
                        label = if (isAdvanced) "⚡ Listo" else "Tom Atom",
                        emoji = "🤖",
                        bgColor = Color(0xFF0F3460)
                    )
                }
            }

            // Turn counter (center top)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color(0xFF0F3460), RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Turno $turn",
                    color = accentYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Enemy character - right
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 48.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HpBar(enemyHp, maxEnemyHp, Color(0xFFEF5350), "Enemigo")
                    Spacer(Modifier.height(8.dp))
                    CharacterBox(
                        label = "Enemigo",
                        emoji = "☕",
                        bgColor = Color(0xFF4A1942)
                    )
                }
            }
        }

        // ── STATUS MESSAGE ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 40.dp)
                .width(300.dp)
                .background(panelColor.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF90A4AE), RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusMsg,
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }

        // ── ACTION BAR (Bottom) ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(panelColor)
                .topBorderModifier(width = 2.dp, color = accentBlue)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (phase) {

                TurnPhase.PLAYER_CHOOSE_ACTION -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AVANZAR
                        ActionButton("AVANZAR", color = Color(0xFF546E7A)) {
                            isAdvanced = true
                            statusMsg = "Te preparas para el siguiente ataque. ⚡"
                            phase = TurnPhase.ENEMY_TURN
                        }
                        // ATACAR
                        ActionButton("ATACAR", color = accentYellow, textColor = Color.Black) {
                            question = generateQuestion()
                            phase = TurnPhase.PLAYER_MATH_QUESTION
                        }
                        // DEFENDER
                        ActionButton("DEFENDER", color = Color(0xFF546E7A)) {
                            isDefending = true
                            statusMsg = "Levantaste el escudo. ¡Tu defensa aumentó! 🛡️"
                            phase = TurnPhase.ENEMY_TURN
                        }
                    }
                }

                TurnPhase.PLAYER_MATH_QUESTION -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "⚔️ Resuelve para atacar: ${question.text}",
                            color = accentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            question.options.forEachIndexed { idx, opt ->
                                ActionButton(opt.toString(), color = Color(0xFF0F3460)) {
                                    val bonus = if (isAdvanced) 4 else 0
                                    isAdvanced = false
                                    if (idx == question.correctIndex) {
                                        val dmg = Random.nextInt(5, 10) + bonus
                                        enemyHp = (enemyHp - dmg).coerceAtLeast(0)
                                        statusMsg = "¡Correcto! Atacaste por $dmg${if (bonus > 0) " (incluye bonus ⚡)" else ""}."
                                        phase = if (enemyHp <= 0) TurnPhase.GAME_OVER_WIN else TurnPhase.ENEMY_TURN
                                    } else {
                                        val dmg = Random.nextInt(2, 5)
                                        enemyHp = (enemyHp - dmg).coerceAtLeast(0)
                                        statusMsg = "¡Incorrecto! El ataque débil hizo solo $dmg de daño."
                                        phase = if (enemyHp <= 0) TurnPhase.GAME_OVER_WIN else TurnPhase.ENEMY_TURN
                                    }
                                }
                            }
                        }
                    }
                }

                TurnPhase.SHOW_RESULT, TurnPhase.ENEMY_TURN -> {
                    Button(
                        onClick = { phase = TurnPhase.PLAYER_CHOOSE_ACTION },
                        colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
                    ) {
                        Text("CONTINUAR →", fontWeight = FontWeight.Bold)
                    }
                    // Auto-fire enemy turn after show result
                    LaunchedEffect(phase) {
                        if (phase == TurnPhase.ENEMY_TURN) {
                            kotlinx.coroutines.delay(800)
                            doEnemyTurn()
                        }
                    }
                }

                TurnPhase.GAME_OVER_WIN, TurnPhase.GAME_OVER_LOSE -> { /* handled by overlay */ }
            }
        }

        // ── WIN / LOSE OVERLAY ─────────────────────────────────────────────
        if (phase == TurnPhase.GAME_OVER_WIN || phase == TurnPhase.GAME_OVER_LOSE) {
            val isWin = phase == TurnPhase.GAME_OVER_WIN
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { if (isWin) onLevelComplete() else { /* reset */ playerHp = maxPlayerHp; enemyHp = maxEnemyHp; turn = 1; isDefending = false; isAdvanced = false; statusMsg = "¡Tu turno! Elige una acción."; phase = TurnPhase.PLAYER_CHOOSE_ACTION } },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (isWin) "¡VICTORIA! 🏆" else "¡DERROTA! 💀",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWin) accentYellow else Color(0xFFEF5350)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (isWin) "Toca para continuar" else "Toca para intentarlo de nuevo",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Action Button ────────────────────────────────────────────────────────────

@Composable
fun ActionButton(
    label: String,
    color: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(52.dp)
            .widthIn(min = 100.dp)
            .background(color, RoundedCornerShape(26.dp))
            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
}

// ─── Extension helper ─────────────────────────────────────────────────────────
// Draw a top border on a Box
private fun Modifier.topBorderModifier(width: androidx.compose.ui.unit.Dp, color: Color): Modifier =
    this.then(
        Modifier.drawWithContent {
            drawContent()
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end   = androidx.compose.ui.geometry.Offset(size.width, 0f),
                strokeWidth = width.toPx()
            )
        }
    )
