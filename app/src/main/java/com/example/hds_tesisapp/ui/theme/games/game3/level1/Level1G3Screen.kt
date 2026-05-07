package com.example.hds_tesisapp.ui.theme.games.game3.level1

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
import androidx.compose.ui.draw.rotate
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
import com.example.hds_tesisapp.ui.theme.games.game3.PlankData
import com.example.hds_tesisapp.ui.theme.games.game3.buildPlanks
import kotlinx.coroutines.delay

private fun Context.findAct(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findAct()
    else -> null
}

private val PLANK_VALUES = listOf(1, 2, 3, 4, 5, 6)
private const val MAX_LIVES = 3

@Composable
fun Level1G3Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findAct() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val pool          = remember { mutableStateListOf<PlankData>().also { it.addAll(buildPlanks(PLANK_VALUES)) } }
    val slots         = remember { mutableStateListOf<PlankData?>().also { repeat(6) { _ -> it.add(null) } } }
    var selected      by remember { mutableStateOf<PlankData?>(null) }
    var lives         by remember { mutableStateOf(MAX_LIVES) }
    var showTutorial  by remember { mutableStateOf(true) }
    var showError     by remember { mutableStateOf(false) }
    var showVictory   by remember { mutableStateOf(false) }
    var bridgeShake   by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(bridgeShake) {
        if (bridgeShake) {
            repeat(5) {
                shakeAnim.animateTo(8f, tween(60))
                shakeAnim.animateTo(-8f, tween(60))
            }
            shakeAnim.animateTo(0f, tween(60))
            bridgeShake = false
        }
    }
    LaunchedEffect(showError) {
        if (showError) { delay(350); showError = false }
    }

    fun isAscending(): Boolean {
        val filled = slots.filterNotNull()
        if (filled.size != PLANK_VALUES.size) return false
        return filled.zipWithNext().all { (a, b) -> a.value < b.value }
    }

    fun confirmOrder() {
        if (slots.any { it == null }) { showError = true; return }
        if (isAscending()) {
            showVictory = true
        } else {
            lives--
            bridgeShake = true
            if (lives <= 0) {
                // Reset slots back to pool
                slots.forEachIndexed { i, p -> if (p != null) { pool.add(p); slots[i] = null } }
                lives = MAX_LIVES
            }
        }
    }

    fun tapSlot(index: Int) {
        val sel = selected
        if (sel != null) {
            // Place selected plank in this slot (swap if occupied)
            val current = slots[index]
            slots[index] = sel
            pool.remove(sel)
            if (current != null) pool.add(current)
            selected = null
        } else {
            // Pick up plank from slot
            val current = slots[index]
            if (current != null) {
                selected = current
                slots[index] = null
                pool.add(current)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(R.drawable.bridge_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dimmer
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "NIVEL 1  ·  EL PUENTE ROTO",
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFFFFD600), letterSpacing = 1.sp
                    )
                    Text(
                        "Ordena los tablones de menor a mayor",
                        fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                // Lives
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(MAX_LIVES) { i ->
                        Text(if (i < lives) "❤️" else "🖤", fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bridge with slots — shakes on error
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer { translationX = shakeAnim.value },
                contentAlignment = Alignment.Center
            ) {
                BridgeSlots(
                    slots    = slots,
                    selected = selected,
                    onTap    = { tapSlot(it) }
                )
            }

            Spacer(Modifier.height(10.dp))

            // Pool of planks
            Text(
                if (selected == null) "Toca un tablón para seleccionarlo"
                else "Tablón ${selected!!.value} seleccionado → toca una ranura del puente",
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pool.forEach { plank ->
                    PlankChip(
                        plank      = plank,
                        isSelected = selected?.id == plank.id,
                        onTap      = {
                            selected = if (selected?.id == plank.id) null else plank
                        }
                    )
                }
                if (pool.isEmpty() && slots.none { it == null }) {
                    Text("✅ Todos colocados", fontSize = 11.sp,
                        fontFamily = Baloo2FontFamily, color = Color(0xFF69FF47))
                }
            }

            Spacer(Modifier.height(10.dp))

            // Confirm button
            if (slots.none { it == null }) {
                ConfirmButton(onClick = { showConfirmDialog = true })
            }
        }

        // Error flash
        if (showError) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFFF5252).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("❌ Faltan tablones", fontSize = 14.sp,
                    fontFamily = Baloo2FontFamily, fontWeight = FontWeight.Bold,
                    color = Color.White)
            }
        }

        // Confirm dialog
        if (showConfirmDialog) {
            ConfirmOrderDialog(
                onConfirm = { showConfirmDialog = false; confirmOrder() },
                onCancel  = { showConfirmDialog = false }
            )
        }

        // Tutorial
        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TutorialBubble(onDismiss = { showTutorial = false })
            }
        }

        // Victory
        if (showVictory) {
            BridgeVictoryOverlay(onNext = onLevelComplete)
        }

        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).zIndex(5f),
            onClick  = onNavigateToMenu
        )
    }
}

// ─── Bridge Slots ─────────────────────────────────────────────────────────────

@Composable
private fun BridgeSlots(
    slots: List<PlankData?>,
    selected: PlankData?,
    onTap: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Bridge supports (top rail)
        BridgeRail()
        // Plank slots in a row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            slots.forEachIndexed { index, plank ->
                BridgeSlot(
                    index    = index,
                    plank    = plank,
                    isTarget = selected != null,
                    onTap    = { onTap(index) }
                )
            }
        }
        // Bridge supports (bottom rail)
        BridgeRail()
    }
}

