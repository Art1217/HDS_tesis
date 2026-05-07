package com.example.hds_tesisapp.ui.theme.games.game3.level3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.hds_tesisapp.ui.theme.games.game1.G1MenuButton
import com.example.hds_tesisapp.ui.theme.games.game3.FlowerItem
import com.example.hds_tesisapp.ui.theme.games.game3.buildFlowers
import kotlinx.coroutines.delay

private fun Context.findAct(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findAct()
    else -> null
}

private const val TIMER_SECONDS = 120
private const val MAX_LIVES = 3

@Composable
fun Level3G3Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findAct() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool          = remember { mutableStateListOf<FlowerItem>().also { it.addAll(buildFlowers()) } }
    val slots         = remember { mutableStateListOf<FlowerItem?>().also { repeat(5) { _ -> it.add(null) } } }
    var selected      by remember { mutableStateOf<FlowerItem?>(null) }
    var lives         by remember { mutableStateOf(MAX_LIVES) }
    var timerLeft     by remember { mutableStateOf(TIMER_SECONDS) }
    var timerRunning  by remember { mutableStateOf(false) }
    var showTutorial  by remember { mutableStateOf(true) }
    var showError     by remember { mutableStateOf(false) }
    var showVictory   by remember { mutableStateOf(false) }
    var showTimeUp    by remember { mutableStateOf(false) }
    var gardenShake   by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(gardenShake) {
        if (gardenShake) {
            repeat(5) {
                shakeAnim.animateTo(8f, tween(60))
                shakeAnim.animateTo(-8f, tween(60))
            }
            shakeAnim.animateTo(0f, tween(60))
            gardenShake = false
        }
    }
    LaunchedEffect(showError) {
        if (showError) { delay(350); showError = false }
    }
    LaunchedEffect(timerRunning) {
        if (!timerRunning) return@LaunchedEffect
        while (timerLeft > 0 && !showVictory) {
            delay(1000L)
            timerLeft--
        }
        if (timerLeft == 0 && !showVictory) showTimeUp = true
    }

    fun isAscending(): Boolean {
        val filled = slots.filterNotNull()
        if (filled.size != 5) return false
        return filled.zipWithNext().all { (a, b) -> a.type.height < b.type.height }
    }

    fun confirmOrder() {
        if (slots.any { it == null }) { showError = true; return }
        if (isAscending()) {
            showVictory = true
        } else {
            lives--
            gardenShake = true
            if (lives <= 0) {
                slots.forEachIndexed { i, f -> if (f != null) { pool.add(f); slots[i] = null } }
                lives = MAX_LIVES
            }
        }
    }

    fun tapSlot(index: Int) {
        if (!timerRunning) timerRunning = true
        val sel = selected
        if (sel != null) {
            val current = slots[index]
            slots[index] = sel
            pool.remove(sel)
            if (current != null) pool.add(current)
            selected = null
        } else {
            val current = slots[index]
            if (current != null) {
                selected = current
                slots[index] = null
                pool.add(current)
            }
        }
    }

    // Timer color
    val timerColor = when {
        timerLeft > 60 -> Color(0xFF69FF47)
        timerLeft > 30 -> Color(0xFFFFD600)
        else           -> Color(0xFFFF5252)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.garden_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("NIVEL 3  ·  EL JARDÍN DE LAS FLORES",
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFFF48FB1), letterSpacing = 1.sp)
                    Text("Ordena de más baja a más alta",
                        fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.75f))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A0A00).copy(0.8f))
                            .border(1.dp, timerColor.copy(0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("⏱ ${timerLeft}s", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                            color = timerColor, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(MAX_LIVES) { i ->
                            Text(if (i < lives) "❤️" else "🖤", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("⬅ Más baja", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.55f))
                Spacer(Modifier.weight(1f))
                Text("Más alta ➡", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.55f))
            }

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer { translationX = shakeAnim.value },
                contentAlignment = Alignment.Center
            ) {
                FlowerBed(slots = slots, selected = selected, onTap = { tapSlot(it) })
            }

            Spacer(Modifier.height(6.dp))

            Text(
                if (selected == null) "Toca una flor para seleccionarla"
                else "Flor seleccionada: ${selected!!.type.emoji} ${selected!!.type.label} → toca una posición",
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pool.forEach { flower ->
                    FlowerChip(
                        flower     = flower,
                        isSelected = selected?.id == flower.id,
                        onTap      = {
                            if (!timerRunning) timerRunning = true
                            selected = if (selected?.id == flower.id) null else flower
                        }
                    )
                }
                if (pool.isEmpty() && slots.none { it == null }) {
                    Text("✅ Todas colocadas", fontSize = 11.sp,
                        fontFamily = Baloo2FontFamily, color = Color(0xFF69FF47))
                }
            }

            Spacer(Modifier.height(10.dp))

            if (slots.none { it == null }) {
                FlowerConfirmButton(onClick = { showConfirmDialog = true })
            }
        }

        if (showError) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFF5252).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center) {
                Text("❌ Faltan flores", fontSize = 14.sp,
                    fontFamily = Baloo2FontFamily, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (showConfirmDialog) {
            FlowerConfirmDialog(
                onConfirm = { showConfirmDialog = false; confirmOrder() },
                onCancel  = { showConfirmDialog = false }
            )
        }

        if (showTimeUp) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TimeUpBubble(onDismiss = {
                    pool.clear(); pool.addAll(buildFlowers())
                    slots.forEachIndexed { i, _ -> slots[i] = null }
                    selected = null; timerLeft = TIMER_SECONDS
                    timerRunning = false; showTimeUp = false; lives = MAX_LIVES
                })
            }
        }

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FlowerTutorialBubble(onDismiss = { showTutorial = false })
            }
        }

        if (showVictory) {
            FlowerVictoryOverlay(onNext = onLevelComplete)
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).zIndex(5f),
            onClick  = onNavigateToMenu
        )
    }
}

