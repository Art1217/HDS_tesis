package com.example.hds_tesisapp.ui.theme.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import kotlinx.coroutines.delay
import com.example.hds_tesisapp.R

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
            painter = painterResource(id = R.drawable.fondo_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.titulo),
            contentDescription = "Título de la aplicación",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .fillMaxWidth(0.95f),
            contentScale = ContentScale.FillWidth
        )

        // 2. Imagen inferior
        Image(
            painter = painterResource(id = R.drawable.pj),
            contentDescription = "Personajes",
            modifier = Modifier
                .align(Alignment.BottomCenter)

                .fillMaxWidth(0.95f),
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
