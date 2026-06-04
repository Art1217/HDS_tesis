package com.example.hds_tesisapp.ui.theme.games.game8.level2

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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game8.G8Hero
import com.example.hds_tesisapp.ui.theme.games.game8.G8Obj
import com.example.hds_tesisapp.ui.theme.games.game8.G8ObjType
import com.example.hds_tesisapp.ui.theme.games.game8.G8_LEVEL_CONFIGS
import com.example.hds_tesisapp.ui.theme.games.game8.generateEventSequence
import com.example.hds_tesisapp.ui.theme.games.game8.hero
import com.example.hds_tesisapp.ui.theme.games.game8.isGround
import com.example.hds_tesisapp.ui.theme.games.game8.label
import com.example.hds_tesisapp.ui.theme.games.game8.level1.ActionButton
import com.example.hds_tesisapp.ui.theme.games.game8.level1.EventProgressBar
import com.example.hds_tesisapp.ui.theme.games.game8.level1.FallingObjCard
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8LivesRow
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8MenuButton
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8_CYAN
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8_GREEN
import com.example.hds_tesisapp.ui.theme.games.game8.level1.G8_RED
import com.example.hds_tesisapp.ui.theme.games.game8.level1.GroundObjCard
import com.example.hds_tesisapp.ui.theme.games.game8.level1.HeroFaceButton
import com.example.hds_tesisapp.ui.theme.games.game8.spriteRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CONFIG = G8_LEVEL_CONFIGS[1]
private const val HERO_COOLDOWN_MS = 2500L
private const val HERO_MOVE_STEP   = 0.12f   // fraction moved per button press

