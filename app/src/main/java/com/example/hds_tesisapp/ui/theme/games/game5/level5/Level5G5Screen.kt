package com.example.hds_tesisapp.ui.theme.games.game5.level5

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.hds_tesisapp.ui.theme.games.game5.*
import com.example.hds_tesisapp.ui.theme.games.game5.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 6
private const val TIMER_MS     = 6000L
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

    var roundIndex  by remember { mutableIntStateOf(0) }
    var lives       by remember { mutableIntStateOf(3) }
    var flash       by remember { mutableStateOf<Boolean?>(null) }
    var done        by remember { mutableStateOf(false) }
    var failed      by remember { mutableStateOf(false) }
    var stateIndex  by remember { mutableIntStateOf(0) }
    var timerFrac   by remember { mutableFloatStateOf(1f) }
    var bossHp      by remember { mutableFloatStateOf(1f) }
    val scope       = rememberCoroutineScope()

    val round = remember(roundIndex) { buildBossRound(roundIndex) }
    val state = round.states[stateIndex.coerceAtMost(round.states.lastIndex)]

    // Boss shake
    val shakeX = remember { Animatable(0f) }

    // Timer coroutine — resets when stateIndex or roundIndex changes
    LaunchedEffect(stateIndex, roundIndex) {
        if (done || failed) return@LaunchedEffect
        val start = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - start
            timerFrac = 1f - (elapsed.toFloat() / TIMER_MS).coerceIn(0f, 1f)
            if (elapsed >= TIMER_MS) {
                // Number changes automatically
                stateIndex = (stateIndex + 1) % round.states.size
                break
            }
            delay(32L)
        }
    }

    fun onTap(portal: Portal) {
        if (flash != null || done || failed) return
        val correct = portal.id == state.correctTap.id
        scope.launch {
            flash = correct
            if (correct) {
                launch { shakeX.animateTo(12f, tween(60)); repeat(4) { shakeX.animateTo(if (it%2==0) -10f else 10f, tween(60)) }; shakeX.animateTo(0f, tween(60)) }
                bossHp = (bossHp - 1f / TOTAL_ROUNDS).coerceAtLeast(0f)
            }
            delay(600)
            flash = null
            if (correct) {
                if (roundIndex + 1 >= TOTAL_ROUNDS) done = true
                else { roundIndex++; stateIndex = 0; timerFrac = 1f }
            } else {
                lives--
                if (lives <= 0) failed = true
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
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        // Glitch scanlines effect
        repeat(12) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .offset(y = (i * 38 + 20).dp)
                    .background(GLITCH_C.copy(alpha = 0.06f))
            )
        }

        flash?.let { ok ->
            Box(modifier = Modifier.fillMaxSize()
                .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                .zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("JEFE · Portales Caóticos", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = GLITCH_C)
                    Text("Condiciones anidadas", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = GLITCH_C.copy(alpha = 0.7f))
                }
                G5LivesRow(lives)
            }

            // Boss HP bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("HP GLITCH", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = GLITCH_C)
                    Text("${(bossHp * 100).toInt()}%", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        color = GLITCH_C)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(bossHp).fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(listOf(GLITCH_C, GLITCH_DARK)))
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Boss panel
                Column(
                    modifier = Modifier.width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.miniboss_zone5),
                        contentDescription = "Glitch Boss",
                        modifier = Modifier
                            .height(100.dp)
                            .graphicsLayer { translationX = shakeX.value }
                    )
                    Text("GLITCH", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = GLITCH_C)
                }

                // Center: number + timer + condition
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Timer bar
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("⏱️ Tiempo", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                                color = Color.White.copy(alpha = 0.6f))
                            Text("¡El número cambiará!", fontSize = 9.sp,
                                fontFamily = Baloo2FontFamily,
                                color = if (timerFrac < 0.3f) Color(0xFFFF5252) else Color.White.copy(alpha = 0.5f))
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(timerFrac)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
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

                    NumberCard(state.number, GLITCH_C)
                    ConditionCard(round.conditionLines)
                }

                // Portals
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    round.portals.forEach { p ->
                        PortalButton(p, flash == null && !done && !failed) { onTap(p) }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G5DoneOverlay { onLevelComplete() }
        if (failed) G5FailOverlay { roundIndex = 0; stateIndex = 0; lives = 3; failed = false; bossHp = 1f }
    }
}
