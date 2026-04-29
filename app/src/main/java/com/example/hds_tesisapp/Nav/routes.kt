package com.example.hds_tesisapp.Nav

sealed class Routes(val route: String) {
    object Splash          : Routes("splash")
    object Menu            : Routes("menu")
    object Story           : Routes("story")
    object ZoneIntro       : Routes("zone_intro")
    object MaxCharacter    : Routes("max_character")
    object LinaCharacter   : Routes("lina_character")
    object TomAtomCharacter: Routes("tom_atom_character")
    object Levels          : Routes("levels")

    // Zona 1
    object AlgorithmTutorial : Routes("algorithm_tutorial")
    object Level1          : Routes("level1")
    object Level2          : Routes("level2")
    object Level3          : Routes("level3")
    object Level4          : Routes("level4")
    object Level5          : Routes("level5")
    object Zone1Complete   : Routes("zone1_complete")
    object Game1Transition : Routes("game1_transition/{toLevel}")

    // Zona 2
    object Level1G2        : Routes("level1_g2")
    object Level2G2        : Routes("level2_g2")
    object Level3G2        : Routes("level3_g2")
    object Level4G2        : Routes("level4_g2")
    object Level5G2        : Routes("level5_g2")
    object Zone2Complete   : Routes("zone2_complete")
}

fun game1TransitionRoute(toLevel: Int) = "game1_transition/$toLevel"
