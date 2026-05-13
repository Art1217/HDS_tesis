package com.example.hds_tesisapp.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.hds_tesisapp.ui.theme.menu.MenuScreen
import com.example.hds_tesisapp.ui.theme.splash.SplashScreen
import com.example.hds_tesisapp.ui.theme.personajes.MaxScreen
import com.example.hds_tesisapp.ui.theme.personajes.LinaScreen
import com.example.hds_tesisapp.ui.theme.personajes.TomAtomScreen
import com.example.hds_tesisapp.ui.theme.levels.LevelsScreen
import com.example.hds_tesisapp.ui.theme.story.StoryScreen
import com.example.hds_tesisapp.ui.theme.story.AlgorithmTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.ClassificationTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.SortingTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.ZoneIntroScreen
import com.example.hds_tesisapp.ui.theme.games.game1.level1.Level1Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level2.Level2Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level3.Level3Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level4.Level4Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level5.Level5Screen
import com.example.hds_tesisapp.ui.theme.games.game1.Game1TransitionScreen
import com.example.hds_tesisapp.ui.theme.games.game1.Zone1CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game2.level1.Level1G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level2.Level2G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level3.Level3G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level4.Level4G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level5.Level5G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.Zone2CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game3.level1.Level1G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level2.Level2G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level3.Level3G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level4.Level4G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level5.Level5G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.Zone3CompleteScreen
import com.example.hds_tesisapp.ui.theme.story.PatternTutorialScreen
import com.example.hds_tesisapp.ui.theme.games.game4.level1.Level1G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level2.Level2G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level3.Level3G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level4.Level4G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level5.Level5G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.Zone4CompleteScreen
import com.example.hds_tesisapp.ui.theme.story.PortalTutorialScreen
import com.example.hds_tesisapp.ui.theme.games.game5.level1.Level1G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level2.Level2G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level3.Level3G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level4.Level4G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level5.Level5G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.Zone5CompleteScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.Menu.route) {
            MenuScreen(navController)
        }

        composable(Routes.Story.route) {
            StoryScreen(navController)
        }

        composable(Routes.ZoneIntro.route) {
            ZoneIntroScreen(navController)
        }

        composable(Routes.AlgorithmTutorial.route) {
            AlgorithmTutorialScreen(navController)
        }

        // ── Zona 1: El Bit Perdido ────────────────────────────────────────────

        composable(Routes.Level1.route) {
            Level1Screen(
                onLevelComplete = {
                    navController.navigate(game1TransitionRoute(2)) {
                        popUpTo(Routes.Level1.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2.route) {
            Level2Screen(
                onLevelComplete = {
                    navController.navigate(game1TransitionRoute(3)) {
                        popUpTo(Routes.Level2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3.route) {
            Level3Screen(
                onLevelComplete = {
                    navController.navigate(game1TransitionRoute(4)) {
                        popUpTo(Routes.Level3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4.route) {
            Level4Screen(
                onLevelComplete = {
                    navController.navigate(game1TransitionRoute(5)) {
                        popUpTo(Routes.Level4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5.route) {
            Level5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone1Complete.route) {
                        popUpTo(Routes.Level5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone1Complete.route) {
            Zone1CompleteScreen(onContinue = {
                navController.navigate(Routes.ClassificationTutorial.route) {
                    popUpTo(Routes.Zone1Complete.route) { inclusive = true }
                }
            })
        }

        composable(
            route = Routes.Game1Transition.route,
            arguments = listOf(navArgument("toLevel") { type = NavType.IntType })
        ) { backStackEntry ->
            val toLevel = backStackEntry.arguments?.getInt("toLevel") ?: 2
            Game1TransitionScreen(
                toLevel    = toLevel,
                onContinue = {
                    val dest = when (toLevel) {
                        2    -> Routes.Level2.route
                        3    -> Routes.Level3.route
                        4    -> Routes.Level4.route
                        5    -> Routes.Level5.route
                        else -> Routes.Menu.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Routes.Game1Transition.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Zona 2: El Bosque de los Grupos ──────────────────────────────────

        composable(Routes.ClassificationTutorial.route) {
            ClassificationTutorialScreen(navController)
        }

        composable(Routes.Level1G2.route) {
            Level1G2Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G2.route) {
                        popUpTo(Routes.Level1G2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G2.route) {
            Level2G2Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G2.route) {
                        popUpTo(Routes.Level2G2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G2.route) {
            Level3G2Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G2.route) {
                        popUpTo(Routes.Level3G2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G2.route) {
            Level4G2Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G2.route) {
                        popUpTo(Routes.Level4G2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G2.route) {
            Level5G2Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone2Complete.route) {
                        popUpTo(Routes.Level5G2.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone2Complete.route) {
            Zone2CompleteScreen(onContinue = {
                navController.navigate(Routes.SortingTutorial.route) {
                    popUpTo(Routes.Zone2Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 3: Las Cascadas en Secuencia ────────────────────────────────

        composable(Routes.SortingTutorial.route) {
            SortingTutorialScreen(navController)
        }

        composable(Routes.Level1G3.route) {
            Level1G3Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G3.route) {
                        popUpTo(Routes.Level1G3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G3.route) {
            Level2G3Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G3.route) {
                        popUpTo(Routes.Level2G3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G3.route) {
            Level3G3Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G3.route) {
                        popUpTo(Routes.Level3G3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G3.route) {
            Level4G3Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G3.route) {
                        popUpTo(Routes.Level4G3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G3.route) {
            Level5G3Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone3Complete.route) {
                        popUpTo(Routes.Level5G3.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone3Complete.route) {
            Zone3CompleteScreen(onContinue = {
                navController.navigate(Routes.PatternTutorial.route) {
                    popUpTo(Routes.Zone3Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 4: El Valle de los Patrones ─────────────────────────────────

        composable(Routes.PatternTutorial.route) {
            PatternTutorialScreen(navController)
        }

        composable(Routes.Level1G4.route) {
            Level1G4Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G4.route) {
                        popUpTo(Routes.Level1G4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G4.route) {
            Level2G4Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G4.route) {
                        popUpTo(Routes.Level2G4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G4.route) {
            Level3G4Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G4.route) {
                        popUpTo(Routes.Level3G4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G4.route) {
            Level4G4Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G4.route) {
                        popUpTo(Routes.Level4G4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G4.route) {
            Level5G4Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone4Complete.route) {
                        popUpTo(Routes.Level5G4.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone4Complete.route) {
            Zone4CompleteScreen(onContinue = {
                navController.navigate(Routes.PortalTutorial.route) {
                    popUpTo(Routes.Zone4Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 5: La Ciudad de los Portales ────────────────────────────────

        composable(Routes.PortalTutorial.route) {
            PortalTutorialScreen(navController)
        }

        composable(Routes.Level1G5.route) {
            Level1G5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G5.route) {
                        popUpTo(Routes.Level1G5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G5.route) {
            Level2G5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G5.route) {
                        popUpTo(Routes.Level2G5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G5.route) {
            Level3G5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G5.route) {
                        popUpTo(Routes.Level3G5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G5.route) {
            Level4G5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G5.route) {
                        popUpTo(Routes.Level4G5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G5.route) {
            Level5G5Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone5Complete.route) {
                        popUpTo(Routes.Level5G5.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone5Complete.route) {
            Zone5CompleteScreen(onContinue = {
                navController.navigate(Routes.Menu.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        // ── Personajes ────────────────────────────────────────────────────────

        composable(Routes.MaxCharacter.route) {
            MaxScreen(navController)
        }

        composable(Routes.LinaCharacter.route) {
            LinaScreen(navController)
        }

        composable(Routes.TomAtomCharacter.route) {
            TomAtomScreen(navController)
        }

        composable(Routes.Levels.route) {
            LevelsScreen(navController)
        }
    }
}
