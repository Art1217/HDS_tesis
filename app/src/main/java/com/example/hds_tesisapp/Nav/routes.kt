package com.example.hds_tesisapp.Nav

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Menu : Routes("menu")
    object Story : Routes("story")
    object ZoneIntro : Routes("zone_intro")
    object Level1 : Routes("level1")
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
    object Levels : Routes("levels")
    object Game8 : Routes("game8")
    object Game9 : Routes("game9")
    object Level2 : Routes("level2")
    object Level3 : Routes("level3")
    object Level4 : Routes("level4")
    object Game1Transition : Routes("game1_transition/{toLevel}")
}

fun game1TransitionRoute(toLevel: Int) = "game1_transition/$toLevel"
