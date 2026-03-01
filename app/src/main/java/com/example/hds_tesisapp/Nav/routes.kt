package com.example.hds_tesisapp.Nav

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Menu : Routes("menu")
    object Game : Routes("game")
    object MaxCharacter : Routes("max_character")
    object LinaCharacter : Routes("lina_character")
    object TomAtomCharacter : Routes("tom_atom_character")
    object Game2 : Routes("game2")
    object Game3 : Routes("game3")
    object Game4 : Routes("game4")
    object Game5 : Routes("game5")
    object Game6 : Routes("game6")
    object Game7 : Routes("game7")
}
