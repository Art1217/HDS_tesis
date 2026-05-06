package com.example.hds_tesisapp.ui.theme.games.game1.level5

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game1.Direction
import com.example.hds_tesisapp.ui.theme.games.game1.G1AtomDialog
import com.example.hds_tesisapp.ui.theme.games.game1.G1HelpButton
import com.example.hds_tesisapp.ui.theme.games.game1.G1MenuButton
import com.example.hds_tesisapp.ui.theme.games.game1.G1VictoryOverlay
import com.example.hds_tesisapp.ui.theme.games.game1.Game1Board
import com.example.hds_tesisapp.ui.theme.games.game1.GameCommand
import com.example.hds_tesisapp.ui.theme.games.game1.LevelConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Constantes del nivel ─────────────────────────────────────────────────────

private const val L5_ROWS = 4
private const val L5_COLS = 4
private const val L5_SLOTS = 7
private val L5_START_DIR = Direction.EAST

private val L5_PATH = setOf(
    Pair(3, 0), Pair(3, 1), Pair(3, 2), Pair(3, 3),
    Pair(2, 3), Pair(1, 3), Pair(0, 3)
)
private val L5_OBSTACLES     = setOf(Pair(2, 0), Pair(1, 0), Pair(2, 1))
private val L5_FALSE_ROUTES  = setOf(Pair(0, 0), Pair(0, 1), Pair(0, 2))
private val L5_COMMANDS = listOf(
    GameCommand.AVANZAR, GameCommand.GIRAR_IZQUIERDA,
    GameCommand.GIRAR_DERECHA, GameCommand.RETROCEDER,
)

private val L5_CONFIG = LevelConfig(
    levelNumber = 5, levelTitle = "Jefe Zona 1",
    gridRows = L5_ROWS, gridCols = L5_COLS,
    startRow = 3, startCol = 0, startDir = L5_START_DIR,
    houseRow = 0, houseCol = 3,
    pathCells = L5_PATH, obstacleCells = L5_OBSTACLES, falseRouteCells = L5_FALSE_ROUTES,
    slotCount = L5_SLOTS, availableCommands = L5_COMMANDS,
    tutorialMessage = "", helpMessage = "", errorMessage = ""
)

// ─── Fases ────────────────────────────────────────────────────────────────────

private enum class L5Phase {
    INTRO, PLAYING, GLITCH_INTRO, GLITCH_STRIKE, DEBUGGING, EXECUTING, VICTORY, ERROR_DIALOG
}

private fun Context.findL5Activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findL5Activity()
    else -> null
}

// ─── Level5Screen ─────────────────────────────────────────────────────────────

