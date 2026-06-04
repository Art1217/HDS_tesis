package com.example.hds_tesisapp.ui.theme.games.game6.level1

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game6.buildL1G6Round
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal val G6_ORANGE = Color(0xFFFF6D00)
internal val G6_AMBER  = Color(0xFFFFB300)

private const val TOTAL_ROUNDS = 4

@Composable
fun Level1G6Screen(
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
    val scope      = rememberCoroutineScope()

    val round = remember(roundIndex) { buildL1G6Round(roundIndex) }

    fun onTap(option: Int) {
        if (flash != null || done || failed) return
        val correct = option == round.correct
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
            painter = painterResource(R.drawable.factory_repair_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        flash?.let { ok ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                    .zIndex(5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 1 · Reparar la Máquina", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Bucle simple · repetir(N) { accion() }", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G6_AMBER.copy(alpha = 0.8f))
                }
                G6LivesRow(lives)
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(G6_ORANGE.copy(alpha = 0.12f))
                    .border(1.dp, G6_AMBER.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    "Elige cuántas veces debe repetirse la acción para reparar la máquina.",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = G6_AMBER.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: character + task info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.width(160.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.bit_triste),
                        contentDescription = "Bit dañado",
                        modifier = Modifier.height(80.dp),
                        contentScale = ContentScale.Fit
                    )
                    TaskCard(round.taskTitle, round.taskDesc, G6_ORANGE)
                }

                // Center: code block
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("¿Cuántas veces repetir?", fontSize = 10.sp,
                        fontFamily = Baloo2FontFamily, color = G6_AMBER.copy(alpha = 0.7f))
                    CodeBlock(round.codeLines)
                }

                // Right: answer options
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Elige:", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = G6_AMBER.copy(alpha = 0.7f))
                    round.options.forEach { opt ->
                        LoopOptionButton(
                            value   = opt,
                            color   = G6_ORANGE,
                            enabled = flash == null && !done && !failed,
                            onClick = { onTap(opt) }
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G6DoneOverlay { onLevelComplete() }
        if (failed) G6FailOverlay { roundIndex = 0; lives = 3; failed = false }
    }
}

// ─── Shared internal composables ─────────────────────────────────────────────

@Composable
internal fun G6MenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, G6_AMBER.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } },
        contentAlignment = Alignment.Center
    ) {
        Text("☰", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
internal fun G6LivesRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            Text(if (i < lives) "❤️" else "🖤", fontSize = 14.sp)
        }
    }
}

@Composable
internal fun G6RoundDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == current) 9.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i <= current) G6_ORANGE else Color.White.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
internal fun TaskCard(title: String, desc: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
        Text(desc, fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

@Composable
internal fun CodeBlock(lines: List<String>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, G6_AMBER.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        lines.forEach { line ->
            val color = when {
                line.startsWith("//")    -> Color(0xFF69FF47).copy(alpha = 0.75f)
                line.contains("repetir") -> G6_AMBER
                line.trimStart().startsWith("}") -> G6_AMBER
                else -> Color.White.copy(alpha = 0.85f)
            }
            Text(line, fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = color)
        }
    }
}

@Composable
internal fun LoopOptionButton(value: Int, color: Color, enabled: Boolean, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 44.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) color.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f))
            .border(1.5.dp, if (enabled) color.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(10.dp))
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(onPress = {
                    scope.launch { scale.animateTo(0.88f, tween(80)); scale.animateTo(1f, tween(100)) }
                    tryAwaitRelease()
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$value",
            fontSize = 20.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
internal fun G6DoneOverlay(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .zIndex(10f)
            .pointerInput(Unit) { detectTapGestures { onContinue() } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0A00))
                .border(2.dp, G6_AMBER, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Text("⚙️", fontSize = 48.sp)
            Text("¡Reparación Completa!", fontSize = 20.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = G6_AMBER)
            Text("¡La fábrica vuelve a funcionar!", fontSize = 13.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f))
            Text("Toca para continuar →", fontSize = 11.sp,
                fontFamily = Baloo2FontFamily, color = G6_AMBER.copy(alpha = 0.65f))
        }
    }
}

@Composable
internal fun G6FailOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .zIndex(10f)
            .pointerInput(Unit) { detectTapGestures { onRetry() } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0A00))
                .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Text("🔧", fontSize = 48.sp)
            Text("¡La máquina falló!", fontSize = 20.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
            Text("Recalcula los bucles e inténtalo de nuevo.", fontSize = 13.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f))
            Text("Toca para reintentar →", fontSize = 11.sp,
                fontFamily = Baloo2FontFamily, color = Color(0xFFFF5252).copy(alpha = 0.65f))
        }
    }
}
