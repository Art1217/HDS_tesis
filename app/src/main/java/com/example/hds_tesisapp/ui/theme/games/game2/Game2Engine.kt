package com.example.hds_tesisapp.ui.theme.games.game2

import com.example.hds_tesisapp.R

enum class FruitType(val emoji: String, val label: String) {
    MANZANA("🍎", "Manzana"),
    NARANJA("🍊", "Naranja"),
    LIMON("🍋", "Limón"),
    DURAZNO("🍑", "Durazno"),
    MANGO("🥭", "Mango"),
}

enum class AnimalType(val emoji: String, val label: String, val habitat: HabitatType) {
    PAJARO("🐦", "Pájaro",     HabitatType.COPA),
    AGUILA("🦅", "Águila",     HabitatType.COPA),
    ARDILLA("🐿️", "Ardilla",  HabitatType.TRONCO),
    CARPINTERO("🪶", "Carpintero", HabitatType.TRONCO),
    ZORRO("🦊", "Zorro",       HabitatType.MADRIGUERA),
    CONEJO("🐰", "Conejo",     HabitatType.MADRIGUERA),
}

enum class HabitatType(val label: String, val icon: String) {
    COPA("Copa del árbol", "🌿"),
    TRONCO("Tronco", "🪵"),
    MADRIGUERA("Madriguera", "🕳️"),
}

enum class TreeType(val label: String, val drawableRes: Int, val fruit: FruitType) {
    MANZANA("Árbol Manzano",   R.drawable.tree_manzana, FruitType.MANZANA),
    NARANJA("Árbol Naranjo",   R.drawable.tree_naranja, FruitType.NARANJA),
    LIMON("Árbol Limonero",    R.drawable.tree_limon,   FruitType.LIMON),
}

sealed class DragItem {
    abstract val id: Int
    abstract val emoji: String
    abstract val label: String

    data class Fruit(override val id: Int, val type: FruitType) : DragItem() {
        override val emoji get() = type.emoji
        override val label get() = type.label
    }
    data class Animal(override val id: Int, val type: AnimalType) : DragItem() {
        override val emoji get() = type.emoji
        override val label get() = type.label
    }
}

fun buildFruitPool(vararg counts: Pair<FruitType, Int>): List<DragItem.Fruit> {
    var id = 0
    return counts.flatMap { (type, n) -> List(n) { DragItem.Fruit(id++, type) } }.shuffled()
}

fun buildAnimalPool(vararg counts: Pair<AnimalType, Int>): List<DragItem.Animal> {
    var id = 100
    return counts.flatMap { (type, n) -> List(n) { DragItem.Animal(id++, type) } }.shuffled()
}
