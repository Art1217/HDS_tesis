package com.example.hds_tesisapp.ui.theme.games.game8.level1

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
import com.example.hds_tesisapp.ui.theme.games.game8.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Theme ────────────────────────────────────────────────────────────────────

internal val G8_BLUE   = Color(0xFF2196F3)
internal val G8_CYAN   = Color(0xFF00E5FF)
internal val G8_ORANGE = Color(0xFFFF9800)
internal val G8_PURPLE = Color(0xFF9C27B0)
internal val G8_RED    = Color(0xFFFF5252)
internal val G8_GREEN  = Color(0xFF69FF47)
internal val G8_GOLD   = Color(0xFFFFD600)

internal fun G8Hero.color() = when (this) {
    G8Hero.MAX  -> G8_BLUE
    G8Hero.LINA -> G8_CYAN
    G8Hero.TOM  -> G8_ORANGE
    G8Hero.ATOM -> G8_PURPLE
}

// ─── Shared Composables ───────────────────────────────────────────────────────

@Composable
internal fun G8MenuButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("☰", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
internal fun G8LivesRow(lives: Int, hasShield: Boolean, hasBomb: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        if (hasShield) {
            Text("🛡️", fontSize = 14.sp)
        }
        if (hasBomb) {
            Text("💣", fontSize = 14.sp)
        }
        repeat(3) { i ->
            Text(if (i < lives) "❤️" else "🖤", fontSize = 14.sp)
        }
    }
}

@Composable
internal fun G8DoneOverlay(message: String, onContinue: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)).zIndex(10f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF001A00))
                .border(2.dp, G8_GREEN.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("✅ ¡ZONA SUPERADA!", fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G8_GREEN)
            Text(message, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(G8_GREEN.copy(alpha = 0.18f))
                    .border(1.5.dp, G8_GREEN.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { onContinue() }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("CONTINUAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G8_GREEN)
            }
        }
    }
}

@Composable
internal fun G8FailOverlay(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)).zIndex(10f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, G8_RED.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("💥 ¡CIUDAD EN CAOS!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = G8_RED)
            Text("Los eventos te superaron.\n¡Reagrúpate e inténtalo de nuevo!", fontSize = 12.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(G8_RED.copy(alpha = 0.18f))
                    .border(1.5.dp, G8_RED.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { onRetry() }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("REINTENTAR", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G8_RED)
            }
        }
    }
}

// Hero face button with cooldown ring
@Composable
internal fun HeroFaceButton(
    hero: G8Hero,
    isSelected: Boolean,
    isCoolingDown: Boolean,
    cooldownFrac: Float,        // 0f=cooling, 1f=ready
    onClick: () -> Unit
) {
    val color = hero.color()
    Box(
        modifier = Modifier
            .size(80.dp)
            .drawBehind {
                if (isCoolingDown) {
                    // Cooldown ring
                    drawArc(
                        color = color.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f * (1f - cooldownFrac),
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                    )
                }
                if (isSelected) {
                    drawCircle(color.copy(alpha = 0.3f), radius = size.minDimension / 2f + 8f)
                }
            }
            .clip(CircleShape)
            .background(
                if (isSelected) color.copy(alpha = 0.30f)
                else if (isCoolingDown) Color.Gray.copy(alpha = 0.15f)
                else color.copy(alpha = 0.15f)
            )
            .border(
                2.dp,
                if (isSelected) color else if (isCoolingDown) Color.Gray.copy(alpha = 0.3f) else color.copy(alpha = 0.5f),
                CircleShape
            )
            .clickable(enabled = !isCoolingDown) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(hero.spriteRes()), hero.label(),
            Modifier.fillMaxSize(0.82f), contentScale = ContentScale.Fit)
    }
}

// Big action button
@Composable
internal fun ActionButton(
    selectedHero: G8Hero?,
    hasBomb: Boolean,
    onAction: () -> Unit,
    onUseBomb: () -> Unit
) {
    val enabled = selectedHero != null
    val color   = selectedHero?.color() ?: Color.White.copy(alpha = 0.2f)
    val label   = selectedHero?.actionLabel() ?: "—"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) color.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.05f))
                .border(
                    2.dp,
                    if (enabled) color.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(14.dp)
                )
                .clickable(enabled = enabled) { onAction() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 15.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (enabled) color else Color.White.copy(alpha = 0.2f))
        }
        // Bomb usage button
        if (hasBomb) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(G8_GOLD.copy(alpha = 0.18f))
                    .border(1.dp, G8_GOLD.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable { onUseBomb() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("💣 USAR BOMBA", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = G8_GOLD)
            }
        }
    }
}

