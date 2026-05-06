package com.example.hds_tesisapp.ui.theme.games.game2.level1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.games.game1.G1MenuButton
import com.example.hds_tesisapp.ui.theme.games.game2.*
import kotlinx.coroutines.delay

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// 9 items: 3 manzanas, 3 naranjas, 3 limones
private val INITIAL_POOL: List<DragItem> = buildFruitPool(
    FruitType.MANZANA to 3,
    FruitType.NARANJA  to 3,
    FruitType.LIMON    to 3,
)

private const val TIMER_SECONDS = 90

@Composable
fun Level1G2Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // State
    val pool          = remember { mutableStateListOf<DragItem>().also { it.addAll(INITIAL_POOL) } }
    val manzanaZone   = remember { mutableStateListOf<DragItem>() }
    val naranjaZone   = remember { mutableStateListOf<DragItem>() }
    val limonZone     = remember { mutableStateListOf<DragItem>() }
    var selectedItem  by remember { mutableStateOf<DragItem?>(null) }
    var showError     by remember { mutableStateOf(false) }
    var showTutorial  by remember { mutableStateOf(true) }
    var timerLeft     by remember { mutableStateOf(TIMER_SECONDS) }
    var timerRunning  by remember { mutableStateOf(false) }
    var showVictory   by remember { mutableStateOf(false) }
    var showTimeUp    by remember { mutableStateOf(false) }

    // Timer
    LaunchedEffect(timerRunning) {
        if (!timerRunning) return@LaunchedEffect
        while (timerLeft > 0 && !showVictory) {
            delay(1000L)
            timerLeft--
        }
        if (timerLeft == 0 && !showVictory) showTimeUp = true
    }

    // Error flash auto-clear
    LaunchedEffect(showError) {
        if (showError) { delay(400L); showError = false }
    }

    // Win check
    LaunchedEffect(pool.size) {
        if (pool.isEmpty() && !showVictory) showVictory = true
    }

    fun placeSelected(correctFruitType: FruitType, zone: MutableList<DragItem>) {
        val item = selectedItem ?: return
        if (item is DragItem.Fruit && item.type == correctFruitType) {
            pool.remove(item)
            zone.add(item)
            selectedItem = null
        } else {
            showError = true
            selectedItem = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ForestBackground()

        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            // Timer
            G2TimerBar(
                secondsTotal = TIMER_SECONDS,
                secondsLeft  = timerLeft,
                modifier     = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Trees row
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                G2TreeZone(
                    tree          = TreeType.MANZANA,
                    placedItems   = manzanaZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(FruitType.MANZANA, manzanaZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.9f)
                )
                G2TreeZone(
                    tree          = TreeType.NARANJA,
                    placedItems   = naranjaZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(FruitType.NARANJA, naranjaZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.9f)
                )
                G2TreeZone(
                    tree          = TreeType.LIMON,
                    placedItems   = limonZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(FruitType.LIMON, limonZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.9f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Item pool
            G2ItemPool(
                items        = pool,
                selectedItem = selectedItem,
                onItemTap    = { item ->
                    selectedItem = if (selectedItem?.id == item.id) null else item
                    if (!timerRunning) timerRunning = true
                }
            )
        }

        // Error flash
        G2ErrorFlash(visible = showError)

        // Tutorial
        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.lina,
                    characterName = "Lina",
                    message       = "¡Bienvenido al Bosque de los Grupos!\n\nToca una fruta de abajo para seleccionarla, luego toca el árbol al que pertenece.\n\n¡Clasifica todas las frutas antes de que se acabe el tiempo!",
                    onDismiss     = { showTutorial = false }
                )
            }
        }

        // Time up dialog
        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.lina,
                    characterName = "Lina",
                    message       = "¡Se acabó el tiempo! No te preocupes, ¡inténtalo de nuevo!",
                    onDismiss     = {
                        pool.clear(); pool.addAll(INITIAL_POOL)
                        manzanaZone.clear(); naranjaZone.clear(); limonZone.clear()
                        selectedItem = null; timerLeft = TIMER_SECONDS
                        timerRunning = false; showTimeUp = false
                    }
                )
            }
        }

        // Victory
        if (showVictory) {
            G2VictoryOverlay(levelNumber = 1, onNext = onLevelComplete)
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            onClick  = onNavigateToMenu
        )
    }
}
