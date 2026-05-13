package com.example.hds_tesisapp.ui.theme.games.game5

import androidx.compose.ui.graphics.Color

// ─── Portal ──────────────────────────────────────────────────────────────────

data class Portal(
    val id: Int,
    val name: String,
    val color: Color,
    val emoji: String
)

val PORTAL_BLUE   = Portal(0, "Azul",   Color(0xFF2196F3), "🔵")
val PORTAL_RED    = Portal(1, "Rojo",   Color(0xFFF44336), "🔴")
val PORTAL_GREEN  = Portal(2, "Verde",  Color(0xFF4CAF50), "🟢")
val PORTAL_PURPLE = Portal(3, "Morado", Color(0xFF9C27B0), "🟣")

// ─── Ronda genérica (niveles 1-4) ────────────────────────────────────────────

data class PortalRound(
    val number: Int,
    val conditionLines: List<String>,
    val portals: List<Portal>,
    val logicalPortal: Portal,   // el que cumple la condición (roto → NO tocar)
    val correctTap: Portal       // el que el jugador DEBE tocar (invertido)
)

// ─── Nivel 1: IF simple — "mayor que 5 → Azul" ───────────────────────────────

private val NUMS_L1 = listOf(8, 3, 7, 2, 9)

fun buildLevel1Round(roundIndex: Int): PortalRound {
    val n = NUMS_L1[roundIndex % NUMS_L1.size]
    val logical = if (n > 5) PORTAL_BLUE else PORTAL_RED
    val correct = if (logical == PORTAL_BLUE) PORTAL_RED else PORTAL_BLUE
    return PortalRound(
        number         = n,
        conditionLines = listOf("Si el número es mayor que 5", "→ Portal Azul", "Si no → Portal Rojo"),
        portals        = listOf(PORTAL_BLUE, PORTAL_RED),
        logicalPortal  = logical,
        correctTap     = correct
    )
}

// ─── Nivel 2: IF/ELSE — par/impar ────────────────────────────────────────────

private val NUMS_L2 = listOf(7, 4, 9, 6, 3)

fun buildLevel2Round(roundIndex: Int): PortalRound {
    val n = NUMS_L2[roundIndex % NUMS_L2.size]
    val isPar = n % 2 == 0
    val logical = if (isPar) PORTAL_GREEN else PORTAL_RED
    val correct = if (isPar) PORTAL_RED else PORTAL_GREEN
    return PortalRound(
        number         = n,
        conditionLines = listOf("Si el número es par → Portal Verde", "Si no → Portal Rojo"),
        portals        = listOf(PORTAL_GREEN, PORTAL_RED),
        logicalPortal  = logical,
        correctTap     = correct
    )
}

// ─── Nivel 3: AND — (>5) Y (par) ─────────────────────────────────────────────

private val NUMS_L3 = listOf(8, 7, 4, 9, 6)

fun buildLevel3Round(roundIndex: Int): PortalRound {
    val n = NUMS_L3[roundIndex % NUMS_L3.size]
    val bothTrue = n > 5 && n % 2 == 0
    val logical = if (bothTrue) PORTAL_BLUE else PORTAL_RED
    val correct = if (bothTrue) PORTAL_RED else PORTAL_BLUE
    return PortalRound(
        number         = n,
        conditionLines = listOf(
            "Si el número es mayor que 5",
            "Y el número es par → Portal Azul",
            "Si no → Portal Rojo"
        ),
        portals        = listOf(PORTAL_BLUE, PORTAL_RED),
        logicalPortal  = logical,
        correctTap     = correct
    )
}

// ─── Nivel 4: if-else-if — 3 portales ────────────────────────────────────────
// <3 → Azul | 3..7 → Verde | >7 → Rojo
// Inversión: rotación (Azul→Verde, Verde→Rojo, Rojo→Azul)

private val NUMS_L4 = listOf(2, 5, 9, 3, 7)

private fun rotatePortal3(p: Portal): Portal = when (p) {
    PORTAL_BLUE  -> PORTAL_GREEN
    PORTAL_GREEN -> PORTAL_RED
    else         -> PORTAL_BLUE
}

fun buildLevel4Round(roundIndex: Int): PortalRound {
    val n = NUMS_L4[roundIndex % NUMS_L4.size]
    val logical = when {
        n < 3  -> PORTAL_BLUE
        n <= 7 -> PORTAL_GREEN
        else   -> PORTAL_RED
    }
    val correct = rotatePortal3(logical)
    return PortalRound(
        number         = n,
        conditionLines = listOf(
            "Si número < 3 → Portal Azul",
            "Si número entre 3 y 7 → Portal Verde",
            "Si número > 7 → Portal Rojo"
        ),
        portals        = listOf(PORTAL_BLUE, PORTAL_GREEN, PORTAL_RED),
        logicalPortal  = logical,
        correctTap     = correct
    )
}

// ─── Nivel 5 (Boss): condición anidada + timer ───────────────────────────────
// >5 → (par→Azul, impar→Verde) | ≤5 → Rojo

data class BossState(
    val number: Int,
    val logicalPortal: Portal,
    val correctTap: Portal
)

data class BossPortalRound(
    val states: List<BossState>,
    val conditionLines: List<String>,
    val portals: List<Portal>
)

private val BOSS_NUMBERS = listOf(
    listOf(8, 3, 9, 2, 6, 5),
    listOf(7, 4, 2, 8, 3, 6),
    listOf(6, 9, 4, 7, 1, 8),
    listOf(3, 6, 9, 2, 7, 4),
    listOf(4, 7, 1, 6, 9, 2),
    listOf(9, 2, 6, 5, 8, 3),
)

private fun bossLogical(n: Int): Portal = when {
    n > 5 && n % 2 == 0 -> PORTAL_BLUE
    n > 5               -> PORTAL_GREEN
    else                -> PORTAL_RED
}

fun buildBossRound(roundIndex: Int): BossPortalRound {
    val nums = BOSS_NUMBERS[roundIndex.coerceAtMost(BOSS_NUMBERS.lastIndex)]
    val states = nums.map { n ->
        val logical = bossLogical(n)
        val correct = rotatePortal3(logical)
        BossState(n, logical, correct)
    }
    return BossPortalRound(
        states        = states,
        conditionLines = listOf(
            "Si el número es mayor que 5:",
            "  Si es par → Portal Azul",
            "  Si no → Portal Verde",
            "Si no (≤ 5) → Portal Rojo"
        ),
        portals       = listOf(PORTAL_BLUE, PORTAL_GREEN, PORTAL_RED)
    )
}
