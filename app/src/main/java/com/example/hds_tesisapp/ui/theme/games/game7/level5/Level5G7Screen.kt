package com.example.hds_tesisapp.ui.theme.games.game7.level5

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
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
import com.example.hds_tesisapp.ui.theme.games.game7.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CENTRAL_GOLD  = Color(0xFFFFD700)
private val CENTRAL_CYAN  = Color(0xFF00E5FF)
private val CENTRAL_GREEN = Color(0xFF00E676)
private val CENTRAL_PURPLE = Color(0xFFCE93D8)

private const val TOTAL_PHASES   = 3
private const val TOTAL_TIMER_MS = 90_000L
private const val ALT_OP_MS      = 3_000L
private const val TARGET_SHIFT_MS = 5_000L

@Composable
fun Level5G7Screen(
    onLevelComplete: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var phaseIndex    by remember { mutableIntStateOf(0) }
    var done          by remember { mutableStateOf(false) }
    var failed        by remember { mutableStateOf(false) }
    var flash         by remember { mutableStateOf<Boolean?>(null) }
    var timeLeftMs    by remember { mutableLongStateOf(TOTAL_TIMER_MS) }
    var altOpIndex    by remember { mutableIntStateOf(0) }
    var targetShiftIdx by remember { mutableIntStateOf(0) }
    var phaseKey      by remember { mutableIntStateOf(0) }
    val scope         = rememberCoroutineScope()

    val phase   = remember(phaseIndex) { L5_PHASES[phaseIndex] }
    var current by remember(phaseIndex, phaseKey) { mutableIntStateOf(phase.initial) }

    val effectiveTarget = remember(phaseIndex, targetShiftIdx) {
        if (phaseIndex == 2) L5_PHASE3_TARGETS[targetShiftIdx % L5_PHASE3_TARGETS.size]
        else phase.target
    }

    val altOp = remember(altOpIndex) { L5_ALT_OPS[altOpIndex % L5_ALT_OPS.size] }

    val displayOps = remember(phaseIndex, altOpIndex) {
        val base = phase.baseOps.toMutableList()
        if (base.size >= 2) base[1] = altOp
        base
    }

    // Global countdown
    LaunchedEffect(Unit) {
        while (timeLeftMs > 0 && !done && !failed) {
            delay(100L)
            timeLeftMs -= 100L
        }
        if (!done && !failed) failed = true
    }

    // Rotate alt ops every 3s
    LaunchedEffect(Unit) {
        while (!done && !failed) {
            delay(ALT_OP_MS)
            altOpIndex++
        }
    }

    // Shift phase 3 target every 5s
    LaunchedEffect(phaseIndex) {
        if (phaseIndex == 2) {
            while (!done && !failed) {
                delay(TARGET_SHIFT_MS)
                targetShiftIdx++
            }
        }
    }

    fun onOp(op: L5Op) {
        if (flash != null || done || failed) return
        val divisor = when (op.label) { "÷2" -> 2; "÷3" -> 3; "÷4" -> 4; "÷5" -> 5; else -> 0 }
        if (divisor > 0 && current % divisor != 0) return
        val next = op.apply(current)
        current = next
        if (next == effectiveTarget) {
            scope.launch {
                flash = true; delay(600); flash = null
                if (phaseIndex + 1 >= TOTAL_PHASES) {
                    done = true
                } else {
                    phaseIndex++
                }
            }
        }
    }

    fun resetPhase() {
        phaseKey++
    }

    val timerFrac = (timeLeftMs / TOTAL_TIMER_MS.toFloat()).coerceIn(0f, 1f)
    val timerColor = when {
        timerFrac > 0.5f -> CENTRAL_GREEN
        timerFrac > 0.25f -> CENTRAL_GOLD
        else -> Color(0xFFFF5252)
    }

    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(800)); glow.animateTo(0.4f, tween(800)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.lab_central_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.50f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize()
                .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                .zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G7MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 5 · Sala Central", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Activa los tres sistemas con los compuestos", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = CENTRAL_CYAN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    PhaseDots(phaseIndex, TOTAL_PHASES)
                    TimerChip(timeLeftMs, timerColor)
                }
            }

            // ── Main content ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character
                Column(
                    modifier = Modifier.width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .drawBehind {
                                drawCircle(CENTRAL_CYAN.copy(alpha = glow.value * 0.3f),
                                    radius = size.minDimension / 2f + 14f)
                            }
                            .clip(CircleShape)
                            .background(CENTRAL_CYAN.copy(alpha = 0.08f))
                            .border(2.dp, CENTRAL_CYAN.copy(alpha = glow.value * 0.7f + 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painterResource(R.drawable.lina_lab), "Lina",
                            Modifier.fillMaxSize(0.88f), contentScale = ContentScale.Fit)
                    }
                    Text("LINA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = CENTRAL_CYAN)
                    CompoundStatusColumn(phaseIndex)
                }

                // Central panel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.5.dp, phaseColor(phaseIndex).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PhaseLabel(phaseIndex)

                    // Timer bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(timerFrac)
                                .clip(RoundedCornerShape(3.dp))
                                .background(timerColor)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current value
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ACTUAL", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                color = Color.White.copy(alpha = 0.5f))
                            Text("$current", fontSize = 42.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Text("→", fontSize = 28.sp, color = Color.White.copy(alpha = 0.4f))
                        // Target
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (phaseIndex == 2) "META ↺" else "META",
                                fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                color = phaseColor(phaseIndex).copy(alpha = 0.7f))
                            Text("$effectiveTarget", fontSize = 42.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.ExtraBold, color = phaseColor(phaseIndex))
                        }
                    }

                    if (phaseIndex == 2) {
                        Text("⚡ El objetivo cambia cada 5s — ¡adáptate!", fontSize = 9.sp,
                            fontFamily = Baloo2FontFamily, color = CENTRAL_GOLD.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center)
                    }
                }

                // Ops column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (phaseIndex >= 1) {
                        Text("Op rotativa ↺", fontSize = 8.sp, fontFamily = Baloo2FontFamily,
                            color = CENTRAL_GOLD.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    }
                    displayOps.forEachIndexed { idx, op ->
                        val isRotating = phaseIndex >= 1 && idx == 1
                        L5OpButton(op.label, phaseColor(phaseIndex), flash == null && !done && !failed, isRotating) {
                            onOp(op)
                        }
                    }
                    ResetButton { resetPhase() }
                }
            }

            // ── Footer ────────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                Text("Fase ${phaseIndex + 1} / $TOTAL_PHASES   •   Toca una operación para modificar la variable",
                    fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.4f))
            }
        }

        if (done)   G7DoneOverlay("¡Sala Central activada!\n🔬 Los tres compuestos funcionan.") { onLevelComplete() }
        if (failed) L5FailOverlay { phaseIndex = 0; done = false; failed = false; timeLeftMs = TOTAL_TIMER_MS; altOpIndex = 0; targetShiftIdx = 0; phaseKey++ }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun phaseColor(phaseIndex: Int) = when (phaseIndex) {
    0 -> Color(0xFF00E5FF)
    1 -> Color(0xFF00E676)
    else -> Color(0xFFCE93D8)
}

