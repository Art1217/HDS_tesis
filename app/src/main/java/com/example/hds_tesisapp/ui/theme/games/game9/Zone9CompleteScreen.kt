package com.example.hds_tesisapp.ui.theme.games.game9

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

private val Z9_CYAN   = Color(0xFF00BCD4)
private val Z9_GREEN  = Color(0xFF4CAF50)
private val Z9_RED    = Color(0xFFFF5252)
private val Z9_AMBER  = Color(0xFFFFC107)
private val BG_TOP    = Color(0xFF060E1A)
private val BG_BOT    = Color(0xFF0D1B30)

// Communication phases
private enum class Phase { SUCCESS, ANALYSIS, INTERRUPTION, ALERT }

@Composable
fun Zone9CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    val scope = rememberCoroutineScope()

    var phase        by remember { mutableStateOf(Phase.SUCCESS) }
    var showStatic   by remember { mutableStateOf(false) }
    var shakeOffset  by remember { mutableStateOf(0.dp to 0.dp) }
    var alertVisible by remember { mutableStateOf(false) }

    // Glow animation
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        launch { while (true) { glow.animateTo(1f, tween(800)); glow.animateTo(0.4f, tween(800)) } }

        // Phase timeline
        delay(1500L)
        phase = Phase.ANALYSIS
        delay(3000L)

        // Interruption: static + shake
        phase = Phase.INTERRUPTION
        repeat(6) {
            showStatic = true; delay(120L); showStatic = false; delay(80L)
        }
        // Earthquake shake
        repeat(8) { i ->
            val dx = if (i % 2 == 0) 4.dp else (-4).dp
            val dy = if (i % 3 == 0) 3.dp else (-2).dp
            shakeOffset = dx to dy; delay(60L)
        }
        shakeOffset = 0.dp to 0.dp

        delay(600L)
        showStatic = true; delay(300L); showStatic = false

        delay(500L)
        phase = Phase.ALERT
        alertVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .offset(shakeOffset.first, shakeOffset.second)
            .clickable { if (phase == Phase.ALERT) onContinue() }
    ) {
        Image(painterResource(R.drawable.workshop_bg), null,
            Modifier.fillMaxSize().graphicsLayer { alpha = 0.10f }, contentScale = ContentScale.Crop)

        // Static noise overlay
        if (showStatic) {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.08f)).zIndex(9f))
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(
                    Z9_RED.copy(alpha = 0.06f), Color.Transparent, Z9_CYAN.copy(alpha = 0.06f)
                ))
            ).zIndex(9f))
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Left panel — terminal / comms icon
            Column(
                modifier = Modifier.width(130.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(if (phase == Phase.ALERT) "🚨" else "📡", fontSize = 52.sp)
                Text(
                    text = when (phase) {
                        Phase.SUCCESS      -> "ZONA 9"
                        Phase.ANALYSIS     -> "CUARTELES"
                        Phase.INTERRUPTION -> "⚡ SEÑAL ⚡"
                        Phase.ALERT        -> "¡ALERTA!"
                    },
                    fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (phase) {
                        Phase.ALERT        -> Z9_RED
                        Phase.INTERRUPTION -> Z9_AMBER
                        else               -> Z9_CYAN
                    }
                )

                // Skill badges (shown on success/analysis)
                if (phase == Phase.SUCCESS || phase == Phase.ANALYSIS) {
                    listOf("Lógica" to Z9_CYAN, "Patrones" to Z9_GREEN,
                        "Corrección" to Z9_AMBER).forEach { (label, col) ->
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
                            Phase.ALERT        -> Z9_RED.copy(alpha = 0.6f * glow.value)
                            Phase.INTERRUPTION -> Z9_AMBER.copy(alpha = 0.5f)
                            else               -> Z9_CYAN.copy(alpha = 0.3f)
                        },
                        RoundedCornerShape(18.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (phase) {
                    Phase.SUCCESS -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z9_GREEN.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("✅ TALLER RESTAURADO", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, color = Z9_GREEN)
                        }
                        Text("El Taller de Correcciones está de nuevo en línea", fontSize = 20.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Todos los patrones han sido corregidos.\nEl Glitch perdió el control sobre la lógica del mundo natural.",
                            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.8f), lineHeight = 19.sp)
                        Text("Contactando con los cuarteles...", fontSize = 10.sp,
                            fontFamily = OrbitronFontFamily, color = Z9_CYAN.copy(alpha = 0.6f))
                    }

                    Phase.ANALYSIS -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z9_CYAN.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("📡 CUARTELES EN LÍNEA", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, color = Z9_CYAN)
                        }
                        Text("Análisis de huellas: COMPLETO", fontSize = 18.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("El Glitch no pertenece a esta dimensión.\nNo podemos saber su ubicación exacta, pero gracias a las huellas identificamos los lugares donde ha estado.\n\nPronto podremos anticipar sus próximos movimientos.",
                            fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.85f), lineHeight = 18.sp)
                    }

                    Phase.INTERRUPTION -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z9_AMBER.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("⚡ INTERFERENCIA DE SEÑAL", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, color = Z9_AMBER)
                        }
                        Text("...las huellas ya no...  ████████  ...ser nece-", fontSize = 16.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                            color = Z9_AMBER.copy(alpha = 0.9f))
                        Text("...████ cuartel ha sido... ██████... vuelvan... lo más...",
                            fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.6f), lineHeight = 18.sp)
                        // Sismo note
                        Box(Modifier.clip(RoundedCornerShape(6.dp))
                            .background(Z9_RED.copy(alpha = 0.12f))
                            .border(1.dp, Z9_RED.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("⚠️ Sismo leve detectado en la red", fontSize = 8.sp,
                                fontFamily = OrbitronFontFamily, color = Z9_RED)
                        }
                    }

                    Phase.ALERT -> {
                        Box(Modifier.clip(RoundedCornerShape(5.dp))
                            .background(Z9_RED.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)) {
                            Text("🚨 ALERTA MÁXIMA", fontSize = 9.sp,
                                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                                color = Z9_RED)
                        }
                        Text("¡EL CUARTEL HA SIDO TOMADO!", fontSize = 20.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                            color = Z9_RED)
                        Text("Las huellas ya no son necesarias.\nEl Glitch ha invadido los cuarteles.\n¡Debemos regresar de inmediato!",
                            fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)

                        Spacer(Modifier.weight(1f))
                        Text("Toca para continuar →", fontSize = 11.sp,
                            fontFamily = Baloo2FontFamily, color = Z9_RED.copy(alpha = 0.7f),
                            textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
