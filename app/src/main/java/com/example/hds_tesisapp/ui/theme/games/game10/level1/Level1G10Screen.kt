package com.example.hds_tesisapp.ui.theme.games.game10.level1

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game10.G10Item
import com.example.hds_tesisapp.ui.theme.games.game10.G10LevelConfig
import com.example.hds_tesisapp.ui.theme.games.game10.G10_LEVEL_CONFIGS

// ─── Theme ────────────────────────────────────────────────────────────────────

internal val G10_AMBER  = Color(0xFFFFC107)
internal val G10_CYAN   = Color(0xFF00BCD4)
internal val G10_GREEN  = Color(0xFF4CAF50)
internal val G10_RED    = Color(0xFFFF5252)
internal val G10_PURPLE = Color(0xFF9C27B0)

// ─── Shared UI components ─────────────────────────────────────────────────────

@Composable
internal fun G10MenuButton(onClick: () -> Unit) {
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
internal fun G10TimerBar(remainingMs: Long, totalMs: Long) {
    val frac = (remainingMs.toFloat() / totalMs).coerceIn(0f, 1f)
    val color = when {
        frac > 0.5f -> G10_GREEN
        frac > 0.25f -> G10_AMBER
        else -> G10_RED
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
internal fun G10DoneOverlay(message: String, onContinue: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)).zIndex(10f),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF001A00))
                .border(2.dp, G10_GREEN.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("✅ ¡SALA ASEGURADA!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G10_GREEN)
            Text(message, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Box(Modifier.clip(RoundedCornerShape(12.dp))
                .background(G10_GREEN.copy(alpha = 0.18f))
                .border(1.5.dp, G10_GREEN.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable { onContinue() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("CONTINUAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G10_GREEN)
            }
        }
    }
}

@Composable
internal fun G10FailOverlay(reason: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)).zIndex(10f),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, G10_RED.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(reason, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G10_RED)
            Text("No reunimos los objetos correctos.\n¡Inténtalo de nuevo!", fontSize = 12.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center)
            Box(Modifier.clip(RoundedCornerShape(12.dp))
                .background(G10_RED.copy(alpha = 0.18f))
                .border(1.5.dp, G10_RED.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable { onRetry() }.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("REINTENTAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G10_RED)
            }
        }
    }
}

// List of tasks (descriptions only — never reveals the target object)
@Composable
internal fun G10TaskPanel(tasks: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, G10_AMBER.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("📋 TAREAS", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold, color = G10_AMBER)
        tasks.forEach { task ->
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("•", fontSize = 11.sp, color = G10_AMBER.copy(alpha = 0.8f))
                Text(task, fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 14.sp)
            }
        }
    }
}

// A tile representing one item in the room. Hidden items show as a closed
// box/drawer until tapped open; then they behave like normal items.
@Composable
internal fun G10ItemTile(
    item: G10Item,
    isOpened: Boolean,
    isSelected: Boolean,
    flashOk: Boolean?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showClosed = item.hidden && !isOpened
    val bg = when {
        flashOk == true  -> G10_GREEN.copy(alpha = 0.30f)
        flashOk == false -> G10_RED.copy(alpha = 0.30f)
        isSelected       -> G10_AMBER.copy(alpha = 0.22f)
        else             -> Color.White.copy(alpha = 0.05f)
    }
    val border = when {
        flashOk == true  -> G10_GREEN
        flashOk == false -> G10_RED
        isSelected       -> G10_AMBER
        else             -> Color.White.copy(alpha = 0.18f)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(12.dp))
            .clickable { onTap() }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        if (showClosed) {
            Text("📦", fontSize = 34.sp)
        } else if (item.drawableRes != null) {
            Image(painterResource(item.drawableRes), item.name,
                modifier = Modifier.size(44.dp), contentScale = ContentScale.Fit)
        } else {
            Text(item.emoji, fontSize = 34.sp)
        }
        if (!showClosed) {
            Text(item.name, fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center,
                maxLines = 2, lineHeight = 11.sp)
        } else {
            Text("?", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.4f))
        }
    }
}

