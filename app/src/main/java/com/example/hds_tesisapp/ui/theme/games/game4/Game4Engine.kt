package com.example.hds_tesisapp.ui.theme.games.game4

import androidx.compose.ui.graphics.Color

// ─── Símbolos del patrón ─────────────────────────────────────────────────────

data class PatternSymbol(
    val id: Int,
    val emoji: String,
    val label: String,
    val color: Color
)

// ─── Nivel 1: Flores AB ───────────────────────────────────────────────────────

val FLOWER_A = PatternSymbol(0, "🌸", "Roja",    Color(0xFFE91E63))
val FLOWER_B = PatternSymbol(1, "🌼", "Amarilla", Color(0xFFFFD600))

// Generates an AB round: sequence length 7, last position = ?
data class ABRound(
    val sequence: List<PatternSymbol>,   // 7 items, last is the hidden answer
    val answer: PatternSymbol,
    val options: List<PatternSymbol>     // [A, B]
)

fun buildABRound(roundIndex: Int): ABRound {
    // Alternate starting with A or B depending on round
    val startWithA = roundIndex % 2 == 0
    val sequence = (0..6).map { i ->
        if ((i % 2 == 0) == startWithA) FLOWER_A else FLOWER_B
    }
    val answer = sequence.last()
    return ABRound(
        sequence = sequence.dropLast(1),   // show 6, hide last
        answer   = answer,
        options  = listOf(FLOWER_A, FLOWER_B)
    )
}

// ─── Nivel 2: Luciérnagas ABC ─────────────────────────────────────────────────

val FIREFLY_A = PatternSymbol(0, "🔵", "Azul",    Color(0xFF40C4FF))
val FIREFLY_B = PatternSymbol(1, "🟢", "Verde",   Color(0xFF69FF47))
val FIREFLY_C = PatternSymbol(2, "🟡", "Amarillo", Color(0xFFFFD600))

data class ABCRound(
    val sequence: List<PatternSymbol>,
    val answer: PatternSymbol,
    val options: List<PatternSymbol>
)

// Cycles: A B C A B C...  length 6, last hidden
fun buildABCRound(roundIndex: Int): ABCRound {
    val symbols = listOf(FIREFLY_A, FIREFLY_B, FIREFLY_C)
    val offset  = roundIndex % 3
    val sequence = (0..5).map { i -> symbols[(i + offset) % 3] }
    val answer   = sequence.last()
    return ABCRound(
        sequence = sequence.dropLast(1),
        answer   = answer,
        options  = symbols.shuffled()
    )
}

// ─── Nivel 3: Piedras AAB / ABB ───────────────────────────────────────────────

val STONE_A = PatternSymbol(0, "🔴", "Roja",    Color(0xFFEF5350))
val STONE_B = PatternSymbol(1, "🔵", "Azul",    Color(0xFF42A5F5))
val STONE_C = PatternSymbol(2, "🟠", "Naranja", Color(0xFFFF7043))

enum class GroupPattern(val cycle: List<Int>) {
    AAB(listOf(0, 0, 1)),   // A A B
    ABB(listOf(0, 1, 1)),   // A B B
    AABB(listOf(0, 0, 1, 1)) // A A B B
}

data class StoneRound(
    val sequence: List<PatternSymbol>,
    val answer: PatternSymbol,
    val options: List<PatternSymbol>,
    val patternLabel: String
)

fun buildStoneRound(roundIndex: Int): StoneRound {
    val pattern = when (roundIndex) {
        0, 1 -> GroupPattern.AAB
        2, 3 -> GroupPattern.ABB
        else  -> GroupPattern.AABB
    }
    val symbols = listOf(STONE_A, STONE_B)
    // Build 8-item sequence using the cycle
    val full = (0..7).map { i -> symbols[pattern.cycle[i % pattern.cycle.size]] }
    val answer = full.last()
    return StoneRound(
        sequence     = full.dropLast(1),
        answer       = answer,
        options      = listOf(STONE_A, STONE_B, STONE_C).shuffled(),
        patternLabel = pattern.name
    )
}

