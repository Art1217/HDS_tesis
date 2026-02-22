package com.example.hds_tesisapp.ui.theme.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(true) {
        delay(6000)
        navController.navigate(Routes.Menu.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo (cubre todo)
        Image(
            painter = painterResource(id = R.drawable.background_landscape),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.titulo),
            contentDescription = "Título de la aplicación",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 40.dp)
                .fillMaxWidth(0.5f),
            contentScale = ContentScale.FillWidth
        )

        // 2. Imagen inferior
        Image(
            painter = painterResource(id = R.drawable.pj),
            contentDescription = "Personajes",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 40.dp)
                .fillMaxWidth(0.4f),
            contentScale = ContentScale.FillWidth
        )
    }
}
@Composable
@Preview
fun PreviewSplash() {
    val navController = rememberNavController()
    SplashScreen(navController)
}
