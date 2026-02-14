package com.example.hds_tesisapp.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hds_tesisapp.ui.theme.games.game1.GameScreen
import com.example.hds_tesisapp.ui.theme.menu.MenuScreen
import com.example.hds_tesisapp.ui.theme.splash.SplashScreen
import com.example.hds_tesisapp.ui.theme.personajes.MaxScreen
import com.example.hds_tesisapp.ui.theme.personajes.LinaScreen
import com.example.hds_tesisapp.ui.theme.personajes.TomAtomScreen
import com.example.hds_tesisapp.ui.theme.games.game2.Game2Screen
import com.example.hds_tesisapp.ui.theme.games.game3.Game3Screen

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
            Game3Screen()
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
    }
}
