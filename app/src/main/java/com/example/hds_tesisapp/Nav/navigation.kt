package com.example.hds_tesisapp.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hds_tesisapp.ui.theme.games.game1.Game1TransitionScreen
import com.example.hds_tesisapp.ui.theme.games.game1.Zone1CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game1.level1.Level1Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level2.Level2Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level3.Level3Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level4.Level4Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level5.Level5Screen
import com.example.hds_tesisapp.ui.theme.games.game2.Zone2CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game2.level1.Level1G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level2.Level2G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level3.Level3G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level4.Level4G2Screen
import com.example.hds_tesisapp.ui.theme.games.game2.level5.Level5G2Screen
import com.example.hds_tesisapp.ui.theme.games.game3.Zone3CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game3.level1.Level1G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level2.Level2G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level3.Level3G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level4.Level4G3Screen
import com.example.hds_tesisapp.ui.theme.games.game3.level5.Level5G3Screen
import com.example.hds_tesisapp.ui.theme.games.game4.Zone4CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game4.level1.Level1G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level2.Level2G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level3.Level3G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level4.Level4G4Screen
import com.example.hds_tesisapp.ui.theme.games.game4.level5.Level5G4Screen
import com.example.hds_tesisapp.ui.theme.games.game5.Zone5CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game5.level1.Level1G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level2.Level2G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level3.Level3G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level4.Level4G5Screen
import com.example.hds_tesisapp.ui.theme.games.game5.level5.Level5G5Screen
import com.example.hds_tesisapp.ui.theme.games.game6.Zone6CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game6.level1.Level1G6Screen
import com.example.hds_tesisapp.ui.theme.games.game6.level2.Level2G6Screen
import com.example.hds_tesisapp.ui.theme.games.game6.level3.Level3G6Screen
import com.example.hds_tesisapp.ui.theme.games.game6.level4.Level4G6Screen
import com.example.hds_tesisapp.ui.theme.games.game6.level5.Level5G6Screen
import com.example.hds_tesisapp.ui.theme.games.game7.Zone7CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game7.level1.Level1G7Screen
import com.example.hds_tesisapp.ui.theme.games.game7.level2.Level2G7Screen
import com.example.hds_tesisapp.ui.theme.games.game7.level3.Level3G7Screen
import com.example.hds_tesisapp.ui.theme.games.game7.level4.Level4G7Screen
import com.example.hds_tesisapp.ui.theme.games.game7.level5.Level5G7Screen
import com.example.hds_tesisapp.ui.theme.games.game8.Zone8CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game8.level1.Level1G8Screen
import com.example.hds_tesisapp.ui.theme.games.game8.level2.Level2G8Screen
import com.example.hds_tesisapp.ui.theme.games.game8.level3.Level3G8Screen
import com.example.hds_tesisapp.ui.theme.games.game8.level4.Level4G8Screen
import com.example.hds_tesisapp.ui.theme.games.game8.level5.Level5G8Screen
import com.example.hds_tesisapp.ui.theme.games.game9.WorkshopTutorialScreen
import com.example.hds_tesisapp.ui.theme.games.game9.Zone9CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game9.level1.Level1G9Screen
import com.example.hds_tesisapp.ui.theme.games.game9.level2.Level2G9Screen
import com.example.hds_tesisapp.ui.theme.games.game9.level3.Level3G9Screen
import com.example.hds_tesisapp.ui.theme.games.game9.level4.Level4G9Screen
import com.example.hds_tesisapp.ui.theme.games.game9.level5.Level5G9Screen
import com.example.hds_tesisapp.ui.theme.levels.LevelsScreen
import com.example.hds_tesisapp.ui.theme.menu.MenuScreen
import com.example.hds_tesisapp.ui.theme.personajes.LinaScreen
import com.example.hds_tesisapp.ui.theme.personajes.MaxScreen
import com.example.hds_tesisapp.ui.theme.personajes.TomAtomScreen
import com.example.hds_tesisapp.ui.theme.splash.SplashScreen
import com.example.hds_tesisapp.ui.theme.story.AlgorithmTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.CityTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.ClassificationTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.FactoryTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.LabTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.PatternTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.PortalTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.SortingTutorialScreen
import com.example.hds_tesisapp.ui.theme.story.StoryScreen
import com.example.hds_tesisapp.ui.theme.story.ZoneIntroScreen

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
                navController.navigate(Routes.FactoryTutorial.route) {
                    popUpTo(Routes.Zone5Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 6: La Fábrica de Bits ────────────────────────────────────────

        composable(Routes.FactoryTutorial.route) {
            FactoryTutorialScreen(navController)
        }

        composable(Routes.Level1G6.route) {
            Level1G6Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G6.route) {
                        popUpTo(Routes.Level1G6.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G6.route) {
            Level2G6Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G6.route) {
                        popUpTo(Routes.Level2G6.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G6.route) {
            Level3G6Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G6.route) {
                        popUpTo(Routes.Level3G6.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G6.route) {
            Level4G6Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G6.route) {
                        popUpTo(Routes.Level4G6.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G6.route) {
            Level5G6Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone6Complete.route) {
                        popUpTo(Routes.Level5G6.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone6Complete.route) {
            Zone6CompleteScreen(onContinue = {
                navController.navigate(Routes.LabTutorial.route) {
                    popUpTo(Routes.Zone6Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 7: El Laboratorio del Cambio ────────────────────────────────

        composable(Routes.LabTutorial.route) {
            LabTutorialScreen(navController)
        }

        composable(Routes.Level1G7.route) {
            Level1G7Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level2G7.route) {
                        popUpTo(Routes.Level1G7.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level2G7.route) {
            Level2G7Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level3G7.route) {
                        popUpTo(Routes.Level2G7.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level3G7.route) {
            Level3G7Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level4G7.route) {
                        popUpTo(Routes.Level3G7.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level4G7.route) {
            Level4G7Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Level5G7.route) {
                        popUpTo(Routes.Level4G7.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Level5G7.route) {
            Level5G7Screen(
                onLevelComplete = {
                    navController.navigate(Routes.Zone7Complete.route) {
                        popUpTo(Routes.Level5G7.route) { inclusive = true }
                    }
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Zone7Complete.route) {
            Zone7CompleteScreen(onContinue = {
                navController.navigate(Routes.CityTutorial.route) {
                    popUpTo(Routes.Zone7Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 8: La Ciudad de las Reacciones ───────────────────────────────

        composable(Routes.CityTutorial.route) {
            CityTutorialScreen(navController)
        }

        composable(Routes.Level1G8.route) {
            Level1G8Screen(
                onLevelComplete = { navController.navigate(Routes.Level2G8.route) { popUpTo(Routes.Level1G8.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level2G8.route) {
            Level2G8Screen(
                onLevelComplete = { navController.navigate(Routes.Level3G8.route) { popUpTo(Routes.Level2G8.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level3G8.route) {
            Level3G8Screen(
                onLevelComplete = { navController.navigate(Routes.Level4G8.route) { popUpTo(Routes.Level3G8.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level4G8.route) {
            Level4G8Screen(
                onLevelComplete = { navController.navigate(Routes.Level5G8.route) { popUpTo(Routes.Level4G8.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level5G8.route) {
            Level5G8Screen(
                onLevelComplete = { navController.navigate(Routes.Zone8Complete.route) { popUpTo(Routes.Level5G8.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Zone8Complete.route) {
            Zone8CompleteScreen(onContinue = {
                navController.navigate(Routes.WorkshopTutorial.route) {
                    popUpTo(Routes.Zone8Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 9 ────────────────────────────────────────────────────────────

        composable(Routes.WorkshopTutorial.route) {
            WorkshopTutorialScreen(
                onStart = { navController.navigate(Routes.Level1G9.route) { popUpTo(Routes.WorkshopTutorial.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level1G9.route) {
            Level1G9Screen(
                onLevelComplete = { navController.navigate(Routes.Level2G9.route) { popUpTo(Routes.Level1G9.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level2G9.route) {
            Level2G9Screen(
                onLevelComplete = { navController.navigate(Routes.Level3G9.route) { popUpTo(Routes.Level2G9.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level3G9.route) {
            Level3G9Screen(
                onLevelComplete = { navController.navigate(Routes.Level4G9.route) { popUpTo(Routes.Level3G9.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level4G9.route) {
            Level4G9Screen(
                onLevelComplete = { navController.navigate(Routes.Level5G9.route) { popUpTo(Routes.Level4G9.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level5G9.route) {
            Level5G9Screen(
                onLevelComplete = { navController.navigate(Routes.Zone9Complete.route) { popUpTo(Routes.Level5G9.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Zone9Complete.route) {
            Zone9CompleteScreen(onContinue = {
                navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } }
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