@Composable
private fun BridgeRail() {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF5D3A1A), Color(0xFF8B5E3C), Color(0xFF5D3A1A))
                )
            )
            .border(1.dp, Color(0xFF3E1F00), RoundedCornerShape(4.dp))
    )
}

@Composable
private fun BridgeSlot(
    index: Int,
    plank: PlankData?,
    isTarget: Boolean,
    onTap: () -> Unit
) {
    val borderColor = when {
        plank != null -> Color(0xFF8B5E3C)
        isTarget      -> Color(0xFFFFD600)
        else          -> Color(0xFF5D3A1A).copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 52.dp)
            .drawBehind {
                if (isTarget && plank == null) {
                    drawRoundRect(
                        color  = Color(0xFFFFD600).copy(alpha = 0.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style  = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                    )
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (plank != null)
                    Brush.verticalGradient(listOf(Color(0xFF8B5E3C), Color(0xFF5D3A1A)))
                else
                    Brush.verticalGradient(listOf(Color(0xFF2A1500).copy(alpha = 0.7f), Color(0xFF1A0A00).copy(alpha = 0.7f)))
            )
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        if (plank != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${plank.value}",
                    fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = Color(0xFFFFD600)
                )
                Text(
                    "Paso ${index + 1}", fontSize = 7.sp,
                    fontFamily = Baloo2FontFamily, color = Color(0xFFFFD600).copy(alpha = 0.65f)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "···", fontSize = 14.sp,
                    color = if (isTarget) Color(0xFFFFD600).copy(alpha = 0.7f)
                            else Color.White.copy(alpha = 0.25f)
                )
                Text(
                    "Paso ${index + 1}", fontSize = 7.sp,
                    fontFamily = Baloo2FontFamily,
                    color = if (isTarget) Color(0xFFFFD600).copy(alpha = 0.5f)
                            else Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

// ─── Plank Chip (pool) ───────────────────────────────────────────────────────

@Composable
private fun PlankChip(plank: PlankData, isSelected: Boolean, onTap: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.12f else 1f, spring(), label = "ps")

    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 44.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected)
                    Brush.verticalGradient(listOf(Color(0xFFFFD600), Color(0xFFFFA000)))
                else
                    Brush.verticalGradient(listOf(Color(0xFFA0784A), Color(0xFF6B4226)))
            )
            .border(
                2.dp,
                if (isSelected) Color(0xFFFFFFFF) else Color(0xFF5D3A1A),
                RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${plank.value}",
            fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = if (isSelected) Color(0xFF3E1F00) else Color(0xFFFFD600)
        )
    }
}

// ─── Confirm Button ──────────────────────────────────────────────────────────

@Composable
private fun ConfirmButton(onClick: () -> Unit) {
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "✅  Confirmar orden",
            fontSize = 13.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = Color.White
        )
    }
}

// ─── Confirm Dialog ──────────────────────────────────────────────────────────

@Composable
private fun ConfirmOrderDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).zIndex(8f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0D00), Color(0xFF2C1800))))
                .border(2.dp, Color(0xFFFFD600).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🌉", fontSize = 36.sp)
            Text(
                "¿El puente está en orden?",
                fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFFFD600),
                textAlign = TextAlign.Center
            )
            Text(
                "Si los tablones están de menor a mayor el puente aguantará. Si no… ¡cuidado!",
                fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Cancel
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A1500))
                        .border(1.5.dp, Color(0xFFFF5252).copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onCancel
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("Revisar", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFFF5252)) }

                // Confirm
                Box(
                    modifier = Modifier
                        .weight(1f).height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(1.5.dp, Color(0xFF69FF47).copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onConfirm
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("¡Sí!", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color.White) }
            }
        }
    }
}

// ─── Tutorial ────────────────────────────────────────────────────────────────

@Composable
private fun TutorialBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0D00), Color(0xFF2C1800))))
                .border(2.dp, Color(0xFFFFD600), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🌉", fontSize = 40.sp)
            Text(
                "EL PUENTE ROTO",
                fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFFFD600)
            )
            Text(
                "El Glitch desordenó los tablones del puente.\n\n" +
                "Coloca cada tablón en orden ascendente (de menor a mayor) para que el puente sea sólido.\n\n" +
                "Toca un tablón del pool → luego toca la ranura donde quieres colocarlo.\n\n" +
                "Tienes ❤️❤️❤️ 3 intentos.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF5D3A1A), Color(0xFF8B5E3C))))
                    .border(1.5.dp, Color(0xFFFFD600), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onDismiss
                    )
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Entendido!", fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    fontWeight = FontWeight.Bold, color = Color(0xFFFFD600))
            }
        }
    }
}

// ─── Victory Overlay ─────────────────────────────────────────────────────────

@Composable
private fun BridgeVictoryOverlay(onNext: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val glow  = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) { glow.animateTo(1f, tween(600)); glow.animateTo(0.6f, tween(600)) }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f)).zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0D00), Color(0xFF2C1800))))
                .border(2.5.dp, Color(0xFFFFD600).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("⭐  ⭐  ⭐", fontSize = 32.sp)
            Text(
                "¡PUENTE CRUZADO!",
                fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD600),
                textAlign = TextAlign.Center
            )
            Text(
                "¡Los tablones están en orden ascendente!\nEl puente es sólido.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFFFD600).copy(alpha = glow.value * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF5D3A1A), Color(0xFF8B5E3C))))
                    .border(2.dp, Color(0xFFFFD600).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onNext
                    )
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Siguiente →", fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
