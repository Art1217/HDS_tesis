package com.example.hds_tesisapp.ui.theme.games.game2.level4

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

private fun buildLevel4Pool(): List<DragItem> {
    val fruits  = buildFruitPool(
        FruitType.MANZANA to 2, FruitType.NARANJA to 2, FruitType.LIMON to 2
    )
    val animals = buildAnimalPool(
        AnimalType.PAJARO  to 1, AnimalType.AGUILA  to 1,
        AnimalType.ARDILLA to 1, AnimalType.CARPINTERO to 1,
        AnimalType.ZORRO   to 1, AnimalType.CONEJO  to 1,
    )
    return (fruits + animals).shuffled()
}

private const val TIMER_SECONDS = 180

@Composable
fun Level4G2Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool           = remember { mutableStateListOf<DragItem>().also { it.addAll(buildLevel4Pool()) } }
    // Tree zones (fruits)
    val manzanaZone    = remember { mutableStateListOf<DragItem>() }
    val naranjaZone    = remember { mutableStateListOf<DragItem>() }
    val limonZone      = remember { mutableStateListOf<DragItem>() }
    // Habitat zones (animals)
    val copaZone       = remember { mutableStateListOf<DragItem>() }
    val troncoZone     = remember { mutableStateListOf<DragItem>() }
    val madrigZone     = remember { mutableStateListOf<DragItem>() }

    var selectedItem   by remember { mutableStateOf<DragItem?>(null) }
    var showError      by remember { mutableStateOf(false) }
    var showTutorial   by remember { mutableStateOf(true) }
    var timerLeft      by remember { mutableStateOf(TIMER_SECONDS) }
    var timerRunning   by remember { mutableStateOf(false) }
    var showVictory    by remember { mutableStateOf(false) }
    var showTimeUp     by remember { mutableStateOf(false) }

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
        if (correct) {
            pool.remove(item); zone.add(item); selectedItem = null
        } else {
            showError = true; selectedItem = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ForestBackground()

        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            G2TimerBar(TIMER_SECONDS, timerLeft, Modifier.fillMaxWidth().padding(bottom = 8.dp))

            // Top row: Trees (fruits)
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                G2TreeZone(
                    tree = TreeType.MANZANA, placedItems = manzanaZone,
                    isHighlighted = selectedItem is DragItem.Fruit,
                    onZoneTap = { placeSelected(targetFruit = FruitType.MANZANA, zone = manzanaZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 100.dp
                )
                G2TreeZone(
                    tree = TreeType.NARANJA, placedItems = naranjaZone,
                    isHighlighted = selectedItem is DragItem.Fruit,
                    onZoneTap = { placeSelected(targetFruit = FruitType.NARANJA, zone = naranjaZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 100.dp
                )
                G2TreeZone(
                    tree = TreeType.LIMON, placedItems = limonZone,
                    isHighlighted = selectedItem is DragItem.Fruit,
                    onZoneTap = { placeSelected(targetFruit = FruitType.LIMON, zone = limonZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.9f), treeHeight = 100.dp
                )

                Spacer(Modifier.width(4.dp))

                // Habitat zones (animals)
                G2HabitatZone(
                    habitat = HabitatType.COPA, placedItems = copaZone,
                    isHighlighted = selectedItem is DragItem.Animal,
                    onZoneTap = { placeSelected(targetHabitat = HabitatType.COPA, zone = copaZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
                G2HabitatZone(
                    habitat = HabitatType.TRONCO, placedItems = troncoZone,
                    isHighlighted = selectedItem is DragItem.Animal,
                    onZoneTap = { placeSelected(targetHabitat = HabitatType.TRONCO, zone = troncoZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
                G2HabitatZone(
                    habitat = HabitatType.MADRIGUERA, placedItems = madrigZone,
                    isHighlighted = selectedItem is DragItem.Animal,
                    onZoneTap = { placeSelected(targetHabitat = HabitatType.MADRIGUERA, zone = madrigZone); startTimer() },
                    modifier = Modifier.weight(1f).fillMaxHeight(0.85f)
                )
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

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.max,
                    characterName = "Max",
                    message       = "¡Ahora el bosque tiene frutas Y animales mezclados!\n\nLas frutas van a sus árboles 🍎🍊🍋\nLos animales van a sus hábitats 🌿🪵🕳️\n\n¡Presta atención a qué tipo de elemento tienes seleccionado!",
                    onDismiss     = { showTutorial = false }
                )
            }
        }

        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.max,
                    characterName = "Max",
                    message       = "¡Se acabó el tiempo! Vamos, ¡tú puedes lograrlo!",
                    onDismiss     = {
                        pool.clear(); pool.addAll(buildLevel4Pool())
                        manzanaZone.clear(); naranjaZone.clear(); limonZone.clear()
                        copaZone.clear(); troncoZone.clear(); madrigZone.clear()
                        selectedItem = null; timerLeft = TIMER_SECONDS
                        timerRunning = false; showTimeUp = false
                    }
                )
            }
        }

        if (showVictory) {
            G2VictoryOverlay(levelNumber = 4, onNext = onLevelComplete)
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            onClick  = onNavigateToMenu
        )
    }
}
