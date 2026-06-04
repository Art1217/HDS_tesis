package com.example.hds_tesisapp.ui.theme.games.game9

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private val WT_CYAN   = Color(0xFF00BCD4)
private val WT_GREEN  = Color(0xFF4CAF50)
private val WT_AMBER  = Color(0xFFFFC107)
private val WT_RED    = Color(0xFFFF5252)
private val BG_TOP    = Color(0xFF060E1A)
private val BG_BOT    = Color(0xFF0D1F35)

private data class TutSlide(val icon: String, val title: String, val body: String, val accent: Color)

private val SLIDES = listOf(
    TutSlide("🔧", "El Taller de Correcciones",
        "El Glitch corrompió las instrucciones de este taller.\nCada línea de código tiene un error oculto — ¡encuéntralo y depúralo!",
        WT_CYAN),
    TutSlide("🐛", "Encuentra el bug",
        "Cada línea tiene 5 datos. Cuatro son correctos y uno es el BUG — el dato incorrecto.\nToca el dato que no corresponde para depurar la línea.",
        WT_GREEN),
    TutSlide("⚠️", "Dificultad creciente",
        "• Los datos se vuelven borrosos (difíciles de leer)\n• Las líneas se moverán de lado a lado\n• Aparecerá un minijefe que reemplaza líneas depuradas\n¡No bajes la guardia!",
        WT_AMBER),
    TutSlide("❤️", "Vidas y tiempo",
        "Tienes 5 vidas. Cada bug que no detectes te cuesta una.\nEn los primeros niveles hay temporizador.\nEn niveles 4 y 5 el reto es el minijefe que sabotea el código.",
        WT_RED)
)

@Composable
fun WorkshopTutorialScreen(onStart: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var page by remember { mutableIntStateOf(0) }
    val slide = SLIDES[page]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
    ) {
        Image(painterResource(R.drawable.workshop_bg), null,
            Modifier.fillMaxSize().graphicsLayer { alpha = 0.12f }, contentScale = ContentScale.Crop)

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Icon + page dots
            Column(
                modifier = Modifier.width(140.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(slide.icon, fontSize = 64.sp)
                Text("ZONA 9", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = WT_CYAN.copy(alpha = 0.7f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SLIDES.indices.forEach { i ->
                        Box(
                            Modifier.size(if (i == page) 10.dp else 7.dp)
                                .clip(RoundedCornerShape(50))
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
                                .background(WT_GREEN.copy(alpha = 0.22f))
                                .border(1.5.dp, WT_GREEN.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                .clickable { onStart() }
                                .padding(horizontal = 26.dp, vertical = 8.dp)
                        ) { Text("¡EMPEZAR!", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.ExtraBold, color = WT_GREEN) }
                    }
                }
            }
        }
    }
}
