package com.example.hds_tesisapp.ui.theme.games.game10

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

private val CT_CYAN  = Color(0xFF00BCD4)
private val CT_GREEN = Color(0xFF4CAF50)
private val CT_AMBER = Color(0xFFFFC107)
private val CT_RED   = Color(0xFFFF5252)
private val BG_TOP   = Color(0xFF06060E)
private val BG_BOT   = Color(0xFF1A0A0A)

private data class CuartelSlide(val icon: String, val title: String, val body: String, val accent: Color)

private val SLIDES = listOf(
    CuartelSlide("🚀", "Teletransportación de emergencia",
        "El equipo activa la última máquina de teletransportación de CodeLand y aparece en el Lobby del Cuartel Central.\nLas puertas blindadas están selladas: el Glitch cortó la energía de todo el edificio.",
        CT_CYAN),
    CuartelSlide("🧩", "Tu súper poder: Abstracción",
        "En cada sala te dirán qué hace falta (por ejemplo: \"la puerta no tiene luz\"), pero NO te dirán qué objeto usar.\nTú deberás pensar: \"¿qué necesito de verdad para esto?\" y elegir solo esos objetos, dejando los demás donde están.\nEso es ABSTRACCIÓN: quedarte con lo importante y dejar de lado lo que no sirve.",
        CT_GREEN),
    CuartelSlide("🔒", "Cuartel sellado",
        "Para avanzar deberán resolver cada sala: encuentren los objetos correctos según las tareas indicadas y confirmen su elección.\n¡Cuidado! Elegir mal o agotar el tiempo significa repetir la sala.",
        CT_AMBER),
    CuartelSlide("🗺️", "El plan del Glitch",
        "Sala de Seguridad → Área de Operaciones → Laboratorio Central.\nCada sala revela más sobre el plan del Glitch: robó el Mapa de los Portales y prepara una máquina en la Azotea.",
        CT_GREEN),
    CuartelSlide("💎", "El Cristal Protector",
        "En el Laboratorio descubrirán la única arma capaz de debilitar al Glitch: el Cristal Protector.\nNo lo toquen sin guantes — su energía es inestable.",
        CT_CYAN),
    CuartelSlide("⚔️", "Enfrentamiento final",
        "En la Azotea, el Glitch construye un portal hacia su dimensión.\nDeberán esquivar sus ataques, absorber su energía y recuperar el Mapa de los Portales para sellarlo de una vez por todas.",
        CT_RED)
)

@Composable
fun CuartelTutorialScreen(onStart: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var page by remember { mutableIntStateOf(0) }
    val slide = SLIDES[page]

    val floatAnim = remember { Animatable(0f) }
    val glow      = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        launch { while (true) { floatAnim.animateTo(9f, tween(1300)); floatAnim.animateTo(-9f, tween(1300)) } }
        launch { while (true) { glow.animateTo(1f, tween(900)); glow.animateTo(0.4f, tween(900)) } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
    ) {
        Image(painterResource(R.drawable.cuartel_tutorial_bg), null,
            Modifier.fillMaxSize().graphicsLayer { alpha = 0.15f }, contentScale = ContentScale.Crop)

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Personaje + dots
            Column(
                modifier = Modifier.width(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(slide.accent.copy(alpha = 0.15f))
                        .border(2.dp, slide.accent.copy(alpha = 0.65f), CircleShape)
                        .size(44.dp),
                    contentAlignment = Alignment.Center
                ) { Text(slide.icon, fontSize = 20.sp) }

                Spacer(Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .offset(y = floatAnim.value.dp)
                        .size(148.dp)
                        .drawBehind {
                            drawCircle(
                                slide.accent.copy(alpha = glow.value * 0.28f),
                                radius = size.minDimension / 2f + 22f
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.atom),
                        contentDescription = "Atom",
                        modifier = Modifier.fillMaxSize(0.90f),
                        contentScale = ContentScale.Fit
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CT_CYAN.copy(alpha = 0.12f))
                        .border(1.dp, CT_CYAN.copy(alpha = 0.50f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text("ATOM", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = CT_CYAN, letterSpacing = 3.sp)
                }

                Text("ZONA 10", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = CT_CYAN.copy(alpha = 0.65f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SLIDES.indices.forEach { i ->
                        Box(
                            Modifier.size(if (i == page) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(if (i == page) slide.accent else Color.White.copy(alpha = 0.2f))
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, slide.accent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(slide.title, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = slide.accent)
                Text(slide.body, fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 20.sp)

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (page > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .clickable { page-- }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) { Text("← Anterior", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color.White.copy(alpha = 0.6f)) }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .clickable { onNavigateToMenu() }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) { Text("☰ Menú", fontSize = 11.sp, fontFamily = OrbitronFontFamily, color = Color.White.copy(alpha = 0.4f)) }
                    }

                    if (page < SLIDES.lastIndex) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(slide.accent.copy(alpha = 0.2f))
                                .border(1.dp, slide.accent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .clickable { page++ }
                                .padding(horizontal = 22.dp, vertical = 8.dp)
                        ) { Text("Siguiente →", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = slide.accent) }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CT_GREEN.copy(alpha = 0.22f))
                                .border(1.5.dp, CT_GREEN.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                .clickable { onStart() }
                                .padding(horizontal = 26.dp, vertical = 8.dp)
                        ) { Text("¡EMPEZAR!", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.ExtraBold, color = CT_GREEN) }
                    }
                }
            }
        }
    }
}
