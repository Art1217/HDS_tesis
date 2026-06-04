package com.example.hds_tesisapp.ui.theme.games.game6.level5

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game6.buildBossG6Question
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6LivesRow
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6MenuButton
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6RoundDots
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6_AMBER
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6_ORANGE
import com.example.hds_tesisapp.ui.theme.games.game6.level1.LoopOptionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 6
private const val TIMER_MS = 10000L
private val BOSS_RED  = Color(0xFFD50000)
private val BOSS_PINK = Color(0xFFFF1744)

@Composable
fun Level5G6Screen(
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
    var timerFrac  by remember { mutableFloatStateOf(1f) }
    var bossHp     by remember { mutableFloatStateOf(1f) }
    var timerDead  by remember { mutableStateOf(false) }
    var timerKey   by remember { mutableIntStateOf(0) }
    val scope      = rememberCoroutineScope()

    val question = remember(roundIndex) { buildBossG6Question(roundIndex) }
    val shakeX = remember { Animatable(0f) }

    LaunchedEffect(roundIndex, timerKey) {
        if (done || failed) return@LaunchedEffect
        timerFrac = 1f
        val start = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - start
            timerFrac = 1f - (elapsed.toFloat() / TIMER_MS).coerceIn(0f, 1f)
            if (elapsed >= TIMER_MS) {
                timerDead = true
                lives--
                delay(800)
                timerDead = false
                if (lives <= 0) { failed = true; break }
                if (roundIndex + 1 < TOTAL_ROUNDS) { roundIndex++ }
                else timerKey++  // restart timer on last round
                break
            }
            delay(32L)
        }
    }

    fun onTap(option: Int) {
        if (flash != null || done || failed || timerDead) return
        val correct = option == question.correct
        scope.launch {
            flash = correct
            if (correct) {
                launch {
                    shakeX.animateTo(10f, tween(50))
                    repeat(4) { shakeX.animateTo(if (it % 2 == 0) -8f else 8f, tween(50)) }
                    shakeX.animateTo(0f, tween(50))
                }
                bossHp = (bossHp - 1f / TOTAL_ROUNDS).coerceAtLeast(0f)
            }
            delay(550)
            flash = null
            if (correct) {
                if (roundIndex + 1 >= TOTAL_ROUNDS) done = true
                else roundIndex++
            } else {
                lives--
                if (lives <= 0) failed = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.factory_boss_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.50f)))

        // Scanlines
        repeat(8) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(1.dp)
                    .offset(y = (i * 55 + 28).dp)
                    .background(BOSS_RED.copy(alpha = 0.05f))
            )
        }

        val flashColor = when {
            timerDead        -> Color(0xFFFF5252)
            flash == true    -> Color(0xFF69FF47)
            flash == false   -> Color(0xFFFF5252)
            else             -> null
        }
        flashColor?.let { col ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(col.copy(alpha = 0.22f))
                    .zIndex(5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("JEFE · El Glitch Maestro", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                        color = BOSS_PINK)
                    Text("Bucles anidados · ¡Responde antes de que el glitch gane!", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = BOSS_PINK.copy(alpha = 0.7f))
                }
                G6LivesRow(lives)
            }

            // ── HP + Timer bars ─────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("HP GLITCH", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = BOSS_PINK)
                        Text("${(bossHp * 100).toInt()}%", fontSize = 8.sp,
                            fontFamily = OrbitronFontFamily, color = BOSS_PINK)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(bossHp.coerceIn(0f, 1f)).fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(listOf(BOSS_PINK, BOSS_RED))))
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("⏱️ TIEMPO", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (timerFrac < 0.3f) Color(0xFFFF5252) else Color.White.copy(alpha = 0.6f))
                        Text(
                            if (timerFrac < 0.3f) "¡Se acaba! Pierdes ❤️" else "¡Calcula rápido!",
                            fontSize = 8.sp, fontFamily = Baloo2FontFamily,
                            color = if (timerFrac < 0.3f) Color(0xFFFF5252) else Color.White.copy(alpha = 0.45f)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(timerFrac.coerceIn(0f, 1f)).fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(listOf(
                                if (timerFrac < 0.3f) Color(0xFFFF5252) else G6_AMBER,
                                if (timerFrac < 0.3f) Color(0xFFFF1744) else G6_ORANGE
                            ))))
                    }
                }
            }

            // ── Center: duelo Bit Maestro vs Glitch ─────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Izquierda: Bit Maestro (jugador) ──────────────────────────
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.bit_maestro),
                        contentDescription = "Bit Maestro",
                        modifier = Modifier.height(110.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text("BIT MAESTRO", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = G6_AMBER)
                }

                // ── Centro: pregunta + código ──────────────────────────────────
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BOSS_RED.copy(alpha = 0.12f))
                            .border(1.dp, BOSS_PINK.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(question.question, fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, BOSS_PINK.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        question.codeLines.forEach { line ->
                            val color = when {
                                line.trimStart().startsWith("repetir") &&
                                        !line.trimStart().startsWith("  repetir") -> G6_AMBER
                                line.trimStart().startsWith("  repetir") -> Color(0xFF40C4FF)
                                line.trimStart() == "}" -> G6_AMBER
                                else -> Color.White.copy(alpha = 0.85f)
                            }
                            Text(line, fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = color)
                        }
                    }
                }

                // ── Derecha: Glitch (jefe) + opciones ─────────────────────────
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.miniboss_zone6),
                        contentDescription = "Glitch Boss",
                        modifier = Modifier
                            .height(110.dp)
                            .graphicsLayer { translationX = shakeX.value },
                        contentScale = ContentScale.Fit
                    )
                    Text("GLITCH", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = BOSS_PINK)
                    Spacer(Modifier.height(2.dp))
                    Text("¿Total?", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                        color = BOSS_PINK.copy(alpha = 0.7f))
                    question.options.forEach { opt ->
                        LoopOptionButton(
                            value   = opt,
                            color   = BOSS_PINK,
                            enabled = flash == null && !done && !failed && !timerDead,
                            onClick = { onTap(opt) }
                        )
                    }
                }
            }

            // ── Footer ───────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G6DoneOverlay { onLevelComplete() }
        if (failed) G6FailOverlay {
            roundIndex = 0; lives = 3; failed = false; bossHp = 1f; timerFrac = 1f; timerKey = 0
        }
    }
}
