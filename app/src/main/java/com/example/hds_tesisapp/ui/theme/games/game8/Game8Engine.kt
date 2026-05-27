package com.example.hds_tesisapp.ui.theme.games.game8

import com.example.hds_tesisapp.R

// ─── Hero ─────────────────────────────────────────────────────────────────────

enum class G8Hero { MAX, LINA, TOM, ATOM }

fun G8Hero.label()       = when (this) { G8Hero.MAX -> "Max";  G8Hero.LINA -> "Lina"; G8Hero.TOM -> "Tom";  G8Hero.ATOM -> "Atom" }
fun G8Hero.actionLabel() = when (this) { G8Hero.MAX -> "DESTRUIR"; G8Hero.LINA -> "RECOGER"; G8Hero.TOM -> "REPARAR"; G8Hero.ATOM -> "ACTIVAR" }
fun G8Hero.spriteRes()   = when (this) { G8Hero.MAX -> R.drawable.max_city; G8Hero.LINA -> R.drawable.lina_city; G8Hero.TOM -> R.drawable.tom_city; G8Hero.ATOM -> R.drawable.atom_city }

// ─── Object type ──────────────────────────────────────────────────────────────

enum class G8ObjType {
    // Max — falling impact
    ROCK, CONCRETE, CAR_DOOR, BARREL,
    // Lina — beneficial (falling)
    HEART_BLOCK, SHIELD_BLOCK, SNAIL_BLOCK, BOMB_BLOCK,
    // Tom — ground (appear on path)
    MACHINE, BUILDING_RUBBLE, CAR_BROKEN, WOOD_HOLE,
    // Atom — tech (falling, electric)
    BATTERY, ANTENNA, ENERGY_PANEL, DRONE,
    // Malicious
    EVIL_BOMB
}

val G8ObjType.hero: G8Hero? get() = when (this) {
    G8ObjType.ROCK, G8ObjType.CONCRETE, G8ObjType.CAR_DOOR, G8ObjType.BARREL -> G8Hero.MAX
    G8ObjType.HEART_BLOCK, G8ObjType.SHIELD_BLOCK, G8ObjType.SNAIL_BLOCK, G8ObjType.BOMB_BLOCK -> G8Hero.LINA
    G8ObjType.MACHINE, G8ObjType.BUILDING_RUBBLE, G8ObjType.CAR_BROKEN, G8ObjType.WOOD_HOLE -> G8Hero.TOM
    G8ObjType.BATTERY, G8ObjType.ANTENNA, G8ObjType.ENERGY_PANEL, G8ObjType.DRONE -> G8Hero.ATOM
    G8ObjType.EVIL_BOMB -> null
}

// Ground objects (Tom) appear on path, don't fall
val G8ObjType.isGround: Boolean get() = hero == G8Hero.TOM

// Sprite resources per type
fun G8ObjType.spriteRes() = when (this) {
    G8ObjType.ROCK             -> R.drawable.obj_rock
    G8ObjType.CONCRETE         -> R.drawable.obj_concrete
    G8ObjType.CAR_DOOR         -> R.drawable.obj_car_door
    G8ObjType.BARREL           -> R.drawable.obj_barrel
    G8ObjType.HEART_BLOCK      -> R.drawable.obj_heart_block
    G8ObjType.SHIELD_BLOCK     -> R.drawable.obj_shield_block
    G8ObjType.SNAIL_BLOCK      -> R.drawable.obj_snail_block
    G8ObjType.BOMB_BLOCK       -> R.drawable.obj_bomb_block
    G8ObjType.MACHINE          -> R.drawable.obj_machine
    G8ObjType.BUILDING_RUBBLE  -> R.drawable.obj_building_rubble
    G8ObjType.CAR_BROKEN       -> R.drawable.obj_car_broken
    G8ObjType.WOOD_HOLE        -> R.drawable.obj_wood_hole
    G8ObjType.BATTERY          -> R.drawable.obj_battery
    G8ObjType.ANTENNA          -> R.drawable.obj_antenna
    G8ObjType.ENERGY_PANEL     -> R.drawable.obj_energy_panel
    G8ObjType.DRONE            -> R.drawable.obj_drone
    G8ObjType.EVIL_BOMB        -> R.drawable.obj_bomb_evil
}

// Label shown on the object card
fun G8ObjType.displayLabel() = when (this) {
    G8ObjType.ROCK             -> "Roca"
    G8ObjType.CONCRETE         -> "Bloque"
    G8ObjType.CAR_DOOR         -> "Puerta"
    G8ObjType.BARREL           -> "Barril"
    G8ObjType.HEART_BLOCK      -> "❤️"
    G8ObjType.SHIELD_BLOCK     -> "🛡️"
    G8ObjType.SNAIL_BLOCK      -> "🐌"
    G8ObjType.BOMB_BLOCK       -> "💣"
    G8ObjType.MACHINE          -> "Máquina"
    G8ObjType.BUILDING_RUBBLE  -> "Edificio"
    G8ObjType.CAR_BROKEN       -> "Auto"
    G8ObjType.WOOD_HOLE        -> "Agujero"
    G8ObjType.BATTERY          -> "Batería"
    G8ObjType.ANTENNA          -> "Antena"
    G8ObjType.ENERGY_PANEL     -> "Panel"
    G8ObjType.DRONE            -> "Drone"
    G8ObjType.EVIL_BOMB        -> "⚠️ BOMBA"
}

// ─── Game object instance ─────────────────────────────────────────────────────