// Falling object visual card
@Composable
internal fun FallingObjCard(obj: G8Obj, isGlitching: Boolean = false) {
    val heroColor = obj.type.hero?.color() ?: G8_RED
    val scale = if (isGlitching) {
        val inf = rememberInfiniteTransition(label = "glitch")
        inf.animateFloat(0.92f, 1.08f, infiniteRepeatable(tween(120), RepeatMode.Reverse), label = "gs").value
    } else 1f

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .size(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isGlitching) Color(0xFFFF1744).copy(alpha = 0.2f)
                else heroColor.copy(alpha = 0.15f)
            )
            .border(
                2.dp,
                if (isGlitching) Color(0xFFFF1744) else heroColor.copy(alpha = 0.7f),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(obj.type.spriteRes()), obj.type.displayLabel(),
            Modifier.fillMaxSize(0.8f), contentScale = ContentScale.Fit)
    }
}

// Ground object visual
@Composable
internal fun GroundObjCard(obj: G8Obj) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(G8_ORANGE.copy(alpha = 0.12f))
            .border(1.5.dp, G8_ORANGE.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(painterResource(obj.type.spriteRes()), obj.type.displayLabel(),
                Modifier.size(64.dp), contentScale = ContentScale.Fit)
            Text(obj.type.displayLabel(), fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                fontWeight = FontWeight.Bold, color = G8_ORANGE, textAlign = TextAlign.Center)
        }
    }
}

// Progress bar
@Composable
internal fun EventProgressBar(handled: Int, total: Int, color: Color) {
    val frac = (handled.toFloat() / total).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("$handled / $total eventos", fontSize = 7.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.5f))
        Box(
            modifier = Modifier.width(120.dp).height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(frac)
                    .clip(RoundedCornerShape(3.dp)).background(color)
            )
        }
    }
}

// ─── Level 1 Screen ───────────────────────────────────────────────────────────

private val CONFIG = G8_LEVEL_CONFIGS[0]
private const val HERO_COOLDOWN_MS = 2500L

