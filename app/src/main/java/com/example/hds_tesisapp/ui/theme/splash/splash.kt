package com.example.hds_tesisapp.ui.theme.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.splash.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel = hiltViewModel()) {

    LaunchedEffect(true) {
        delay(6000)
        val destination = if (viewModel.hasActiveSession()) Routes.Menu.route else Routes.Login.route
        navController.navigate(destination) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo único
        Image(
            painter = painterResource(id = R.drawable.fondo_intro_unificado),
            contentDescription = "Fondo de introducción",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
@Composable
@Preview
fun PreviewSplash() {
    val navController = rememberNavController()
    SplashScreen(navController)
}
