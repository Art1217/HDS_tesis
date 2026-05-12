package com.example.hds_tesisapp.ui.theme.games.game3.level2

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
import com.example.hds_tesisapp.ui.theme.games.game3.RockItem
import com.example.hds_tesisapp.ui.theme.games.game3.buildRocks
import kotlinx.coroutines.delay

private fun Context.findAct(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findAct()
    else -> null
}

private const val MAX_LIVES = 3

@Composable
fun Level2G3Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findAct() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool          = remember { mutableStateListOf<RockItem>().also { it.addAll(buildRocks()) } }
    val slots         = remember { mutableStateListOf<RockItem?>().also { repeat(5) { _ -> it.add(null) } } }
    var selected      by remember { mutableStateOf<RockItem?>(null) }
    var lives         by remember { mutableStateOf(MAX_LIVES) }
    var showTutorial  by remember { mutableStateOf(true) }
    var showError     by remember { mutableStateOf(false) }
    var showVictory   by remember { mutableStateOf(false) }
    var pathShake     by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(pathShake) {
        if (pathShake) {
            repeat(5) {
                shakeAnim.animateTo(8f, tween(60))
                shakeAnim.animateTo(-8f, tween(60))
            }
            shakeAnim.animateTo(0f, tween(60))
            pathShake = false
        }
    }
    LaunchedEffect(showError) {
        if (showError) { delay(350); showError = false }
    }

    fun isAscending(): Boolean {
        val filled = slots.filterNotNull()
        if (filled.size != 5) return false
        return filled.zipWithNext().all { (a, b) -> a.size.ordinal < b.size.ordinal }
    }

    fun confirmOrder() {
        if (slots.any { it == null }) { showError = true; return }
        if (isAscending()) {
            showVictory = true
        } else {
            lives--
            pathShake = true
            if (lives <= 0) {
                slots.forEachIndexed { i, r -> if (r != null) { pool.add(r); slots[i] = null } }
                lives = MAX_LIVES
            }
        }
    }

    fun tapSlot(index: Int) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.rocks_path_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)))

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
                G1MenuButton(onClick = onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text(
                        "NIVEL 2  ·  LAS PIEDRAS DE ESCALADA",
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFFB0BEC5), letterSpacing = 1.sp
                    )
                    Text(
                        "Ordena de más pequeña (XS) a más grande (XL)",
                        fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(MAX_LIVES) { i ->
                        Text(if (i < lives) "❤️" else "🖤", fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⬅ Más pequeña", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.55f))
                Spacer(Modifier.weight(1f))
                Text("Más grande ➡", fontSize = 9.sp, fontFamily = Baloo2FontFamily,
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
                RockPath(slots = slots, selected = selected, onTap = { tapSlot(it) })
            }

            Spacer(Modifier.height(6.dp))

            Text(
                if (selected == null) "Toca una piedra para seleccionarla"
                else "Piedra ${selected!!.size.label} seleccionada → toca una posición",
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
                pool.forEach { rock ->
                    RockChip(
                        rock       = rock,
                        isSelected = selected?.id == rock.id,
                        onTap      = { selected = if (selected?.id == rock.id) null else rock }
                    )
                }
                if (pool.isEmpty() && slots.none { it == null }) {
                    Text("✅ Todas colocadas", fontSize = 11.sp,
                        fontFamily = Baloo2FontFamily, color = Color(0xFF69FF47))
                }
            }

            Spacer(Modifier.height(10.dp))

            if (slots.none { it == null }) {
                RockConfirmButton(onClick = { showConfirmDialog = true })
            }
        }

        if (showError) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFF5252).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center) {
                Text("❌ Faltan piedras", fontSize = 14.sp,
                    fontFamily = Baloo2FontFamily, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (showConfirmDialog) {
            RockConfirmDialog(
                onConfirm = { showConfirmDialog = false; confirmOrder() },
                onCancel  = { showConfirmDialog = false }
            )
        }

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                RockTutorialBubble(onDismiss = { showTutorial = false })
            }
        }

        if (showVictory) {
            RockVictoryOverlay(onNext = onLevelComplete)
        }

    }
}

@Composable
private fun RockPath(slots: List<RockItem?>, selected: RockItem?, onTap: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        slots.forEachIndexed { index, rock ->
            RockSlot(index = index, rock = rock, isTarget = selected != null, onTap = { onTap(index) })
        }
    }
}

