package com.example.hds_tesisapp.ui.theme.games.game1

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

private val CELL_DP = 82.dp
private val BIT_DP  = 60.dp

// ─── Game1Board ───────────────────────────────────────────────────────────────

@Composable
fun Game1Board(
    config: LevelConfig,
    bitRow: Int,
    bitCol: Int,
    bitDir: Direction,
    modifier: Modifier = Modifier
) {
    val targetX = CELL_DP * bitCol + (CELL_DP - BIT_DP) / 2
    val targetY = CELL_DP * bitRow + (CELL_DP - BIT_DP) / 2

    val bitOffsetX by animateDpAsState(targetX, tween(620), label = "bx")
    val bitOffsetY by animateDpAsState(targetY, tween(620), label = "by")
    val arrowAngle by animateFloatAsState(bitDir.arrowDegrees(), tween(280), label = "ba")

    Box(modifier = modifier.size(CELL_DP * config.gridCols, CELL_DP * config.gridRows)) {
        Column {
            repeat(config.gridRows) { row ->
                Row {
                    repeat(config.gridCols) { col ->
                        val cell = Pair(row, col)
                        G1GridCell(
                            isHouse      = row == config.houseRow && col == config.houseCol,
                            isPath       = cell in config.pathCells,
                            isObstacle   = cell in config.obstacleCells,
                            isFalseRoute = cell in config.falseRouteCells,
                        )
                    }
                }
            }
        }
        G1BitCharacter(
            modifier   = Modifier.size(BIT_DP).offset(x = bitOffsetX, y = bitOffsetY),
            arrowAngle = arrowAngle
        )
    }
}

@Composable
private fun G1GridCell(
    isHouse: Boolean,
    isPath: Boolean,
    isObstacle: Boolean,
    isFalseRoute: Boolean,
) {
    val bgColor = when {
        isHouse      -> Color(0xFF0D2B0D)
        isObstacle   -> Color(0xFF1A0505)
        isPath       -> Color(0xFF0C1A2E)
        isFalseRoute -> Color(0xFF0A1628)
        else         -> Color(0xFF05080F)
    }
    val borderColor = when {
        isHouse      -> Color(0xFF69FF47).copy(alpha = 0.7f)
        isObstacle   -> Color(0xFFEF5350).copy(alpha = 0.5f)
        isPath       -> Color(0xFF00E5FF).copy(alpha = 0.25f)
        isFalseRoute -> Color(0xFF00E5FF).copy(alpha = 0.14f)
        else         -> Color(0xFF111827).copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier.size(CELL_DP).background(bgColor).border(1.dp, borderColor),
        contentAlignment = Alignment.Center
    ) {
        when {
            isHouse    -> G1HouseCell()
            isObstacle -> G1ObstacleCell()
            isPath || isFalseRoute -> G1PathDot()
        }
    }
}

@Composable
private fun G1HouseCell() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF69FF47).copy(alpha = 0.12f))
            .border(1.5.dp, Color(0xFF69FF47).copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏠", fontSize = 18.sp)
            Text(
                "META", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
                color = Color(0xFF69FF47), fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun G1ObstacleCell() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEF5350).copy(alpha = 0.15f))
            .border(1.5.dp, Color(0xFFEF5350).copy(alpha = 0.7f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚡", fontSize = 18.sp)
            Text(
                "ERROR", fontSize = 6.sp, fontFamily = OrbitronFontFamily,
                color = Color(0xFFEF5350), fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun G1PathDot() {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(Color(0xFF00E5FF).copy(alpha = 0.30f), RoundedCornerShape(50))
    )
}

@Composable
private fun G1BitCharacter(modifier: Modifier = Modifier, arrowAngle: Float) {
    Box(
        modifier = modifier.drawBehind {
            drawCircle(Color(0xFF00E5FF).copy(alpha = 0.18f), radius = size.minDimension / 2f + 10f)
        },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.bit_triste),
            contentDescription = "Bit",
            modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = arrowAngle },
            contentScale = ContentScale.Fit
        )
    }
}

// ─── Game1CommandPanel ────────────────────────────────────────────────────────

