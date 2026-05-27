package com.example.hds_tesisapp.ui.theme.games.game8.level4

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
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
import com.example.hds_tesisapp.ui.theme.games.game8.*
import com.example.hds_tesisapp.ui.theme.games.game8.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CONFIG            = G8_LEVEL_CONFIGS[3]
private const val HERO_COOLDOWN_MS = 2500L
private const val HERO_MOVE_STEP   = 0.12f
private const val SNAIL_SPEED_MULT = 0.45f

@Composable
fun Level4G8Screen(
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

    var lives        by remember { mutableIntStateOf(3) }
    var hasShield    by remember { mutableStateOf(false) }
    var hasBomb      by remember { mutableStateOf(false) }
    var isSlowed     by remember { mutableStateOf(false) }
    var handledCount by remember { mutableIntStateOf(0) }
    var done         by remember { mutableStateOf(false) }
    var failed       by remember { mutableStateOf(false) }
    var gameKey      by remember { mutableIntStateOf(0) }
    var heroXFrac    by remember { mutableFloatStateOf(0.5f) }
    var selectedHero by remember { mutableStateOf<G8Hero?>(null) }
    val heroCooldowns= remember { mutableStateMapOf<G8Hero, Float>() }
    var flash        by remember { mutableStateOf<Boolean?>(null) }
    var bombWarning  by remember { mutableStateOf(false) }

    var objs    by remember { mutableStateOf<List<G8Obj>>(emptyList()) }
    var objYs   by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var handled by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var targetObjId by remember { mutableStateOf<Int?>(null) }

    val sequence = remember(gameKey) { generateEventSequence(CONFIG) }
    var seqIndex by remember(gameKey) { mutableIntStateOf(0) }
    val rng      = remember(gameKey) { java.util.Random() }

    fun loseLife() {
        if (hasShield) { hasShield = false; return }
        lives--; if (lives <= 0) failed = true
    }

    fun spawnOne() {
        if (seqIndex >= sequence.size || done || failed) return
        if (objs.filter { it.id !in handled }.size >= CONFIG.maxOnScreen) return
        val type = sequence[seqIndex++]
        val x = if (type.isGround) 0.5f else rng.nextFloat().coerceIn(0.1f, 0.9f)
        val newObj = G8Obj(id = seqIndex, type = type, xFrac = x,
            yFrac = if (type.isGround) 1f else 0f)
        objs = objs + newObj
        objYs = objYs + (newObj.id to newObj.yFrac)
        if (type == G8ObjType.EVIL_BOMB) bombWarning = true
    }

    fun applyBeneficial(type: G8ObjType) {
        when (type) {
            G8ObjType.HEART_BLOCK  -> if (lives < 3) lives++
            G8ObjType.SHIELD_BLOCK -> hasShield = true
            G8ObjType.SNAIL_BLOCK  -> isSlowed = true
            G8ObjType.BOMB_BLOCK   -> hasBomb = true
            else -> {}
        }
    }

    fun inRange(obj: G8Obj) = obj.type.isGround || obj.type.hero == G8Hero.LINA ||
            kotlin.math.abs(heroXFrac - obj.xFrac) < 0.28f

    fun onAction() {
        val hero = selectedHero ?: return
        val obj  = objs.firstOrNull { it.id == targetObjId && it.id !in handled } ?: return
        if (flash != null) return
        if (obj.type == G8ObjType.EVIL_BOMB) return  // can't be handled by hero directly
        val correct = obj.type.hero == hero && (obj.type.isGround || inRange(obj))
        handled = handled + obj.id
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
                targetObjId = null; selectedHero = null
                if (handledCount >= CONFIG.totalEvents) done = true
                else if (objs.none { it.id !in handled }) spawnOne()
            } else {
                if (!obj.type.isGround) loseLife()
                targetObjId = null; selectedHero = null
                if (!failed && objs.none { it.id !in handled }) spawnOne()
            }
        }
    }

    fun onUseBomb() {
        val obj = objs.firstOrNull { it.id == targetObjId && it.id !in handled } ?: return
        if (!hasBomb) return
        hasBomb = false; handled = handled + obj.id
        scope.launch {
            flash = true; delay(500); flash = null
            if (obj.type != G8ObjType.EVIL_BOMB) handledCount++
            targetObjId = null
            if (handledCount >= CONFIG.totalEvents) done = true
            else if (objs.none { it.id !in handled }) spawnOne()
        }
    }

    LaunchedEffect(isSlowed) {
        if (isSlowed) { delay(8000L); isSlowed = false }
    }

    LaunchedEffect(gameKey) {
        delay(800L); spawnOne(); delay(CONFIG.spawnIntervalMs); spawnOne()
        while (!done && !failed) {
            delay(16L)
            val speedMult = if (isSlowed) SNAIL_SPEED_MULT else 1f
            val delta = 16f / CONFIG.fallDurationMs * speedMult
            val newYs = objYs.toMutableMap()
            val toMiss = mutableListOf<Int>()
            objs.forEach { obj ->
                if (obj.id in handled || obj.type.isGround) return@forEach
                val y = (newYs[obj.id] ?: 0f) + delta
                newYs[obj.id] = y.coerceAtMost(1f)
                if (y >= 1f) toMiss.add(obj.id)
            }
            objYs = newYs
            toMiss.forEach { id ->
                if (id !in handled) {
                    val obj = objs.firstOrNull { it.id == id }
                    handled = handled + id
                    if (obj?.type == G8ObjType.EVIL_BOMB) {
                        // Bomb hits ground — check hero position
                        val bombX = obj?.xFrac ?: 0.5f
                        if (kotlin.math.abs(heroXFrac - bombX) < 0.18f) {
                            flash = false; delay(400L); flash = null; loseLife()
                        }
                        bombWarning = false
                    } else {
                        flash = false; delay(400L); flash = null; loseLife()
                    }
                    if (!failed && objs.none { it.id !in handled }) spawnOne()
                }
            }
            if (!done && !failed && objs.filter { it.id !in handled }.size < CONFIG.maxOnScreen && seqIndex < sequence.size) {
                spawnOne(); delay(CONFIG.spawnIntervalMs)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.city_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background((if (ok) G8_GREEN else G8_RED).copy(alpha = 0.20f)).zIndex(5f))
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G8MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 4 · ¡Bombas!", fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Esquiva las bombas malignas — o usa la bomba de Lina", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G8_RED.copy(alpha = 0.9f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (bombWarning) Text("⚠️ BOMBA", fontSize = 9.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, color = G8_RED)
                    if (isSlowed) Text("🐌", fontSize = 12.sp)
                    EventProgressBar(handledCount, CONFIG.totalEvents, G8_RED)
                    G8LivesRow(lives, hasShield, hasBomb)
                }
            }

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

                // Hero (movable)
                val heroXOff = ((gameW - heroW) * heroXFrac.coerceIn(0f, 1f)).coerceIn(0.dp, gameW - heroW)
                val heroYOff = gameH - heroH - 4.dp
                Box(modifier = Modifier.absoluteOffset(x = heroXOff, y = heroYOff).zIndex(2f)) {
                    val heroToShow = selectedHero ?: G8Hero.MAX
                    Image(painterResource(heroToShow.spriteRes()), heroToShow.label(),
                        Modifier.height(heroH), contentScale = ContentScale.Fit)
                }

                // Objects
                objs.filter { it.id !in handled }.forEach { obj ->
                    val y = objYs[obj.id] ?: obj.yFrac
                    val isTarget = obj.id == targetObjId
                    val heroColor = obj.type.hero?.color() ?: G8_RED
                    val xOff = ((gameW - objSize) * obj.xFrac.coerceIn(0f, 1f)).coerceIn(0.dp, gameW - objSize)
                    val yOff = if (obj.type.isGround) maxYOff
                               else (maxYOff * y).coerceIn(0.dp, maxYOff)
                    Box(
                        modifier = Modifier
                            .absoluteOffset(x = xOff, y = yOff)
                            .zIndex(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isTarget) heroColor.copy(alpha = 0.3f) else Color.Transparent)
                            .border(if (isTarget) 2.dp else 0.dp, if (isTarget) heroColor else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { targetObjId = obj.id; selectedHero = null }
                            .padding(2.dp)
                    ) {
                        if (obj.type.isGround) GroundObjCard(obj) else FallingObjCard(obj)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .clickable { heroXFrac = (heroXFrac - HERO_MOVE_STEP).coerceAtLeast(0.05f) },
                    contentAlignment = Alignment.Center) { Text("◀", fontSize = 24.sp, color = Color.White.copy(alpha = 0.8f)) }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    targetObjId?.let { tid ->
                        objs.firstOrNull { it.id == tid && it.id !in handled }?.let { tObj ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background((tObj.type.hero?.color() ?: G8_RED).copy(alpha = 0.2f))
                                .border(1.dp, (tObj.type.hero?.color() ?: G8_RED).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text("▶ ${tObj.type.displayLabel()}", fontSize = 8.sp,
                                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                                    color = tObj.type.hero?.color() ?: G8_RED)
                            }
                        }
                    }
                    G8Hero.entries.forEach { hero ->
                        val isCooling = heroCooldowns.containsKey(hero)
                        HeroFaceButton(hero, selectedHero == hero, isCooling, heroCooldowns[hero] ?: 1f) {
                            if (!isCooling) selectedHero = hero
                        }
                    }
                    ActionButton(selectedHero, hasBomb, { onAction() }, { onUseBomb() })
                }

                Box(modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .clickable { heroXFrac = (heroXFrac + HERO_MOVE_STEP).coerceAtMost(0.95f) },
                    contentAlignment = Alignment.Center) { Text("▶", fontSize = 24.sp, color = Color.White.copy(alpha = 0.8f)) }
            }
        }

        if (done)   G8DoneOverlay("¡Las bombas fueron neutralizadas!\nEl Glitch pierde terreno.") { onLevelComplete() }
        if (failed) G8FailOverlay {
            lives = 3; hasShield = false; hasBomb = false; isSlowed = false; bombWarning = false
            handledCount = 0; done = false; failed = false; objs = emptyList()
            objYs = emptyMap(); handled = emptySet(); seqIndex = 0
            targetObjId = null; selectedHero = null; heroXFrac = 0.5f; gameKey++
        }
    }
}
