package com.example.hds_tesisapp.ui.theme.games.game4.level5

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game4.buildGlitchRound
import com.example.hds_tesisapp.ui.theme.games.game4.level1.DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game4.level1.FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game4.level1.MenuBtn
import com.example.hds_tesisapp.ui.theme.games.game4.level1.ZoneBadge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findL5G4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findL5G4Activity()
    else              -> null
}

private val GLITCH_C = Color(0xFFE040FB)
private val HP_GREEN = Color(0xFF69FF47)
private val HP_RED   = Color(0xFFFF1744)

@Composable
fun Level5G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL5G4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val TOTAL = 6
    var roundIndex  by remember { mutableIntStateOf(0) }
    var lives       by remember { mutableIntStateOf(3) }
    var flash       by remember { mutableStateOf<Boolean?>(null) }
    var levelDone   by remember { mutableStateOf(false) }
    var gameOver    by remember { mutableStateOf(false) }
    var selectedIdx by remember { mutableIntStateOf(-1) }

    val round = remember(roundIndex) { buildGlitchRound(roundIndex) }
    val scope = rememberCoroutineScope()

    val bossShake = remember { Animatable(0f) }
    val hpFraction = remember(roundIndex) { 1f - roundIndex.toFloat() / TOTAL.toFloat() }

    fun onSymbolTapped(idx: Int) {
        if (flash != null || selectedIdx != -1 || levelDone || gameOver) return
        selectedIdx = idx
        val ok = idx == round.errorIndex
        flash = ok
        scope.launch {
            if (ok) {
                bossShake.animateTo(10f, tween(60))
                bossShake.animateTo(-10f, tween(60))
                bossShake.animateTo(6f, tween(50))
                bossShake.animateTo(0f, tween(50))
            }
            delay(800)
            if (ok) {
                if (roundIndex + 1 >= TOTAL) levelDone = true
                else roundIndex++
            } else {
                lives--
                if (lives <= 0) gameOver = true
            }
            selectedIdx = -1
            flash = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.glitch_valley_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.62f)))

        // Scan-lines glitch effect
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            val h = size.height
            for (i in 0..28) drawLine(
                GLITCH_C.copy(alpha = 0.035f),
                Offset(0f, h * i / 28f), Offset(size.width, h * i / 28f), 2f
            )
        })

        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background(
                if (ok) HP_GREEN.copy(0.18f) else HP_RED.copy(0.18f)
            ))
        }

        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {

            // ── Área de juego ─────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuBtn(onClick = onNavigateToMenu)
                    ZoneBadge("ZONA 4  ·  NIVEL 5", GLITCH_C)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(3) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 18.sp) }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text("ENCUENTRA EL ERROR", fontSize = 14.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                    color = GLITCH_C, letterSpacing = 2.sp)

                Spacer(Modifier.height(10.dp))

                // ── Panel de pista: ciclo correcto ────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A0033))
                        .border(1.5.dp, GLITCH_C.copy(0.50f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Ciclo:", fontSize = 10.sp,
                            fontFamily = OrbitronFontFamily, color = GLITCH_C.copy(0.80f))
                        // Repeat the cycle twice so the pattern is obvious
                        val hint = round.cycleHint + round.cycleHint
                        hint.forEach { sym ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(sym.color.copy(0.25f))
                                    .border(1.dp, sym.color.copy(0.70f), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) { Text(sym.emoji, fontSize = 16.sp) }
                        }
                        Text("…", fontSize = 14.sp, color = GLITCH_C.copy(0.55f))
                        Spacer(Modifier.width(4.dp))
                        Text("¿Cuál rompe el ciclo?", fontSize = 9.sp,
                            fontFamily = Baloo2FontFamily, color = Color.White.copy(0.45f))
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Secuencia completa en una sola fila ───────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    round.sequence.forEachIndexed { idx, sym ->
                        val isSelected = selectedIdx == idx
                        val isCorrectAnswer = flash == true && idx == round.errorIndex
                        val isWrongAnswer   = flash == false && isSelected

                        val borderColor = when {
                            isCorrectAnswer -> HP_GREEN
                            isWrongAnswer   -> HP_RED
                            isSelected      -> GLITCH_C
                            else            -> sym.color.copy(0.55f)
                        }
                        val bgAlpha   = if (isSelected || isCorrectAnswer) 0.45f else 0.18f
                        val cellScale by animateFloatAsState(
                            if (isSelected) 1.10f else 1f,
                            spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                            label = "sc$idx"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer { scaleX = cellScale; scaleY = cellScale }
                        ) {
                            // Position number above cell
                            Text(
                                "${idx + 1}", fontSize = 7.sp,
                                fontFamily = OrbitronFontFamily,
                                color = Color.White.copy(0.30f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(sym.color.copy(bgAlpha))
                                    .border(2.dp, borderColor, RoundedCornerShape(10.dp))
                                    .drawBehind {
                                        if (isCorrectAnswer) drawRoundRect(
                                            HP_GREEN.copy(0.35f),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
                                        )
                                    }
                                    .pointerInput(idx) {
                                        detectTapGestures(onPress = {
                                            tryAwaitRelease(); onSymbolTapped(idx)
                                        })
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sym.emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Text("Ronda ${roundIndex + 1} / $TOTAL", fontSize = 10.sp,
                    fontFamily = OrbitronFontFamily, color = GLITCH_C.copy(0.65f))
            }

            Spacer(Modifier.width(10.dp))

            // ── Panel del jefe ────────────────────────────────────────────────
            BossPanel(
                hpFraction  = hpFraction,
                shakeOffset = bossShake.value,
                modifier    = Modifier.width(130.dp).fillMaxHeight()
            )
        }

        if (levelDone) DoneOverlay(GLITCH_C) { onLevelComplete() }
        if (gameOver)  FailOverlay(GLITCH_C) {
            roundIndex = 0; lives = 3; flash = null; selectedIdx = -1; gameOver = false
        }
    }
}

@Composable
private fun BossPanel(hpFraction: Float, shakeOffset: Float, modifier: Modifier = Modifier) {
    val animHp by animateFloatAsState(hpFraction, tween(600), label = "hp")
    val glowPulse = rememberInfiniteTransition(label = "glow")
    val glow by glowPulse.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "glowVal"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("GLITCH", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            letterSpacing = 2.sp, color = GLITCH_C)
        Spacer(Modifier.height(4.dp))

        // HP label
        Row(
            modifier = Modifier.fillMaxWidth(0.88f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("HP", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                color = GLITCH_C.copy(0.60f))
            Text("${(animHp * 100).toInt()}%", fontSize = 8.sp,
                fontFamily = OrbitronFontFamily, color = GLITCH_C.copy(0.60f))
        }
        Spacer(Modifier.height(3.dp))

        // HP bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(0.10f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animHp.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(HP_RED, GLITCH_C)))
            )
        }

        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.miniboss_zone4),
            contentDescription = "Glitch Boss",
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer { translationX = shakeOffset },
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GLITCH_C.copy(0.12f))
                .border(1.dp, GLITCH_C.copy(glow * 0.7f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "\"¡No me\nencontrarás!\"",
                fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = GLITCH_C.copy(0.85f), textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}
