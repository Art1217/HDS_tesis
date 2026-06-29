package com.example.hds_tesisapp.ui.theme.games.game10.level5

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game10.G10GlitchPhase
import com.example.hds_tesisapp.ui.theme.games.game10.G10ItemKind
import com.example.hds_tesisapp.ui.theme.games.game10.G10Shadow
import com.example.hds_tesisapp.ui.theme.games.game10.G10_ABSORB_DAMAGE
import com.example.hds_tesisapp.ui.theme.games.game10.G10_BOSS_GRID_SIZE
import com.example.hds_tesisapp.ui.theme.games.game10.G10_CRYSTAL_ITEM
import com.example.hds_tesisapp.ui.theme.games.game10.G10_GLITCH_MAX_HP
import com.example.hds_tesisapp.ui.theme.games.game10.G10_PHASE_SEQUENCE
import com.example.hds_tesisapp.ui.theme.games.game10.G10_SHADOW_HEAL
import com.example.hds_tesisapp.ui.theme.games.game10.G10_SHADOW_SPAWN_MS
import com.example.hds_tesisapp.ui.theme.games.game10.G10_SHADOW_TIME_MS
import com.example.hds_tesisapp.ui.theme.games.game10.G10_WRONG_PICK_HEAL
import com.example.hds_tesisapp.ui.theme.games.game10.g10BossItemById
import com.example.hds_tesisapp.ui.theme.games.game10.g10PhaseDurationMs
import com.example.hds_tesisapp.ui.theme.games.game10.generateG10BossBoxes
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10MenuButton
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10_AMBER
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10_CYAN
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10_GREEN
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10_RED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FLASHLIGHT_STEAL_MS  = 15_000L
private const val ENERGY_KEY_STEAL_MS  = 18_000L
private const val PREVIEW_MS            = 5_000L