@Composable
fun Game1CommandPanel(
    config: LevelConfig,
    slots: List<GameCommand?>,
    hintSlotIndex: Int,
    hintsActive: Boolean,
    canExecute: Boolean,
    isExecuting: Boolean,
    onCommandTap: (GameCommand) -> Unit,
    onReset: () -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Título ──
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PROGRAMA", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = OrbitronFontFamily, color = Color.Cyan, letterSpacing = 2.sp
            )
            Text(
                if (!hintsActive) "Escucha a Atom primero"
                else "Toca un comando para agregarlo",
                fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.55f), textAlign = TextAlign.Center
            )
        }

        // ── Slots de programa ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            slots.chunked(3).forEachIndexed { rowIndex, rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSlots.forEachIndexed { indexInRow, _ ->
                        val absoluteIndex = rowIndex * 3 + indexInRow
                        G1CommandSlot(
                            stepNumber = absoluteIndex + 1,
                            command    = slots[absoluteIndex],
                            isHinted   = hintsActive && hintSlotIndex == absoluteIndex
                        )
                    }
                }
            }
        }

        // ── Divisor ──
        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp)
                .background(Color(0xFF00E5FF).copy(alpha = 0.20f))
        )

        // ── Paleta de comandos ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "COMANDOS", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp
            )
            config.availableCommands.chunked(3).forEach { cmdRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    cmdRow.forEach { cmd ->
                        G1CommandCard(
                            command = cmd,
                            enabled = hintsActive && !isExecuting,
                            onTap   = { onCommandTap(cmd) }
                        )
                    }
                }
            }
        }

        // ── Botones ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            G1ActionButton(
                text = "↺  Reset", accentColor = Color(0xFFFF5252),
                enabled = slots.any { it != null } && !isExecuting,
                onClick = onReset
            )
            G1ActionButton(
                text = "▶  Ejecutar", accentColor = Color(0xFF69FF47),
                enabled = canExecute && !isExecuting,
                onClick = onExecute
            )
        }
    }
}

