package com.example.hds_tesisapp.ui.theme.games.game3.level5

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
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
import com.example.hds_tesisapp.ui.theme.games.game3.WaterDrop
import com.example.hds_tesisapp.ui.theme.games.game3.buildDropRow
import com.example.hds_tesisapp.ui.theme.games.game3.isSorted
import kotlinx.coroutines.delay

private fun Context.findAct(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findAct()
    else -> null
}

private const val MAX_LIVES = 3
private val DROP_VALUES = (1..6).toList()

@Composable
fun Level5G3Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findAct() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val drops      = remember { mutableStateListOf<WaterDrop>().also { it.addAll(buildDropRow(DROP_VALUES.shuffled())) } }
    var compIndex  by remember { mutableIntStateOf(0) }
    var passIndex  by remember { mutableIntStateOf(0) }
    var lives      by remember { mutableIntStateOf(MAX_LIVES) }
    var showTutorial by remember { mutableStateOf(true) }
    var showVictory  by remember { mutableStateOf(false) }
    var showError    by remember { mutableStateOf(false) }
    var errorCorrect by remember { mutableStateOf(false) }

    // Boss HP: starts at 1.0, decreases with each completed pass
    val maxPasses = DROP_VALUES.size - 1 // 5 passes max for 6 drops
    val bossHP by remember(passIndex, showVictory) {
        derivedStateOf {
            if (showVictory) 0f
            else (1f - (passIndex.toFloat() / maxPasses.toFloat())).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(showError) {
        if (showError) { delay(500); showError = false }
    }

    val passEnd = drops.size - 1 - passIndex

    fun resetDrops() {
        drops.clear()
        drops.addAll(buildDropRow(DROP_VALUES.shuffled()))
        compIndex = 0; passIndex = 0; lives = MAX_LIVES
    }

    fun onAnswer(shouldSwap: Boolean) {
        val a = drops[compIndex].value
        val b = drops[compIndex + 1].value
        val needsSwap = a > b

        if (shouldSwap == needsSwap) {
            if (needsSwap) {
                val tmp = drops[compIndex]
                drops[compIndex] = drops[compIndex + 1]
                drops[compIndex + 1] = tmp
            }
            val nextComp = compIndex + 1
            if (nextComp >= passEnd) {
                if (drops.isSorted()) {
                    showVictory = true
                } else {
                    compIndex = 0
                    passIndex++
                }
            } else {
                compIndex = nextComp
            }
        } else {
            errorCorrect = !shouldSwap
            lives--
            showError = true
            if (lives <= 0) resetDrops()
        }
    }

    val currentA = drops.getOrNull(compIndex)?.value ?: 0
    val currentB = drops.getOrNull(compIndex + 1)?.value ?: 0

    // Boss shake animation on each completed pass
    val bossShake = remember { Animatable(0f) }
    LaunchedEffect(passIndex) {
        if (passIndex > 0) {
            repeat(3) {
                bossShake.animateTo(6f, tween(80))
                bossShake.animateTo(-6f, tween(80))
            }
            bossShake.animateTo(0f, tween(80))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.waterfall_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.40f)))

        Row(modifier = Modifier.fillMaxSize()) {
            // Main game area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    G1MenuButton(onClick = onNavigateToMenu)
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text("NIVEL 5  ·  MINI JEFE",
                            fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                            color = Color(0xFFFF5252), letterSpacing = 1.sp)
                        Text("Pasada ${passIndex + 1}  ·  Comparación ${compIndex + 1} / $passEnd",
                            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.75f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(MAX_LIVES) { i ->
                            Text(if (i < lives) "❤️" else "🖤", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // 6 drops row
                DropsRow6(drops = drops, compIndex = compIndex)

                Spacer(Modifier.height(14.dp))

                // Comparator + buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BossAnswerButton(
                        label    = "✓  Sin cambios",
                        sublabel = "A ≤ B",
                        color    = Color(0xFF00B0FF),
                        onClick  = { onAnswer(false) }
                    )
                    BossStone(aValue = currentA, bValue = currentB)
                    BossAnswerButton(
                        label    = "⇄  Intercambiar",
                        sublabel = "A > B",
                        color    = Color(0xFFFF5252),
                        onClick  = { onAnswer(true) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "¡El Mini Jefe del Glitch bloquea la cascada! Ordena las gotas para derrotarlo.",
                    fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.60f), textAlign = TextAlign.Center
                )
            }

            // Boss panel
            BossPanel(
                bossHP     = bossHP,
                shakeX     = bossShake.value,
                modifier   = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .padding(end = 12.dp, top = 10.dp, bottom = 10.dp)
            )
        }

        // Error overlay
        if (showError) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFFF5252).copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌ Incorrecto", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        if (errorCorrect) "Estaban bien — A ≤ B" else "¡Necesitaban intercambiarse! A > B",
                        fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White.copy(0.85f)
                    )
                }
            }
        }

        if (showTutorial) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BossTutorialBubble(onDismiss = { showTutorial = false })
            }
        }

        if (showVictory) {
            BossVictoryOverlay(onNext = onLevelComplete)
        }

    }
}

// ─── Drops Row (6 items) ──────────────────────────────────────────────────────

