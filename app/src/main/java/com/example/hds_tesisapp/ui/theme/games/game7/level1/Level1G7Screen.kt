package com.example.hds_tesisapp.ui.theme.games.game7.level1

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
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
import com.example.hds_tesisapp.ui.theme.games.game7.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal val G7_CYAN = Color(0xFF00E5FF)
internal val G7_TEAL = Color(0xFF00BFA5)

@Composable
fun Level1G7Screen(
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
    var done        by remember { mutableStateOf(false) }
    var failed      by remember { mutableStateOf(false) }
    var flash       by remember { mutableStateOf<Boolean?>(null) }
    val scope       = rememberCoroutineScope()

    val puzzle      = remember(roundIndex) { buildL1G7Puzzle(roundIndex) }
    var current     by remember(roundIndex) { mutableIntStateOf(puzzle.initial) }
    var opsUsed     by remember(roundIndex) { mutableIntStateOf(0) }
    var usesLeft    by remember(roundIndex) { mutableStateOf(puzzle.ops.map { it.maxUses }) }

    fun reset() {
        current = puzzle.initial
        opsUsed = 0
        usesLeft = puzzle.ops.map { it.maxUses }
    }

    fun onOp(idx: Int) {
        if (flash != null || done || failed) return
        val op = puzzle.ops[idx]
        if (usesLeft[idx] <= 0) return
        // Disable division if not evenly divisible
        if (op.label.startsWith("÷") && current % op.apply(0) != 0) {
            val divisor = when (op.label) { "÷2" -> 2; "÷3" -> 3; "÷4" -> 4; "÷5" -> 5; else -> 1 }
            if (current % divisor != 0) return
        }
        val newVal = op.apply(current)
        current = newVal
        usesLeft = usesLeft.toMutableList().also { it[idx]-- }
        opsUsed++

        if (current == puzzle.target) {
            scope.launch {
                flash = true
                delay(600)
                flash = null
                if (roundIndex + 1 >= TOTAL_ROUNDS_L1) done = true
                else roundIndex++
            }
        } else if (opsUsed >= puzzle.maxOps) {
            scope.launch {
                flash = false
                delay(700)
                flash = null
                lives--
                if (lives <= 0) failed = true
                else reset()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.lab_door_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize()
                .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                .zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G7MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 1 · La Primera Puerta", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Aplica operaciones para llegar a la energía exacta", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G7_CYAN.copy(alpha = 0.8f))
                }
                G7LivesRow(lives)
            }

            // Centro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Personaje
                Image(painterResource(R.drawable.lina_lab), "Lina",
                    Modifier.height(130.dp), contentScale = ContentScale.Fit)

                // Panel de la puerta
                DoorPanel(
                    current = current,
                    target  = puzzle.target,
                    opsUsed = opsUsed,
                    maxOps  = puzzle.maxOps,
                    modifier = Modifier.weight(1f)
                )

                // Botones de operación
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    puzzle.ops.forEachIndexed { idx, op ->
                        OpButton(
                            label    = op.label,
                            uses     = usesLeft[idx],
                            enabled  = flash == null && !done && !failed && usesLeft[idx] > 0
                                    && opsUsed < puzzle.maxOps && current != puzzle.target,
                            color    = G7_CYAN,
                            onClick  = { onOp(idx) }
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    ResetButton { reset() }
                }
            }

            // Footer
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G7RoundDots(roundIndex, TOTAL_ROUNDS_L1)
                Text("Sala ${roundIndex + 1} / $TOTAL_ROUNDS_L1", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G7DoneOverlay("¡Todas las puertas abiertas!") { onLevelComplete() }
        if (failed) G7FailOverlay { roundIndex = 0; lives = 3; failed = false }
    }
}

// ─── Composables internos compartidos ─────────────────────────────────────────

@Composable
internal fun G7MenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, G7_TEAL.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } },
        contentAlignment = Alignment.Center
    ) { Text("☰", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f)) }
}

@Composable
internal fun G7LivesRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 14.sp) }
    }
}

