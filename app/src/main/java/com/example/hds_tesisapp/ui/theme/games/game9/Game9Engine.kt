package com.example.hds_tesisapp.ui.theme.games.game9

// ─── Symbol sets (different per level) ───────────────────────────────────────

val G9_SYMBOLS_A = listOf("🔴", "🔵", "🟢", "🟡", "🟣")          // L1 — colored dots
val G9_SYMBOLS_B = listOf("⭐", "💎", "🔷", "🔶", "🔺")          // L2 — gems / shapes
val G9_SYMBOLS_C = listOf("🌿", "🌊", "⛰️", "☀️", "❄️")         // L3 — nature
val G9_SYMBOLS_D = G9_SYMBOLS_A + G9_SYMBOLS_B + G9_SYMBOLS_C     // L4/L5 — full mix

// ─── Data ─────────────────────────────────────────────────────────────────────

data class G9Row(
    val id: Int,
    val symbols: List<String>,      // 5 symbols: 4 × correctSymbol + 1 wrongSymbol
    val correctSymbol: String,      // the pattern (majority)
    val wrongIndex: Int,            // position of the wrong symbol (0–4)
    val isFixed: Boolean = false
)

data class G9LevelConfig(
    val symbolSet: List<String>,
    val rowCount: Int = 5,
    val isBlurry: Boolean = false,
    val isScrolling: Boolean = false,
    val hasModal: Boolean = false,
    val hasMiniJefe: Boolean = false,
    val timerMs: Long = 0L,            // 0 = no timer
    val scrollSpeedMs: Long = 4000L,   // ms to traverse full row width
    val bossSwapIntervalMs: Long = 9_000L
)

val G9_LEVEL_CONFIGS = listOf(
    // L1: basic, timer 120 s
    G9LevelConfig(symbolSet = G9_SYMBOLS_A, timerMs = 120_000L),
    // L2: blurry symbols, timer 90 s
    G9LevelConfig(symbolSet = G9_SYMBOLS_B, isBlurry = true, timerMs = 90_000L),
    // L3: blurry + scrolling, timer 90 s
    G9LevelConfig(symbolSet = G9_SYMBOLS_C, isBlurry = true, isScrolling = true, timerMs = 90_000L),
    // L4: blurry + scrolling + modal confirmation
    G9LevelConfig(symbolSet = G9_SYMBOLS_D, isBlurry = true, isScrolling = true, hasModal = true),
    // L5: same as L4 + mini-boss that swaps rows
    G9LevelConfig(
        symbolSet = G9_SYMBOLS_D, isBlurry = true, isScrolling = true,
        hasModal = true, hasMiniJefe = true, bossSwapIntervalMs = 9_000L
    )
)

// ─── Generators ───────────────────────────────────────────────────────────────

fun generateG9Row(id: Int, symbolSet: List<String>, rng: java.util.Random): G9Row {
    val main = symbolSet[rng.nextInt(symbolSet.size)]
    var wrong = symbolSet[rng.nextInt(symbolSet.size)]
    while (wrong == main) wrong = symbolSet[rng.nextInt(symbolSet.size)]
    val wrongIdx = rng.nextInt(5)
    val syms = List(5) { i -> if (i == wrongIdx) wrong else main }
    return G9Row(id = id, symbols = syms, correctSymbol = main, wrongIndex = wrongIdx)
}

fun generateG9Rows(config: G9LevelConfig, seed: Long = System.currentTimeMillis()): List<G9Row> {
    val rng = java.util.Random(seed)
    return List(config.rowCount) { i -> generateG9Row(i, config.symbolSet, rng) }
}

// Returns [correctSymbol, distractor1, distractor2] shuffled
fun generateModalOptions(row: G9Row, symbolSet: List<String>, rng: java.util.Random): List<String> {
    val pool = symbolSet.filter { it != row.correctSymbol }.toMutableList()
    pool.shuffle(rng)
    return (listOf(row.correctSymbol) + pool.take(2)).shuffled(rng)
}
