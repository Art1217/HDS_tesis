package com.example.hds_tesisapp.ui.theme.games.game2.level3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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

// 3 trees accept 2 fruits each; pool has 2 of each of 3 types + 2 "wrong" ones
private val ALL_FRUITS = listOf(FruitType.MANZANA, FruitType.NARANJA, FruitType.LIMON)

private fun buildLevel3Pool(): List<DragItem> = buildFruitPool(
    FruitType.MANZANA to 3,
    FruitType.NARANJA  to 3,
    FruitType.LIMON    to 3,
    FruitType.DURAZNO  to 2,
    FruitType.MANGO    to 2,
)

private const val TIMER_SECONDS = 150
private const val ROTATE_SECONDS = 20

@Composable
fun Level3G2Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Which tree accepts which fruit — rotates every ROTATE_SECONDS
    // Initial assignment: tree[i] accepts ALL_FRUITS[i]
    var treeAssignment by remember { mutableStateOf(List(3) { ALL_FRUITS[it] }) }
    var rotateCountdown by remember { mutableStateOf(ROTATE_SECONDS) }

    val pool           = remember { mutableStateListOf<DragItem>().also { it.addAll(buildLevel3Pool()) } }
    // For "otros" — wrongly placed items go to reject
    val zones          = remember { List(3) { mutableStateListOf<DragItem>() } }
    val rejectZone     = remember { mutableStateListOf<DragItem>() }
    var selectedItem   by remember { mutableStateOf<DragItem?>(null) }
    var showError      by remember { mutableStateOf(false) }
    var showTutorial   by remember { mutableStateOf(true) }
    var timerLeft      by remember { mutableStateOf(TIMER_SECONDS) }
    var timerRunning   by remember { mutableStateOf(false) }
    var showVictory    by remember { mutableStateOf(false) }
    var showTimeUp     by remember { mutableStateOf(false) }
    var showRotateMsg  by remember { mutableStateOf(false) }

    // Main timer + rotate ticker
    LaunchedEffect(timerRunning) {
        if (!timerRunning) return@LaunchedEffect
        while (timerLeft > 0 && !showVictory) {
            delay(1000L)
            timerLeft--
            rotateCountdown--
            if (rotateCountdown <= 0) {
                // Shuffle tree assignment
                treeAssignment = treeAssignment.shuffled()
                rotateCountdown = ROTATE_SECONDS
                showRotateMsg = true
            }
        }
        if (timerLeft == 0 && !showVictory) showTimeUp = true
    }

    LaunchedEffect(showError) {
        if (showError) { delay(400L); showError = false }
    }

    LaunchedEffect(showRotateMsg) {
        if (showRotateMsg) { delay(1800L); showRotateMsg = false }
    }

    // Win: pool empty (all correct fruits placed) — reject zone items don't count toward win
    // Win when all 9 "real" fruits (manzana+naranja+limon, 3 each) are in correct zones
    val correctlyPlaced = zones.flatMap { it.toList() }.count { it is DragItem.Fruit && it.type in ALL_FRUITS }
    LaunchedEffect(correctlyPlaced) {
        if (correctlyPlaced >= 9 && !showVictory) showVictory = true
    }

    fun placeSelected(zoneIndex: Int) {
        val item = selectedItem ?: return
        if (item !is DragItem.Fruit) { showError = true; selectedItem = null; return }
        val acceptedFruit = treeAssignment[zoneIndex]
        if (item.type == acceptedFruit) {
            pool.remove(item)
            zones[zoneIndex].add(item)
            selectedItem = null
        } else {
            // Wrong tree — goes to reject / pool stays, just error
            showError = true
            selectedItem = null
        }
    }

    fun placeInReject() {
        val item = selectedItem ?: return
        if (item is DragItem.Fruit && item.type !in ALL_FRUITS) {
            pool.remove(item)
            rejectZone.add(item)
            selectedItem = null
        } else {
            showError = true
            selectedItem = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ForestBackground()

        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                G2TimerBar(
                    secondsTotal = TIMER_SECONDS,
                    secondsLeft  = timerLeft,
                    modifier     = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                // Rotate countdown chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A0030).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFFAB47BC).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "🔄 ${rotateCountdown}s",
                        fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFFAB47BC), fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                // 3 trees with dynamic fruit assignment
                treeAssignment.forEachIndexed { i, acceptedFruit ->
                    val treeType = TreeType.entries.first { it.fruit == acceptedFruit }
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(0.9f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // "Acepta: X" badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF002200).copy(alpha = 0.85f))
                                .border(1.dp, Color(0xFF69FF47).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Acepta: ${acceptedFruit.emoji}",
                                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                                color = Color(0xFF69FF47), fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        G2TreeZone(
                            tree          = treeType,
                            placedItems   = zones[i],
                            isHighlighted = selectedItem != null,
                            onZoneTap     = { placeSelected(i); if (!timerRunning) timerRunning = true },
                            modifier      = Modifier.weight(1f).fillMaxWidth()
                        )
                    }
                }

                // "Otros" reject box
                Column(
                    modifier = Modifier.weight(0.7f).fillMaxHeight(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF200000).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("No válidos", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                            color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A0000).copy(alpha = 0.75f))
                            .border(
                                width = if (selectedItem != null) 2.dp else 1.dp,
                                color = if (selectedItem != null) Color(0xFFFF5252).copy(alpha = 0.7f)
                                        else Color(0xFFFF5252).copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                placeInReject()
                                if (!timerRunning) timerRunning = true
                            }
                            .padding(6.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (rejectZone.isEmpty()) {
                            Text("🗑️", fontSize = 24.sp)
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                rejectZone.chunked(2).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.Center) {
                                        row.forEach { item ->
                                            Text(item.emoji, fontSize = 14.sp,
                                                modifier = Modifier.padding(horizontal = 2.dp),
                                                textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

        // Rotate announcement
        if (showRotateMsg) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A0050).copy(alpha = 0.92f))
                        .border(2.dp, Color(0xFFAB47BC), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        "🔄 ¡Los árboles cambiaron de fruta!",
                        fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.lina,
                    characterName = "Lina",
                    message       = "¡El bosque se mezcló!\n\nCada árbol muestra qué fruta acepta ahora. ¡Pero ojo! Cada ${ROTATE_SECONDS}s los árboles cambian de fruta.\n\nLas frutas raras (durazno, mango) van a la caja de 'No válidos'.",
                    onDismiss     = { showTutorial = false }
                )
            }
        }

        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                G2CharacterBubble(
                    characterRes  = R.drawable.lina,
                    characterName = "Lina",
                    message       = "¡Tiempo! El bosque sigue esperando tu ayuda. ¡A intentarlo de nuevo!",
                    onDismiss     = {
                        pool.clear(); pool.addAll(buildLevel3Pool())
                        zones.forEach { it.clear() }; rejectZone.clear()
                        treeAssignment = List(3) { ALL_FRUITS[it] }
                        selectedItem = null; timerLeft = TIMER_SECONDS
                        rotateCountdown = ROTATE_SECONDS
                        timerRunning = false; showTimeUp = false
                    }
                )
            }
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            onClick  = onNavigateToMenu
        )

        if (showVictory) {
            G2VictoryOverlay(levelNumber = 3, onNext = onLevelComplete)
        }
    }
}