@Composable
private fun FlowerBed(slots: List<FlowerItem?>, selected: FlowerItem?, onTap: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        slots.forEachIndexed { index, flower ->
            FlowerSlot(index = index, flower = flower, isTarget = selected != null, onTap = { onTap(index) })
        }
    }
}

@Composable
private fun FlowerSlot(index: Int, flower: FlowerItem?, isTarget: Boolean, onTap: () -> Unit) {
    val borderColor = when {
        flower != null -> Color(0xFFF48FB1)
        isTarget       -> Color(0xFFFFD600)
        else           -> Color(0xFF880E4F).copy(alpha = 0.5f)
    }
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(90.dp)
            .drawBehind {
                if (isTarget && flower == null) {
                    drawRoundRect(
                        color = Color(0xFFFFD600).copy(alpha = 0.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                    )
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (flower != null)
                    Brush.verticalGradient(listOf(Color(0xFF1A002A).copy(0.8f), Color(0xFF0D1A0A).copy(0.85f)))
                else
                    Brush.verticalGradient(listOf(Color(0xFF1A0022).copy(0.6f), Color(0xFF0D1300).copy(0.6f)))
            )
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (flower != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(flower.type.emoji, fontSize = 26.sp)
                // Stem height proportional
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height((flower.type.height * 7).dp)
                        .background(Color(0xFF4CAF50))
                )
                Text(flower.type.label, fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                    color = Color(0xFFF48FB1))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    "${index + 1}", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                    color = if (isTarget) Color(0xFFFFD600).copy(0.7f) else Color.White.copy(0.25f)
                )
                Text("Pos ${index + 1}", fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                    color = if (isTarget) Color(0xFFFFD600).copy(0.5f) else Color.White.copy(0.2f))
            }
        }
    }
}

