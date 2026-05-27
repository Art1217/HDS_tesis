package com.example.hds_tesisapp.ui.theme.games.game7

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Z7_CYAN   = Color(0xFF00E5FF)
private val Z7_TEAL   = Color(0xFF00BFA5)
private val Z7_GREEN  = Color(0xFF00E676)
private val Z7_PURPLE = Color(0xFFCE93D8)
private val BG_TOP    = Color(0xFF001020)
private val BG_BOT    = Color(0xFF002840)

@Composable
fun Zone7CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val glow    = remember { Animatable(0.4f) }
    val visible = remember { mutableStateListOf(false, false, false, false) }

    LaunchedEffect(Unit) {
        launch { while (true) { glow.animateTo(1f, tween(900)); glow.animateTo(0.4f, tween(900)) } }
        delay(300L)
        visible.indices.forEach { i -> delay(250L); visible[i] = true }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(Unit) { detectTapGestures { onContinue() } }
    ) {
        // Floating particles
        val floatAnim = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            while (true) { floatAnim.animateTo(12f, tween(2000, easing = LinearEasing)); floatAnim.animateTo(0f, tween(2000, easing = LinearEasing)) }
        }

        val emojis = listOf("🔬", "⚗️", "🧪", "💡", "🧬", "✨")
        emojis.forEachIndexed { i, e ->
            val xFrac = (i * 0.17f + 0.03f).coerceIn(0f, 0.88f)
            val yBase = (i * 0.14f + 0.05f).coerceIn(0f, 0.8f)
            Text(
                e, fontSize = if (i % 2 == 0) 20.sp else 14.sp,
                modifier = Modifier.offset(
                    (xFrac * 800).dp,
                    (yBase * 450 + if (i % 2 == 0) floatAnim.value else -floatAnim.value).dp
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Character
            Column(
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .drawBehind {
                            drawCircle(Z7_CYAN.copy(alpha = glow.value * 0.4f),
                                radius = size.minDimension / 2f + 22f)
                        }
                        .clip(CircleShape)
                        .background(Z7_CYAN.copy(alpha = 0.10f))
                        .border(2.dp, Z7_TEAL.copy(alpha = glow.value * 0.85f + 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.lina_lab), "Lina",
                        Modifier.fillMaxSize(0.9f), contentScale = ContentScale.Fit)
                }
                Text("LINA", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Z7_TEAL)

                // Compound badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallBadge("COMP. A", Z7_GREEN)
                    SmallBadge("COMP. B", Z7_PURPLE)
                }
            }

            // Content panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, Z7_TEAL.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Zone badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Z7_CYAN.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 7 COMPLETADA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Z7_CYAN, letterSpacing = 1.sp)
                }

                Text("¡Laboratorio dominado!", fontSize = 22.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)

                Text(
                    "Los dos compuestos han activado la Sala Central.\nLina detectó un rastro del Glitch en los registros de la variable Energía.\n¡La persecución continúa!",
                    fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp
                )

                // Skill badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val badges = listOf(
                        "Variable" to Z7_CYAN,
                        "Asignación" to Z7_TEAL,
                        "Operaciones" to Z7_GREEN,
                        "Maestro Lab" to Z7_PURPLE
                    )
                    badges.forEach { (label, color) ->
                        if (visible[badges.indexOfFirst { it.first == label }.coerceAtLeast(0)]) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = 0.15f))
                                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold, color = color)
                            }
                        }
                    }
                }

                // Message chip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF003020).copy(alpha = 0.5f))
                        .border(1.dp, Z7_GREEN.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("📡 Mensaje del cuartel: «Buen trabajo, agente. Los cuarteles te esperan. El Glitch no tiene escapatoria.»",
                        fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Z7_GREEN.copy(alpha = 0.85f), lineHeight = 16.sp)
                }

                Spacer(Modifier.weight(1f))
                Text("Toca para continuar →", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = Z7_TEAL.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SmallBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 7.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = color)
    }
}
