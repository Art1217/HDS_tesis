package com.example.hds_tesisapp.domain.model

/**
 * Traduce (zona, nivel) a los `id_level` autoincrementales de la tabla `level`
 * del backend, generados en el orden exacto del seed SQL: Zona 1 niveles 1-5
 * -> id 1-5, Zona 2 niveles 1-5 -> id 6-10, etc.
 */
object LevelCatalog {
    fun idFor(zone: Int, levelNumber: Int): Int = (zone - 1) * 5 + levelNumber
}