@Composable
fun Level5Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context  = LocalContext.current
    val activity = remember { context.findL5Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()
    var phase  by remember { mutableStateOf(L5Phase.INTRO) }

    var bitRow by remember { mutableStateOf(3) }
    var bitCol by remember { mutableStateOf(0) }
    var bitDir by remember { mutableStateOf(L5_START_DIR) }

    val slots           = remember { mutableStateListOf<GameCommand?>().also { l -> repeat(L5_SLOTS) { l.add(null) } } }
    val glitchedIndices = remember { mutableStateListOf<Int>() }

    val hintsActive    = phase in listOf(L5Phase.PLAYING, L5Phase.DEBUGGING)
    val hintSlotIndex  = if (hintsActive) slots.indexOfFirst { it == null } else -1
    val noGlitchedLeft = glitchedIndices.isEmpty()
    val canExecute     = slots.none { it == null } && noGlitchedLeft
    val isExecuting    = phase == L5Phase.EXECUTING

    // Glitch strike — triggered when phase changes to GLITCH_STRIKE
    LaunchedEffect(phase) {
        if (phase == L5Phase.GLITCH_STRIKE) {
            delay(700)
            val filled = (0 until L5_SLOTS).filter { slots[it] != null }.shuffled().take(2)
            for (idx in filled) {
                val current = slots[idx]!!
                slots[idx]  = L5_COMMANDS.filter { it != current }.random()
                glitchedIndices.add(idx)
            }
            phase = L5Phase.DEBUGGING
        }
    }

    fun resetAll() {
        for (i in slots.indices) slots[i] = null
        glitchedIndices.clear()
        bitRow = 3; bitCol = 0; bitDir = L5_START_DIR
    }

    fun addCommand(cmd: GameCommand) {
        val idx = slots.indexOfFirst { it == null }
        if (idx != -1) slots[idx] = cmd
    }

    fun clearGlitchedSlot(idx: Int) {
        slots[idx] = null
        glitchedIndices.remove(idx)
    }

    fun executeProgram() {
        scope.launch {
            phase = L5Phase.EXECUTING
            var row = 3; var col = 0; var dir = L5_START_DIR
            var hitError = false

            for (cmd in slots) {
                when (cmd) {
                    GameCommand.AVANZAR -> {
                        val (r, c) = dir.move(row, col)
                        if (r !in 0 until L5_ROWS || c !in 0 until L5_COLS || Pair(r, c) in L5_OBSTACLES) {
                            hitError = true; break
                        }
                        row = r; col = c; bitRow = row; bitCol = col; delay(750)
                    }
                    GameCommand.RETROCEDER -> {
                        val (r, c) = dir.moveBack(row, col)
                        if (r !in 0 until L5_ROWS || c !in 0 until L5_COLS || Pair(r, c) in L5_OBSTACLES) {
                            hitError = true; break
                        }
                        row = r; col = c; bitRow = row; bitCol = col; delay(750)
                    }
                    GameCommand.GIRAR_IZQUIERDA -> { dir = dir.turnLeft(); bitDir = dir; delay(450) }
                    GameCommand.GIRAR_DERECHA   -> { dir = dir.turnRight(); bitDir = dir; delay(450) }
                    null -> Unit
                }
            }
            delay(300)
            phase = if (!hitError && row == 0 && col == 3) L5Phase.VICTORY else L5Phase.ERROR_DIALOG
        }
    }

    fun onExecutePressed() {
        when (phase) {
            L5Phase.PLAYING   -> phase = L5Phase.GLITCH_INTRO
            L5Phase.DEBUGGING -> executeProgram()
            else -> Unit
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001020), Color(0xFF000A1A))))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Izquierda: tablero ──
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "NIVEL 5  ·  JEFE ZONA 1",
                    fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFAB47BC).copy(alpha = 0.85f), letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF030810)).padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Game1Board(config = L5_CONFIG, bitRow = bitRow, bitCol = bitCol, bitDir = bitDir)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    when (phase) {
                        L5Phase.INTRO, L5Phase.PLAYING -> "Construye el algoritmo"
                        L5Phase.GLITCH_INTRO           -> "Mini-Glitch interviene..."
                        L5Phase.GLITCH_STRIKE          -> "¡Saboteando tu código!"
                        L5Phase.DEBUGGING              -> "¡Depura los pasos rojos!"
                        L5Phase.EXECUTING              -> "Ejecutando..."
                        L5Phase.VICTORY                -> "¡Mini-Glitch derrotado!"
                        L5Phase.ERROR_DIALOG           -> "El Bit se perdió..."
                    },
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily, fontWeight = FontWeight.Bold,
                    color = when (phase) {
                        L5Phase.GLITCH_INTRO, L5Phase.GLITCH_STRIKE, L5Phase.DEBUGGING -> Color(0xFFCE93D8)
                        L5Phase.VICTORY                                                 -> Color(0xFF69FF47)
                        L5Phase.ERROR_DIALOG                                            -> Color(0xFFFF5252)
                        L5Phase.EXECUTING                                               -> Color(0xFFFFD600)
                        else                                                            -> Color.White.copy(alpha = 0.55f)
                    }
                )
            }

            // Divider
            Box(
                modifier = Modifier.width(1.dp).fillMaxHeight(0.75f)
                    .background(Color(0xFFAB47BC).copy(alpha = 0.25f))
            )

            // ── Derecha: panel de comandos ──
            Column(
                modifier = Modifier.weight(1.1f).fillMaxHeight().padding(start = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mini-Glitch character
                if (phase in listOf(L5Phase.GLITCH_STRIKE, L5Phase.DEBUGGING, L5Phase.GLITCH_INTRO)) {
                    MiniGlitchCharacter(
                        isStriking = phase == L5Phase.GLITCH_STRIKE,
                        isDefeated = false
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Level5CommandPanel(
                    slots           = slots,
                    glitchedIndices = glitchedIndices,
                    hintSlotIndex   = hintSlotIndex,
                    hintsActive     = hintsActive,
                    canExecute      = canExecute,
                    isExecuting     = isExecuting,
                    phase           = phase,
                    onCommandTap    = { addCommand(it) },
                    onClearGlitch   = { clearGlitchedSlot(it) },
                    onReset         = { resetAll(); phase = L5Phase.PLAYING },
                    onExecute       = { onExecutePressed() }
                )
            }
        }

        // Help button
        if (phase in listOf(L5Phase.PLAYING, L5Phase.DEBUGGING)) {
            G1HelpButton(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp).zIndex(5f)
            ) { phase = L5Phase.INTRO }
        }

        // Menu button
        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).zIndex(5f),
            onClick  = onNavigateToMenu
        )

        // ── Overlays ──
        when (phase) {
            L5Phase.INTRO -> G1AtomDialog(
                message =
                    "¡Nivel 5 — El Jefe de la Zona 1!\n\n" +
                    "Mini-Glitch ha bloqueado el último camino. " +
                    "Es astuto: cuando intentes ejecutar tu algoritmo, " +
                    "¡lo saboteará desordenando 2 pasos!\n\n" +
                    "Construye la secuencia correcta, detecta cuáles pasos rompió, " +
                    "corrígelos y ejecútalo de nuevo.\n\n" +
                    "Usa la lógica. No el azar.",
                onDismiss = { phase = L5Phase.PLAYING }
            )
            L5Phase.GLITCH_INTRO -> GlitchWarningDialog(onDismiss = { phase = L5Phase.GLITCH_STRIKE })
            L5Phase.ERROR_DIALOG -> G1AtomDialog(
                message =
                    "¡El Bit no llegó!\n\n" +
                    "Revisa bien cada paso. Recuerda que debes rodear " +
                    "los bloques ERROR por la fila inferior antes de subir.\n\n" +
                    "¡Tú puedes con Mini-Glitch!",
                onDismiss = { resetAll(); phase = L5Phase.PLAYING }
            )
            L5Phase.VICTORY -> G1VictoryOverlay(levelNumber = 5, onNext = onLevelComplete)
            else -> Unit
        }
    }
}

