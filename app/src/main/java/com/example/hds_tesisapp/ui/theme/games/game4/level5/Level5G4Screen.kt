package com.example.hds_tesisapp.ui.theme.games.game4.level5

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game4.GlitchRound
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

private val GLITCH_C  = Color(0xFFE040FB)
private val GLITCH_D  = Color(0xFF1A0033)
private val HP_GREEN  = Color(0xFF69FF47)
private val HP_RED    = Color(0xFFFF1744)

@Composable
fun Level5G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL5G4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val TOTAL = 6
    var roundIndex   by remember { mutableIntStateOf(0) }
    var lives        by remember { mutableIntStateOf(3) }
    var flash        by remember { mutableStateOf<Boolean?>(null) }
    var levelDone    by remember { mutableStateOf(false) }
    var gameOver     by remember { mutableStateOf(false) }
    var selectedIdx  by remember { mutableIntStateOf(-1) }

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
            delay(700)
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
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.60f)))

        // Glitch scan-lines effect
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            val h = size.height
            for (i in 0..30) {
                drawLine(
                    Color(0xFFE040FB).copy(alpha = 0.04f),
                    Offset(0f, h * i / 30f), Offset(size.width, h * i / 30f), 2f
                )
            }
        })

        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background(
                if (ok) Color(0xFF69FF47).copy(0.20f) else Color(0xFFFF5252).copy(0.20f)))
        }

        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {

            // Left: game area
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Spacer(Modifier.height(10.dp))

                Text("ENCUENTRA EL ERROR", fontSize = 14.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                    color = GLITCH_C, letterSpacing = 2.sp)
                Spacer(Modifier.height(4.dp))
                Text("Toca el símbolo que NO sigue el patrón", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(0.60f))

                Spacer(Modifier.height(14.dp))

                // Sequence - wrap in rows of up to 5
                val seqChunks = round.sequence.chunked(5)
                seqChunks.forEachIndexed { rowIdx, chunk ->
                    val baseOffset = rowIdx * 5
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        chunk.forEachIndexed { colIdx, sym ->
                            val idx = baseOffset + colIdx
                            val isSelected = selectedIdx == idx
                            val isError = flash == true && idx == round.errorIndex
                            val borderColor = when {
                                isError    -> HP_GREEN
                                isSelected && flash == false -> HP_RED
                                isSelected -> GLITCH_C
                                else       -> sym.color.copy(0.55f)
                            }
                            val bgAlpha = if (isSelected) 0.40f else 0.15f

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(sym.color.copy(bgAlpha))
                                    .border(2.dp, borderColor, RoundedCornerShape(10.dp))
                                    .pointerInput(idx) {
                                        detectTapGestures(onPress = {
                                            tryAwaitRelease(); onSymbolTapped(idx)
                                        })
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sym.emoji, fontSize = 22.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Round counter
                Text("Ronda ${roundIndex + 1} / $TOTAL", fontSize = 10.sp,
                    fontFamily = OrbitronFontFamily, color = GLITCH_C.copy(0.70f))
            }

            Spacer(Modifier.width(12.dp))

            // Right: boss panel
            BossPanel(
                hpFraction = hpFraction,
                shakeOffset = bossShake.value,
                modifier = Modifier.width(140.dp).fillMaxHeight()
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

        // HP bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White.copy(0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animHp.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(HP_RED, GLITCH_C)
                        )
                    )
            )
        }

        Spacer(Modifier.height(8.dp))

        // Boss image
        Image(
            painter = painterResource(R.drawable.miniboss_zone4),
            contentDescription = "Glitch Boss",
            modifier = Modifier
                .size(110.dp)
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
