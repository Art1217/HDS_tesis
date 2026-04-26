package com.example.hds_tesisapp.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.hds_tesisapp.ui.theme.games.game1.GameScreen
import com.example.hds_tesisapp.ui.theme.menu.MenuScreen
import com.example.hds_tesisapp.ui.theme.splash.SplashScreen
import com.example.hds_tesisapp.ui.theme.personajes.MaxScreen
import com.example.hds_tesisapp.ui.theme.personajes.LinaScreen
import com.example.hds_tesisapp.ui.theme.personajes.TomAtomScreen
import com.example.hds_tesisapp.ui.theme.games.game2.Game2Screen
import com.example.hds_tesisapp.ui.theme.games.game3.Game3Screen
import com.example.hds_tesisapp.ui.theme.games.game4.Game4Screen
import com.example.hds_tesisapp.ui.theme.games.game5.Game5Screen
import com.example.hds_tesisapp.ui.theme.games.game6.Game6Screen
import com.example.hds_tesisapp.ui.theme.games.game7.Game7Screen
import com.example.hds_tesisapp.ui.theme.levels.LevelsScreen
import com.example.hds_tesisapp.ui.theme.games.game8.Game8Screen
import com.example.hds_tesisapp.ui.theme.games.game9.Game9Screen
import com.example.hds_tesisapp.ui.theme.story.StoryScreen
import com.example.hds_tesisapp.ui.theme.story.ZoneIntroScreen
import com.example.hds_tesisapp.ui.theme.games.game1.level1.Level1Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level2.Level2Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level3.Level3Screen
import com.example.hds_tesisapp.ui.theme.games.game1.level4.Level4Screen
import com.example.hds_tesisapp.ui.theme.games.game1.Game1TransitionScreen

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

        composable(Routes.Level1.route) {
            Level1Screen(onLevelComplete = {
                navController.navigate(game1TransitionRoute(2)) {
                    popUpTo(Routes.Level1.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Level2.route) {
            Level2Screen(onLevelComplete = {
                navController.navigate(game1TransitionRoute(3)) {
                    popUpTo(Routes.Level2.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Level3.route) {
            Level3Screen(onLevelComplete = {
                navController.navigate(game1TransitionRoute(4)) {
                    popUpTo(Routes.Level3.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Level4.route) {
            Level4Screen(onLevelComplete = {
                navController.navigate(Routes.Menu.route) {
                    popUpTo(Routes.Level4.route) { inclusive = true }
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
                        else -> Routes.Menu.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Routes.Game1Transition.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Game.route) {
            GameScreen(onLevelComplete = {
                navController.navigate(Routes.Game2.route) {
                    popUpTo(Routes.Game.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game2.route) {
            Game2Screen(onLevelComplete = {
                navController.navigate(Routes.Game3.route) {
                    popUpTo(Routes.Game2.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game3.route) {
            Game3Screen(onLevelComplete = {
                navController.navigate(Routes.Game4.route) {
                    popUpTo(Routes.Game3.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game4.route) {
            Game4Screen(onLevelComplete = {
                navController.navigate(Routes.Game5.route) {
                    popUpTo(Routes.Game4.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game5.route) {
            Game5Screen(onLevelComplete = {
                navController.navigate(Routes.Game6.route) {
                    popUpTo(Routes.Game5.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game6.route) {
            Game6Screen(onLevelComplete = {
                navController.navigate(Routes.Game7.route) {
                    popUpTo(Routes.Game6.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game7.route) {
            Game7Screen(onLevelComplete = {
                navController.navigate(Routes.Game8.route) {
                    popUpTo(Routes.Game7.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game8.route) {
            Game8Screen(onLevelComplete = {
                navController.navigate(Routes.Game9.route) {
                    popUpTo(Routes.Game8.route) { inclusive = true }
                }
            })
        }

        composable(Routes.Game9.route) {
            Game9Screen(onLevelComplete = {
                navController.navigate(Routes.Menu.route) {
                    popUpTo(Routes.Game9.route) { inclusive = true }
                }
            })
        }

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
