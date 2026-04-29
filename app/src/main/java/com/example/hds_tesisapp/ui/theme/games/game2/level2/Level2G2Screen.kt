package com.example.hds_tesisapp.ui.theme.games.game2.level2

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
import com.example.hds_tesisapp.ui.theme.games.game2.*
import kotlinx.coroutines.delay

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// 9 animals: 3 per habitat
private val INITIAL_POOL: List<DragItem> = buildAnimalPool(
    AnimalType.PAJARO     to 2,
    AnimalType.AGUILA     to 1,
    AnimalType.ARDILLA    to 2,
    AnimalType.CARPINTERO to 1,
    AnimalType.ZORRO      to 1,
    AnimalType.CONEJO     to 2,
)

private const val TIMER_SECONDS = 120

@Composable
fun Level2G2Screen(onLevelComplete: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool         = remember { mutableStateListOf<DragItem>().also { it.addAll(INITIAL_POOL) } }
    val copaZone     = remember { mutableStateListOf<DragItem>() }
    val troncoZone   = remember { mutableStateListOf<DragItem>() }
    val madrigZone   = remember { mutableStateListOf<DragItem>() }
    var selectedItem by remember { mutableStateOf<DragItem?>(null) }
    var showError    by remember { mutableStateOf(false) }
    var showTutorial by remember { mutableStateOf(true) }
    var timerLeft    by remember { mutableStateOf(TIMER_SECONDS) }
    var timerRunning by remember { mutableStateOf(false) }
    var showVictory  by remember { mutableStateOf(false) }
    var showTimeUp   by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (!timerRunning) return@LaunchedEffect
        while (timerLeft > 0 && !showVictory) {
            delay(1000L)
            timerLeft--
        }
        if (timerLeft == 0 && !showVictory) showTimeUp = true
    }

    LaunchedEffect(showError) {
        if (showError) { delay(400L); showError = false }
    }

    LaunchedEffect(pool.size) {
        if (pool.isEmpty() && !showVictory) showVictory = true
    }

    fun placeSelected(targetHabitat: HabitatType, zone: MutableList<DragItem>) {
        val item = selectedItem ?: return
        if (item is DragItem.Animal && item.type.habitat == targetHabitat) {
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
            G2TimerBar(
                secondsTotal = TIMER_SECONDS,
                secondsLeft  = timerLeft,
                modifier     = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                G2HabitatZone(
                    habitat       = HabitatType.COPA,
                    placedItems   = copaZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(HabitatType.COPA, copaZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
                G2HabitatZone(
                    habitat       = HabitatType.TRONCO,
                    placedItems   = troncoZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(HabitatType.TRONCO, troncoZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
                G2HabitatZone(
                    habitat       = HabitatType.MADRIGUERA,
                    placedItems   = madrigZone,
                    isHighlighted = selectedItem != null,
                    onZoneTap     = { placeSelected(HabitatType.MADRIGUERA, madrigZone) },
                    modifier      = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
            }

            Spacer(Modifier.height(8.dp))

            G2ItemPool(
                items        = pool,
                selectedItem = selectedItem,
                onItemTap    = { item ->
                    selectedItem = if (selectedItem?.id == item.id) null else item
                    if (!timerRunning) timerRunning = true
                }
            )
        }

        G2ErrorFlash(visible = showError)

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.tom_y_atom,
                    characterName = "Tom y Atom",
                    message       = "Cada animal vive en un lugar especial del árbol.\n🌿 Copa: animales que vuelan\n🪵 Tronco: animales que trepan\n🕳️ Madriguera: animales que cavan\n\n¡Coloca cada uno en su hábitat!",
                    onDismiss     = { showTutorial = false }
                )
            }
        }

        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.tom_y_atom,
                    characterName = "Tom y Atom",
                    message       = "¡El tiempo se acabó! Los animales necesitan tu ayuda. ¡Inténtalo de nuevo!",
                    onDismiss     = {
                        pool.clear(); pool.addAll(INITIAL_POOL)
                        copaZone.clear(); troncoZone.clear(); madrigZone.clear()
                        selectedItem = null; timerLeft = TIMER_SECONDS
                        timerRunning = false; showTimeUp = false
                    }
                )
            }
        }

        if (showVictory) {
            G2VictoryOverlay(levelNumber = 2, onNext = onLevelComplete)
        }
    }
}