// ─── Nivel 4: Puente con distractor ──────────────────────────────────────────

val BRIDGE_A = PatternSymbol(0, "🟢", "Verde",   Color(0xFF4CAF50))
val BRIDGE_B = PatternSymbol(1, "🔵", "Azul",    Color(0xFF2196F3))
val BRIDGE_C = PatternSymbol(2, "🟣", "Morado",  Color(0xFF9C27B0))
val BRIDGE_D = PatternSymbol(3, "🔴", "Rojo",    Color(0xFFF44336)) // distractor

data class BridgeRound(
    val sequence: List<PatternSymbol>,
    val answer: PatternSymbol,
    val options: List<PatternSymbol>   // correct + 1 wrong + distractor
)

fun buildBridgeRound(roundIndex: Int): BridgeRound {
    val patterns = listOf(
        listOf(BRIDGE_A, BRIDGE_B, BRIDGE_B),   // ABB
        listOf(BRIDGE_A, BRIDGE_B, BRIDGE_C),   // ABC
        listOf(BRIDGE_A, BRIDGE_A, BRIDGE_B),   // AAB
        listOf(BRIDGE_A, BRIDGE_B, BRIDGE_A),   // ABA
        listOf(BRIDGE_B, BRIDGE_C, BRIDGE_B),   // BCB
    )
    val cycle  = patterns[roundIndex % patterns.size]
    val full   = (0..6).map { i -> cycle[i % cycle.size] }
    val answer = full.last()
    // Wrong option = a cycle symbol that is NOT the answer
    val wrong  = cycle.first { it.id != answer.id }
    return BridgeRound(
        sequence = full.dropLast(1),
        answer   = answer,
        options  = listOf(answer, wrong, BRIDGE_D).shuffled()
    )
}

// ─── Nivel 5: Mini Jefe — encontrar el error ─────────────────────────────────

data class GlitchRound(
    val sequence: List<PatternSymbol>,
    val errorIndex: Int,                // which position has the wrong symbol
    val correctSymbol: PatternSymbol    // what should be there
)

val GLITCH_SYMBOLS = listOf(
    PatternSymbol(0, "🔴", "Rojo",    Color(0xFFEF5350)),
    PatternSymbol(1, "🔵", "Azul",    Color(0xFF42A5F5)),
    PatternSymbol(2, "🟢", "Verde",   Color(0xFF66BB6A)),
    PatternSymbol(3, "🟡", "Amarillo",Color(0xFFFFD600)),
)

fun buildGlitchRound(roundIndex: Int): GlitchRound {
    // Patterns of varying complexity
    val cycles = listOf(
        listOf(0, 1),          // AB  (round 0)
        listOf(0, 1, 2),       // ABC (round 1)
        listOf(0, 0, 1),       // AAB (round 2)
        listOf(0, 1, 1),       // ABB (round 3)
        listOf(0, 1, 2, 0),    // ABCA (round 4)
        listOf(0, 0, 1, 2),    // AABC (round 5)
    )
    val cycle  = cycles[roundIndex.coerceAtMost(cycles.lastIndex)]
    val seqLen = 7 + (roundIndex / 2)   // grows: 7, 7, 8, 8, 9, 9
    // Build correct sequence
    val correct = (0 until seqLen).map { i -> GLITCH_SYMBOLS[cycle[i % cycle.size]] }
    // Insert error at a random mid position (not first or last)
    val errorIdx = (2 until seqLen - 1).random()
    val wrongSym = GLITCH_SYMBOLS.filter { it.id != correct[errorIdx].id }.random()
    val corrupted = correct.toMutableList().also { it[errorIdx] = wrongSym }
    return GlitchRound(
        sequence      = corrupted,
        errorIndex    = errorIdx,
        correctSymbol = correct[errorIdx]
    )
}