// ─── Mini-Glitch Character ────────────────────────────────────────────────────

@Composable
private fun MiniGlitchCharacter(isStriking: Boolean, isDefeated: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = if (isStriking) -6f else -2f,
        targetValue  = if (isStriking) 6f else 2f,
        animationSpec = infiniteRepeatable(tween(if (isStriking) 80 else 300), RepeatMode.Reverse),
        label = "shake"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .offset(x = shakeOffset.dp)
            .size(width = 180.dp, height = 60.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFFAB47BC).copy(alpha = glowAlpha * 0.35f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                )
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF1A0030), Color(0xFF2D0050)))
            )
            .border(2.dp, Color(0xFFAB47BC).copy(alpha = glowAlpha), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("👾", fontSize = 22.sp)
            Column {
                Text(
                    "MINI-GLITCH", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFFCE93D8), fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
                Text(
                    if (isStriking) "¡SABOTEANDO!" else "te observa...",
                    fontSize = 8.sp, fontFamily = Baloo2FontFamily,
                    color = Color(0xFFCE93D8).copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ─── Glitch Warning Dialog ────────────────────────────────────────────────────

@Composable
private fun GlitchWarningDialog(onDismiss: () -> Unit) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(200)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    fun dismiss() {
        scope.launch {
            launch { alpha.animateTo(0f, tween(160)) }
            scale.animateTo(0.85f, tween(160))
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .zIndex(10f)
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.76f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0D0020))
                .border(2.dp, Color(0xFFAB47BC), RoundedCornerShape(24.dp))
                .padding(28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👾", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "MINI-GLITCH",
                    fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color(0xFFCE93D8), letterSpacing = 3.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "¡Ja, ja, ja! ¡He desordenado 2 pasos de tu algoritmo!\n\n" +
                    "Encontrarás 2 pasos marcados en rojo.\n" +
                    "Tócalos para borrarlos y vuelve a escribir los correctos.\n\n" +
                    "Si tu lógica es buena... aún tienes oportunidad.",
                    fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.90f),
                    textAlign = TextAlign.Center, lineHeight = 21.sp
                )
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA))))
                        .border(1.5.dp, Color(0xFFCE93D8), RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = { tryAwaitRelease(); dismiss() })
                        }
                        .padding(horizontal = 28.dp, vertical = 12.dp)
                ) {
                    Text(
                        "¡Acepto el reto!", fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Level5CommandPanel ───────────────────────────────────────────────────────

@Composable
private fun Level5CommandPanel(
    slots: List<GameCommand?>,
    glitchedIndices: List<Int>,
    hintSlotIndex: Int,
    hintsActive: Boolean,
    canExecute: Boolean,
    isExecuting: Boolean,
    phase: L5Phase,
    onCommandTap: (GameCommand) -> Unit,
    onClearGlitch: (Int) -> Unit,
    onReset: () -> Unit,
    onExecute: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Título
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PROGRAMA", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = OrbitronFontFamily, color = Color(0xFFCE93D8), letterSpacing = 2.sp
            )
            Text(
                when (phase) {
                    L5Phase.DEBUGGING -> "Toca los pasos rojos para borrarlos"
                    else              -> "Toca un comando para agregarlo"
                },
                fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center
            )
        }

        // Slots en filas de 3 (3-3-1)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            slots.chunked(3).forEachIndexed { rowIndex, rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSlots.forEachIndexed { indexInRow, _ ->
                        val absIdx   = rowIndex * 3 + indexInRow
                        val isGlitch = absIdx in glitchedIndices
                        L5CommandSlot(
                            stepNumber   = absIdx + 1,
                            command      = slots[absIdx],
                            isGlitched   = isGlitch,
                            isHinted     = hintsActive && hintSlotIndex == absIdx && !isGlitch,
                            isDebugging  = phase == L5Phase.DEBUGGING,
                            onClear      = { if (isGlitch) onClearGlitch(absIdx) }
                        )
                    }
                }
            }
        }

        // Divisor
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFAB47BC).copy(alpha = 0.20f)))

        // Comandos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "COMANDOS", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp
            )
            L5_COMMANDS.chunked(3).forEach { cmdRow ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    cmdRow.forEach { cmd ->
                        L5CommandCard(
                            command = cmd,
                            enabled = hintsActive && !isExecuting,
                            onTap   = { onCommandTap(cmd) }
                        )
                    }
                }
            }
        }

        // Botones
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            L5ActionButton("↺  Reset", Color(0xFFFF5252),
                enabled = slots.any { it != null } && !isExecuting) { onReset() }
            L5ActionButton(
                text    = if (phase == L5Phase.DEBUGGING) "⚔  ¡Vencer!" else "▶  Ejecutar",
                color   = if (phase == L5Phase.DEBUGGING) Color(0xFFAB47BC) else Color(0xFF69FF47),
                enabled = canExecute && !isExecuting
            ) { onExecute() }
        }
    }
}

