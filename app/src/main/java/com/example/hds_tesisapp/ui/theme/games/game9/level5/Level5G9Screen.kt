package com.example.hds_tesisapp.ui.theme.games.game9.level5

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.hds_tesisapp.ui.theme.games.game9.G9_LEVEL_CONFIGS
import com.example.hds_tesisapp.ui.theme.games.game9.generateG9Row
import com.example.hds_tesisapp.ui.theme.games.game9.generateG9Rows
import com.example.hds_tesisapp.ui.theme.games.game9.generateModalOptions
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9LivesRow
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9MenuButton
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9PatternModal
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9ScrollingRow
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9_CYAN
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9_GREEN
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9_RED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CONFIG         = G9_LEVEL_CONFIGS[4]
private val ROW_DIRECTIONS = List(5) { if (it % 2 == 0) 1 else -1 }

@Composable
fun Level5G9Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    val scope = rememberCoroutineScope()
    val rng   = remember { java.util.Random() }

    var lives        by remember { mutableIntStateOf(5) }
    var done         by remember { mutableStateOf(false) }
    var failed       by remember { mutableStateOf(false) }
    var gameKey      by remember { mutableIntStateOf(0) }
    var flashRowIdx  by remember { mutableStateOf<Int?>(null) }
    var flashOk      by remember { mutableStateOf<Boolean?>(null) }
    var modalRowIdx  by remember { mutableStateOf<Int?>(null) }
    var modalOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var bossIsActing by remember { mutableStateOf(false) }   // visual flash on boss
    var swapCounter  by remember { mutableIntStateOf(100) }  // unique row IDs after swaps

    var rows       by remember(gameKey) { mutableStateOf(generateG9Rows(CONFIG)) }
    val rowOffsets  = remember(gameKey) { androidx.compose.runtime.mutableStateListOf(*Array(5) { 0f }) }

    // Boss lives = number of incorrect rows (derived)
    val bossLives = rows.count { !it.isFixed }

    fun openModal(rowIdx: Int) {
        if (rows[rowIdx].isFixed || flashRowIdx != null) return
        modalRowIdx = rowIdx
        modalOptions = generateModalOptions(rows[rowIdx], CONFIG.symbolSet, rng)
    }

    fun onModalChoice(choice: String) {
        val rowIdx = modalRowIdx ?: return
        val row    = rows.getOrNull(rowIdx) ?: return
        val correct = choice == row.correctSymbol
        modalRowIdx = null; modalOptions = emptyList()
        flashRowIdx = rowIdx; flashOk = correct
        scope.launch {
            delay(350L); flashRowIdx = null; flashOk = null
            if (correct) {
                rows = rows.toMutableList().also { it[rowIdx] = row.copy(isFixed = true) }
                if (rows.all { it.isFixed }) done = true
            } else {
                lives--; if (lives <= 0) failed = true
            }
        }
    }

    // Scroll loop
    LaunchedEffect(gameKey) {
        while (!done && !failed) {
            delay(32L)
            if (modalRowIdx == null) {
                val delta = 32f / CONFIG.scrollSpeedMs
                for (i in rowOffsets.indices) rowOffsets[i] = (rowOffsets[i] + delta) % 1f
            }
        }
    }

    // Mini-boss swap loop
    LaunchedEffect(gameKey) {
        while (!done && !failed) {
            delay(CONFIG.bossSwapIntervalMs)
            if (done || failed) break
            bossIsActing = true
            delay(400L)
            val swapIdx = rng.nextInt(CONFIG.rowCount)
            val newRow  = generateG9Row(swapCounter++, CONFIG.symbolSet, rng)
            rows = rows.toMutableList().also { it[swapIdx] = newRow }
            // Close modal if it was open for the swapped row
            if (modalRowIdx == swapIdx) { modalRowIdx = null; modalOptions = emptyList() }
            bossIsActing = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.workshop_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.62f)))

        // Boss flash overlay
        if (bossIsActing) {
            Box(Modifier.fillMaxSize().background(G9_RED.copy(alpha = 0.12f)))
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G9MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 5 · El Glitch Interfiere", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("El minijefe cambia filas cada cierto tiempo — ¡corrígelas antes que vuelva!", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G9_RED.copy(alpha = 0.9f))
                }
                G9LivesRow(lives)
            }

            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {

                // Mini-boss panel
                Column(
                    modifier = Modifier
                        .width(90.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A000A).copy(alpha = 0.85f))
                        .border(
                            if (bossIsActing) 2.dp else 1.dp,
                            if (bossIsActing) G9_RED else G9_RED.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(painterResource(R.drawable.miniboss_zone9), "Glitch",
                        Modifier.height(60.dp)
                            .graphicsLayer { if (bossIsActing) scaleX = 1.15f; if (bossIsActing) scaleY = 1.15f },
                        contentScale = ContentScale.Fit)
                    Text("GLITCH", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = G9_RED)
                    // Boss HP bar
                    repeat(5) { i ->
                        Box(
                            modifier = Modifier.fillMaxWidth().height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i < bossLives) G9_RED.copy(alpha = 0.85f)
                                            else Color.White.copy(alpha = 0.08f))
                        )
                    }
                    Text("$bossLives ❤️", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                        color = G9_RED, textAlign = TextAlign.Center)
                }

                // Grid
                androidx.compose.foundation.layout.BoxWithConstraints(
                    Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    val cellSize = minOf(maxWidth / 6f, maxHeight / 6.2f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        rows.forEachIndexed { rowIdx, row ->
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.width(38.dp), contentAlignment = Alignment.CenterEnd) {
                                    if (row.isFixed)
                                        Text("✓", fontSize = 14.sp, color = G9_GREEN, fontWeight = FontWeight.ExtraBold)
                                    else {
                                        val arrow = if (ROW_DIRECTIONS[rowIdx] > 0) "←" else "→"
                                        Text(arrow, fontSize = 12.sp, color = G9_CYAN.copy(alpha = 0.5f))
                                    }
                                }
                                G9ScrollingRow(
                                    row        = row,
                                    offsetFrac = rowOffsets[rowIdx],
                                    direction  = ROW_DIRECTIONS[rowIdx],
                                    cellSize   = cellSize,
                                    isBlurry   = true,
                                    flashRowIdx = flashRowIdx?.let { if (it == rowIdx) rowIdx else null },
                                    flashOk    = flashOk,
                                    onRowTap   = { openModal(rowIdx) }
                                )
                            }
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                Text("${rows.count { it.isFixed }} / 5 depuradas  ·  Vidas Glitch: $bossLives", fontSize = 9.sp,
                    fontFamily = OrbitronFontFamily, color = G9_CYAN.copy(alpha = 0.6f))
            }
        }

        // Modal
        if (modalRowIdx != null) {
            val ri  = modalRowIdx!!
            val row = rows.getOrNull(ri)
            if (row != null) {
                androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize()) {
                    val cellSize = minOf(maxWidth / 8f, maxHeight / 8f)
                    G9PatternModal(row = row, options = modalOptions, cellSize = cellSize,
                        isBlurry = true) { choice -> onModalChoice(choice) }
                }
            }
        }

        if (done)   G9DoneOverlay("¡El Glitch fue derrotado!\nEl Taller de Correcciones vuelve a la normalidad.") { onLevelComplete() }
        if (failed) G9FailOverlay("💥 Sin Vidas") {
            lives = 5; done = false; failed = false
            flashRowIdx = null; flashOk = null
            modalRowIdx = null; modalOptions = emptyList()
            bossIsActing = false; gameKey++
        }
    }
}
