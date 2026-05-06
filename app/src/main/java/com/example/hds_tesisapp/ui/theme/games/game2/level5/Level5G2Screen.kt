package com.example.hds_tesisapp.ui.theme.games.game2.level5

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game1.G1MenuButton
import com.example.hds_tesisapp.ui.theme.games.game2.*
import kotlinx.coroutines.delay

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun buildLevel5Pool(): List<DragItem> {
    val fruits  = buildFruitPool(
        FruitType.MANZANA to 2, FruitType.NARANJA to 2, FruitType.LIMON to 2
    )
    val animals = buildAnimalPool(
        AnimalType.PAJARO  to 1, AnimalType.AGUILA     to 1,
        AnimalType.ARDILLA to 1, AnimalType.CARPINTERO to 1,
        AnimalType.ZORRO   to 1, AnimalType.CONEJO     to 1,
    )
    return (fruits + animals).shuffled()
}

private const val TIMER_SECONDS  = 200
private const val SWAP_INTERVAL  = 10

@Composable
fun Level5G2Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool           = remember { mutableStateListOf<DragItem>().also { it.addAll(buildLevel5Pool()) } }
    val manzanaZone    = remember { mutableStateListOf<DragItem>() }
    val naranjaZone    = remember { mutableStateListOf<DragItem>() }
    val limonZone      = remember { mutableStateListOf<DragItem>() }
    val copaZone       = remember { mutableStateListOf<DragItem>() }
    val troncoZone     = remember { mutableStateListOf<DragItem>() }
    val madrigZone     = remember { mutableStateListOf<DragItem>() }

    var selectedItem   by remember { mutableStateOf<DragItem?>(null) }
    var showError      by remember { mutableStateOf(false) }
    var showTutorial   by remember { mutableStateOf(true) }
    var timerLeft      by remember { mutableStateOf(TIMER_SECONDS) }
    var swapCountdown  by remember { mutableStateOf(SWAP_INTERVAL) }
    var timerRunning   by remember { mutableStateOf(false) }
    var showVictory    by remember { mutableStateOf(false) }
    var showTimeUp     by remember { mutableStateOf(false) }
    var showSwapAlert  by remember { mutableStateOf(false) }
    var bossVisible    by remember { mutableStateOf(false) }

    // All placed zones for boss to swap from
    val allZones = listOf(manzanaZone, naranjaZone, limonZone, copaZone, troncoZone, madrigZone)

    LaunchedEffect(timerRunning) {
        if (!timerRunning) return@LaunchedEffect
        bossVisible = true
        while (timerLeft > 0 && !showVictory) {
            delay(1000L)
            timerLeft--
            swapCountdown--
            if (swapCountdown <= 0) {
                swapCountdown = SWAP_INTERVAL
                // Mapache intercambia 2 items between filled zones
                val filledZones = allZones.filter { it.isNotEmpty() }
                if (filledZones.size >= 2) {
                    val shuffled = filledZones.shuffled()
                    val zoneA = shuffled[0]; val zoneB = shuffled[1]
                    val itemA = zoneA.random(); val itemB = zoneB.random()
                    zoneA.remove(itemA); zoneB.remove(itemB)
                    pool.add(itemA); pool.add(itemB)
                    showSwapAlert = true
                }
            }
        }
        if (timerLeft == 0 && !showVictory) showTimeUp = true
    }

    LaunchedEffect(showError)     { if (showError)     { delay(400L); showError     = false } }
    LaunchedEffect(showSwapAlert) { if (showSwapAlert) { delay(2000L); showSwapAlert = false } }

    LaunchedEffect(pool.size) {
        if (pool.isEmpty() && !showVictory) showVictory = true
    }

    fun startTimer() { if (!timerRunning) timerRunning = true }

    fun placeSelected(
        targetFruit: FruitType? = null,
        targetHabitat: HabitatType? = null,
        zone: MutableList<DragItem>
    ) {
        val item = selectedItem ?: return
        val correct = when {
            targetFruit   != null && item is DragItem.Fruit  -> item.type == targetFruit
            targetHabitat != null && item is DragItem.Animal -> item.type.habitat == targetHabitat
            else -> false
        }
        if (correct) { pool.remove(item); zone.add(item); selectedItem = null }
        else         { showError = true; selectedItem = null }
    }

    // Boss shake animation
    val bossShake = rememberInfiniteTransition(label = "boss")
    val bossX by bossShake.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(120, easing = LinearEasing), RepeatMode.Reverse),
        label = "bossX"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ForestBackground()

        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                G2TimerBar(TIMER_SECONDS, timerLeft, Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                // Swap countdown
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF200010).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🦝 ${swapCountdown}s", fontSize = 11.sp,
                        fontFamily = OrbitronFontFamily, color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                G2TreeZone(TreeType.MANZANA, manzanaZone, selectedItem is DragItem.Fruit,
                    { placeSelected(targetFruit = FruitType.MANZANA, zone = manzanaZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 90.dp)
                G2TreeZone(TreeType.NARANJA, naranjaZone, selectedItem is DragItem.Fruit,
                    { placeSelected(targetFruit = FruitType.NARANJA, zone = naranjaZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 90.dp)
                G2TreeZone(TreeType.LIMON, limonZone, selectedItem is DragItem.Fruit,
                    { placeSelected(targetFruit = FruitType.LIMON, zone = limonZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 90.dp)

                Spacer(Modifier.width(4.dp))

                G2HabitatZone(HabitatType.COPA, copaZone, selectedItem is DragItem.Animal,
                    { placeSelected(targetHabitat = HabitatType.COPA, zone = copaZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.85f))
                G2HabitatZone(HabitatType.TRONCO, troncoZone, selectedItem is DragItem.Animal,
                    { placeSelected(targetHabitat = HabitatType.TRONCO, zone = troncoZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.85f))
                G2HabitatZone(HabitatType.MADRIGUERA, madrigZone, selectedItem is DragItem.Animal,
                    { placeSelected(targetHabitat = HabitatType.MADRIGUERA, zone = madrigZone); startTimer() },
                    Modifier.weight(1f).fillMaxHeight(0.85f))

                // Boss character
                if (bossVisible) {
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(0.85f)
                            .graphicsLayer { translationX = bossX },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🦝", fontSize = 40.sp)
                            Text(
                                "Mini-Jefe",
                                fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                                color = Color(0xFFFF5252), fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5252).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), CircleShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("¡${swapCountdown}s!", fontSize = 9.sp,
                                    fontFamily = Baloo2FontFamily, color = Color(0xFFFF5252))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            G2ItemPool(
                items = pool, selectedItem = selectedItem,
                onItemTap = { item ->
                    selectedItem = if (selectedItem?.id == item.id) null else item
                    startTimer()
                }
            )
        }

        G2ErrorFlash(visible = showError)

        // Swap alert banner
        if (showSwapAlert) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3A0010).copy(alpha = 0.95f))
                        .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "🦝 ¡El mapache movió dos elementos!",
                        fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.tom_y_atom,
                    characterName = "Tom y Atom",
                    message       = "¡Cuidado! El Mini-Jefe Mapache está aquí.\n\nCada ${SWAP_INTERVAL} segundos intercambia 2 elementos de sus contenedores al pool.\n\n¡Clasifica todo antes de que el caos se apodere del bosque!",
                    onDismiss     = { showTutorial = false }
                )
            }
        }

        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.tom_y_atom,
                    characterName = "Tom y Atom",
                    message       = "¡El mapache ganó esta vez! Pero con un buen algoritmo lo superarás. ¡Inténtalo de nuevo!",
                    onDismiss     = {
                        pool.clear(); pool.addAll(buildLevel5Pool())
                        allZones.forEach { it.clear() }
                        selectedItem = null; timerLeft = TIMER_SECONDS
                        swapCountdown = SWAP_INTERVAL; timerRunning = false
                        showTimeUp = false; bossVisible = false
                    }
                )
            }
        }

        if (showVictory) {
            G2VictoryOverlay(levelNumber = 5, onNext = onLevelComplete)
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            onClick  = onNavigateToMenu
        )
    }
}
