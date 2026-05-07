package com.example.hds_tesisapp.ui.theme.games.game3

// ─── Nivel 1: Puente ─────────────────────────────────────────────────────────

data class PlankData(val id: Int, val value: Int)

fun buildPlanks(values: List<Int>): List<PlankData> =
    values.mapIndexed { i, v -> PlankData(i, v) }.shuffled()

// ─── Nivel 2: Rocas ──────────────────────────────────────────────────────────

enum class RockSize(val label: String, val scale: Float) {
    XS("XS", 0.40f),
    S ("S",  0.55f),
    M ("M",  0.70f),
    L ("L",  0.85f),
    XL("XL", 1.00f),
}

data class RockItem(val id: Int, val size: RockSize)

fun buildRocks(): List<RockItem> =
    RockSize.entries.mapIndexed { i, s -> RockItem(i, s) }.shuffled()

// ─── Nivel 3: Flores ─────────────────────────────────────────────────────────

enum class FlowerType(val emoji: String, val label: String, val height: Int) {
    TULIP ("🌷", "Tulipán",   1),
    DAISY ("🌼", "Margarita", 2),
    SUNFLOWER("🌻", "Girasol",3),
    HIBISCUS ("🌺", "Hibisco", 4),
    ROSE  ("🌹", "Rosa",      5),
}

data class FlowerItem(val id: Int, val type: FlowerType)

fun buildFlowers(): List<FlowerItem> =
    FlowerType.entries.mapIndexed { i, t -> FlowerItem(i, t) }.shuffled()

// ─── Niveles 4 y 5: Bubble Sort (Cascada) ────────────────────────────────────

data class WaterDrop(val id: Int, var value: Int)

fun buildDropRow(values: List<Int>, idOffset: Int = 0): MutableList<WaterDrop> =
    values.mapIndexed { i, v -> WaterDrop(idOffset + i, v) }.toMutableList()

fun buildCascadeRows(rowCount: Int): List<MutableList<WaterDrop>> {
    val rows = mutableListOf<MutableList<WaterDrop>>()
    var offset = 0
    repeat(rowCount) {
        val values = (1..5).shuffled()
        rows.add(buildDropRow(values, offset))
        offset += 5
    }
    return rows
}

fun List<WaterDrop>.isSorted(): Boolean =
    zipWithNext().all { (a, b) -> a.value <= b.value }

fun <T> MutableList<T>.swap(i: Int, j: Int) {
    val tmp = this[i]; this[i] = this[j]; this[j] = tmp
}
