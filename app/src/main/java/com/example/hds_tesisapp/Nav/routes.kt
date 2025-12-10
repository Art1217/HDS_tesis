package com.example.hds_tesisapp.Nav

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Menu : Routes("menu")
    object Game : Routes("game")
}
