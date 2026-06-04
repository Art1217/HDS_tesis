package com.example.hds_tesisapp.ui.theme.games.game9.level1

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game9.G9Row
import com.example.hds_tesisapp.ui.theme.games.game9.G9_LEVEL_CONFIGS
import com.example.hds_tesisapp.ui.theme.games.game9.generateG9Rows
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Theme ────────────────────────────────────────────────────────────────────

internal val G9_CYAN   = Color(0xFF00BCD4)
internal val G9_GREEN  = Color(0xFF4CAF50)
internal val G9_RED    = Color(0xFFFF5252)
internal val G9_AMBER  = Color(0xFFFFC107)
internal val G9_PURPLE = Color(0xFF9C27B0)

// ─── Shared UI components ─────────────────────────────────────────────────────

@Composable
internal fun G9MenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text("☰", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f)) }
}

@Composable
internal fun G9LivesRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 14.sp) }
    }
}

@Composable
internal fun G9TimerBar(remainingMs: Long, totalMs: Long) {
    val frac = (remainingMs.toFloat() / totalMs).coerceIn(0f, 1f)
    val color = when {
        frac > 0.5f -> G9_GREEN
        frac > 0.25f -> G9_AMBER
        else -> G9_RED
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("${(remainingMs / 1000L)}s", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
            color = color, fontWeight = FontWeight.Bold)
        Box(Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.1f))) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(frac)
                .clip(RoundedCornerShape(3.dp)).background(color))
        }
    }
}

