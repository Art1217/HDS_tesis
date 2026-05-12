package com.example.hds_tesisapp.ui.theme.games.game4.level4

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game4.PatternSymbol
import com.example.hds_tesisapp.ui.theme.games.game4.buildBridgeRound
import com.example.hds_tesisapp.ui.theme.games.game4.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findL4G4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findL4G4Activity()
    else              -> null
}

private val PURPLE  = Color(0xFFCE93D8)
private val DARK_P  = Color(0xFF1A0033)

@Composable
fun Level4G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL4G4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val TOTAL = 5
    var roundIndex  by remember { mutableIntStateOf(0) }
    var lives       by remember { mutableIntStateOf(3) }
    var flash       by remember { mutableStateOf<Boolean?>(null) }
    var levelDone   by remember { mutableStateOf(false) }
    var gameOver    by remember { mutableStateOf(false) }
    var replayAnim  by remember { mutableStateOf(false) }
    var replayIndex by remember { mutableIntStateOf(-1) }

    val round = remember(roundIndex) { buildBridgeRound(roundIndex) }
    val scope = rememberCoroutineScope()

    fun onReplay() {
        if (replayAnim) return
        replayAnim = true
        scope.launch {
            val cycle = round.sequence.take(3)
            for (i in cycle.indices) {
                replayIndex = i % round.sequence.size
                delay(500)
            }
            replayIndex = -1
            replayAnim = false
        }
    }

    fun onPick(sym: PatternSymbol) {
        if (flash != null || levelDone || gameOver || replayAnim) return
        val ok = sym.id == round.answer.id
        flash = ok
        scope.launch {
            delay(700)
            if (ok) {
                if (roundIndex + 1 >= TOTAL) levelDone = true
                else roundIndex++
            } else {
                lives--
                if (lives <= 0) gameOver = true
            }
            flash = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.rhythm_bridge_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.55f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background(
                if (ok) Color(0xFF69FF47).copy(0.22f) else Color(0xFFFF5252).copy(0.22f)))
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuBtn(onClick = onNavigateToMenu)
                ZoneBadge("ZONA 4  ·  NIVEL 4", PURPLE)
                LivesRow(lives)
            }

            Spacer(Modifier.height(8.dp))

            Text("EL PUENTE DEL RITMO", fontSize = 13.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                color = PURPLE, letterSpacing = 2.sp)

            Spacer(Modifier.height(4.dp))
            Text("¡Cuidado! Hay un distractor 🔴 entre las opciones", fontSize = 10.sp,
                fontFamily = Baloo2FontFamily, color = Color(0xFFFF7043).copy(0.85f))

            Spacer(Modifier.height(10.dp))

            // Sequence with replay highlight
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                round.sequence.forEachIndexed { idx, sym ->
                    val highlighted = idx == replayIndex
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (highlighted) sym.color.copy(0.55f) else sym.color.copy(0.18f)
                            )
                            .border(
                                2.dp,
                                if (highlighted) Color.White else sym.color.copy(0.70f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) { Text(sym.emoji, fontSize = 24.sp) }
                }
                QuestionCell(PURPLE)
            }

            Spacer(Modifier.height(12.dp))

            // Replay button
            ReplayButton(isPlaying = replayAnim, accent = PURPLE) { onReplay() }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                round.options.forEach { opt ->
                    OptionBtn(opt, PURPLE, DARK_P) { onPick(opt) }
                }
            }

            Spacer(Modifier.weight(1f))
            RoundDots(roundIndex, TOTAL, PURPLE)
        }

        if (levelDone) DoneOverlay(PURPLE) { onLevelComplete() }
        if (gameOver)  FailOverlay(PURPLE) {
            roundIndex = 0; lives = 3; flash = null; gameOver = false
        }
    }
}

@Composable
private fun ReplayButton(isPlaying: Boolean, accent: Color, onClick: () -> Unit) {
    val pulse = remember { Animatable(0.5f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) { pulse.animateTo(1f, tween(300)); pulse.animateTo(0.4f, tween(300)) }
        } else {
            pulse.snapTo(0.7f)
        }
    }
    Box(
        modifier = Modifier
            .drawBehind {
                drawCircle(accent.copy(alpha = pulse.value * 0.25f),
                    radius = size.minDimension / 2f + 8f)
            }
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.15f))
            .border(1.5.dp, accent.copy(pulse.value), RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectTapGestures(onPress = { tryAwaitRelease(); onClick() })
            }
            .padding(horizontal = 18.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(if (isPlaying) "▶ reproduciendo…" else "▶  Reproducir patrón",
                fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                color = accent.copy(if (isPlaying) 0.65f else 1f))
        }
    }
}
