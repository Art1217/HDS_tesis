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
    object ClassificationTutorial : Routes("classification_tutorial")
    object Level1G2        : Routes("level1_g2")
    object Level2G2        : Routes("level2_g2")
    object Level3G2        : Routes("level3_g2")
    object Level4G2        : Routes("level4_g2")
    object Level5G2        : Routes("level5_g2")
    object Zone2Complete   : Routes("zone2_complete")

    // Zona 3
    object SortingTutorial : Routes("sorting_tutorial")
    object Level1G3        : Routes("level1_g3")
    object Level2G3        : Routes("level2_g3")
    object Level3G3        : Routes("level3_g3")
    object Level4G3        : Routes("level4_g3")
    object Level5G3        : Routes("level5_g3")
    object Zone3Complete   : Routes("zone3_complete")

    // Zona 4
    object PatternTutorial : Routes("pattern_tutorial")
    object Level1G4        : Routes("level1_g4")
    object Level2G4        : Routes("level2_g4")
    object Level3G4        : Routes("level3_g4")
    object Level4G4        : Routes("level4_g4")
    object Level5G4        : Routes("level5_g4")
    object Zone4Complete   : Routes("zone4_complete")

    // Zona 5
    object PortalTutorial  : Routes("portal_tutorial")
    object Level1G5        : Routes("level1_g5")
    object Level2G5        : Routes("level2_g5")
    object Level3G5        : Routes("level3_g5")
    object Level4G5        : Routes("level4_g5")
    object Level5G5        : Routes("level5_g5")
    object Zone5Complete   : Routes("zone5_complete")

    // Zona 6
    object FactoryTutorial : Routes("factory_tutorial")
    object Level1G6        : Routes("level1_g6")
    object Level2G6        : Routes("level2_g6")
    object Level3G6        : Routes("level3_g6")
    object Level4G6        : Routes("level4_g6")
    object Level5G6        : Routes("level5_g6")
    object Zone6Complete   : Routes("zone6_complete")

    // Zona 7
    object LabTutorial     : Routes("lab_tutorial")
    object Level1G7        : Routes("level1_g7")
    object Level2G7        : Routes("level2_g7")
    object Level3G7        : Routes("level3_g7")
    object Level4G7        : Routes("level4_g7")
    object Level5G7        : Routes("level5_g7")
    object Zone7Complete   : Routes("zone7_complete")

    // Zona 8
    object CityTutorial    : Routes("city_tutorial")
    object Level1G8        : Routes("level1_g8")
    object Level2G8        : Routes("level2_g8")
    object Level3G8        : Routes("level3_g8")
    object Level4G8        : Routes("level4_g8")
    object Level5G8        : Routes("level5_g8")
    object Zone8Complete   : Routes("zone8_complete")
}

fun game1TransitionRoute(toLevel: Int) = "game1_transition/$toLevel"