@Composable
internal fun G9DoneOverlay(message: String, onContinue: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)).zIndex(10f),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF001A00))
                .border(2.dp, G9_GREEN.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("✅ ¡CÓDIGO DEPURADO!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G9_GREEN)
            Text(message, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Box(Modifier.clip(RoundedCornerShape(12.dp))
                .background(G9_GREEN.copy(alpha = 0.18f))
                .border(1.5.dp, G9_GREEN.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable { onContinue() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("CONTINUAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G9_GREEN)
            }
        }
    }
}

@Composable
internal fun G9FailOverlay(reason: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)).zIndex(10f),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, G9_RED.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(reason, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G9_RED)
            Text("Los errores siguen en el código.\n¡Inténtalo de nuevo!", fontSize = 12.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center)
            Box(Modifier.clip(RoundedCornerShape(12.dp))
                .background(G9_RED.copy(alpha = 0.18f))
                .border(1.5.dp, G9_RED.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable { onRetry() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("REINTENTAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G9_RED)
            }
        }
    }
}

// Single symbol cell — supports blurry rendering via alpha layering
@Composable
internal fun G9SymbolCell(
    symbol: String,
    isFixed: Boolean,
    isBlurry: Boolean,
    isFlashOk: Boolean?,   // null = no flash, true = green, false = red
    modifier: Modifier = Modifier
) {
    val bg = when {
        isFlashOk == true  -> G9_GREEN.copy(alpha = 0.35f)
        isFlashOk == false -> G9_RED.copy(alpha = 0.35f)
        isFixed            -> G9_GREEN.copy(alpha = 0.15f)
        else               -> Color.White.copy(alpha = 0.05f)
    }
    val border = when {
        isFlashOk == true  -> G9_GREEN
        isFlashOk == false -> G9_RED
        isFixed            -> G9_GREEN.copy(alpha = 0.5f)
        else               -> G9_CYAN.copy(alpha = 0.25f)
    }

    Box(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(bg)
        .border(1.5.dp, border, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center) {
        if (isFixed) {
            Text("✓", fontSize = 20.sp, color = G9_GREEN, fontWeight = FontWeight.ExtraBold)
        } else if (isBlurry) {
            // Simulated blur: 3 offset layers at low alpha
            Box(contentAlignment = Alignment.Center) {
                Text(symbol, fontSize = 22.sp, modifier = Modifier.offset(2.dp, 2.dp)
                    .graphicsLayer { alpha = 0.20f })
                Text(symbol, fontSize = 22.sp, modifier = Modifier.offset((-2).dp, (-2).dp)
                    .graphicsLayer { alpha = 0.20f })
                Text(symbol, fontSize = 22.sp, modifier = Modifier.graphicsLayer { alpha = 0.45f },
                    textAlign = TextAlign.Center)
            }
        } else {
            Text(symbol, fontSize = 24.sp, textAlign = TextAlign.Center)
        }
    }
}

// Static row — for levels 1 & 2
@Composable
internal fun G9StaticRow(
    row: G9Row,
    cellSize: Dp,
    isBlurry: Boolean,
    flashRowIdx: Int?,
    flashOk: Boolean?,
    onSymbolTap: (colIdx: Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        row.symbols.forEachIndexed { idx, sym ->
            val myFlash = if (flashRowIdx == row.id) flashOk else null
            G9SymbolCell(
                symbol = sym,
                isFixed = row.isFixed,
                isBlurry = isBlurry,
                isFlashOk = myFlash,
                modifier = Modifier.size(cellSize)
                    .clickable(enabled = !row.isFixed && flashRowIdx == null) { onSymbolTap(idx) }
            )
        }
    }
}

// Scrolling row — for levels 3, 4, 5.
// wrap-around: renders 6 cells from a double-copy to achieve seamless infinite scroll.
@Composable
internal fun G9ScrollingRow(
    row: G9Row,
    offsetFrac: Float,     // 0..1 — view-start fraction of full row width
    direction: Int,        // 1 = leftward content motion, -1 = rightward
    cellSize: Dp,
    isBlurry: Boolean,
    flashRowIdx: Int?,
    flashOk: Boolean?,
    onSymbolTap: ((colIdx: Int) -> Unit)? = null,   // L3: symbol-level
    onRowTap: (() -> Unit)? = null                   // L4/L5: row-level modal trigger
) {
    val density = LocalDensity.current
    val cellSizePx  = with(density) { cellSize.toPx() }
    val rowWidthPx  = cellSizePx * 5f

    // effectiveOffset: increasing means content scrolls LEFT
    val effectiveOffset = if (direction > 0) offsetFrac else (1f - offsetFrac + 1f) % 1f
    val viewStart       = effectiveOffset * rowWidthPx

    val firstSymIdx  = (viewStart / cellSizePx).toInt()
    val renderOffset = -(viewStart - firstSymIdx * cellSizePx)   // negative x shift (px)

    val myFlash = if (flashRowIdx == row.id) flashOk else null
    val canTap  = !row.isFixed && flashRowIdx == null

    // Use rememberUpdatedState so the pointerInput lambda always reads the latest
    // viewStart without needing to restart on every frame (offsetFrac changes every 16ms).
    val latestViewStart by rememberUpdatedState(viewStart)
    val latestCanTap    by rememberUpdatedState(canTap)

    Box(
        modifier = Modifier
            .width(cellSize * 5)
            .height(cellSize)
            .clip(RoundedCornerShape(10.dp))
            .let { m ->
                when {
                    onRowTap != null -> m.clickable(enabled = canTap) { onRowTap() }
                    onSymbolTap != null -> m.pointerInput(row.id) {
                        // Stable key (row.id never changes mid-game for the same row).
                        // latestViewStart/latestCanTap are updated every recomposition
                        // without restarting this coroutine.
                        detectTapGestures { tap ->
                            if (!latestCanTap) return@detectTapGestures
                            val absPos = latestViewStart + tap.x
                            val logIdx = ((absPos / cellSizePx).toInt() % 5 + 5) % 5
                            onSymbolTap(logIdx)
                        }
                    }
                    else -> m
                }
            }
    ) {
        repeat(6) { renderIdx ->
            val logIdx = ((firstSymIdx + renderIdx) % 5 + 5) % 5
            val xDp    = with(density) { (renderOffset + renderIdx * cellSizePx).toDp() }
            Box(modifier = Modifier.absoluteOffset(x = xDp, y = 0.dp).size(cellSize)) {
                G9SymbolCell(
                    symbol    = row.symbols[logIdx],
                    isFixed   = row.isFixed,
                    isBlurry  = isBlurry,
                    isFlashOk = myFlash
                )
            }
        }
    }
}

// Modal overlay for levels 4 & 5 — asks player to identify the correct pattern symbol
@Composable
internal fun G9PatternModal(
    row: G9Row,
    options: List<String>,
    cellSize: Dp,
    isBlurry: Boolean,
    onChoice: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.80f)).zIndex(8f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0A1628))
                .border(2.dp, G9_CYAN.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("¿Cuál es el dato correcto en esta línea?",
                fontSize = 14.sp, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                color = G9_CYAN, textAlign = TextAlign.Center)

            // Preview of the row (static, blurry as configured)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.symbols.forEach { sym ->
                    G9SymbolCell(sym, isFixed = false, isBlurry = isBlurry, isFlashOk = null,
                        modifier = Modifier.size(cellSize * 0.85f))
                }
            }

            Text("Selecciona el valor correcto (sin el bug):", fontSize = 10.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.6f))

            // 3 answer options
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                options.forEach { opt ->
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(G9_CYAN.copy(alpha = 0.12f))
                            .border(2.dp, G9_CYAN.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { onChoice(opt) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, fontSize = 28.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

// ─── Level 1 ──────────────────────────────────────────────────────────────────

private val CONFIG = G9_LEVEL_CONFIGS[0]

@Composable
fun Level1G9Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    val scope = rememberCoroutineScope()

    var lives       by remember { mutableIntStateOf(5) }
    var done        by remember { mutableStateOf(false) }
    var failed      by remember { mutableStateOf(false) }
    var failReason  by remember { mutableStateOf("💥 Sin Vidas") }
    var gameKey     by remember { mutableIntStateOf(0) }
    var timerMs     by remember { mutableLongStateOf(CONFIG.timerMs) }
    var flashRowIdx by remember { mutableStateOf<Int?>(null) }
    var flashOk     by remember { mutableStateOf<Boolean?>(null) }

    var rows by remember(gameKey) { mutableStateOf(generateG9Rows(CONFIG)) }

    fun onSymbolTap(rowIdx: Int, colIdx: Int) {
        if (flashRowIdx != null || done || failed) return
        val row = rows[rowIdx]
        if (row.isFixed) return
        val correct = colIdx == row.wrongIndex
        flashRowIdx = rowIdx; flashOk = correct
        scope.launch {
            delay(350L)
            flashRowIdx = null; flashOk = null
            if (correct) {
                rows = rows.toMutableList().also { it[rowIdx] = row.copy(isFixed = true) }
                if (rows.all { it.isFixed }) done = true
            } else {
                lives--; if (lives <= 0) { failReason = "💥 Sin Vidas"; failed = true }
            }
        }
    }

    LaunchedEffect(gameKey) {
        val start = System.currentTimeMillis()
        while (!done && !failed) {
            delay(100L)
            timerMs = (CONFIG.timerMs - (System.currentTimeMillis() - start)).coerceAtLeast(0L)
            if (timerMs <= 0L) { failReason = "⏰ Tiempo Agotado"; failed = true }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.workshop_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G9MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 1 · Depuración Básica", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Encuentra el bug — toca el dato incorrecto en cada línea", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G9_CYAN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    G9TimerBar(timerMs, CONFIG.timerMs)
                    G9LivesRow(lives)
                }
            }

            // Grid
            androidx.compose.foundation.layout.BoxWithConstraints(
                Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val cellSize = minOf(maxWidth / 6f, maxHeight / 6.2f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rows.forEachIndexed { rowIdx, row ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Row label
                            Box(Modifier.width(38.dp), contentAlignment = Alignment.CenterEnd) {
                                if (row.isFixed)
                                    Text("✓", fontSize = 14.sp, color = G9_GREEN, fontWeight = FontWeight.ExtraBold)
                                else
                                    Text("F${rowIdx + 1}", fontSize = 9.sp,
                                        fontFamily = OrbitronFontFamily, color = G9_CYAN.copy(alpha = 0.5f))
                            }
                            G9StaticRow(row, cellSize, isBlurry = false,
                                flashRowIdx = flashRowIdx?.let { if (it == rowIdx) rowIdx else null },
                                flashOk = flashOk) { colIdx -> onSymbolTap(rowIdx, colIdx) }
                        }
                    }
                }
            }

            // Progress chips
            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                val fixed = rows.count { it.isFixed }
                Text("$fixed / 5 líneas depuradas", fontSize = 10.sp,
                    fontFamily = OrbitronFontFamily, color = G9_CYAN.copy(alpha = 0.7f))
            }
        }

        if (done)   G9DoneOverlay("¡Errores básicos eliminados!\nEl taller empieza a estabilizarse.") { onLevelComplete() }
        if (failed) G9FailOverlay(failReason) {
            lives = 5; done = false; failed = false
            timerMs = CONFIG.timerMs; flashRowIdx = null; flashOk = null; gameKey++
        }
    }
}
