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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Z10_CYAN  = Color(0xFF00BCD4)
private val Z10_GREEN = Color(0xFF4CAF50)
private val Z10_AMBER = Color(0xFFFFC107)
private val Z10_RED   = Color(0xFFFF5252)
private val BG_TOP    = Color(0xFF06060E)
private val BG_BOT    = Color(0xFF0D1F1A)

private enum class Phase { COLLAPSE, VICTORY, RESTORED, FINAL }

@Composable
fun Zone10CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    val scope = rememberCoroutineScope()

    var phase       by remember { mutableStateOf(Phase.COLLAPSE) }
    var showFlash   by remember { mutableStateOf(false) }
    var shakeOffset by remember { mutableStateOf(0.dp to 0.dp) }
    var canContinue by remember { mutableStateOf(false) }

    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch { while (true) { glow.animateTo(1f, tween(800)); glow.animateTo(0.4f, tween(800)) } }

        // Portal collapse: violent shake + flash
        repeat(10) { i ->
            val dx = if (i % 2 == 0) 5.dp else (-5).dp
            val dy = if (i % 3 == 0) 4.dp else (-3).dp
            shakeOffset = dx to dy; delay(70L)
        }
        shakeOffset = 0.dp to 0.dp
        showFlash = true; delay(250L); showFlash = false

        delay(1200L)
        phase = Phase.VICTORY
        delay(3200L)

        phase = Phase.RESTORED
        delay(3200L)

        phase = Phase.FINAL
        canContinue = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .offset(shakeOffset.first, shakeOffset.second)
            .clickable(enabled = canContinue) { onContinue() }
    ) {
        Image(painterResource(R.drawable.g10_rooftop_bg), null,
            Modifier.fillMaxSize().graphicsLayer { alpha = 0.12f }, contentScale = ContentScale.Crop)

        if (showFlash) {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.85f)).zIndex(9f))
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Left panel — icon + status
            Column(
                modifier = Modifier.width(130.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    when (phase) {
                        Phase.COLLAPSE -> "🌀"
                        Phase.VICTORY  -> "✨"
                        Phase.RESTORED -> "🏢"
                        Phase.FINAL    -> "🤖"
                    },
                    fontSize = 52.sp
                )
                Text(
                    text = when (phase) {
                        Phase.COLLAPSE -> "COLAPSO"
                        Phase.VICTORY  -> "VICTORIA"
                        Phase.RESTORED -> "CUARTEL"
                        Phase.FINAL    -> "ATOM"
                    },
                    fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (phase) {
                        Phase.COLLAPSE -> Z10_RED
                        Phase.VICTORY  -> Z10_AMBER
                        else           -> Z10_CYAN
                    }
                )

                if (phase == Phase.VICTORY || phase == Phase.RESTORED || phase == Phase.FINAL) {
                    listOf("Lógica" to Z10_CYAN, "Patrones" to Z10_GREEN,
                        "Resolución" to Z10_AMBER).forEach { (label, col) ->
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp))
                                .background(col.copy(alpha = 0.15f))
                                .border(1.dp, col.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(label, fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = col)
                        }
                    }
                }
            }

            // Right — message panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(
                        1.5.dp,
                        when (phase) {
                            Phase.COLLAPSE -> Z10_RED.copy(alpha = 0.6f * glow.value)
                            Phase.VICTORY  -> Z10_AMBER.copy(alpha = 0.5f)
                            else           -> Z10_CYAN.copy(alpha = 0.4f)
                        },
                        RoundedCornerShape(18.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (phase) {
                    Phase.COLLAPSE -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z10_RED.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("⚠️ PORTAL INESTABLE", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Z10_RED)
                        }
                        Text("¡El portal se está cerrando!", fontSize = 20.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Sin energía y sin el Mapa de los Portales, la máquina del Glitch colapsa sobre sí misma, arrastrándolo de vuelta a su dimensión.",
                            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp)
                    }

                    Phase.VICTORY -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z10_AMBER.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("✅ EL GLITCH FUE ABSORBIDO", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Z10_AMBER)
                        }
                        Text("¡La Azotea está a salvo!", fontSize = 20.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("El Glitch grita mientras el portal lo absorbe de vuelta a su propia dimensión. La máquina queda reducida a chatarra.\n\nEl Mapa de los Portales de CodeLand está a salvo, de nuevo en sus manos.",
                            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp)
                    }

                    Phase.RESTORED -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z10_CYAN.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("📡 COMUNICACIONES RESTAURADAS", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Z10_CYAN)
                        }
                        Text("El Cuartel Central vuelve a la vida", fontSize = 18.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Las puertas blindadas se abren una a una. Las luces regresan, los sistemas se reinician y, por todo CodeLand, las comunicaciones vuelven a conectarse.\n\nDesde cada zona, los héroes que ayudaron en el camino celebran la noticia.",
                            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp)
                    }

                    Phase.FINAL -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z10_GREEN.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("🎉 MISIÓN CUMPLIDA", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Z10_GREEN)
                        }
                        Text("Mensaje de Atom", fontSize = 18.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("\"CodeLand está a salvo gracias a ustedes. En cada zona enfrentaron un reto distinto, pero siempre con la misma herramienta: pensar, aprender y resolver problemas, paso a paso.\n\nEsa es la verdadera magia de la programación. ¡Gracias por ser parte de esta aventura!\"",
                            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.9f), lineHeight = 19.sp)

                        Spacer(Modifier.weight(1f))
                        Text("Toca para volver al menú →", fontSize = 11.sp,
                            fontFamily = Baloo2FontFamily, color = Z10_GREEN.copy(alpha = 0.7f),
                            textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
