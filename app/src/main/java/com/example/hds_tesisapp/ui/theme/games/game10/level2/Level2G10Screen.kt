package com.example.hds_tesisapp.ui.theme.games.game10.level2

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.hds_tesisapp.ui.theme.games.game10.G10_LEVEL_CONFIGS
import com.example.hds_tesisapp.ui.theme.games.game10.level1.G10RoomScreen

private val CONFIG = G10_LEVEL_CONFIGS[1]

@Composable
fun Level2G10Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    G10RoomScreen(
        config = CONFIG,
        levelLabel = "NIVEL 2 · Sala de Seguridad",
        doneMessage = "La grabación revela al Glitch robando el Mapa de los Portales.",
        onLevelComplete = onLevelComplete,
        onNavigateToMenu = onNavigateToMenu
    )
}
