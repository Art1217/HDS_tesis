package com.example.hds_tesisapp.ui.theme.games.game5.level5

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game5.Portal
import com.example.hds_tesisapp.ui.theme.games.game5.buildBossRound
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5LivesRow
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5MenuButton
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5RoundDots
import com.example.hds_tesisapp.ui.theme.games.game5.level1.NumberCard
import com.example.hds_tesisapp.ui.theme.games.game5.level1.PortalButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 6
private const val TIMER_MS     = 7000L
private val GLITCH_C           = Color(0xFFE040FB)
private val GLITCH_DARK        = Color(0xFF9C27B0)

@Composable
fun Level5G5Screen(
    onLevelComplete: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var roundIndex by remember { mutableIntStateOf(0) }
    var lives      by remember { mutableIntStateOf(3) }
    var flash      by remember { mutableStateOf<Boolean?>(null) }
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    var stateIndex by remember { mutableIntStateOf(0) }
    var timerFrac  by remember { mutableFloatStateOf(1f) }
    var bossHp     by remember { mutableFloatStateOf(1f) }
    var timerDead  by remember { mutableStateOf(false) }   // true during timer-penalty flash
    val scope      = rememberCoroutineScope()

    val round = remember(roundIndex) { buildBossRound(roundIndex) }
    val state = round.states[stateIndex.coerceAtMost(round.states.lastIndex)]

    val shakeX = remember { Animatable(0f) }

    // Timer: agotarse = pierde vida + muestra flash rojo, luego siguiente número
    LaunchedEffect(stateIndex, roundIndex) {
        if (done || failed) return@LaunchedEffect
        val start = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - start
            timerFrac = 1f - (elapsed.toFloat() / TIMER_MS).coerceIn(0f, 1f)
            if (elapsed >= TIMER_MS) {
                // Penalty: lose a life for not answering in time
                timerDead = true
                lives--
                delay(800)
                timerDead = false
                if (lives <= 0) { failed = true; break }
                // Advance to next number
                stateIndex = (stateIndex + 1) % round.states.size
                break
            }
            delay(32L)
        }
    }

    fun onTap(portal: Portal) {
        if (flash != null || done || failed || timerDead) return
        val correct = portal.id == state.correctTap.id
        scope.launch {
            flash = correct
            if (correct) {
                launch {
                    shakeX.animateTo(12f, tween(55))
                    repeat(4) { shakeX.animateTo(if (it % 2 == 0) -10f else 10f, tween(55)) }
                    shakeX.animateTo(0f, tween(55))
                }
                bossHp = (bossHp - 1f / TOTAL_ROUNDS).coerceAtLeast(0f)
            }
            delay(550)
            flash = null
            if (correct) {
                if (roundIndex + 1 >= TOTAL_ROUNDS) done = true
                else { roundIndex++; stateIndex = 0; timerFrac = 1f }
            } else {
                lives--
                if (lives <= 0) failed = true
                else { stateIndex = (stateIndex + 1) % round.states.size }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.portal_glitch_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))

        // Glitch scanlines
        repeat(10) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(1.dp)
                    .offset(y = (i * 44 + 22).dp)
                    .background(GLITCH_C.copy(alpha = 0.05f))
            )
        }

        // Flash overlay (correct/wrong tap OR timer-dead)
        val flashColor = when {
            timerDead  -> Color(0xFFFF5252)
            flash == true  -> Color(0xFF69FF47)
            flash == false -> Color(0xFFFF5252)
            else -> null
        }
        flashColor?.let { col ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(col.copy(alpha = 0.25f))
                    .zIndex(5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("JEFE · Portales Caóticos", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                        color = GLITCH_C)
                    Text("Condición anidada · ¡Responde antes de que cambie!", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = GLITCH_C.copy(alpha = 0.7f))
                }
                G5LivesRow(lives)
            }

            // ── HP + Timer bars ─────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.spacedBy(16.dp),
                Alignment.CenterVertically
            ) {
                // Boss HP
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("HP GLITCH", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = GLITCH_C)
                        Text("${(bossHp * 100).toInt()}%", fontSize = 8.sp,
                            fontFamily = OrbitronFontFamily, color = GLITCH_C)
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(bossHp.coerceIn(0f, 1f)).fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.horizontalGradient(listOf(GLITCH_C, GLITCH_DARK)))
                        )
                    }
                }

                // Timer
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("⏱️ TIEMPO", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (timerFrac < 0.3f) Color(0xFFFF5252) else Color.White.copy(alpha = 0.6f))
                        Text(
                            if (timerFrac < 0.3f) "¡Se acaba! Pierdes ❤️" else "¡Responde antes!",
                            fontSize = 8.sp, fontFamily = Baloo2FontFamily,
                            color = if (timerFrac < 0.3f) Color(0xFFFF5252) else Color.White.copy(alpha = 0.45f)
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(timerFrac.coerceIn(0f, 1f)).fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            if (timerFrac < 0.3f) Color(0xFFFF5252) else Color(0xFF40C4FF),
                                            if (timerFrac < 0.3f) Color(0xFFFF1744) else GLITCH_C
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            // ── Center content ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Boss image
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.miniboss_zone5),
                        contentDescription = "Glitch Boss",
                        modifier = Modifier
                            .height(90.dp)
                            .graphicsLayer { translationX = shakeX.value }
                    )
                    Text("GLITCH", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = GLITCH_C)
                }

                // Number + step-by-step evaluation
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberCard(state.number, GLITCH_C)
                    BossConditionPanel(state.number)
                }

                // 3 portals in a ROW (easier to see and tap)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("¿Cuál portal?", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                        color = GLITCH_C.copy(alpha = 0.7f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        round.portals.forEach { p ->
                            PortalButton(
                                portal  = p,
                                enabled = flash == null && !done && !failed && !timerDead,
                                onClick = { onTap(p) }
                            )
                        }
                    }
                }
            }

            // ── Footer ───────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G5DoneOverlay { onLevelComplete() }
        if (failed) G5FailOverlay {
            roundIndex = 0; stateIndex = 0; lives = 3
            failed = false; bossHp = 1f; timerFrac = 1f
        }
    }
}

// ─── Panel de evaluación paso a paso ─────────────────────────────────────────

@Composable
private fun BossConditionPanel(n: Int) {
    val mayorQue5 = n > 5
    val esPar     = n % 2 == 0

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, GLITCH_C.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("EVALÚA PASO A PASO:", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = GLITCH_C, letterSpacing = 0.8.sp)

        // Paso 1: ¿mayor que 5?
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepChip(if (mayorQue5) "✅" else "❌", if (mayorQue5) Color(0xFF4CAF50) else Color(0xFFF44336))
            Text(
                "¿$n es mayor que 5?  →  ${if (mayorQue5) "SÍ" else "NO"}",
                fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.9f)
            )
        }

        // Paso 2: depende del resultado anterior
        if (mayorQue5) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepChip(if (esPar) "✅" else "❌", if (esPar) Color(0xFF4CAF50) else Color(0xFFF44336))
                Text(
                    "¿$n es par?  →  ${if (esPar) "SÍ" else "NO"}",
                    fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Condición
        Text(
            "Si número > 5: par→Azul  impar→Verde\nSi número ≤ 5: → Rojo",
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = GLITCH_C.copy(alpha = 0.75f), lineHeight = 15.sp
        )
    }
}

@Composable
private fun StepChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