@Composable
fun Level5G10Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    val scope = rememberCoroutineScope()

    var gameKey    by remember { mutableIntStateOf(0) }
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }

    var glitchHp        by remember(gameKey) { mutableIntStateOf(G10_GLITCH_MAX_HP) }
    var lives           by remember(gameKey) { mutableFloatStateOf(3f) }
    var phase           by remember(gameKey) { mutableStateOf(G10GlitchPhase.TINKERING) }
    var glitchDefeated  by remember(gameKey) { mutableStateOf(false) }
    var crystalAvailable by remember(gameKey) { mutableStateOf(true) }
    var crystalStolenOnce by remember(gameKey) { mutableStateOf(false) }
    var flashlightStolen  by remember(gameKey) { mutableStateOf(false) }
    var energyKeyStolen   by remember(gameKey) { mutableStateOf(false) }
    var flashlightFoundAt by remember(gameKey) { mutableStateOf<Long?>(null) }
    var energyKeyFoundAt  by remember(gameKey) { mutableStateOf<Long?>(null) }

    var defending by remember { mutableStateOf(false) }
    var absorbing by remember { mutableStateOf(false) }

    var previewing    by remember(gameKey) { mutableStateOf(true) }
    var previewMsLeft by remember(gameKey) { mutableLongStateOf(PREVIEW_MS) }

    val rng = remember(gameKey) { java.util.Random() }
    val boxItems    = remember(gameKey) { mutableStateListOf(*generateG10BossBoxes(rng).toTypedArray()) }
    val boxRevealed = remember(gameKey) { mutableStateListOf(*Array(G10_BOSS_GRID_SIZE) { false }) }
    val boxFlash    = remember(gameKey) { mutableStateListOf<Boolean?>(*arrayOfNulls(G10_BOSS_GRID_SIZE)) }
    val collected   = remember(gameKey) { mutableStateListOf<G10ItemKind>() }
    val shadows     = remember(gameKey) { mutableStateListOf<G10Shadow>() }
    var nextShadowId by remember(gameKey) { mutableIntStateOf(1) }

    fun onBoxTap(idx: Int) {
        if (done || failed || previewing) return
        val itemId = boxItems[idx] ?: return
        if (boxRevealed[idx]) return
        val item = g10BossItemById(itemId)
        when (item.kind) {
            G10ItemKind.SCISSORS, G10ItemKind.ENERGY_KEY, G10ItemKind.FLASHLIGHT -> {
                if (item.kind !in collected) collected.add(item.kind)
                if (item.kind == G10ItemKind.FLASHLIGHT) flashlightFoundAt = System.currentTimeMillis()
                if (item.kind == G10ItemKind.ENERGY_KEY) energyKeyFoundAt = System.currentTimeMillis()
                boxItems[idx] = null
                boxFlash[idx] = true
            }
            G10ItemKind.CRYSTAL -> {
                crystalAvailable = true
                boxItems[idx] = null
                boxFlash[idx] = true
            }
            G10ItemKind.JUNK -> {
                boxRevealed[idx] = true
                boxFlash[idx] = false
                lives = (lives - 1f).coerceAtLeast(0f)
                glitchHp = (glitchHp + G10_WRONG_PICK_HEAL).coerceAtMost(G10_GLITCH_MAX_HP)
                if (lives <= 0f) { failReason = "💥 Sin Vidas"; failed = true }
            }
        }
        scope.launch { delay(450L); boxFlash[idx] = null }
    }

    fun onFlashlightLane(lane: Int) {
        val idx = shadows.indexOfFirst { it.lane == lane }
        if (idx >= 0) shadows.removeAt(idx)
    }

    // Preview countdown — boxes start face-up so the player can memorize where things are.
    LaunchedEffect(gameKey) {
        val start = System.currentTimeMillis()
        while (previewMsLeft > 0L) {
            delay(100L)
            previewMsLeft = (PREVIEW_MS - (System.currentTimeMillis() - start)).coerceAtLeast(0L)
        }
        previewing = false
    }

    // Main phase loop — drives the Glitch's behaviour.
    LaunchedEffect(gameKey) {
        while (previewing) delay(100L)
        var phaseIdx = 0
        while (!done && !failed) {
            val seqPhase = G10_PHASE_SEQUENCE[phaseIdx % G10_PHASE_SEQUENCE.size]
            phaseIdx++
            if (glitchDefeated && seqPhase != G10GlitchPhase.TINKERING) continue

            phase = seqPhase
            when (seqPhase) {
                G10GlitchPhase.TINKERING -> delay(g10PhaseDurationMs(seqPhase))
                G10GlitchPhase.REPOSITION -> {
                    delay(g10PhaseDurationMs(seqPhase))
                    val occupied = boxItems.indices.filter { boxItems[it] != null && !boxRevealed[it] }
                    val shuffled = occupied.map { boxItems[it] }.shuffled(rng)
                    occupied.forEachIndexed { i, idx -> boxItems[idx] = shuffled[i] }
                }
                G10GlitchPhase.ATTACK_WARNING -> {
                    delay(g10PhaseDurationMs(seqPhase))
                    phase = G10GlitchPhase.ATTACK_HIT
                    if (!(defending && crystalAvailable)) {
                        lives = (lives - 1f).coerceAtLeast(0f)
                        if (lives <= 0f) { failReason = "💥 Sin Vidas"; failed = true }
                    }
                    delay(g10PhaseDurationMs(G10GlitchPhase.ATTACK_HIT))
                }
                G10GlitchPhase.ENERGY_INSERT -> {
                    delay(g10PhaseDurationMs(seqPhase))
                    if (absorbing) glitchHp = (glitchHp - G10_ABSORB_DAMAGE).coerceAtLeast(0)
                }
                G10GlitchPhase.ATTACK_HIT -> {}
            }

            if (failed) break

            // Crystal Protector steal — once Glitch is at half HP.
            if (glitchHp <= 50 && crystalAvailable && !crystalStolenOnce) {
                crystalStolenOnce = true
                crystalAvailable = false
                val emptyIdx = boxItems.indices.filter { boxItems[it] == null }.shuffled(rng).firstOrNull()
                if (emptyIdx != null) {
                    boxItems[emptyIdx] = G10_CRYSTAL_ITEM.id
                    boxRevealed[emptyIdx] = false
                }
            }

            // Steal-back: flashlight and energy key, one time each.
            val now = System.currentTimeMillis()
            if (G10ItemKind.FLASHLIGHT in collected && !flashlightStolen &&
                flashlightFoundAt != null && now - flashlightFoundAt!! > FLASHLIGHT_STEAL_MS) {
                flashlightStolen = true
                collected.remove(G10ItemKind.FLASHLIGHT)
                val emptyIdx = boxItems.indices.filter { boxItems[it] == null }.shuffled(rng).firstOrNull()
                if (emptyIdx != null) { boxItems[emptyIdx] = 3; boxRevealed[emptyIdx] = false }
            }
            if (G10ItemKind.ENERGY_KEY in collected && !energyKeyStolen &&
                energyKeyFoundAt != null && now - energyKeyFoundAt!! > ENERGY_KEY_STEAL_MS) {
                energyKeyStolen = true
                collected.remove(G10ItemKind.ENERGY_KEY)
                val emptyIdx = boxItems.indices.filter { boxItems[it] == null }.shuffled(rng).firstOrNull()
                if (emptyIdx != null) { boxItems[emptyIdx] = 2; boxRevealed[emptyIdx] = false }
            }

            if (glitchHp <= 0) glitchDefeated = true
            if (glitchDefeated && G10ItemKind.SCISSORS in collected && G10ItemKind.ENERGY_KEY in collected) {
                done = true
            }
        }
    }

    // Shadow spawn loop — only once the flashlight has been found.
    LaunchedEffect(gameKey) {
        while (previewing) delay(100L)
        while (!done && !failed) {
            delay(G10_SHADOW_SPAWN_MS)
            if (G10ItemKind.FLASHLIGHT in collected && shadows.size < 2) {
                shadows.add(
                    G10Shadow(
                        id = nextShadowId++,
                        targetsPlayer = rng.nextInt(10) < 7,
                        lane = rng.nextInt(2),
                        timeLeftMs = G10_SHADOW_TIME_MS
                    )
                )
            }
        }
    }

    // Shadow countdown loop.
    LaunchedEffect(gameKey) {
        while (!done && !failed) {
            delay(100L)
            for (i in shadows.indices.reversed()) {
                val s = shadows[i]
                val newTime = s.timeLeftMs - 100L
                if (newTime <= 0L) {
                    if (s.targetsPlayer) {
                        lives = (lives - 0.5f).coerceAtLeast(0f)
                        if (lives <= 0f) { failReason = "💥 Sin Vidas"; failed = true }
                    } else {
                        glitchHp = (glitchHp + G10_SHADOW_HEAL).coerceAtMost(G10_GLITCH_MAX_HP)
                    }
                    shadows.removeAt(i)
                } else {
                    shadows[i] = s.copy(timeLeftMs = newTime)
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.g10_rooftop_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G10MenuButton(onNavigateToMenu)
                Text("NIVEL 5 · La Azotea", fontSize = 12.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                G10LivesBar(lives)
            }

            // Big phase instruction banner — tells the player exactly what to do right now.
            val phaseColor = when (phase) {
                G10GlitchPhase.ATTACK_WARNING, G10GlitchPhase.ATTACK_HIT -> G10_CYAN
                G10GlitchPhase.ENERGY_INSERT -> G10_GREEN
                G10GlitchPhase.REPOSITION -> G10_AMBER
                G10GlitchPhase.TINKERING -> Color.White
            }
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background((if (previewing) G10_AMBER else phaseColor).copy(alpha = 0.18f))
                    .border(1.5.dp, (if (previewing) G10_AMBER else phaseColor).copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (previewing) "👁️ Memoriza dónde está cada objeto... ${(previewMsLeft / 1000) + 1}s"
                    else g10PhaseLabel(phase),
                    fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (previewing) G10_AMBER else phaseColor,
                    textAlign = TextAlign.Center
                )
            }

            // Shadow alert — large, so the player knows exactly which side to point the flashlight at.
            if (shadows.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    shadows.forEach { s ->
                        val col = if (s.targetsPlayer) G10_RED else G10_GREEN
                        Box(
                            Modifier.padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(col.copy(alpha = 0.22f))
                                .border(1.5.dp, col, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "👻 ¡Sombra a la ${if (s.lane == 0) "IZQUIERDA ◀" else "DERECHA ▶"}!",
                                fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = col
                            )
                        }
                    }
                }
            }

            // Glitch HP bar
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("GLITCH", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G10_RED.copy(alpha = 0.8f))
                Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.1f))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(glitchHp / G10_GLITCH_MAX_HP.toFloat())
                        .clip(RoundedCornerShape(4.dp)).background(G10_RED))
                }
            }

            // Main row: boxes grid + side panels
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {

                // Inventory panel
                Column(
                    modifier = Modifier.width(96.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, G10_AMBER.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("MOCHILA", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = G10_AMBER.copy(alpha = 0.8f))
                    G10InventorySlot(R.drawable.item_tijeras, "✂️", G10ItemKind.SCISSORS in collected)
                    G10InventorySlot(R.drawable.item_llave_energia, "🗝️", G10ItemKind.ENERGY_KEY in collected)
                    G10InventorySlot(R.drawable.item_linterna, "🔦", G10ItemKind.FLASHLIGHT in collected)
                    G10InventorySlot(R.drawable.item_cristal_protector, "💎", crystalAvailable)
                }

                // Boxes grid
                BoxWithConstraints(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    val cellSize = minOf(maxWidth / 3.4f, maxHeight / 3.4f)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        boxItems.indices.chunked(3).forEach { rowIdxs ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowIdxs.forEach { idx ->
                                    G10BossBoxTile(
                                        itemId = boxItems[idx],
                                        revealed = boxRevealed[idx],
                                        flash = boxFlash[idx],
                                        previewing = previewing,
                                        onTap = { onBoxTap(idx) },
                                        modifier = Modifier.size(cellSize)
                                    )
                                }
                            }
                        }
                    }
                }

                // Glitch + shadows panel
                Column(
                    modifier = Modifier.width(96.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A000A).copy(alpha = 0.85f))
                        .border(1.dp, G10_RED.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val glitchAttacking = phase == G10GlitchPhase.ATTACK_WARNING || phase == G10GlitchPhase.ATTACK_HIT
                    Image(
                        painterResource(if (glitchAttacking) R.drawable.glitch_boss_attack else R.drawable.glitch_boss),
                        "Glitch",
                        Modifier.height(88.dp)
                            .graphicsLayer {
                                if (glitchAttacking) { scaleX = 1.15f; scaleY = 1.15f }
                            },
                        contentScale = ContentScale.Fit)
                    Text("GLITCH", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = G10_RED)
                }
            }

            // Action buttons
            Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    G10HoldButton(
                        label = "🛡️ Defender",
                        color = G10_CYAN,
                        active = defending,
                        onPressChange = { defending = it }
                    )
                    G10HoldButton(
                        label = "🔋 Absorber",
                        color = G10_GREEN,
                        active = absorbing,
                        enabled = phase == G10GlitchPhase.ENERGY_INSERT,
                        onPressChange = { absorbing = it }
                    )
                    if (G10ItemKind.FLASHLIGHT in collected) {
                        val shadowLeft  = shadows.any { it.lane == 0 }
                        val shadowRight = shadows.any { it.lane == 1 }
                        G10TapButton("🔦 ◀", G10_AMBER, highlight = shadowLeft) { onFlashlightLane(0) }
                        G10TapButton("🔦 ▶", G10_AMBER, highlight = shadowRight) { onFlashlightLane(1) }
                    }
                }
            }
        }

        if (done)   G10DoneOverlay("¡El Glitch fue debilitado!\nCortaste los cables y abriste la jaula de energía.\nEl portal comienza a colapsar.") { onLevelComplete() }
        if (failed) G10FailOverlay(failReason) {
            done = false; failed = false; gameKey++
        }
    }
}

