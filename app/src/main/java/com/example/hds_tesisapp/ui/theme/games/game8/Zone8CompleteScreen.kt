package com.example.hds_tesisapp.ui.theme.games.game8

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

private val Z8_CYAN   = Color(0xFF00E5FF)
private val Z8_ORANGE = Color(0xFFFF9800)
private val Z8_GREEN  = Color(0xFF69FF47)
private val Z8_PURPLE = Color(0xFF9C27B0)
private val BG_TOP    = Color(0xFF000D1A)
private val BG_BOT    = Color(0xFF001530)

@Composable
fun Zone8CompleteScreen(onContinue: () -> Unit) {
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

    val floatAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) { floatAnim.animateTo(10f, tween(1800, easing = LinearEasing)); floatAnim.animateTo(0f, tween(1800, easing = LinearEasing)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(Unit) { detectTapGestures { onContinue() } }
    ) {
        // Background city image tinted
        Image(painterResource(R.drawable.city_bg), null,
            Modifier.fillMaxSize().graphicsLayer { alpha = 0.18f }, contentScale = ContentScale.Crop)

        // Floating emojis
        listOf("🏙️", "⚡", "🤖", "🔧", "💥", "✨").forEachIndexed { i, e ->
            val xFrac = (i * 0.17f + 0.03f).coerceIn(0f, 0.87f)
            val yBase = (i * 0.13f + 0.05f).coerceIn(0f, 0.8f)
            Text(e, fontSize = if (i % 2 == 0) 20.sp else 14.sp,
                modifier = Modifier.offset(
                    (xFrac * 800).dp,
                    (yBase * 450 + if (i % 2 == 0) floatAnim.value else -floatAnim.value).dp
                ))
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Left column: heroes + defeated boss
            Column(
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 2x2 hero grid
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(R.drawable.max_city to Color(0xFF2196F3), R.drawable.lina_city to Z8_CYAN).forEach { (res, col) ->
                        Box(
                            modifier = Modifier.size(60.dp)
                                .drawBehind { drawCircle(col.copy(alpha = glow.value * 0.3f), radius = size.minDimension / 2f + 8f) }
                                .clip(CircleShape)
                                .background(col.copy(alpha = 0.12f))
                                .border(2.dp, col.copy(alpha = glow.value * 0.7f + 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painterResource(res), null, Modifier.fillMaxSize(0.85f), contentScale = ContentScale.Fit)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(R.drawable.tom_city to Z8_ORANGE, R.drawable.atom_city to Z8_PURPLE).forEach { (res, col) ->
                        Box(
                            modifier = Modifier.size(60.dp)
                                .drawBehind { drawCircle(col.copy(alpha = glow.value * 0.3f), radius = size.minDimension / 2f + 8f) }
                                .clip(CircleShape)
                                .background(col.copy(alpha = 0.12f))
                                .border(2.dp, col.copy(alpha = glow.value * 0.7f + 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painterResource(res), null, Modifier.fillMaxSize(0.85f), contentScale = ContentScale.Fit)
                        }
                    }
                }
                Text("LOS 4 HÉROES", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Z8_CYAN)

                // Defeated boss
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A0000).copy(alpha = 0.7f))
                        .border(1.5.dp, Color(0xFFFF1744).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Image(
                            painterResource(R.drawable.bossgame8), "El Glitch",
                            Modifier
                                .height(64.dp)
                                .graphicsLayer { alpha = 0.75f; scaleX = -1f },
                            contentScale = ContentScale.Fit
                        )
                        Text("⚡ GLITCH\nDERROTADO", fontSize = 7.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = Color(0xFFFF1744),
                            textAlign = TextAlign.Center)
                    }
                }
            }

            // Content panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, Z8_ORANGE.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Z8_CYAN.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 8 COMPLETADA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Z8_CYAN, letterSpacing = 1.sp)
                }

                Text("¡Ciudad de las Reacciones: Salvada!", fontSize = 20.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)

                Text(
                    "Los 4 héroes trabajaron juntos para controlar todos los eventos.\nEl Glitch intentó mutar los objetos, pero no fue suficiente.\n¡La ciudad futurista vuelve a funcionar!",
                    fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 19.sp
                )

                // Skill badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val badges = listOf("Eventos" to Z8_CYAN, "Reacciones" to Z8_ORANGE,
                        "Condiciones" to Z8_GREEN, "Maestro Ciudad" to Z8_PURPLE)
                    badges.forEachIndexed { i, (label, color) ->
                        if (i < visible.size && visible[i]) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(color.copy(alpha = 0.15f))
                                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)) {
                                Text(label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold, color = color)
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF001030).copy(alpha = 0.5f))
                    .border(1.dp, Z8_GREEN.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)) {
                    Text("📡 «Los cuarteles reportan: El Glitch huye hacia la zona 9. ¡La persecución continúa!»",
                        fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Z8_GREEN.copy(alpha = 0.85f), lineHeight = 16.sp)
                }

                Spacer(Modifier.weight(1f))
                Text("Toca para continuar →", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = Z8_ORANGE.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