@Composable
private fun FlowerChip(flower: FlowerItem, isSelected: Boolean, onTap: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.12f else 1f, spring(), label = "fs")
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 64.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Brush.verticalGradient(listOf(Color(0xFFF48FB1), Color(0xFFAD1457)))
                else Brush.verticalGradient(listOf(Color(0xFF4A0035), Color(0xFF2A0020)))
            )
            .border(2.dp, if (isSelected) Color.White else Color(0xFF880E4F), RoundedCornerShape(8.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(flower.type.emoji, fontSize = 22.sp)
            Text(flower.type.label, fontSize = 8.sp, fontFamily = Baloo2FontFamily,
                color = if (isSelected) Color.White else Color(0xFFF48FB1),
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FlowerConfirmButton(onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(700)); glow.animateTo(0.5f, tween(700)) }
    }
    Box(
        modifier = Modifier
            .width(200.dp).height(44.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFFF48FB1).copy(alpha = glow.value * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))))
            .border(2.dp, Color(0xFFF48FB1).copy(alpha = glow.value), RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("🌸  Confirmar orden", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun FlowerConfirmDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).zIndex(8f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0022), Color(0xFF2A0035))))
                .border(2.dp, Color(0xFFF48FB1).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🌸", fontSize = 36.sp)
            Text("¿Las flores están en orden?",
                fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFF48FB1), textAlign = TextAlign.Center)
            Text("Si están de más baja a más alta, el jardín florecerá.",
                fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A0022))
                        .border(1.5.dp, Color(0xFFFF5252).copy(0.7f), RoundedCornerShape(10.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onCancel),
                    contentAlignment = Alignment.Center
                ) { Text("Revisar", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFFF5252)) }
                Box(
                    modifier = Modifier.weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))))
                        .border(1.5.dp, Color(0xFFF48FB1).copy(0.8f), RoundedCornerShape(10.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) { Text("¡Sí!", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color.White) }
            }
        }
    }
}

@Composable
private fun FlowerTutorialBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0022), Color(0xFF2A0035))))
                .border(2.dp, Color(0xFFF48FB1), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🌸", fontSize = 40.sp)
            Text("EL JARDÍN DE LAS FLORES", fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFF48FB1), textAlign = TextAlign.Center)
            Text(
                "El Glitch desordenó las flores del jardín.\n\n" +
                "Ordénalas de más baja a más alta. ¡Tienes solo $TIMER_SECONDS segundos!\n\n" +
                "Toca una flor del pool → toca la posición donde colocarla.\n\n" +
                "Tienes ❤️❤️❤️ 3 intentos.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.9f), textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))))
                    .border(1.5.dp, Color(0xFFF48FB1), RoundedCornerShape(12.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Entendido!", fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun TimeUpBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.7f)).zIndex(15f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0005), Color(0xFF2A000A))))
                .border(2.dp, Color(0xFFFF5252).copy(0.8f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⏱️", fontSize = 40.sp)
            Text("¡TIEMPO!", fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
            Text("El jardín sigue esperando. ¡Inténtalo de nuevo!",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.85f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))))
                    .border(1.5.dp, Color(0xFFF48FB1), RoundedCornerShape(12.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Reintentar", fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun FlowerVictoryOverlay(onNext: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val glow  = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) { glow.animateTo(1f, tween(600)); glow.animateTo(0.6f, tween(600)) }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.72f)).zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0022), Color(0xFF2A0035))))
                .border(2.5.dp, Color(0xFFF48FB1).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("⭐  ⭐  ⭐", fontSize = 32.sp)
            Text("¡JARDÍN FLORECIDO!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFF48FB1), textAlign = TextAlign.Center)
            Text("¡Las flores están en orden!\nEl jardín es hermoso.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.85f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFF48FB1).copy(alpha = glow.value * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))))
                    .border(2.dp, Color(0xFFF48FB1).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onNext)
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Siguiente →", fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
