package com.example.hds_tesisapp.Nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hds_tesisapp.domain.model.LevelCatalog
import com.example.hds_tesisapp.ui.auth.LoginScreen
import com.example.hds_tesisapp.ui.auth.RegisterScreen
import com.example.hds_tesisapp.ui.progress.ProgressViewModel
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
import com.example.hds_tesisapp.ui.theme.games.game10.CuartelTutorialScreen
import com.example.hds_tesisapp.ui.theme.games.game10.Zone10CompleteScreen
import com.example.hds_tesisapp.ui.theme.games.game10.level1.Level1G10Screen
import com.example.hds_tesisapp.ui.theme.games.game10.level2.Level2G10Screen
import com.example.hds_tesisapp.ui.theme.games.game10.level3.Level3G10Screen
import com.example.hds_tesisapp.ui.theme.games.game10.level4.Level4G10Screen
import com.example.hds_tesisapp.ui.theme.games.game10.level5.Level5G10Screen
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

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.Menu.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(1, 1)) {
                        navController.navigate(game1TransitionRoute(2)) {
                            popUpTo(Routes.Level1.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(1, 2)) {
                        navController.navigate(game1TransitionRoute(3)) {
                            popUpTo(Routes.Level2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(1, 3)) {
                        navController.navigate(game1TransitionRoute(4)) {
                            popUpTo(Routes.Level3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(1, 4)) {
                        navController.navigate(game1TransitionRoute(5)) {
                            popUpTo(Routes.Level4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(1, 5)) {
                        navController.navigate(Routes.Zone1Complete.route) {
                            popUpTo(Routes.Level5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(2, 1)) {
                        navController.navigate(Routes.Level2G2.route) {
                            popUpTo(Routes.Level1G2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(2, 2)) {
                        navController.navigate(Routes.Level3G2.route) {
                            popUpTo(Routes.Level2G2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(2, 3)) {
                        navController.navigate(Routes.Level4G2.route) {
                            popUpTo(Routes.Level3G2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(2, 4)) {
                        navController.navigate(Routes.Level5G2.route) {
                            popUpTo(Routes.Level4G2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G2Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(2, 5)) {
                        navController.navigate(Routes.Zone2Complete.route) {
                            popUpTo(Routes.Level5G2.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(3, 1)) {
                        navController.navigate(Routes.Level2G3.route) {
                            popUpTo(Routes.Level1G3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(3, 2)) {
                        navController.navigate(Routes.Level3G3.route) {
                            popUpTo(Routes.Level2G3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(3, 3)) {
                        navController.navigate(Routes.Level4G3.route) {
                            popUpTo(Routes.Level3G3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(3, 4)) {
                        navController.navigate(Routes.Level5G3.route) {
                            popUpTo(Routes.Level4G3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G3Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(3, 5)) {
                        navController.navigate(Routes.Zone3Complete.route) {
                            popUpTo(Routes.Level5G3.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(4, 1)) {
                        navController.navigate(Routes.Level2G4.route) {
                            popUpTo(Routes.Level1G4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(4, 2)) {
                        navController.navigate(Routes.Level3G4.route) {
                            popUpTo(Routes.Level2G4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(4, 3)) {
                        navController.navigate(Routes.Level4G4.route) {
                            popUpTo(Routes.Level3G4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(4, 4)) {
                        navController.navigate(Routes.Level5G4.route) {
                            popUpTo(Routes.Level4G4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G4Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(4, 5)) {
                        navController.navigate(Routes.Zone4Complete.route) {
                            popUpTo(Routes.Level5G4.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(5, 1)) {
                        navController.navigate(Routes.Level2G5.route) {
                            popUpTo(Routes.Level1G5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(5, 2)) {
                        navController.navigate(Routes.Level3G5.route) {
                            popUpTo(Routes.Level2G5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(5, 3)) {
                        navController.navigate(Routes.Level4G5.route) {
                            popUpTo(Routes.Level3G5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(5, 4)) {
                        navController.navigate(Routes.Level5G5.route) {
                            popUpTo(Routes.Level4G5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G5Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(5, 5)) {
                        navController.navigate(Routes.Zone5Complete.route) {
                            popUpTo(Routes.Level5G5.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G6Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(6, 1)) {
                        navController.navigate(Routes.Level2G6.route) {
                            popUpTo(Routes.Level1G6.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G6Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(6, 2)) {
                        navController.navigate(Routes.Level3G6.route) {
                            popUpTo(Routes.Level2G6.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G6Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(6, 3)) {
                        navController.navigate(Routes.Level4G6.route) {
                            popUpTo(Routes.Level3G6.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G6Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(6, 4)) {
                        navController.navigate(Routes.Level5G6.route) {
                            popUpTo(Routes.Level4G6.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G6Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(6, 5)) {
                        navController.navigate(Routes.Zone6Complete.route) {
                            popUpTo(Routes.Level5G6.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G7Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(7, 1)) {
                        navController.navigate(Routes.Level2G7.route) {
                            popUpTo(Routes.Level1G7.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G7Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(7, 2)) {
                        navController.navigate(Routes.Level3G7.route) {
                            popUpTo(Routes.Level2G7.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G7Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(7, 3)) {
                        navController.navigate(Routes.Level4G7.route) {
                            popUpTo(Routes.Level3G7.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G7Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(7, 4)) {
                        navController.navigate(Routes.Level5G7.route) {
                            popUpTo(Routes.Level4G7.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G7Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(7, 5)) {
                        navController.navigate(Routes.Zone7Complete.route) {
                            popUpTo(Routes.Level5G7.route) { inclusive = true }
                        }
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G8Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(8, 1)) {
                        navController.navigate(Routes.Level2G8.route) { popUpTo(Routes.Level1G8.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level2G8.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G8Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(8, 2)) {
                        navController.navigate(Routes.Level3G8.route) { popUpTo(Routes.Level2G8.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level3G8.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G8Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(8, 3)) {
                        navController.navigate(Routes.Level4G8.route) { popUpTo(Routes.Level3G8.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level4G8.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G8Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(8, 4)) {
                        navController.navigate(Routes.Level5G8.route) { popUpTo(Routes.Level4G8.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level5G8.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G8Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(8, 5)) {
                        navController.navigate(Routes.Zone8Complete.route) { popUpTo(Routes.Level5G8.route) { inclusive = true } }
                    }
                },
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
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G9Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(9, 1)) {
                        navController.navigate(Routes.Level2G9.route) { popUpTo(Routes.Level1G9.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level2G9.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G9Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(9, 2)) {
                        navController.navigate(Routes.Level3G9.route) { popUpTo(Routes.Level2G9.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level3G9.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G9Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(9, 3)) {
                        navController.navigate(Routes.Level4G9.route) { popUpTo(Routes.Level3G9.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level4G9.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G9Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(9, 4)) {
                        navController.navigate(Routes.Level5G9.route) { popUpTo(Routes.Level4G9.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level5G9.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G9Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(9, 5)) {
                        navController.navigate(Routes.Zone9Complete.route) { popUpTo(Routes.Level5G9.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Zone9Complete.route) {
            Zone9CompleteScreen(onContinue = {
                navController.navigate(Routes.CuartelTutorial.route) {
                    popUpTo(Routes.Zone9Complete.route) { inclusive = true }
                }
            })
        }

        // ── Zona 10 ───────────────────────────────────────────────────────────

        composable(Routes.CuartelTutorial.route) {
            CuartelTutorialScreen(
                onStart = { navController.navigate(Routes.Level1G10.route) { popUpTo(Routes.CuartelTutorial.route) { inclusive = true } } },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level1G10.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level1G10Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(10, 1)) {
                        navController.navigate(Routes.Level2G10.route) { popUpTo(Routes.Level1G10.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level2G10.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level2G10Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(10, 2)) {
                        navController.navigate(Routes.Level3G10.route) { popUpTo(Routes.Level2G10.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level3G10.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level3G10Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(10, 3)) {
                        navController.navigate(Routes.Level4G10.route) { popUpTo(Routes.Level3G10.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level4G10.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level4G10Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(10, 4)) {
                        navController.navigate(Routes.Level5G10.route) { popUpTo(Routes.Level4G10.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Level5G10.route) {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            Level5G10Screen(
                onLevelComplete = {
                    progressViewModel.saveLevelCompleted(idLevel = LevelCatalog.idFor(10, 5)) {
                        navController.navigate(Routes.Zone10Complete.route) { popUpTo(Routes.Level5G10.route) { inclusive = true } }
                    }
                },
                onNavigateToMenu = { navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Routes.Zone10Complete.route) {
            Zone10CompleteScreen(onContinue = {
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