@Composable
fun Level1G8Screen(
    onLevelComplete: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()

    // Game state
    var lives         by remember { mutableIntStateOf(3) }
    var hasShield     by remember { mutableStateOf(false) }
    var hasBomb       by remember { mutableStateOf(false) }
    var handledCount  by remember { mutableIntStateOf(0) }
    var done          by remember { mutableStateOf(false) }
    var failed        by remember { mutableStateOf(false) }
    var gameKey       by remember { mutableIntStateOf(0) }

    // Current object
    var currentObj    by remember { mutableStateOf<G8Obj?>(null) }
    var objYFrac      by remember { mutableFloatStateOf(0f) }
    var objHandled    by remember { mutableStateOf(false) }
    var flash         by remember { mutableStateOf<Boolean?>(null) }  // true=good, false=bad

    // Hero selection & cooldown
    var selectedHero    by remember { mutableStateOf<G8Hero?>(null) }
    val heroCooldowns   = remember { mutableStateMapOf<G8Hero, Float>() } // 0f=cooling,1f=ready

    // Event sequence
    val sequence = remember(gameKey) { generateEventSequence(CONFIG) }
    var seqIndex by remember(gameKey) { mutableIntStateOf(0) }

    fun loseLife() {
        if (hasShield) { hasShield = false; return }
        lives--
        if (lives <= 0) failed = true
    }

    fun spawnNext() {
        if (seqIndex >= sequence.size || done || failed) return
        val type = sequence[seqIndex++]
        val x = if (type.isGround) 0.5f else 0.5f  // L1: fixed center
        currentObj = G8Obj(id = seqIndex, type = type, xFrac = x,
            yFrac = if (type.isGround) 1f else 0f)
        objYFrac = if (type.isGround) 1f else 0f
        objHandled = false
        selectedHero = null
    }

    fun applyBeneficialEffect(type: G8ObjType) {
        when (type) {
            G8ObjType.HEART_BLOCK  -> if (lives < 3) lives++
            G8ObjType.SHIELD_BLOCK -> hasShield = true
            G8ObjType.SNAIL_BLOCK  -> { /* handled via speed state in higher levels */ }
            G8ObjType.BOMB_BLOCK   -> hasBomb = true
            else -> {}
        }
    }

    fun onAction() {
        val obj = currentObj ?: return
        val hero = selectedHero ?: return
        if (objHandled || flash != null) return

        val correct = obj.type.hero == hero
        objHandled = true
        scope.launch {
            flash = correct
            delay(500L)
            flash = null
            if (correct) {
                if (obj.type.hero == G8Hero.LINA) applyBeneficialEffect(obj.type)
                handledCount++
                // Start cooldown for hero
                heroCooldowns[hero] = 0f
                scope.launch {
                    val steps = 25
                    repeat(steps) {
                        delay(HERO_COOLDOWN_MS / steps)
                        heroCooldowns[hero] = (it + 1f) / steps
                    }
                    heroCooldowns.remove(hero)
                }
                if (handledCount >= CONFIG.totalEvents) done = true
                else spawnNext()
            } else {
                // Wrong hero — for ground obj no penalty
                if (!obj.type.isGround) loseLife()
                if (!failed) spawnNext()
            }
        }
    }

    fun onUseBomb() {
        val obj = currentObj ?: return
        if (!hasBomb || objHandled) return
        hasBomb = false
        objHandled = true
        scope.launch {
            flash = true; delay(500); flash = null
            handledCount++
            if (handledCount >= CONFIG.totalEvents) done = true else spawnNext()
        }
    }

    // Falling animation loop
    LaunchedEffect(gameKey) {
        delay(800L)
        spawnNext()
        while (!done && !failed) {
            delay(16L)
            val obj = currentObj
            if (obj != null && !obj.type.isGround && !objHandled) {
                val fallDelta = 16f / CONFIG.fallDurationMs
                objYFrac = (objYFrac + fallDelta).coerceAtMost(1f)
                if (objYFrac >= 1f && !objHandled) {
                    // Missed
                    objHandled = true
                    flash = false
                    delay(500L)
                    flash = null
                    loseLife()
                    if (!failed) spawnNext()
                }
            }
        }
    }

    // Build display obj with current y
    val displayObj = currentObj?.copy(yFrac = objYFrac)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.city_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.38f)))

        // Flash overlay
        flash?.let { ok ->
            Box(Modifier.fillMaxSize()
                .background((if (ok) G8_GREEN else G8_RED).copy(alpha = 0.20f))
                .zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G8MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 1 · La Ciudad Reacciona", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Selecciona al héroe correcto para cada evento", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G8_CYAN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    EventProgressBar(handledCount, CONFIG.totalEvents, G8_CYAN)
                    G8LivesRow(lives, hasShield, hasBomb)
                }
            }

            // ── Game area ────────────────────────────────────────────────────
            androidx.compose.foundation.layout.BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                val gameH   = maxHeight
                val gameW   = maxWidth
                val objSize = 100.dp
                val heroH   = 144.dp
                // Max Y so object bottom never overlaps hero
                val maxYOff = (gameH - heroH - objSize).coerceAtLeast(0.dp)

                // Ground line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter)
                        .background(G8_CYAN.copy(alpha = 0.3f))
                )

                // Hero sprite (center, L1 fixed) — always on top via zIndex
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .zIndex(2f)
                ) {
                    val heroToShow = selectedHero ?: G8Hero.MAX
                    Image(painterResource(heroToShow.spriteRes()), heroToShow.label(),
                        Modifier.height(heroH), contentScale = ContentScale.Fit)
                }

                // Falling / ground object — positioned with absoluteOffset
                displayObj?.let { obj ->
                    if (!objHandled && !failed && !done) {
                        val yOff = if (obj.type.isGround) maxYOff
                                   else (maxYOff * obj.yFrac).coerceIn(0.dp, maxYOff)
                        val xOff = ((gameW - objSize) / 2).coerceAtLeast(0.dp)
                        Box(modifier = Modifier.absoluteOffset(x = xOff, y = yOff).zIndex(1f)) {
                            if (obj.type.isGround) GroundObjCard(obj) else FallingObjCard(obj)
                        }
                    }
                }

                // Hero-type hint chip
                displayObj?.let { obj ->
                    if (!objHandled) {
                        val heroColor = obj.type.hero?.color() ?: G8_RED
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(heroColor.copy(alpha = 0.15f))
                                .border(1.dp, heroColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                if (obj.type == G8ObjType.EVIL_BOMB) "⚠️ ¡ESQUIVA!"
                                else obj.type.hero?.label() ?: "?",
                                fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = heroColor
                            )
                        }
                    }
                }
            }

            // ── Bottom controls ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 4 hero buttons
                G8Hero.entries.forEach { hero ->
                    val isCooling = heroCooldowns.containsKey(hero)
                    val coolFrac  = heroCooldowns[hero] ?: 1f
                    HeroFaceButton(
                        hero         = hero,
                        isSelected   = selectedHero == hero,
                        isCoolingDown = isCooling,
                        cooldownFrac  = coolFrac,
                        onClick = { if (!isCooling) selectedHero = hero }
                    )
                }

                // Action button
                ActionButton(
                    selectedHero = selectedHero,
                    hasBomb      = hasBomb,
                    onAction     = { onAction() },
                    onUseBomb    = { onUseBomb() }
                )
            }
        }

        if (done)   G8DoneOverlay("¡Los eventos están bajo control!\nLa ciudad respira de nuevo.") { onLevelComplete() }
        if (failed) G8FailOverlay { lives = 3; hasShield = false; hasBomb = false; handledCount = 0; done = false; failed = false; seqIndex = 0; currentObj = null; objYFrac = 0f; objHandled = false; selectedHero = null; gameKey++ }
    }
}