@Composable
internal fun G7RoundDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            Box(Modifier.size(if (i == current) 9.dp else 6.dp).clip(CircleShape)
                .background(if (i <= current) G7_CYAN else Color.White.copy(alpha = 0.2f)))
        }
    }
}

@Composable
internal fun DoorPanel(current: Int, target: Int, opsUsed: Int, maxOps: Int, modifier: Modifier = Modifier) {
    val reached = current == target
    val borderColor = if (reached) Color(0xFF69FF47) else G7_CYAN
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(2.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Meta
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(G7_CYAN.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text("⚡ META: ", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    color = G7_CYAN, fontWeight = FontWeight.Bold)
                Text("$target", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(" ⚡", fontSize = 11.sp, color = G7_CYAN)
            }

            // Valor actual — grande
            Text(
                "$current",
                fontSize = 52.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold,
                color = if (reached) Color(0xFF69FF47) else Color.White
            )
            if (reached) Text("✅ ¡Exacto!", fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color(0xFF69FF47), fontWeight = FontWeight.Bold)

            // Contador de operaciones
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (opsUsed >= maxOps) Color(0xFFFF5252).copy(alpha = 0.15f)
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    "OPS: $opsUsed / $maxOps",
                    fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = if (opsUsed >= maxOps) Color(0xFFFF5252) else Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
internal fun FlaskPanel(current: Int, target: Int, opsUsed: Int, maxOps: Int, flaskColor: Color, modifier: Modifier = Modifier) {
    val reached = current == target
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Objetivo
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(flaskColor.copy(alpha = 0.15f))
                .border(1.5.dp, flaskColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("OBJETIVO:", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = flaskColor, fontWeight = FontWeight.Bold)
                Text("$target", fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
        // Frasco dibujado en Compose
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 140.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp, topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(listOf(
                        Color.Black.copy(alpha = 0.3f),
                        flaskColor.copy(alpha = 0.25f),
                        flaskColor.copy(alpha = 0.45f)
                    ))
                )
                .border(2.dp, flaskColor.copy(alpha = 0.6f),
                    RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp, topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$current",
                fontSize = 36.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold,
                color = if (reached) Color(0xFF69FF47) else Color.White
            )
        }
        if (reached) Text("✅ ¡Objetivo!", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
            color = Color(0xFF69FF47), fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (opsUsed >= maxOps) Color(0xFFFF5252).copy(alpha = 0.15f)
                    else Color.White.copy(alpha = 0.05f)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text("OPS: $opsUsed / $maxOps", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                color = if (opsUsed >= maxOps) Color(0xFFFF5252) else Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun OpButton(label: String, uses: Int, enabled: Boolean, color: Color, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 44.dp)
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .clip(RoundedCornerShape(10.dp))
                .background(if (enabled) color.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f))
                .border(1.5.dp, if (enabled) color.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(onPress = {
                        scope.launch { scale.animateTo(0.88f, tween(70)); scale.animateTo(1f, tween(90)) }
                        tryAwaitRelease(); onClick()
                    })
                },
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold,
                color = if (enabled) Color.White else Color.White.copy(alpha = 0.25f))
        }
        Text("$uses usos", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
            color = if (uses > 0) color.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.2f))
    }
}

@Composable
internal fun ResetButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("↺ RESET", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
    }
}

@Composable
internal fun G7DoneOverlay(message: String, onContinue: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f))
            .zIndex(10f).pointerInput(Unit) { detectTapGestures { onContinue() } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF001020))
                .border(2.dp, G7_CYAN, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Text("🧪", fontSize = 48.sp)
            Text("¡Nivel Superado!", fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G7_CYAN)
            Text(message, fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Text("Toca para continuar →", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                color = G7_TEAL.copy(alpha = 0.65f))
        }
    }
}

@Composable
internal fun G7FailOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f))
            .zIndex(10f).pointerInput(Unit) { detectTapGestures { onRetry() } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Text("⚗️", fontSize = 48.sp)
            Text("¡Experimento fallido!", fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
            Text("Recalcula las operaciones e inténtalo de nuevo.", fontSize = 13.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f))
            Text("Toca para reintentar →", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                color = Color(0xFFFF5252).copy(alpha = 0.65f))
        }
    }
}