@Composable
private fun PhaseDots(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val color = phaseColor(i)
            Box(
                modifier = Modifier
                    .size(if (i == current) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(if (i <= current) color else color.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun TimerChip(timeLeftMs: Long, color: Color) {
    val secs = (timeLeftMs / 1000L).toInt()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text("⏱ ${secs}s", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun CompoundStatusColumn(phaseIndex: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        listOf(
            Triple("COMP. A", phaseIndex >= 1, CENTRAL_GREEN),
            Triple("COMP. B", phaseIndex >= 2, CENTRAL_PURPLE)
        ).forEach { (label, active, color) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape)
                    .background(if (active) color else Color.White.copy(alpha = 0.15f)))
                Text(label, fontSize = 7.sp, fontFamily = OrbitronFontFamily,
                    color = if (active) color else Color.White.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
private fun PhaseLabel(phaseIndex: Int) {
    val labels = listOf("FASE 1 — Activar circuito base", "FASE 2 — Inyectar Compuesto A", "FASE 3 — Compuesto B · objetivo variable")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(phaseColor(phaseIndex).copy(alpha = 0.15f))
            .border(1.dp, phaseColor(phaseIndex).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(labels[phaseIndex], fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = phaseColor(phaseIndex))
    }
}

@Composable
private fun L5OpButton(label: String, color: Color, enabled: Boolean, isRotating: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (enabled) color.copy(alpha = if (isRotating) 0.22f else 0.15f)
                else Color.White.copy(alpha = 0.04f)
            )
            .border(
                1.5.dp,
                if (enabled) (if (isRotating) CENTRAL_GOLD else color).copy(alpha = 0.6f)
                else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(10.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold,
                color = if (enabled) color else Color.White.copy(alpha = 0.25f))
            if (isRotating) {
                Text("↺", fontSize = 8.sp, color = CENTRAL_GOLD.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun L5FailOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .zIndex(10f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("⏰ TIEMPO AGOTADO", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
            Text("Los compuestos se inestabilizaron.\nVuelve a activar la Sala Central desde el inicio.",
                fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF5252).copy(alpha = 0.18f))
                    .border(1.5.dp, Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { onRetry() }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("REINTENTAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            }
        }
    }
}
