package com.example.hds_tesisapp.ui.theme.games.game10

import com.example.hds_tesisapp.R

// Items that can be hidden under boxes on the rooftop.
enum class G10ItemKind { SCISSORS, ENERGY_KEY, FLASHLIGHT, CRYSTAL, JUNK }

// drawableRes overrides the emoji with real artwork when available.
data class G10HiddenItem(val id: Int, val name: String, val emoji: String, val kind: G10ItemKind, val drawableRes: Int? = null)

const val G10_BOSS_GRID_SIZE = 9

// The 9 boxes on the rooftop. Three are the items the team actually needs;
// the rest are distractors that anger the Glitch if picked.
val G10_BOSS_ITEM_POOL = listOf(
    G10HiddenItem(1, "Tijeras", "✂️", G10ItemKind.SCISSORS, R.drawable.item_tijeras),
    G10HiddenItem(2, "Llave de Energía", "🗝️", G10ItemKind.ENERGY_KEY, R.drawable.item_llave_energia),
    G10HiddenItem(3, "Linterna", "🔦", G10ItemKind.FLASHLIGHT, R.drawable.item_linterna),
    G10HiddenItem(4, "Vela", "🕯️", G10ItemKind.JUNK, R.drawable.item_vela),
    G10HiddenItem(5, "Martillo", "🔨", G10ItemKind.JUNK, R.drawable.item_martillo),
    G10HiddenItem(6, "Candado", "🔒", G10ItemKind.JUNK, R.drawable.item_candado),
    G10HiddenItem(7, "Bomba", "💣", G10ItemKind.JUNK, R.drawable.item_bomba),
    G10HiddenItem(8, "Espada", "🗡️", G10ItemKind.JUNK, R.drawable.item_espada),
    G10HiddenItem(9, "Paraguas", "☂️", G10ItemKind.JUNK, R.drawable.item_paraguas)
)

// Re-hidden if the Glitch steals the Cristal Protector mid-fight.
val G10_CRYSTAL_ITEM = G10HiddenItem(10, "Cristal Protector", "💎", G10ItemKind.CRYSTAL, R.drawable.item_cristal_protector)

fun generateG10BossBoxes(rng: java.util.Random): MutableList<Int?> =
    G10_BOSS_ITEM_POOL.map { it.id }.shuffled(rng).toMutableList()

fun g10BossItemById(id: Int): G10HiddenItem =
    if (id == G10_CRYSTAL_ITEM.id) G10_CRYSTAL_ITEM else G10_BOSS_ITEM_POOL.first { it.id == id }

// A shadow creature that emerges from the portal once the flashlight is found.
data class G10Shadow(
    val id: Int,
    val targetsPlayer: Boolean,  // true = attacks the player, false = heals the Glitch
    val lane: Int,               // 0 = left, 1 = right
    var timeLeftMs: Long
)

enum class G10GlitchPhase { TINKERING, REPOSITION, ATTACK_WARNING, ATTACK_HIT, ENERGY_INSERT }

// Mostly TINKERING, with periodic reposition / attack / energy-insert events.
val G10_PHASE_SEQUENCE = listOf(
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.REPOSITION,
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.ATTACK_WARNING,
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.ENERGY_INSERT,
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.TINKERING,
    G10GlitchPhase.REPOSITION,
    G10GlitchPhase.ATTACK_WARNING,
    G10GlitchPhase.ENERGY_INSERT
)

fun g10PhaseDurationMs(phase: G10GlitchPhase): Long = when (phase) {
    G10GlitchPhase.TINKERING      -> 4000L
    G10GlitchPhase.REPOSITION     -> 1500L
    G10GlitchPhase.ATTACK_WARNING -> 1500L
    G10GlitchPhase.ATTACK_HIT     -> 600L
    G10GlitchPhase.ENERGY_INSERT  -> 2500L
}

const val G10_GLITCH_MAX_HP   = 100
const val G10_ABSORB_DAMAGE   = 25
const val G10_WRONG_PICK_HEAL = 10
const val G10_SHADOW_HEAL     = 15
const val G10_BOX_LOCK_MS     = 4000L
const val G10_SHADOW_TIME_MS  = 4000L
const val G10_SHADOW_SPAWN_MS = 8000L
