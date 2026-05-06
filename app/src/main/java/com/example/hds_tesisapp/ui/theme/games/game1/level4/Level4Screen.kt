package com.example.hds_tesisapp.ui.theme.games.game1.level4

import androidx.compose.runtime.Composable
import com.example.hds_tesisapp.ui.theme.games.game1.Direction
import com.example.hds_tesisapp.ui.theme.games.game1.Game1LevelScreen
import com.example.hds_tesisapp.ui.theme.games.game1.GameCommand
import com.example.hds_tesisapp.ui.theme.games.game1.LevelConfig

// ────────────────────────────────────────────────────────────────────────────
// NIVEL 4 – "El Laberinto de Glitch"
//
// Grid 3×4   Bit (2,0) facing EAST   Casa (0,3)
// Obstáculos: (1,1), (1,2)   Rutas falsas: (0,0), (0,1)
//
//   (0,0)~    (0,1)~    (0,2)·    (0,3)🏠
//   (1,0)·    (1,1)⚡   (1,2)⚡   (1,3)·
//   (2,0)🤖   (2,1)·    (2,2)·    (2,3)·
//
// Solución: Avanzar×3 → GirIzq → Avanzar×2  [6 pasos]
// Retroceder es un distractor — no se necesita.
// ────────────────────────────────────────────────────────────────────────────

private val LEVEL4_CONFIG = LevelConfig(
    levelNumber = 4,
    levelTitle  = "El Laberinto de Glitch",
    gridRows    = 3,
    gridCols    = 4,
    startRow    = 2,
    startCol    = 0,
    startDir    = Direction.EAST,
    houseRow    = 0,
    houseCol    = 3,
    pathCells   = setOf(Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(2, 3), Pair(1, 3), Pair(0, 3)),
    obstacleCells   = setOf(Pair(1, 1), Pair(1, 2)),
    falseRouteCells = setOf(Pair(0, 0), Pair(0, 1)),
    slotCount   = 6,
    availableCommands = listOf(
        GameCommand.AVANZAR,
        GameCommand.GIRAR_IZQUIERDA,
        GameCommand.GIRAR_DERECHA,
        GameCommand.RETROCEDER,
    ),
    tutorialMessage =
        "¡Nivel 4 – El Laberinto de Glitch!\n\n" +
        "Glitch puso dos bloques de ERROR en el medio y dos rutas falsas arriba.\n\n" +
        "Tienes un comando nuevo: Retroceder. Pero ojo... " +
        "¡esta vez no lo necesitarás! Es una trampa para confundirte.\n\n" +
        "Seis pasos. Rodea los obstáculos por la fila inferior.",
    helpMessage =
        "El Bit mira a la derecha →.\n\n" +
        "Los dos bloques ERROR te obligan a ir por la fila inferior completa. " +
        "Avanza tres veces, gira a la izquierda y avanza dos veces más.\n\n" +
        "Retroceder no es necesario en este nivel.",
    errorMessage =
        "¡El Bit se perdió en el laberinto!\n\n" +
        "Recuerda: los bloques ERROR bloquean el centro. " +
        "Ve por la fila inferior hasta el final, luego sube. ¡Tú puedes!"
)

@Composable
fun Level4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit = {}) {
    Game1LevelScreen(config = LEVEL4_CONFIG, onLevelComplete = onLevelComplete, onNavigateToMenu = onNavigateToMenu)
}