@Composable
private fun L5CommandSlot(
    stepNumber: Int,
    command: GameCommand?,
    isGlitched: Boolean,
    isHinted: Boolean,
    isDebugging: Boolean,
    onClear: () -> Unit
) {
    val hintAlpha  = remember { Animatable(0.25f) }
    val glitchAnim = remember { Animatable(0.5f) }

    LaunchedEffect(isHinted) {
        if (isHinted) { while (true) { hintAlpha.animateTo(1f, tween(480)); hintAlpha.animateTo(0.2f, tween(480)) } }
        else hintAlpha.snapTo(0.25f)
    }
    LaunchedEffect(isGlitched) {
        if (isGlitched) { while (true) { glitchAnim.animateTo(1f, tween(400)); glitchAnim.animateTo(0.4f, tween(400)) } }
        else glitchAnim.snapTo(0.5f)
    }

    val borderColor = when {
        isGlitched      -> Color(0xFFEF5350).copy(alpha = glitchAnim.value)
        command != null -> command.accentColor.copy(alpha = 0.9f)
        isHinted        -> Color(0xFFFFD600).copy(alpha = hintAlpha.value)
        else            -> Color.White.copy(alpha = 0.18f)
    }
    val bgBrush = when {
        isGlitched      -> Brush.radialGradient(listOf(Color(0xFFB71C1C).copy(alpha = 0.25f), Color(0xFF050A14)))
        command != null -> Brush.radialGradient(listOf(command.accentColor.copy(alpha = 0.18f), Color(0xFF050A14)))
        else            -> Brush.radialGradient(listOf(Color(0xFF0D1B2A), Color(0xFF050A14)))
    }

    Box(
        modifier = Modifier
            .size(72.dp, 52.dp)
            .drawBehind {
                if (isGlitched) {
                    drawRoundRect(
                        color = Color(0xFFEF5350).copy(alpha = glitchAnim.value * 0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (isGlitched && isDebugging)
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onPress = { tryAwaitRelease(); onClear() })
                    }
                else Modifier
            )
    ) {
        // Step number badge
        Text(
            "$stepNumber", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
            color = when {
                isGlitched -> Color(0xFFEF5350).copy(alpha = glitchAnim.value)
                isHinted   -> Color(0xFFFFD600).copy(alpha = hintAlpha.value)
                else       -> Color.White.copy(alpha = 0.35f)
            },
            modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp, top = 2.dp)
        )
        if (command != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    command.icon, fontSize = 18.sp,
                    color = if (isGlitched) Color(0xFFEF5350) else command.accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isGlitched) "¡ERROR!" else command.label,
                    fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                    color = if (isGlitched) Color(0xFFEF5350).copy(alpha = glitchAnim.value)
                            else command.accentColor.copy(alpha = 0.85f),
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
private fun L5CommandCard(command: GameCommand, enabled: Boolean, onTap: () -> Unit) {
    val pressAlpha = remember { Animatable(1f) }
    Box(
        modifier = Modifier
            .size(68.dp, 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(command.accentColor.copy(alpha = if (enabled) 0.20f else 0.06f), Color(0xFF050A14))
                )
            )
            .border(2.dp, command.accentColor.copy(alpha = if (enabled) 0.85f else 0.22f), RoundedCornerShape(12.dp))
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) detectTapGestures(onPress = {
                    pressAlpha.animateTo(0.6f, tween(80))
                    tryAwaitRelease()
                    pressAlpha.animateTo(1f, tween(120))
                    onTap()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(command.icon, fontSize = 22.sp,
                color = command.accentColor.copy(alpha = if (enabled) 1f else 0.32f), fontWeight = FontWeight.Bold)
            Text(command.label, fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                color = command.accentColor.copy(alpha = if (enabled) 0.85f else 0.28f),
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun L5ActionButton(text: String, color: Color, enabled: Boolean, onClick: () -> Unit) {
    val pressAlpha = remember { Animatable(1f) }
    Box(
        modifier = Modifier
            .width(116.dp).height(40.dp)
            .drawBehind {
                if (enabled) drawRoundRect(
                    color = color.copy(alpha = 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14f)
                )
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    if (enabled) listOf(color.copy(alpha = 0.18f), Color(0xFF050A14))
                    else listOf(Color(0xFF0A0F1F), Color(0xFF050A14))
                )
            )
            .border(2.dp, color.copy(alpha = if (enabled) 0.80f else 0.18f), RoundedCornerShape(14.dp))
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) detectTapGestures(onPress = {
                    pressAlpha.animateTo(0.6f, tween(80))
                    tryAwaitRelease()
                    pressAlpha.animateTo(1f, tween(120))
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily, color = color.copy(alpha = if (enabled) 1f else 0.28f))
    }
}