@Composable
private fun G1CommandSlot(stepNumber: Int, command: GameCommand?, isHinted: Boolean) {
    val hintAlpha = remember { Animatable(0.25f) }
    LaunchedEffect(isHinted) {
        if (isHinted) {
            while (true) {
                hintAlpha.animateTo(1f, tween(480))
                hintAlpha.animateTo(0.2f, tween(480))
            }
        } else {
            hintAlpha.snapTo(0.25f)
        }
    }

    val borderColor = when {
        command != null -> command.accentColor.copy(alpha = 0.9f)
        isHinted        -> Color(0xFFFFD600).copy(alpha = hintAlpha.value)
        else            -> Color.White.copy(alpha = 0.18f)
    }

    Box(
        modifier = Modifier
            .size(72.dp, 52.dp)
            .drawBehind {
                if (isHinted) {
                    drawRoundRect(
                        color = Color(0xFFFFD600).copy(alpha = hintAlpha.value * 0.25f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (command != null)
                    Brush.radialGradient(listOf(command.accentColor.copy(alpha = 0.18f), Color(0xFF050A14)))
                else
                    Brush.radialGradient(listOf(Color(0xFF0D1B2A), Color(0xFF050A14)))
            )
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
    ) {
        // Step number badge — top-left
        Text(
            "$stepNumber", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
            color = if (isHinted) Color(0xFFFFD600).copy(alpha = hintAlpha.value)
                    else Color.White.copy(alpha = 0.35f),
            modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp, top = 2.dp)
        )
        // Content — centered
        if (command != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(command.icon, fontSize = 18.sp, color = command.accentColor, fontWeight = FontWeight.Bold)
                Text(
                    command.label, fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                    color = command.accentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                "·  ·  ·", fontSize = 12.sp, color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun G1CommandCard(command: GameCommand, enabled: Boolean, onTap: () -> Unit) {
    val pressAlpha = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .size(68.dp, 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        command.accentColor.copy(alpha = if (enabled) 0.22f else 0.07f),
                        Color(0xFF050A14)
                    )
                )
            )
            .border(
                2.dp,
                command.accentColor.copy(alpha = if (enabled) 0.85f else 0.25f),
                RoundedCornerShape(12.dp)
            )
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(onPress = {
                        pressAlpha.animateTo(0.6f, tween(80))
                        tryAwaitRelease()
                        pressAlpha.animateTo(1f, tween(120))
                        onTap()
                    })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                command.icon, fontSize = 20.sp,
                color = command.accentColor.copy(alpha = if (enabled) 1f else 0.35f),
                fontWeight = FontWeight.Bold
            )
            Text(
                command.label, fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                color = command.accentColor.copy(alpha = if (enabled) 0.85f else 0.3f),
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun G1ActionButton(text: String, accentColor: Color, enabled: Boolean, onClick: () -> Unit) {
    val pressAlpha = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .width(120.dp).height(42.dp)
            .drawBehind {
                if (enabled) {
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.25f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                    )
                }
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    if (enabled) listOf(accentColor.copy(alpha = 0.20f), Color(0xFF050A14))
                    else listOf(Color(0xFF0A0F1F), Color(0xFF050A14))
                )
            )
            .border(2.dp, accentColor.copy(alpha = if (enabled) 0.80f else 0.20f), RoundedCornerShape(14.dp))
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(onPress = {
                        pressAlpha.animateTo(0.6f, tween(80))
                        tryAwaitRelease()
                        pressAlpha.animateTo(1f, tween(120))
                        onClick()
                    })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = accentColor.copy(alpha = if (enabled) 1f else 0.30f)
        )
    }
}

// ─── Atom Dialog ──────────────────────────────────────────────────────────────

@Composable
fun G1AtomDialog(message: String, onDismiss: () -> Unit) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(220)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    fun dismiss() {
        scope.launch {
            launch { alpha.animateTo(0f, tween(180)) }
            scale.animateTo(0.85f, tween(180))
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .zIndex(10f)
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.76f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.atom),
                    contentDescription = "Atom",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "ATOM", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF), letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(message, fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White, lineHeight = 21.sp)
                    Spacer(Modifier.height(18.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF00838F), Color(0xFF00BCD4))))
                            .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = { tryAwaitRelease(); dismiss() })
                            }
                            .padding(horizontal = 22.dp, vertical = 10.dp)
                    ) {
                        Text("¡Entendido!", fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─── Victory Overlay ─────────────────────────────────────────────────────────

@Composable
fun G1VictoryOverlay(levelNumber: Int, onNext: () -> Unit) {
    val scale    = remember { Animatable(0.5f) }
    val alpha    = remember { Animatable(0f) }
    val starScale = remember { Animatable(0f) }
    val glow     = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(300)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        starScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow))
        while (true) {
            glow.animateTo(1f, tween(700))
            glow.animateTo(0.6f, tween(700))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.58f)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF001830), Color(0xFF002040))))
                .border(2.5.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(28.dp))
                .padding(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "⭐  ⭐  ⭐", fontSize = 36.sp,
                    modifier = Modifier.graphicsLayer { scaleX = starScale.value; scaleY = starScale.value }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "¡NIVEL $levelNumber COMPLETADO!", fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = OrbitronFontFamily,
                    color = Color(0xFF69FF47), textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "¡El Bit llegó a la meta!\n¡Excelente algoritmo!",
                    fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center, lineHeight = 21.sp
                )
                Spacer(Modifier.height(26.dp))
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.35f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 22f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = { tryAwaitRelease(); onNext() })
                        }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        "Siguiente Nivel  →", fontSize = 15.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Help Button ─────────────────────────────────────────────────────────────

@Composable
fun G1HelpButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) {
            glow.animateTo(1f, tween(900))
            glow.animateTo(0.5f, tween(900))
        }
    }
    Box(
        modifier = modifier
            .size(46.dp)
            .drawBehind {
                drawCircle(
                    Color(0xFFFFD600).copy(alpha = glow.value * 0.25f),
                    radius = size.minDimension / 2f + 8f
                )
            }
            .clip(CircleShape)
            .background(Color(0xFF0A0F1F))
            .border(2.dp, Color(0xFFFFD600).copy(alpha = glow.value), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { tryAwaitRelease(); onClick() })
            },
        contentAlignment = Alignment.Center
    ) {
        Text("?", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily, color = Color(0xFFFFD600))
    }
}