// Slots showing what the player has currently selected (max = maxPicks)
@Composable
internal fun G10SelectionTray(maxPicks: Int, selected: List<G10Item>) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("MOCHILA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            color = G10_CYAN.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
        repeat(maxPicks) { idx ->
            val item = selected.getOrNull(idx)
            Box(
                modifier = Modifier.size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (item != null) G10_AMBER.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f))
                    .border(1.dp, if (item != null) G10_AMBER.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item != null) {
                    if (item.drawableRes != null) {
                        Image(painterResource(item.drawableRes), item.name,
                            modifier = Modifier.size(22.dp), contentScale = ContentScale.Fit)
                    } else {
                        Text(item.emoji, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
internal fun G10ConfirmButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) G10_GREEN.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.04f))
            .border(1.5.dp, if (enabled) G10_GREEN.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        Text("LISTO ✓", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = if (enabled) G10_GREEN else Color.White.copy(alpha = 0.3f))
    }
}

// ─── Level 1 ──────────────────────────────────────────────────────────────────

private val CONFIG = G10_LEVEL_CONFIGS[0]

@Composable
fun Level1G10Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    G10RoomScreen(
        config = CONFIG,
        levelLabel = "NIVEL 1 · Lobby del Cuartel",
        doneMessage = "Las puertas hacia la Sala de Seguridad se abren.",
        onLevelComplete = onLevelComplete,
        onNavigateToMenu = onNavigateToMenu
    )
}

// Generic "find & select objects" room screen, shared by levels 1-4.
@Composable
internal fun G10RoomScreen(
    config: G10LevelConfig,
    levelLabel: String,
    doneMessage: String,
    onLevelComplete: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    var gameKey    by remember { mutableIntStateOf(0) }
    var timerMs    by remember { mutableLongStateOf(config.timerMs) }
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }

    val openedIds   = remember(gameKey) { androidx.compose.runtime.mutableStateListOf<Int>() }
    val selectedIds = remember(gameKey) { androidx.compose.runtime.mutableStateListOf<Int>() }
    var flashOk     by remember { mutableStateOf<Boolean?>(null) }

    fun onTileTap(item: G10Item) {
        if (done || failed) return
        if (item.hidden && item.id !in openedIds) {
            openedIds.add(item.id)
            return
        }
        if (item.id in selectedIds) {
            selectedIds.remove(item.id)
        } else if (selectedIds.size < config.maxPicks) {
            selectedIds.add(item.id)
        }
    }

    fun onConfirm() {
        val correctIds = config.items.filter { it.isCorrect }.map { it.id }.toSet()
        if (selectedIds.toSet() == correctIds) {
            flashOk = true
            done = true
        } else {
            flashOk = false
            failReason = "❌ Objetos Incorrectos"
            failed = true
        }
    }

    LaunchedEffect(gameKey) {
        val start = System.currentTimeMillis()
        while (!done && !failed) {
            kotlinx.coroutines.delay(100L)
            timerMs = (config.timerMs - (System.currentTimeMillis() - start)).coerceAtLeast(0L)
            if (timerMs <= 0L) { failReason = "⏰ Tiempo Agotado"; failed = true }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(painterResource(config.backgroundRes), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G10MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(levelLabel, fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(config.subtitle, fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G10_AMBER.copy(alpha = 0.8f))
                }
                G10TimerBar(timerMs, config.timerMs)
            }

            // Content: tasks + items grid
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                G10TaskPanel(config.tasks, modifier = Modifier.width(180.dp).fillMaxHeight())

                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    val columns = 4
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        config.items.chunked(columns).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                rowItems.forEach { item ->
                                    G10ItemTile(
                                        item = item,
                                        isOpened = item.id in openedIds,
                                        isSelected = item.id in selectedIds,
                                        flashOk = if (item.id in selectedIds) flashOk else null,
                                        onTap = { onTileTap(item) },
                                        modifier = Modifier.size(78.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Footer: tray + confirm
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G10SelectionTray(config.maxPicks, selectedIds.mapNotNull { id -> config.items.find { it.id == id } })
                G10ConfirmButton(enabled = selectedIds.size == config.maxPicks) { onConfirm() }
            }
        }

        if (done)   G10DoneOverlay(doneMessage) { onLevelComplete() }
        if (failed) G10FailOverlay(failReason) {
            done = false; failed = false; flashOk = null
            timerMs = config.timerMs; gameKey++
        }
    }
}
