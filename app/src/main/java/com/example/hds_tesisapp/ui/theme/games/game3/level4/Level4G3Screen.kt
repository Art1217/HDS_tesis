package com.example.hds_tesisapp.ui.theme.games.game3.level4

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
private val DROP_VALUES = (1..5).toList()

@Composable
fun Level4G3Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
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
    var errorCorrect by remember { mutableStateOf(false) } // true = should have been "correct", false = should have been "swap"

    LaunchedEffect(showError) {
        if (showError) { delay(500); showError = false }
    }

    // passEnd: how many pairs to compare in the current pass
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
            // Correct!
            if (needsSwap) {
                val tmp = drops[compIndex]
                drops[compIndex] = drops[compIndex + 1]
                drops[compIndex + 1] = tmp
            }
            val nextComp = compIndex + 1
            if (nextComp >= passEnd) {
                // End of pass
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
            // Wrong
            errorCorrect = !shouldSwap // if player said "swap" but was wrong, correct was "no swap"
            lives--
            showError = true
            if (lives <= 0) resetDrops()
        }
    }

    val currentA = drops.getOrNull(compIndex)?.value ?: 0
    val currentB = drops.getOrNull(compIndex + 1)?.value ?: 0

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.waterfall_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.38f)))

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
                G1MenuButton(onClick = onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text("NIVEL 4  ·  LA CASCADA",
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFF00E5FF), letterSpacing = 1.sp)
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

            // Drops row
            DropsRow(drops = drops, compIndex = compIndex)

            Spacer(Modifier.height(16.dp))

            // Comparator Stone + buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "No swap" button
                BubbleAnswerButton(
                    label    = "✓  Sin cambios",
                    sublabel = "A ≤ B",
                    color    = Color(0xFF00B0FF),
                    onClick  = { onAnswer(false) }
                )

                // Stone comparator
                ComparatorStone(aValue = currentA, bValue = currentB)

                // "Swap" button
                BubbleAnswerButton(
                    label    = "⇄  Intercambiar",
                    sublabel = "A > B",
                    color    = Color(0xFFFF9800),
                    onClick  = { onAnswer(true) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Compara A y B: ¿A es mayor que B? → Intercambiar  /  ¿A es menor o igual a B? → Sin cambios",
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.60f), textAlign = TextAlign.Center
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
                BubbleTutorialBubble(onDismiss = { showTutorial = false })
            }
        }

        if (showVictory) {
            CascadeVictoryOverlay(onNext = onLevelComplete)
        }

    }
}

// ─── Drops Row ────────────────────────────────────────────────────────────────

@Composable
private fun DropsRow(drops: List<WaterDrop>, compIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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
            DropCircle(value = drop.value, isHighlighted = isHighlighted, label = labelLetter)
        }
    }
}

@Composable
private fun DropCircle(value: Int, isHighlighted: Boolean, label: String) {
    val pulse = remember { Animatable(0.8f) }
    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            while (true) { pulse.animateTo(1f, tween(400)); pulse.animateTo(0.8f, tween(400)) }
        } else {
            pulse.animateTo(0.8f, tween(200))
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                color = Color(0xFFFFD600), fontWeight = FontWeight.ExtraBold)
        } else {
            Spacer(Modifier.height(16.dp))
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer { scaleX = pulse.value; scaleY = pulse.value }
                .drawBehind {
                    if (isHighlighted) {
                        drawCircle(Color(0xFFFFD600).copy(alpha = 0.3f), radius = size.minDimension / 2f + 8f)
                    }
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
            Text("$value", fontSize = 22.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold,
                color = if (isHighlighted) Color(0xFFFFD600) else Color(0xFF00E5FF))
        }
    }
}

// ─── Comparator Stone ─────────────────────────────────────────────────────────

@Composable
private fun ComparatorStone(aValue: Int, bValue: Int) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(80.dp)
            .drawBehind {
                // Stone base
                drawRoundRect(
                    color = Color(0xFF546E7A),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
                // Stone highlight (top sheen)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF78909C).copy(0.5f), Color.Transparent),
                        startY = 0f, endY = size.height * 0.45f
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
                // Border
                drawRoundRect(
                    color = Color(0xFF90A4AE),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                // Texture bumps
                for (row in 0..1) {
                    for (col in 0..3) {
                        drawCircle(
                            color = Color.White.copy(0.06f),
                            radius = 4.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(
                                size.width * (col + 1) / 5f,
                                size.height * (row + 1) / 3f
                            )
                        )
                    }
                }
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("A", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFB0BEC5))
                Text("$aValue", fontSize = 28.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF))
            }
            // Question mark
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🪨", fontSize = 14.sp)
                Text("vs", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFFFD600))
            }
            // B value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("B", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFB0BEC5))
                Text("$bValue", fontSize = 28.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF))
            }
        }
    }
}

// ─── Answer Button ────────────────────────────────────────────────────────────

@Composable
private fun BubbleAnswerButton(label: String, sublabel: String, color: Color, onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(600)); glow.animateTo(0.5f, tween(600)) }
    }
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(56.dp)
            .drawBehind {
                drawRoundRect(
                    color = color.copy(alpha = glow.value * 0.25f),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(color.copy(0.22f), Color(0xFF001828))))
            .border(2.dp, color.copy(alpha = glow.value), RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color.White)
            Text(sublabel, fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = color.copy(0.75f))
        }
    }
}

// ─── Tutorial ─────────────────────────────────────────────────────────────────

@Composable
private fun BubbleTutorialBubble(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.70f)).zIndex(15f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF001828), Color(0xFF002840))))
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("💧", fontSize = 40.sp)
            Text("LA CASCADA", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Text(
                "Las gotas de agua están desordenadas.\n\n" +
                "La Piedra Comparadora te muestra dos gotas (A y B).\n\n" +
                "Tú decides:\n• ✓ Sin cambios → si A ≤ B (orden correcto)\n• ⇄ Intercambiar → si A > B (hay que cambiarlas)\n\n" +
                "¡Completa todas las pasadas para ordenar la cascada!",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.9f), textAlign = TextAlign.Center, lineHeight = 20.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B))))
                    .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
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

// ─── Victory ──────────────────────────────────────────────────────────────────

@Composable
private fun CascadeVictoryOverlay(onNext: () -> Unit) {
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
                .background(Brush.verticalGradient(listOf(Color(0xFF001828), Color(0xFF002840))))
                .border(2.5.dp, Color(0xFF00E5FF).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("⭐  ⭐  ⭐", fontSize = 32.sp)
            Text("¡CASCADA ORDENADA!", fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E5FF), textAlign = TextAlign.Center)
            Text("¡Bubble Sort completado!\nLas gotas fluyen en perfecta armonía.",
                fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.85f), textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFF00E5FF).copy(alpha = glow.value * 0.3f),
                            cornerRadius = CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B))))
                    .border(2.dp, Color(0xFF00E5FF).copy(alpha = glow.value), RoundedCornerShape(14.dp))
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
