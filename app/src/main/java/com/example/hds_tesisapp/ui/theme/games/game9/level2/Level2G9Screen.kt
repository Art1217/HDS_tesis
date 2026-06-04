package com.example.hds_tesisapp.ui.theme.games.game9.level2

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game9.G9_LEVEL_CONFIGS
import com.example.hds_tesisapp.ui.theme.games.game9.generateG9Rows
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9LivesRow
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9MenuButton
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9StaticRow
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9TimerBar
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9_CYAN
import com.example.hds_tesisapp.ui.theme.games.game9.level1.G9_GREEN
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CONFIG = G9_LEVEL_CONFIGS[1]

@Composable
fun Level2G9Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
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
            delay(350L); flashRowIdx = null; flashOk = null
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
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.60f)))

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G9MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 2 · Datos Borrosos", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Los datos están borrosos — concéntrate para detectar el bug", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G9_CYAN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    G9TimerBar(timerMs, CONFIG.timerMs)
                    G9LivesRow(lives)
                }
            }

            androidx.compose.foundation.layout.BoxWithConstraints(
                Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                val cellSize = minOf(maxWidth / 6f, maxHeight / 6.2f)
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    rows.forEachIndexed { rowIdx, row ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            androidx.compose.foundation.layout.Box(
                                androidx.compose.ui.Modifier.width(38.dp),
                                contentAlignment = Alignment.CenterEnd) {
                                if (row.isFixed)
                                    Text("✓", fontSize = 14.sp, color = G9_GREEN, fontWeight = FontWeight.ExtraBold)
                                else
                                    Text("F${rowIdx + 1}", fontSize = 9.sp,
                                        fontFamily = OrbitronFontFamily, color = G9_CYAN.copy(alpha = 0.5f))
                            }
                            G9StaticRow(row, cellSize, isBlurry = true,
                                flashRowIdx = flashRowIdx?.let { if (it == rowIdx) rowIdx else null },
                                flashOk = flashOk) { colIdx -> onSymbolTap(rowIdx, colIdx) }
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                Text("${rows.count { it.isFixed }} / 5 líneas depuradas", fontSize = 10.sp,
                    fontFamily = OrbitronFontFamily, color = G9_CYAN.copy(alpha = 0.7f))
            }
        }

        if (done)   G9DoneOverlay("¡Encontraste los bugs incluso con datos borrosos!") { onLevelComplete() }
        if (failed) G9FailOverlay(failReason) {
            lives = 5; done = false; failed = false
            timerMs = CONFIG.timerMs; flashRowIdx = null; flashOk = null; gameKey++
        }
    }
}
