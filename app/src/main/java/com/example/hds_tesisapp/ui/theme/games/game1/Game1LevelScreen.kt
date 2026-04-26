package com.example.hds_tesisapp.ui.theme.games.game1

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class G1Phase {
    TUTORIAL_DIALOG, PLAYING, EXECUTING, VICTORY, ERROR_DIALOG
}

private fun Context.findG1Activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findG1Activity()
    else -> null
}

@Composable
fun Game1LevelScreen(config: LevelConfig, onLevelComplete: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findG1Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()
    var phase  by remember { mutableStateOf(G1Phase.TUTORIAL_DIALOG) }

    var bitRow by remember { mutableStateOf(config.startRow) }
    var bitCol by remember { mutableStateOf(config.startCol) }
    var bitDir by remember { mutableStateOf(config.startDir) }

    val slots = remember {
        mutableStateListOf<GameCommand?>().also { list ->
            repeat(config.slotCount) { list.add(null) }
        }
    }

    val hintSlotIndex = slots.indexOfFirst { it == null }
    val hintsActive   = phase == G1Phase.PLAYING
    val canExecute    = slots.none { it == null }
    val isExecuting   = phase == G1Phase.EXECUTING

    fun resetBit() {
        bitRow = config.startRow; bitCol = config.startCol; bitDir = config.startDir
    }
    fun resetSlots() { for (i in slots.indices) slots[i] = null }
    fun addCommand(cmd: GameCommand) {
        val idx = slots.indexOfFirst { it == null }
        if (idx != -1) slots[idx] = cmd
    }

    fun executeProgram() {
        scope.launch {
            phase = G1Phase.EXECUTING
            var row = config.startRow
            var col = config.startCol
            var dir = config.startDir
            var hitError = false

            for (cmd in slots) {
                when (cmd) {
                    GameCommand.AVANZAR -> {
                        val (r, c) = dir.move(row, col)
                        if (r !in 0 until config.gridRows || c !in 0 until config.gridCols
                            || Pair(r, c) in config.obstacleCells) {
                            hitError = true; break
                        }
                        row = r; col = c
                        bitRow = row; bitCol = col
                        delay(750)
                    }
                    GameCommand.RETROCEDER -> {
                        val (r, c) = dir.moveBack(row, col)
                        if (r !in 0 until config.gridRows || c !in 0 until config.gridCols
                            || Pair(r, c) in config.obstacleCells) {
                            hitError = true; break
                        }
                        row = r; col = c
                        bitRow = row; bitCol = col
                        delay(750)
                    }
                    GameCommand.GIRAR_IZQUIERDA -> {
                        dir = dir.turnLeft(); bitDir = dir; delay(450)
                    }
                    GameCommand.GIRAR_DERECHA -> {
                        dir = dir.turnRight(); bitDir = dir; delay(450)
                    }
                    null -> Unit
                }
            }

            delay(300)
            phase = if (!hitError && row == config.houseRow && col == config.houseCol)
                G1Phase.VICTORY else G1Phase.ERROR_DIALOG
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001020), Color(0xFF000A1A))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: board
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "NIVEL ${config.levelNumber}  ·  ${config.levelTitle.uppercase()}",
                    fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = Color(0xFF00E5FF).copy(alpha = 0.65f), letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF030810))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Game1Board(config = config, bitRow = bitRow, bitCol = bitCol, bitDir = bitDir)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    when (phase) {
                        G1Phase.TUTORIAL_DIALOG -> "Lee el mensaje de Atom"
                        G1Phase.PLAYING         -> "Crea el programa y pulsa Ejecutar"
                        G1Phase.EXECUTING       -> "Ejecutando programa..."
                        G1Phase.VICTORY         -> "¡El Bit llegó a la meta!"
                        G1Phase.ERROR_DIALOG    -> "El Bit se perdió..."
                    },
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = when (phase) {
                        G1Phase.VICTORY      -> Color(0xFF69FF47)
                        G1Phase.ERROR_DIALOG -> Color(0xFFFF5252)
                        G1Phase.EXECUTING    -> Color(0xFFFFD600)
                        else                 -> Color.White.copy(alpha = 0.55f)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            // Divider
            Box(
                modifier = Modifier.width(1.dp).fillMaxHeight(0.75f)
                    .background(Color(0xFF00E5FF).copy(alpha = 0.18f))
            )

            // Right: commands
            Game1CommandPanel(
                config        = config,
                slots         = slots,
                hintSlotIndex = hintSlotIndex,
                hintsActive   = hintsActive,
                canExecute    = canExecute,
                isExecuting   = isExecuting,
                onCommandTap  = { addCommand(it) },
                onReset       = { resetSlots(); resetBit() },
                onExecute     = { executeProgram() },
                modifier      = Modifier.weight(1.1f).fillMaxHeight().padding(start = 12.dp)
            )
        }

        // Help button
        if (phase == G1Phase.PLAYING) {
            G1HelpButton(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp).zIndex(5f)
            ) { phase = G1Phase.TUTORIAL_DIALOG }
        }

        // Overlays
        when (phase) {
            G1Phase.TUTORIAL_DIALOG -> G1AtomDialog(config.tutorialMessage) {
                phase = G1Phase.PLAYING
            }
            G1Phase.ERROR_DIALOG -> G1AtomDialog(config.errorMessage) {
                resetBit(); resetSlots(); phase = G1Phase.PLAYING
            }
            G1Phase.VICTORY -> G1VictoryOverlay(config.levelNumber, onNext = onLevelComplete)
            else -> Unit
        }
    }
}