@Composable
private fun DropsRow6(drops: List<WaterDrop>, compIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        drops.forEachIndexed { index, drop ->
            val isHighlighted = index == compIndex || index == compIndex + 1
            val labelLetter   = when {
                index == compIndex     -> "A"
                index == compIndex + 1 -> "B"
                else                   -> ""
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (labelLetter.isNotEmpty()) {
                    Text(labelLetter, fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFFFFD600), fontWeight = FontWeight.ExtraBold)
                } else {
                    Spacer(Modifier.height(14.dp))
                }
                val pulse = remember { Animatable(0.85f) }
                LaunchedEffect(isHighlighted) {
                    if (isHighlighted) {
                        while (true) { pulse.animateTo(1f, tween(400)); pulse.animateTo(0.85f, tween(400)) }
                    } else pulse.animateTo(0.85f, tween(200))
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .graphicsLayer { scaleX = pulse.value; scaleY = pulse.value }
                        .drawBehind {
                            if (isHighlighted)
                                drawCircle(Color(0xFFFFD600).copy(0.3f), radius = size.minDimension / 2f + 6f)
                        }
                        .clip(CircleShape)
                        .background(
                            if (isHighlighted)
                                Brush.radialGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B)))
                            else
                                Brush.radialGradient(listOf(Color(0xFF004D70), Color(0xFF002A40)))
                        )
                        .border(2.dp,
                            if (isHighlighted) Color(0xFFFFD600) else Color(0xFF0288D1).copy(0.5f),
                            CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${drop.value}", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isHighlighted) Color(0xFFFFD600) else Color(0xFF00E5FF))
                }
            }
        }
    }
}

// ─── Boss Stone (comparator) ──────────────────────────────────────────────────

@Composable
private fun BossStone(aValue: Int, bValue: Int) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(72.dp)
            .drawBehind {
                drawRoundRect(color = Color(0xFF546E7A), cornerRadius = CornerRadius(14.dp.toPx()))
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF78909C).copy(0.45f), Color.Transparent),
                        startY = 0f, endY = size.height * 0.45f
                    ),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFFFF5252).copy(0.8f),
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("A", fontSize = 8.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFB0BEC5))
                Text("$aValue", fontSize = 24.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🪨", fontSize = 12.sp)
                Text("vs", fontSize = 8.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFFFD600))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("B", fontSize = 8.sp, fontFamily = OrbitronFontFamily, color = Color(0xFFB0BEC5))
                Text("$bValue", fontSize = 24.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF))
            }
        }
    }
}

// ─── Answer Button ────────────────────────────────────────────────────────────

@Composable
private fun BossAnswerButton(label: String, sublabel: String, color: Color, onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(600)); glow.animateTo(0.5f, tween(600)) }
    }
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(52.dp)
            .drawBehind {
                drawRoundRect(
                    color = color.copy(alpha = glow.value * 0.25f),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(color.copy(0.22f), Color(0xFF001828))))
            .border(2.dp, color.copy(alpha = glow.value), RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text(sublabel, fontSize = 8.sp, fontFamily = Baloo2FontFamily, color = color.copy(0.75f))
        }
    }
}

// ─── Boss Panel ───────────────────────────────────────────────────────────────

@Composable
private fun BossPanel(bossHP: Float, shakeX: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.graphicsLayer { translationX = shakeX },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Boss HP bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2A0000))
                .border(1.dp, Color(0xFFFF5252).copy(0.5f), RoundedCornerShape(6.dp))
                .height(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(bossHP)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFFF5252), Color(0xFFD32F2F)))
                    )
            )
        }
        Text("HP", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
            color = Color(0xFFFF5252).copy(0.7f))

        Spacer(Modifier.height(8.dp))

        // Boss image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFFFF5252).copy(0.12f),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFFFF5252).copy(0.4f),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.miniboss_zone3),
                contentDescription = "Mini Jefe",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .graphicsLayer { alpha = 0.85f + bossHP * 0.15f },
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(6.dp))

        Text("MINI JEFE", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
            color = Color(0xFFFF5252), letterSpacing = 1.sp)
        Text(
            if (bossHP > 0.5f) "¡Defiende la cascada!"
            else if (bossHP > 0.1f) "¡Casi lo tienes!"
            else "¡Último golpe!",
            fontSize = 8.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(0.65f), textAlign = TextAlign.Center
        )
    }
}

// ─── Tutorial ─────────────────────────────────────────────────────────────────

@Composable
private fun BossTutorialBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.72f)).zIndex(15f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1A0005), Color(0xFF001828))))
                .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("👾", fontSize = 40.sp)
            Text("¡MINI JEFE!", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            Text(
                "El Mini Jefe del Glitch bloqueó la cascada con 6 gotas desordenadas.\n\n" +
                "Usa la Piedra Comparadora para ordenarlas con Bubble Sort.\n\n" +
                "Cada pasada que completes le quitará vida al jefe.\n\n" +
                "¡Ordena todas las gotas para derrotarlo!",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.9f), textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))))
                    .border(1.5.dp, Color(0xFFFF5252), RoundedCornerShape(12.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("¡A la batalla!", fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ─── Victory ──────────────────────────────────────────────────────────────────

@Composable
private fun BossVictoryOverlay(onNext: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val glow  = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) { glow.animateTo(1f, tween(600)); glow.animateTo(0.6f, tween(600)) }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.75f)).zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF001828), Color(0xFF1A0005))))
                .border(2.5.dp, Color(0xFFFFD600).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🏆", fontSize = 40.sp)
            Text("¡MINI JEFE DERROTADO!", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD600), textAlign = TextAlign.Center)
            Text("¡Bubble Sort completado con éxito!\nLas gotas fluyen en armonía y el Glitch retrocede.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.85f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFFFD600).copy(alpha = glow.value * 0.3f),
                            cornerRadius = CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                    .border(2.dp, Color(0xFFFFD600).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onNext)
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Completar Zona 3!  →", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