data class G8Obj(
    val id: Int,
    val type: G8ObjType,
    val xFrac: Float,           // 0f..1f horizontal position
    var yFrac: Float = 0f,      // 0f=top, 1f=ground; ground objs start at 1f
    var handled: Boolean = false,
    var missed: Boolean = false,
    var isGlitching: Boolean = false,   // Level 5 morph warning
    var morphTo: G8ObjType? = null,     // Level 5: type to morph into
    var morphProgress: Float = 0f       // 0f..1f (morph happens at 0.7f)
)

// ─── Level config ─────────────────────────────────────────────────────────────

data class G8LevelConfig(
    val totalEvents: Int,
    val fallDurationMs: Long,       // ms for object to traverse full screen
    val spawnIntervalMs: Long,      // ms between new spawns
    val maxOnScreen: Int,
    val heroCanMove: Boolean,
    val availableTypes: List<G8ObjType>,
    val hasEvilBomb: Boolean = false,
    val hasMorphing: Boolean = false,
    val heroCooldownMs: Long = 2500L
)

// Falling types per level
private val BASE_MAX   = listOf(G8ObjType.ROCK, G8ObjType.CONCRETE, G8ObjType.CAR_DOOR, G8ObjType.BARREL)
private val BASE_LINA1 = listOf(G8ObjType.HEART_BLOCK, G8ObjType.SHIELD_BLOCK)
private val BASE_LINA3 = BASE_LINA1 + G8ObjType.SNAIL_BLOCK
private val BASE_LINA4 = BASE_LINA3 + G8ObjType.BOMB_BLOCK
private val BASE_TOM   = listOf(G8ObjType.MACHINE, G8ObjType.BUILDING_RUBBLE, G8ObjType.CAR_BROKEN, G8ObjType.WOOD_HOLE)
private val BASE_ATOM  = listOf(G8ObjType.BATTERY, G8ObjType.ANTENNA, G8ObjType.ENERGY_PANEL, G8ObjType.DRONE)

val G8_LEVEL_CONFIGS = listOf(
    // Level 1: slow, 1 object, fixed hero pos, no Atom
    G8LevelConfig(
        totalEvents      = 10,
        fallDurationMs   = 5000L,
        spawnIntervalMs  = 6000L,
        maxOnScreen      = 1,
        heroCanMove      = false,
        availableTypes   = BASE_MAX + BASE_LINA1 + BASE_TOM
    ),
    // Level 2: medium speed, 1 object, hero moves, +Atom
    G8LevelConfig(
        totalEvents      = 12,
        fallDurationMs   = 3500L,
        spawnIntervalMs  = 4500L,
        maxOnScreen      = 1,
        heroCanMove      = true,
        availableTypes   = BASE_MAX + BASE_LINA1 + BASE_TOM + BASE_ATOM
    ),
    // Level 3: same speed, 2 objects, +snail
    G8LevelConfig(
        totalEvents      = 14,
        fallDurationMs   = 3500L,
        spawnIntervalMs  = 3000L,
        maxOnScreen      = 2,
        heroCanMove      = true,
        availableTypes   = BASE_MAX + BASE_LINA3 + BASE_TOM + BASE_ATOM
    ),
    // Level 4: faster, +bomb block, +evil bomb
    G8LevelConfig(
        totalEvents      = 15,
        fallDurationMs   = 2800L,
        spawnIntervalMs  = 2600L,
        maxOnScreen      = 2,
        heroCanMove      = true,
        availableTypes   = BASE_MAX + BASE_LINA4 + BASE_TOM + BASE_ATOM + listOf(G8ObjType.EVIL_BOMB),
        hasEvilBomb      = true
    ),
    // Level 5: same speed, morphing objects
    G8LevelConfig(
        totalEvents      = 18,
        fallDurationMs   = 2800L,
        spawnIntervalMs  = 2500L,
        maxOnScreen      = 2,
        heroCanMove      = true,
        availableTypes   = BASE_MAX + BASE_LINA4 + BASE_TOM + BASE_ATOM + listOf(G8ObjType.EVIL_BOMB),
        hasEvilBomb      = true,
        hasMorphing      = true
    )
)

// ─── Sequence generator ───────────────────────────────────────────────────────

// Generates a balanced sequence: ensures mix of heroes, no 3 identical consecutive
fun generateEventSequence(config: G8LevelConfig, seed: Long = System.currentTimeMillis()): List<G8ObjType> {
    val rng = java.util.Random(seed)
    val types = config.availableTypes.toMutableList()
    val result = mutableListOf<G8ObjType>()
    var lastType: G8ObjType? = null
    var lastLastType: G8ObjType? = null

    // Ensure at least 1 of each hero present in first 8 events
    val heroes = listOf(G8Hero.MAX, G8Hero.LINA, G8Hero.TOM, G8Hero.ATOM)
    val guaranteed = heroes.mapNotNull { h -> types.filter { it.hero == h }.randomOrNull() }
    val shuffled = guaranteed.toMutableList().also { it.shuffle(rng) }

    repeat(config.totalEvents) { i ->
        val candidates = if (i < shuffled.size) {
            listOf(shuffled[i])
        } else {
            types.filter { it != lastType || it != lastLastType }
                .ifEmpty { types }
        }
        val pick = candidates[rng.nextInt(candidates.size)]
        result.add(pick)
        lastLastType = lastType
        lastType = pick
    }
    return result
}

// Falling objects eligible to morph (L5) — only falling non-ground non-bomb
val MORPHABLE_TYPES = BASE_MAX + BASE_ATOM + listOf(G8ObjType.HEART_BLOCK, G8ObjType.SHIELD_BLOCK)
