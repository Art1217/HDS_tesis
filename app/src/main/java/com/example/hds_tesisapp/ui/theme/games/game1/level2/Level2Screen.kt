package com.example.hds_tesisapp.ui.theme.games.game1.level2

import androidx.compose.runtime.Composable
import com.example.hds_tesisapp.ui.theme.games.game1.Direction
import com.example.hds_tesisapp.ui.theme.games.game1.Game1LevelScreen
import com.example.hds_tesisapp.ui.theme.games.game1.GameCommand
import com.example.hds_tesisapp.ui.theme.games.game1.LevelConfig

// ────────────────────────────────────────────────────────────────────────────
// NIVEL 2 – "El Callejón Sin Salida"
//
// Grid 3×3   Bit (2,0) facing NORTH   Casa (1,2)   Obstáculo (0,0)
//
//   (0,0)⚡   (0,1)·    (0,2)·
//   (1,0)·    (1,1)·    (1,2)🏠
//   (2,0)🤖   (2,1)·    (2,2)·
//
// Solución: Avanzar↑ → GirDer → Avanzar → Avanzar  [4 pasos]
// ────────────────────────────────────────────────────────────────────────────

private val LEVEL2_CONFIG = LevelConfig(
    levelNumber = 2,
    levelTitle  = "El Callejón Sin Salida",
    gridRows    = 3,
    gridCols    = 3,
    startRow    = 2,
    startCol    = 0,
    startDir    = Direction.NORTH,
    houseRow    = 1,
    houseCol    = 2,
    pathCells   = setOf(Pair(2, 0), Pair(1, 0), Pair(1, 1), Pair(1, 2)),
    obstacleCells   = setOf(Pair(0, 0)),
    falseRouteCells = emptySet(),
    slotCount   = 4,
    availableCommands = listOf(
        GameCommand.AVANZAR,
        GameCommand.GIRAR_IZQUIERDA,
        GameCommand.GIRAR_DERECHA,
    ),
    tutorialMessage =
        "¡Bienvenido al Nivel 2!\n\n" +
        "El Bit ahora mira hacia arriba ↑.\n\n" +
        "Cuidado con el bloque de ERROR en la esquina superior izquierda. " +
        "¡Si el Bit llega ahí, chocará!\n\n" +
        "Piensa primero: ¿cuántos pasos hacia arriba necesita antes de girar?",
    helpMessage =
        "El Bit empieza mirando hacia arriba ↑.\n\n" +
        "Avanza una vez, luego gira a la derecha.\n" +
        "Después avanza dos veces más para llegar a la meta.",
    errorMessage =
        "¡El Bit se perdió o chocó con el bloque de ERROR!\n\n" +
        "Recuerda: hay un obstáculo en la esquina superior izquierda. " +
        "No subas recto desde el inicio más de una vez."
)

@Composable
fun Level2Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    Game1LevelScreen(config = LEVEL2_CONFIG, onLevelComplete = onLevelComplete, onNavigateToMenu = onNavigateToMenu)
}
