package com.example.hds_tesisapp.ui.theme.games.game1

import androidx.compose.ui.graphics.Color

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnLeft(): Direction = when (this) {
        NORTH -> WEST
        EAST  -> NORTH
        SOUTH -> EAST
        WEST  -> SOUTH
    }

    fun turnRight(): Direction = when (this) {
        NORTH -> EAST
        EAST  -> SOUTH
        SOUTH -> WEST
        WEST  -> NORTH
    }

    fun move(row: Int, col: Int): Pair<Int, Int> = when (this) {
        NORTH -> Pair(row - 1, col)
        EAST  -> Pair(row, col + 1)
        SOUTH -> Pair(row + 1, col)
        WEST  -> Pair(row, col - 1)
    }

    fun moveBack(row: Int, col: Int): Pair<Int, Int> = when (this) {
        NORTH -> Pair(row + 1, col)
        EAST  -> Pair(row, col - 1)
        SOUTH -> Pair(row - 1, col)
        WEST  -> Pair(row, col + 1)
    }

    fun arrowDegrees(): Float = when (this) {
        NORTH ->   0f
        EAST  ->  90f
        SOUTH -> 180f
        WEST  -> 270f
    }
}

enum class GameCommand(
    val label: String,
    val icon: String,
    val accentColor: Color
) {
    AVANZAR         ("Avanzar",     "▲", Color(0xFF00E5FF)),
    GIRAR_IZQUIERDA ("Girar izq.",  "◄", Color(0xFFFFD600)),
    GIRAR_DERECHA   ("Girar der.",  "►", Color(0xFFFF7043)),
    RETROCEDER      ("Retroceder",  "▼", Color(0xFFAB47BC)),
}

data class LevelConfig(
    val levelNumber: Int,
    val levelTitle: String,
    val gridRows: Int,
    val gridCols: Int,
    val startRow: Int,
    val startCol: Int,
    val startDir: Direction,
    val houseRow: Int,
    val houseCol: Int,
    val pathCells: Set<Pair<Int, Int>>,
    val obstacleCells: Set<Pair<Int, Int>> = emptySet(),
    val falseRouteCells: Set<Pair<Int, Int>> = emptySet(),
    val slotCount: Int,
    val availableCommands: List<GameCommand>,
    val tutorialMessage: String,
    val helpMessage: String,
    val errorMessage: String,
)