@Composable
private fun RockSlot(index: Int, rock: RockItem?, isTarget: Boolean, onTap: () -> Unit) {
    val borderColor = when {
        rock != null -> Color(0xFF90A4AE)
        isTarget     -> Color(0xFFFFD600)
        else         -> Color(0xFF546E7A).copy(alpha = 0.5f)
    }
    Box(
        modifier = Modifier
            .size(80.dp)
            .drawBehind {
                if (isTarget && rock == null) {
                    drawRoundRect(
                        color = Color(0xFFFFD600).copy(alpha = 0.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                    )
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (rock != null)
                    Brush.verticalGradient(listOf(Color(0xFF546E7A), Color(0xFF37474F)))
                else
                    Brush.verticalGradient(listOf(Color(0xFF1A2A33).copy(0.7f), Color(0xFF0D1A20).copy(0.7f)))
            )
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        if (rock != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.rocks),
                    contentDescription = null,
                    modifier = Modifier.size((rock.size.scale * 52).dp),
                    contentScale = ContentScale.Fit
                )
                Text(rock.size.label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFB0BEC5))
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${index + 1}", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                    color = if (isTarget) Color(0xFFFFD600).copy(0.7f) else Color.White.copy(0.25f))
                Text("Pos ${index + 1}", fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                    color = if (isTarget) Color(0xFFFFD600).copy(0.5f) else Color.White.copy(0.2f))
            }
        }
    }
}

@Composable
private fun RockChip(rock: RockItem, isSelected: Boolean, onTap: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.12f else 1f, spring(), label = "rs")
    Box(
        modifier = Modifier
            .size(68.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Brush.verticalGradient(listOf(Color(0xFF78909C), Color(0xFF546E7A)))
                else Brush.verticalGradient(listOf(Color(0xFF37474F), Color(0xFF263238)))
            )
            .border(2.dp, if (isSelected) Color(0xFFFFD600) else Color(0xFF546E7A), RoundedCornerShape(8.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.rocks),
                contentDescription = null,
                modifier = Modifier.size((rock.size.scale * 40).dp),
                contentScale = ContentScale.Fit
            )
            Text(rock.size.label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = if (isSelected) Color(0xFFFFD600) else Color(0xFFB0BEC5))
        }
    }
}

@Composable
private fun RockConfirmButton(onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(700)); glow.animateTo(0.5f, tween(700)) }
    }
    Box(
        modifier = Modifier
            .width(200.dp).height(44.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFF69FF47).copy(alpha = glow.value * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
            .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("✅  Confirmar orden", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun RockConfirmDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).zIndex(8f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF0D1A20), Color(0xFF1A2A33))))
                .border(2.dp, Color(0xFF90A4AE).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🪨", fontSize = 36.sp)
            Text("¿Las piedras están en orden?",
                fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFB0BEC5), textAlign = TextAlign.Center)
            Text("Si están de XS a XL podrás escalar hasta la cima.",
                fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A2A33))
                        .border(1.5.dp, Color(0xFFFF5252).copy(0.7f), RoundedCornerShape(10.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onCancel),
                    contentAlignment = Alignment.Center
                ) { Text("Revisar", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFFF5252)) }
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(1.5.dp, Color(0xFF69FF47).copy(0.8f), RoundedCornerShape(10.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) { Text("¡Sí!", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color.White) }
            }
        }
    }
}

@Composable
private fun RockTutorialBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF0D1A20), Color(0xFF1A2A33))))
                .border(2.dp, Color(0xFF90A4AE), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🪨", fontSize = 40.sp)
            Text("LAS PIEDRAS DE ESCALADA", fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFB0BEC5), textAlign = TextAlign.Center)
            Text(
                "El Glitch mezcló las piedras del camino.\n\n" +
                "Ordénalas de más pequeña (XS) a más grande (XL) para poder escalar hasta la cima.\n\n" +
                "Toca una piedra del pool → toca la posición donde colocarla.\n\n" +
                "Tienes ❤️❤️❤️ 3 intentos.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.9f), textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                    .border(1.5.dp, Color(0xFF69FF47), RoundedCornerShape(12.dp))
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
private fun RockVictoryOverlay(onNext: () -> Unit) {
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
                .background(Brush.verticalGradient(listOf(Color(0xFF0D1A20), Color(0xFF1A2A33))))
                .border(2.5.dp, Color(0xFF90A4AE).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("⭐  ⭐  ⭐", fontSize = 32.sp)
            Text("¡CIMA ALCANZADA!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFB0BEC5), textAlign = TextAlign.Center)
            Text("¡Las piedras están en orden!\nPuedes escalar hasta la cima.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.85f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFF90A4AE).copy(alpha = glow.value * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF37474F), Color(0xFF546E7A))))
                    .border(2.dp, Color(0xFF90A4AE).copy(alpha = glow.value), RoundedCornerShape(14.dp))
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
