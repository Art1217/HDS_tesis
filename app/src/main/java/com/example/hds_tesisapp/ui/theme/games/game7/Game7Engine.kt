package com.example.hds_tesisapp.ui.theme.games.game7

// ─── Operación ────────────────────────────────────────────────────────────────

data class LabOp(
    val label: String,
    val maxUses: Int,          // -1 = ilimitado (nivel 5)
    val apply: (Int) -> Int
)

// ─── Puzzle (Niveles 1-4) ─────────────────────────────────────────────────────

data class LabPuzzle(
    val initial: Int,
    val target: Int,
    val ops: List<LabOp>,
    val maxOps: Int
)

// ─── Nivel 1: puerta simple — actual < objetivo ───────────────────────────────

private val PUZZLES_L1 = listOf(
    LabPuzzle(5, 12, listOf(LabOp("+1", 3) { it + 1 }, LabOp("×2", 1) { it * 2 }, LabOp("−1", 2) { it - 1 }), 3),
    LabPuzzle(3, 8,  listOf(LabOp("+1", 3) { it + 1 }, LabOp("×2", 1) { it * 2 }, LabOp("−1", 2) { it - 1 }), 3),
    LabPuzzle(7, 3,  listOf(LabOp("−1", 3) { it - 1 }, LabOp("÷2", 1) { it / 2 }, LabOp("+1", 2) { it + 1 }), 3),
    LabPuzzle(4, 9,  listOf(LabOp("+1", 3) { it + 1 }, LabOp("×2", 1) { it * 2 }, LabOp("−1", 2) { it - 1 }), 3)
)

fun buildL1G7Puzzle(i: Int): LabPuzzle = PUZZLES_L1[i % PUZZLES_L1.size]
val TOTAL_ROUNDS_L1 = PUZZLES_L1.size

// ─── Nivel 2: puerta sobrecargada — actual > objetivo ─────────────────────────

private val PUZZLES_L2 = listOf(
    LabPuzzle(23, 7,  listOf(LabOp("−1", 3) { it - 1 }, LabOp("÷3", 1) { it / 3 }, LabOp("+1", 2) { it + 1 }), 3),
    LabPuzzle(16, 5,  listOf(LabOp("−1", 3) { it - 1 }, LabOp("÷3", 1) { it / 3 }, LabOp("+1", 2) { it + 1 }), 3),
    LabPuzzle(25, 6,  listOf(LabOp("−1", 3) { it - 1 }, LabOp("÷4", 1) { it / 4 }, LabOp("+1", 2) { it + 1 }), 3),
    LabPuzzle(17, 5,  listOf(LabOp("−2", 3) { it - 2 }, LabOp("÷3", 1) { it / 3 }, LabOp("+1", 2) { it + 1 }), 3)
)

fun buildL2G7Puzzle(i: Int): LabPuzzle = PUZZLES_L2[i % PUZZLES_L2.size]
val TOTAL_ROUNDS_L2 = PUZZLES_L2.size

// ─── Nivel 3: frasco verde — cadenas largas, max 4 ops ───────────────────────

private val PUZZLES_L3 = listOf(
    LabPuzzle(13, 67, listOf(LabOp("×3", 1) { it * 3 }, LabOp("×2", 3) { it * 2 }, LabOp("+1", 5) { it + 1 }, LabOp("−2", 4) { it - 2 }), 4),
    LabPuzzle(4,  25, listOf(LabOp("×3", 1) { it * 3 }, LabOp("×2", 2) { it * 2 }, LabOp("+1", 5) { it + 1 }, LabOp("−1", 3) { it - 1 }), 4),
    LabPuzzle(2,  15, listOf(LabOp("×3", 1) { it * 3 }, LabOp("×2", 3) { it * 2 }, LabOp("+1", 5) { it + 1 }, LabOp("−1", 3) { it - 1 }), 4)
)

fun buildL3G7Puzzle(i: Int): LabPuzzle = PUZZLES_L3[i % PUZZLES_L3.size]
val TOTAL_ROUNDS_L3 = PUZZLES_L3.size

// ─── Nivel 4: frasco morado — reducción desde valores grandes ─────────────────

private val PUZZLES_L4 = listOf(
    LabPuzzle(50,  11, listOf(LabOp("−2", 3) { it - 2 }, LabOp("÷4", 1) { it / 4 }, LabOp("÷2", 2) { it / 2 }, LabOp("−1", 3) { it - 1 }), 3),
    LabPuzzle(36,  7,  listOf(LabOp("÷4", 1) { it / 4 }, LabOp("−1", 3) { it - 1 }, LabOp("−2", 3) { it - 2 }, LabOp("÷2", 2) { it / 2 }), 3),
    LabPuzzle(100, 11, listOf(LabOp("÷5", 1) { it / 5 }, LabOp("÷2", 2) { it / 2 }, LabOp("−1", 3) { it - 1 }, LabOp("+1", 2) { it + 1 }), 3)
)

fun buildL4G7Puzzle(i: Int): LabPuzzle = PUZZLES_L4[i % PUZZLES_L4.size]
val TOTAL_ROUNDS_L4 = PUZZLES_L4.size

// ─── Nivel 5: Sala Central — fases con timer ──────────────────────────────────

data class L5Phase(
    val initial: Int,
    val target: Int,
    val baseOps: List<L5Op>   // sin límite de usos
)

data class L5Op(
    val label: String,
    val apply: (Int) -> Int
)

val L5_PHASES = listOf(
    L5Phase(8,  30, listOf(L5Op("+2") { it + 2 }, L5Op("×3") { it * 3 }, L5Op("−1") { it - 1 })),
    L5Phase(5,  21, listOf(L5Op("×4") { it * 4 }, L5Op("+1") { it + 1 }, L5Op("−2") { it - 2 })),
    L5Phase(12, 50, listOf(L5Op("×4") { it * 4 }, L5Op("+2") { it + 2 }, L5Op("−1") { it - 1 }))
)

// Opciones alternativas que pueden aparecer al cambiar cada 3s
val L5_ALT_OPS = listOf(
    L5Op("+3") { it + 3 }, L5Op("+1") { it + 1 }, L5Op("×2") { it * 2 },
    L5Op("−2") { it - 2 }, L5Op("÷2") { it / 2 }, L5Op("×3") { it * 3 },
    L5Op("+2") { it + 2 }, L5Op("−3") { it - 3 }, L5Op("÷3") { it / 3 }
)

// Variaciones del objetivo en fase 3 (cambia cada 5s, dentro de rango razonable)
val L5_PHASE3_TARGETS = listOf(50, 48, 52, 50, 46, 54, 50)