private fun g10PhaseLabel(phase: G10GlitchPhase): String = when (phase) {
    G10GlitchPhase.TINKERING      -> "El Glitch hace retoques en su máquina..."
    G10GlitchPhase.REPOSITION     -> "¡El Glitch reordena los objetos!"
    G10GlitchPhase.ATTACK_WARNING -> "⚠️ ¡El Glitch va a atacar! Mantén Defender"
    G10GlitchPhase.ATTACK_HIT     -> "💥 ¡Ataque del Glitch!"
    G10GlitchPhase.ENERGY_INSERT  -> "⚡ ¡Inserta su energía al portal! Mantén Absorber"
}

@Composable
private fun G10LivesBar(lives: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            val icon = when {
                lives >= i + 1f -> "❤️"
                lives >= i + 0.5f -> "💛"
                else -> "🖤"
            }
            Text(icon, fontSize = 14.sp)
        }
    }
}

@Composable
private fun G10InventorySlot(drawableRes: Int, emoji: String, has: Boolean) {
    Box(
        modifier = Modifier.size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (has) G10_AMBER.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f))
            .border(1.dp, if (has) G10_AMBER.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(drawableRes), emoji,
            modifier = Modifier.size(28.dp).graphicsLayer { alpha = if (has) 1f else 0.25f },
            contentScale = ContentScale.Fit)
    }
}

