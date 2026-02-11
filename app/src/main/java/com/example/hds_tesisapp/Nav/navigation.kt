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
            GameScreen()
        }

        composable(Routes.MaxCharacter.route) {
            MaxScreen(navController)
        }

        composable(Routes.LinaCharacter.route) {
            LinaScreen(navController)
        }
    }
}