@Composable
fun Level2G8Screen(
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

    var lives         by remember { mutableIntStateOf(3) }
    var hasShield     by remember { mutableStateOf(false) }
    var hasBomb       by remember { mutableStateOf(false) }
    var handledCount  by remember { mutableIntStateOf(0) }
    var done          by remember { mutableStateOf(false) }
    var failed        by remember { mutableStateOf(false) }
    var gameKey       by remember { mutableIntStateOf(0) }
    var heroXFrac     by remember { mutableFloatStateOf(0.5f) }

    var currentObj    by remember { mutableStateOf<G8Obj?>(null) }
    var objYFrac      by remember { mutableFloatStateOf(0f) }
    var objHandled    by remember { mutableStateOf(false) }
    var flash         by remember { mutableStateOf<Boolean?>(null) }
    var selectedHero  by remember { mutableStateOf<G8Hero?>(null) }
    val heroCooldowns = remember { mutableStateMapOf<G8Hero, Float>() }

    val sequence  = remember(gameKey) { generateEventSequence(CONFIG) }
    var seqIndex  by remember(gameKey) { mutableIntStateOf(0) }
    val rng       = remember(gameKey) { java.util.Random() }

    fun loseLife() {
        if (hasShield) { hasShield = false; return }
        lives--
        if (lives <= 0) failed = true
    }

    fun spawnNext() {
        if (seqIndex >= sequence.size || done || failed) return
        val type = sequence[seqIndex++]
        val x = if (type.isGround) 0.5f else rng.nextFloat().coerceIn(0.1f, 0.9f)
        currentObj = G8Obj(id = seqIndex, type = type, xFrac = x,
            yFrac = if (type.isGround) 1f else 0f)
        objYFrac   = if (type.isGround) 1f else 0f
        objHandled = false
        selectedHero = null
    }

    fun applyBeneficial(type: G8ObjType) {
        when (type) {
            G8ObjType.HEART_BLOCK  -> if (lives < 3) lives++
            G8ObjType.SHIELD_BLOCK -> hasShield = true
            G8ObjType.BOMB_BLOCK   -> hasBomb = true
            else -> {}
        }
    }

    fun inRange(obj: G8Obj): Boolean {
        return obj.type.isGround || obj.type.hero == G8Hero.LINA ||
               kotlin.math.abs(heroXFrac - obj.xFrac) < 0.28f
    }

    fun onAction() {
        val obj  = currentObj ?: return
        val hero = selectedHero ?: return
        if (objHandled || flash != null) return
        val correct = obj.type.hero == hero && (obj.type.isGround || inRange(obj))
        objHandled = true
        scope.launch {
            flash = correct; delay(500L); flash = null
            if (correct) {
                if (hero == G8Hero.LINA) applyBeneficial(obj.type)
                handledCount++
                heroCooldowns[hero] = 0f
                scope.launch {
                    val steps = 25
                    repeat(steps) { delay(HERO_COOLDOWN_MS / steps); heroCooldowns[hero] = (it + 1f) / steps }
                    heroCooldowns.remove(hero)
                }
                if (handledCount >= CONFIG.totalEvents) done = true else spawnNext()
            } else {
                if (!obj.type.isGround) loseLife()
                if (!failed) spawnNext()
            }
        }
    }

    fun onUseBomb() {
        val obj = currentObj ?: return
        if (!hasBomb || objHandled) return
        hasBomb = false; objHandled = true
        scope.launch { flash = true; delay(500); flash = null; handledCount++; if (handledCount >= CONFIG.totalEvents) done = true else spawnNext() }
    }

    LaunchedEffect(gameKey) {
        delay(800L); spawnNext()
        while (!done && !failed) {
            delay(16L)
            val obj = currentObj
            if (obj != null && !obj.type.isGround && !objHandled) {
                objYFrac = (objYFrac + 16f / CONFIG.fallDurationMs).coerceAtMost(1f)
                if (objYFrac >= 1f && !objHandled) {
                    objHandled = true; flash = false; delay(500L); flash = null
                    loseLife(); if (!failed) spawnNext()
                }
            }
        }
    }

    val displayObj = currentObj?.copy(yFrac = objYFrac)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.city_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.40f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize()
                .background((if (ok) G8_GREEN else G8_RED).copy(alpha = 0.20f)).zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G8MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 2 · Velocidad Aumentada", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Los objetos caen más rápido — ¡muévete!", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G8_CYAN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    EventProgressBar(handledCount, CONFIG.totalEvents, G8_CYAN)
                    G8LivesRow(lives, hasShield, hasBomb)
                }
            }

            // Game area
            androidx.compose.foundation.layout.BoxWithConstraints(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 6.dp)
            ) {
                val gameH   = maxHeight
                val gameW   = maxWidth
                val objSize = 100.dp
                val heroH   = 144.dp
                val heroW   = 80.dp
                val maxYOff = (gameH - heroH - objSize).coerceAtLeast(0.dp)

                Box(Modifier.fillMaxWidth().height(2.dp).align(Alignment.BottomCenter)
                    .background(G8_CYAN.copy(alpha = 0.3f)))

                // Hero sprite (movable via absoluteOffset)
                val heroXOff = ((gameW - heroW) * heroXFrac.coerceIn(0f, 1f)).coerceIn(0.dp, gameW - heroW)
                val heroYOff = gameH - heroH - 4.dp
                Box(modifier = Modifier.absoluteOffset(x = heroXOff, y = heroYOff).zIndex(2f)) {
                    val heroToShow = selectedHero ?: G8Hero.MAX
                    Image(painterResource(heroToShow.spriteRes()), heroToShow.label(),
                        Modifier.height(heroH), contentScale = ContentScale.Fit)
                }

                // Falling obj — precise absoluteOffset
                displayObj?.let { obj ->
                    if (!objHandled && !failed && !done) {
                        val yOff = if (obj.type.isGround) maxYOff
                                   else (maxYOff * obj.yFrac).coerceIn(0.dp, maxYOff)
                        val xOff = ((gameW - objSize) * obj.xFrac).coerceIn(0.dp, gameW - objSize)
                        Box(modifier = Modifier.absoluteOffset(x = xOff, y = yOff).zIndex(1f)) {
                            if (obj.type.isGround) GroundObjCard(obj) else FallingObjCard(obj)
                        }
                    }
                }

                // Range indicator
                displayObj?.let { obj ->
                    if (!objHandled && !obj.type.isGround && obj.type.hero != G8Hero.LINA) {
                        val inR = inRange(obj)
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background((if (inR) G8_GREEN else G8_RED).copy(alpha = 0.15f))
                            .border(1.dp, (if (inR) G8_GREEN else G8_RED).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(if (inR) "✅ Rango OK" else "❌ Muévete",
                                fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (inR) G8_GREEN else G8_RED)
                        }
                    }
                }
            }

            // Bottom controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Move left
                Box(
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .clickable { heroXFrac = (heroXFrac - HERO_MOVE_STEP).coerceAtLeast(0.05f) },
                    contentAlignment = Alignment.Center
                ) { Text("◀", fontSize = 24.sp, color = Color.White.copy(alpha = 0.8f)) }

                // Hero buttons + action
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    G8Hero.entries.forEach { hero ->
                        val isCooling = heroCooldowns.containsKey(hero)
                        HeroFaceButton(hero, selectedHero == hero, isCooling, heroCooldowns[hero] ?: 1f) {
                            if (!isCooling) selectedHero = hero
                        }
                    }
                    ActionButton(selectedHero, hasBomb, { onAction() }, { onUseBomb() })
                }

                // Move right
                Box(
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .clickable { heroXFrac = (heroXFrac + HERO_MOVE_STEP).coerceAtMost(0.95f) },
                    contentAlignment = Alignment.Center
                ) { Text("▶", fontSize = 24.sp, color = Color.White.copy(alpha = 0.8f)) }
            }
        }

        if (done)   G8DoneOverlay("¡Velocidad controlada!\nAtom se une al equipo.") { onLevelComplete() }
        if (failed) G8FailOverlay {
            lives = 3; hasShield = false; hasBomb = false; handledCount = 0
            done = false; failed = false; seqIndex = 0; currentObj = null
            objYFrac = 0f; objHandled = false; selectedHero = null; heroXFrac = 0.5f; gameKey++
        }
    }
}
