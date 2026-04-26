package com.example.hds_tesisapp.ui.theme.games.game1.level3

import androidx.compose.runtime.Composable
import com.example.hds_tesisapp.ui.theme.games.game1.Direction
import com.example.hds_tesisapp.ui.theme.games.game1.Game1LevelScreen
import com.example.hds_tesisapp.ui.theme.games.game1.GameCommand
import com.example.hds_tesisapp.ui.theme.games.game1.LevelConfig

// ────────────────────────────────────────────────────────────────────────────
// NIVEL 3 – "La Ruta Larga"
//
// Grid 3×4   Bit (2,0) facing EAST   Casa (0,2)
// Obstáculo: (1,1)   Ruta falsa: (2,3)
//
//   (0,0)·    (0,1)·    (0,2)🏠   (0,3)·
//   (1,0)·    (1,1)⚡   (1,2)·    (1,3)·
//   (2,0)🤖   (2,1)·    (2,2)·    (2,3)~
//
// Solución: Avanzar → Avanzar → GirIzq → Avanzar → Avanzar  [5 pasos]
// ────────────────────────────────────────────────────────────────────────────

private val LEVEL3_CONFIG = LevelConfig(
    levelNumber = 3,
    levelTitle  = "La Ruta Larga",
    gridRows    = 3,
    gridCols    = 4,
    startRow    = 2,
    startCol    = 0,
    startDir    = Direction.EAST,
    houseRow    = 0,
    houseCol    = 2,
    pathCells   = setOf(Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(1, 2), Pair(0, 2)),
    obstacleCells   = setOf(Pair(1, 1)),
    falseRouteCells = setOf(Pair(2, 3)),
    slotCount   = 5,
    availableCommands = listOf(
        GameCommand.AVANZAR,
        GameCommand.GIRAR_IZQUIERDA,
        GameCommand.GIRAR_DERECHA,
    ),
    tutorialMessage =
        "¡Nivel 3 – La Ruta Larga!\n\n" +
        "El Bit empieza mirando a la derecha →.\n\n" +
        "Un bloque de ERROR bloquea el camino directo hacia arriba. " +
        "También hay una celda al final de la fila que parece un camino... " +
        "¡pero es una trampa!\n\n" +
        "Busca el momento exacto para girar hacia arriba.",
    helpMessage =
        "El Bit mira a la derecha →.\n\n" +
        "Avanza dos veces por la fila inferior, luego gira a la izquierda " +
        "y avanza dos veces más para llegar a la meta.\n\n" +
        "¡No avances tres veces o caerás en la trampa!",
    errorMessage =
        "¡El Bit no llegó a la meta!\n\n" +
        "Si fue al extremo derecho, cayó en la ruta falsa. " +
        "Si subió muy pronto, se topó con el bloque ERROR.\n\n" +
        "Avanza exactamente dos veces antes de girar a la izquierda."
)

@Composable
fun Level3Screen(onLevelComplete: () -> Unit) {
    Game1LevelScreen(config = LEVEL3_CONFIG, onLevelComplete = onLevelComplete)
}
