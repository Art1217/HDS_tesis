package com.example.hds_tesisapp.ui.theme.games.game1.level1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game1.G1MenuButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// ============================================================
//  Fases del juego
// ============================================================
private enum class GamePhase {
    TUTORIAL_DIALOG,   // Atom explica el nivel al inicio
    PLAYING,           // El jugador coloca comandos
    EXECUTING,         // El Bit está animándose
    VICTORY,           // Nivel completado
    ERROR_DIALOG       // El Bit no llegó a casa — Atom da pista
}


private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


// ============================================================
//  Nivel 1: "El Camino a Casa"
// ============================================================
@Composable
fun Level1Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()

    // ── Estado del juego ──
    var phase by remember { mutableStateOf(GamePhase.TUTORIAL_DIALOG) }

    // ── Estado del Bit en la grilla ──
    var bitRow by remember { mutableStateOf(LEVEL1_START_CELL.first) }
    var bitCol by remember { mutableStateOf(LEVEL1_START_CELL.second) }
    var bitDir by remember { mutableStateOf(Direction.EAST) }

    // ── Slots de comandos (3 pasos) ──
    val slots = remember { mutableStateListOf<GameCommand?>(null, null, null) }

    // Slot sugerido: el primer slot vacío
    val hintSlotIndex = slots.indexOfFirst { it == null }
    val hintsActive   = phase == GamePhase.PLAYING
    val canExecute    = slots.none { it == null }
    val isExecuting   = phase == GamePhase.EXECUTING


    // ── Helpers ──
    fun resetBit() {
        bitRow = LEVEL1_START_CELL.first
        bitCol = LEVEL1_START_CELL.second
        bitDir = Direction.EAST
    }

    fun resetSlots() { for (i in slots.indices) slots[i] = null }

    fun addCommand(cmd: GameCommand) {
        val idx = slots.indexOfFirst { it == null }
        if (idx != -1) slots[idx] = cmd
    }

    fun executeProgram() {
        scope.launch {
            phase = GamePhase.EXECUTING
            var row = LEVEL1_START_CELL.first
            var col = LEVEL1_START_CELL.second
            var dir = Direction.EAST

            for (cmd in slots) {
                when (cmd) {
                    GameCommand.AVANZAR -> {
                        val (r, c) = dir.move(row, col)
                        row = r; col = c
                        bitRow = row; bitCol = col
                        delay(750)
                    }
                    GameCommand.GIRAR_IZQUIERDA -> {
                        dir = dir.turnLeft()
                        bitDir = dir
                        delay(450)
                    }
                    GameCommand.GIRAR_DERECHA -> {
                        dir = dir.turnRight()
                        bitDir = dir
                        delay(450)
                    }
                    null -> Unit
                }
            }

            delay(300)

            // El Bit llegó a la casa si terminó en (0,1)
            if (row == LEVEL1_HOUSE_CELL.first && col == LEVEL1_HOUSE_CELL.second) {
                phase = GamePhase.VICTORY
            } else {
                phase = GamePhase.ERROR_DIALOG
            }
        }
    }

    // ── Mensajes de Atom según la fase ──
    val tutorialMessage =
        "¡Hola! Soy Atom.\n\n" +
        "Mira a ese Bit... está dando vueltas en círculos porque Glitch " +
        "desordenó su lógica. Su casa está muy cerca, pero no sabe cómo llegar.\n\n" +
        "¡Tú puedes ayudarlo! Dale una secuencia de pasos en orden " +
        "y el Bit los seguirá exactamente.\n\n" +
        "Eso es un algoritmo: hacer las cosas paso a paso."

    val helpMessage =
        "El Bit empieza mirando hacia la derecha →\n\n" +
        "Recuerda: primero muévelo hacia adelante, luego hazlo girar " +
        "hacia la izquierda, y avanza una vez más hacia la casa."

    val errorMessage =
        "¡Ups! El Bit no llegó a casa.\n\n" +
        "Observa bien hacia dónde apunta la flecha del Bit y hacia " +
        "dónde está la casa. ¡Tú puedes lograrlo!"


    // ════════════════════════════════════════════════════════
    //  UI PRINCIPAL
    // ════════════════════════════════════════════════════════
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001020), Color(0xFF000A1A)))
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ════════════════════════
            //  IZQUIERDA: Tablero
            // ════════════════════════
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Etiqueta del nivel
                Text(
                    text = "NIVEL 1  ·  EL CAMINO A CASA",
                    fontSize = 11.sp,
                    fontFamily = OrbitronFontFamily,
                    color = Color(0xFF00E5FF).copy(alpha = 0.65f),
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Tablero con el Bit
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF030810))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Level1GameBoard(
                        bitRow = bitRow,
                        bitCol = bitCol,
                        bitDirection = bitDir
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Leyenda de estado
                Text(
                    text = when (phase) {
                        GamePhase.TUTORIAL_DIALOG -> "Lee el mensaje de Atom"
                        GamePhase.PLAYING         -> "Crea el programa y presiona Ejecutar"
                        GamePhase.EXECUTING       -> "El Bit está ejecutando tu programa..."
                        GamePhase.VICTORY         -> "¡El Bit llegó a casa!"
                        GamePhase.ERROR_DIALOG    -> "El Bit se perdió..."
                    },
                    fontSize = 11.sp,
                    fontFamily = Baloo2FontFamily,
                    color = when (phase) {
                        GamePhase.VICTORY      -> Color(0xFF69FF47)
                        GamePhase.ERROR_DIALOG -> Color(0xFFFF5252)
                        GamePhase.EXECUTING    -> Color(0xFFFFD600)
                        else                   -> Color.White.copy(alpha = 0.55f)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            // Divisor
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.75f)
                    .background(Color(0xFF00E5FF).copy(alpha = 0.18f))
            )

            // ════════════════════════
            //  DERECHA: Comandos
            // ════════════════════════
            Level1CommandPanel(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp),
                slots          = slots,
                hintSlotIndex  = hintSlotIndex,
                hintsActive    = hintsActive,
                canExecute     = canExecute,
                isExecuting    = isExecuting,
                onCommandTap   = { addCommand(it) },
                onReset        = { resetSlots(); resetBit() },
                onExecute      = { executeProgram() }
            )
        }

        // ── Botón de ayuda ──
        if (phase == GamePhase.PLAYING) {
            Level1HelpButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .zIndex(5f)
            ) { phase = GamePhase.TUTORIAL_DIALOG }
        }

        // ── Botón menú ──
        G1MenuButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).zIndex(5f),
            onClick  = onNavigateToMenu
        )

        // ── Overlays (dialogs + victoria) ──
        when (phase) {
            GamePhase.TUTORIAL_DIALOG -> Level1AtomDialog(
                message   = tutorialMessage,
                onDismiss = { phase = GamePhase.PLAYING }
            )
            GamePhase.ERROR_DIALOG -> Level1AtomDialog(
                message   = errorMessage,
                onDismiss = {
                    resetBit()
                    resetSlots()
                    phase = GamePhase.PLAYING
                }
            )
            GamePhase.VICTORY -> Level1VictoryOverlay(onNext = onLevelComplete)
            else -> Unit
        }
    }
}
