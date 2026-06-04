package com.example.hds_tesisapp.ui.theme.story

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

private val CITY_ORANGE = Color(0xFFFF6F00)
private val CITY_YELLOW = Color(0xFFFFD600)
private val CITY_CYAN   = Color(0xFF00E5FF)
private val BG_TOP      = Color(0xFF0A0015)
private val BG_BOT      = Color(0xFF1A0030)

private data class CSlide(val title: String, val body: String)

private val CSLIDES = listOf(
    CSlide("¿Qué son los Eventos?",
        "Un evento es algo que OCURRE y dispara una reacción.\nEn la ciudad, objetos caen del cielo o aparecen en el camino — cada uno es un evento que debes manejar."),
    CSlide("Los 4 Héroes",
        "Max destruye objetos de impacto.\nLina recoge bloques beneficiosos.\nTom repara objetos mecánicos.\nAtom activa tecnología eléctrica.\n¡Cada héroe solo puede manejar su tipo!"),
    CSlide("Selecciona al Héroe Correcto",
        "Presiona el botón del héroe adecuado y luego el botón de acción (DESTRUIR, RECOGER, REPARAR o ACTIVAR).\nEl héroe entrará en espera por unos segundos."),
    CSlide("¡Cuidado con los Objetos!",
        "Si un objeto que cae llega al suelo sin ser manejado, perderás una vida.\nLas BOMBAS malignas no pueden ser tocadas — esquívalas o usa la bomba especial de Lina.")
)

@Composable
fun CityTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var slideIndex by remember { mutableIntStateOf(0) }
    val slide = CSLIDES[slideIndex]
    val scope = rememberCoroutineScope()
    val glow  = remember { Animatable(0.4f) }

    LaunchedEffect(slideIndex) {
        launch { while (true) { glow.animateTo(1f, tween(900)); glow.animateTo(0.4f, tween(900)) } }
    }

    fun advance() {
        if (slideIndex < CSLIDES.lastIndex) slideIndex++
        else navController.navigate(Routes.Level1G8.route) {
            popUpTo(Routes.CityTutorial.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(slideIndex) { detectTapGestures { advance() } }
    ) {
        // Decorative particles
        repeat(8) { i ->
            val xFrac = (i * 0.13f + 0.03f).coerceIn(0f, 0.93f)
            val yFrac = (i * 0.11f + 0.05f).coerceIn(0f, 0.93f)
            Box(
                modifier = Modifier
                    .offset((xFrac * 800).dp, (yFrac * 450).dp)
                    .size(if (i % 3 == 0) 8.dp else 4.dp)
                    .clip(CircleShape)
                    .background(if (i % 2 == 0) CITY_CYAN.copy(alpha = 0.12f) else CITY_ORANGE.copy(alpha = 0.09f))
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Character panel
            Column(
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .drawBehind {
                            drawCircle(CITY_CYAN.copy(alpha = glow.value * 0.35f),
                                radius = size.minDimension / 2f + 18f)
                        }
                        .clip(CircleShape)
                        .background(CITY_CYAN.copy(alpha = 0.10f))
                        .border(2.dp, CITY_ORANGE.copy(alpha = glow.value * 0.8f + 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.max_city), "Max",
                        Modifier.fillMaxSize(0.88f), contentScale = ContentScale.Fit)
                }
                Text("Max", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = CITY_YELLOW)

                // Hero mini lineup
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("M", "L", "T", "A").forEachIndexed { i, l ->
                        val colors = listOf(Color(0xFF2196F3), CITY_CYAN, Color(0xFFFF9800), Color(0xFF9C27B0))
                        Box(
                            modifier = Modifier.size(22.dp).clip(CircleShape)
                                .background(colors[i].copy(alpha = 0.25f))
                                .border(1.dp, colors[i].copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(l, fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = colors[i])
                        }
                    }
                }

                // Slide dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CSLIDES.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == slideIndex) 9.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (i == slideIndex) CITY_CYAN else CITY_CYAN.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            // Content panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, CITY_ORANGE.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CITY_CYAN.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 8 · TUTORIAL", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = CITY_CYAN, letterSpacing = 1.sp)
                }
                Text(slide.title, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(slide.body, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp)

                // Slide 1: hero grid hint
                if (slideIndex == 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Max", "Destruir", Color(0xFF2196F3)),
                            Triple("Lina", "Recoger", CITY_CYAN),
                            Triple("Tom", "Reparar", Color(0xFFFF9800)),
                            Triple("Atom", "Activar", Color(0xFF9C27B0))
                        ).forEach { (name, action, color) ->
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = 0.12f))
                                    .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(name, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold, color = color)
                                Text(action, fontSize = 7.sp, fontFamily = Baloo2FontFamily,
                                    color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // Slide 3: object type chips
                if (slideIndex == 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("DESTRUIR" to Color(0xFF2196F3), "RECOGER" to CITY_CYAN,
                            "REPARAR" to Color(0xFFFF9800), "ACTIVAR" to Color(0xFF9C27B0)).forEach { (lbl, col) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(col.copy(alpha = 0.18f))
                                    .border(1.dp, col.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(lbl, fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold, color = col)
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Text(
                    if (slideIndex < CSLIDES.lastIndex) "Toca para continuar ›" else "¡A la ciudad! →",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = CITY_ORANGE.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Skip
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CITY_CYAN.copy(alpha = 0.10f))
                    .border(1.dp, CITY_ORANGE.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            navController.navigate(Routes.Level1G8.route) {
                                popUpTo(Routes.CityTutorial.route) { inclusive = true }
                            }
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("SKIP", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = CITY_CYAN.copy(alpha = 0.7f),
                    letterSpacing = 1.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
