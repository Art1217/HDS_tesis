package com.example.hds_tesisapp.ui.theme.games.game5.level1

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.hds_tesisapp.ui.theme.games.game5.Portal
import com.example.hds_tesisapp.ui.theme.games.game5.buildLevel1Round
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Compartidos (usados por niveles 2-5) ─────────────────────────────────────

private val ZONE5_ACCENT  = Color(0xFF9C27B0)
private val ZONE5_LT      = Color(0xFFCE93D8)
private val ZONE5_BG_TOP  = Color(0xFF0D0020)
private val ZONE5_BG_BOT  = Color(0xFF260050)
private const val MAX_LIVES = 3

internal fun Color.Companion.zone5Accent()  = ZONE5_ACCENT
internal fun Color.Companion.zone5Lt()      = ZONE5_LT
internal fun Color.Companion.zone5BgTop()   = ZONE5_BG_TOP
internal fun Color.Companion.zone5BgBot()   = ZONE5_BG_BOT

@Composable
internal fun G5MenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, ZONE5_LT.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } },
        contentAlignment = Alignment.Center
    ) {
        Text("☰", fontSize = 18.sp, color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
internal fun G5LivesRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(MAX_LIVES) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 16.sp) }
    }
}

@Composable
internal fun NumberCard(number: Int, accent: Color = ZONE5_ACCENT) {
    val pulse = rememberInfiniteTransition(label = "numPulse")
    val glow by pulse.animateFloat(
        0.3f, 0.9f,
        infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "nPulse"
    )
    Box(
        modifier = Modifier
            .drawBehind { drawRoundRect(accent.copy(alpha = glow * 0.4f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()), style = Stroke(12f)) }
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.2f), ZONE5_BG_BOT.copy(alpha = 0.8f)))
            )
            .border(2.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(horizontal = 28.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            fontSize = 42.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold, color = Color.White
        )
    }
}

@Composable
internal fun ConditionCard(lines: List<String>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, ZONE5_LT.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text("CONDICIÓN:", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = ZONE5_LT, letterSpacing = 1.sp)
        lines.forEach { line ->
            Text(line, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.88f), lineHeight = 17.sp)
        }
    }
}

@Composable
internal fun PortalButton(
    portal: Portal,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val pulse = rememberInfiniteTransition(label = "pp${portal.id}")
    val glow by pulse.animateFloat(
        0.35f, 0.85f,
        infiniteRepeatable(tween(1000 + portal.id * 120), RepeatMode.Reverse), label = "pg"
    )
    var pressed by remember { mutableStateOf(false) }
    val sc by animateFloatAsState(if (pressed) 0.88f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh), label = "ps")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.graphicsLayer { scaleX = sc; scaleY = sc }
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(portal.color.copy(alpha = glow * 0.45f), radius = size.minDimension / 2f + 20f)
                    drawCircle(portal.color.copy(alpha = glow * 0.2f),  radius = size.minDimension / 2f + 36f)
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(portal.color.copy(alpha = 0.35f), ZONE5_BG_BOT.copy(alpha = 0.9f)))
                )
                .border(3.dp, portal.color.copy(alpha = if (pressed) 1f else 0.75f), CircleShape)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = {
                        pressed = true; tryAwaitRelease(); pressed = false; onClick()
                    })
                },
            contentAlignment = Alignment.Center
        ) {
            Text(portal.emoji, fontSize = 38.sp)
        }
        Text(
            "Portal ${portal.name}",
            fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = portal.color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun G5DoneOverlay(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .zIndex(10f)
            .pointerInput(Unit) { detectTapGestures { onContinue() } },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("🎉", fontSize = 52.sp)
            Text("¡NIVEL COMPLETO!", fontSize = 22.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = ZONE5_LT)
            Text("Toca para continuar", fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
internal fun G5FailOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .zIndex(10f)
            .pointerInput(Unit) { detectTapGestures { onRetry() } },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("💀", fontSize = 52.sp)
            Text("¡FALLASTE!", fontSize = 22.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
            Text("Toca para reintentar", fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
internal fun G5RoundDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == current) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(if (i < current) ZONE5_ACCENT else if (i == current) ZONE5_LT else Color.White.copy(0.2f))
            )
        }
    }
}

// ─── Level 1 ─────────────────────────────────────────────────────────────────

private const val TOTAL_ROUNDS = 5

@Composable
fun Level1G5Screen(
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
    var lives      by remember { mutableIntStateOf(MAX_LIVES) }
    var flash      by remember { mutableStateOf<Boolean?>(null) }
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()

    val round = remember(roundIndex) { buildLevel1Round(roundIndex) }

    fun onTap(portal: Portal) {
        if (flash != null || done || failed) return
        val correct = portal.id == round.correctTap.id
        scope.launch {
            flash = correct
            delay(600)
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
            painter = painterResource(R.drawable.portal_room_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

        // Flash overlay
        flash?.let { ok ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                    .zIndex(5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 1 · El Portal Inicial", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Zona 5 · La Ciudad de los Portales", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = ZONE5_LT.copy(alpha = 0.8f))
                }
                G5LivesRow(lives)
            }

            // Instrucción clara
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF9C27B0).copy(alpha = 0.18f))
                    .border(1.dp, Color(0xFFCE93D8).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text("Evalúa la condición con el número y toca el portal correcto.",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = Color(0xFFCE93D8), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

            // Center content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: number + condition
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NumberCard(round.number)
                    ConditionCard(round.conditionLines)
                }

                // Right: portals
                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    round.portals.forEach { p ->
                        PortalButton(p, flash == null && !done && !failed) { onTap(p) }
                    }
                }
            }

            // Footer
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G5DoneOverlay { onLevelComplete() }
        if (failed) G5FailOverlay {
            roundIndex = 0; lives = MAX_LIVES; failed = false
        }
    }
}
