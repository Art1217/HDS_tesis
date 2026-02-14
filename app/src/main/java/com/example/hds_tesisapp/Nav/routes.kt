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
}