@Composable
private fun G10BossBoxTile(
    itemId: Int?,
    revealed: Boolean,
    flash: Boolean?,
    previewing: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when {
        flash == true  -> G10_GREEN.copy(alpha = 0.3f)
        flash == false -> G10_RED.copy(alpha = 0.3f)
        previewing     -> G10_AMBER.copy(alpha = 0.12f)
        else           -> Color.White.copy(alpha = 0.05f)
    }
    val border = when {
        flash == true  -> G10_GREEN
        flash == false -> G10_RED
        previewing     -> G10_AMBER.copy(alpha = 0.6f)
        else           -> G10_AMBER.copy(alpha = 0.3f)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(10.dp))
            .let { m ->
                if (itemId != null && !revealed && !previewing) {
                    m.pointerInput(Unit) { detectTapGestures { onTap() } }
                } else m
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            itemId == null -> Text("·", fontSize = 20.sp, color = Color.White.copy(alpha = 0.15f))
            revealed -> {
                val revealedItem = g10BossItemById(itemId)
                if (revealedItem.drawableRes != null) {
                    Image(painterResource(revealedItem.drawableRes), revealedItem.name,
                        modifier = Modifier.size(56.dp).graphicsLayer { alpha = 0.4f },
                        contentScale = ContentScale.Fit)
                } else {
                    Text(revealedItem.emoji, fontSize = 40.sp, modifier = Modifier.graphicsLayer { alpha = 0.4f })
                }
            }
            previewing -> {
                val previewItem = g10BossItemById(itemId)
                if (previewItem.drawableRes != null) {
                    Image(painterResource(previewItem.drawableRes), previewItem.name,
                        modifier = Modifier.size(56.dp), contentScale = ContentScale.Fit)
                } else {
                    Text(previewItem.emoji, fontSize = 40.sp)
                }
            }
            else -> Text("📦", fontSize = 44.sp)
        }
    }
}

@Composable
private fun G10HoldButton(
    label: String,
    color: Color,
    active: Boolean,
    enabled: Boolean = true,
    onPressChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) color.copy(alpha = 0.30f) else color.copy(alpha = 0.12f))
            .border(1.5.dp, color.copy(alpha = if (enabled) 0.6f else 0.2f), RoundedCornerShape(10.dp))
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        onPressChange(true)
                        tryAwaitRelease()
                        onPressChange(false)
                    }
                )
            }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (enabled) color else color.copy(alpha = 0.35f))
    }
}

@Composable
private fun G10TapButton(label: String, color: Color, highlight: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = if (highlight) 0.40f else 0.10f))
            .border(if (highlight) 2.dp else 1.dp, color.copy(alpha = if (highlight) 1f else 0.3f), RoundedCornerShape(10.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = if (highlight) color else color.copy(alpha = 0.45f))
    }
}
