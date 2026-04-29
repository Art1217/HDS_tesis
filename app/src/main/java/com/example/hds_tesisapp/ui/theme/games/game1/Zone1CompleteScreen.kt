package com.example.hds_tesisapp.ui.theme.games.game1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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

private fun Context.findZone1Activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findZone1Activity()
    else -> null
}

// Datos de los edificios: (máxima altura en dp, color)
private val BUILDINGS = listOf(
    Pair(80,  Color(0xFF003060)),
    Pair(140, Color(0xFF004080)),
    Pair(100, Color(0xFF0050A0)),
    Pair(160, Color(0xFF003870)),
    Pair(90,  Color(0xFF0058B0)),
    Pair(120, Color(0xFF002850)),
    Pair(110, Color(0xFF004490)),
    Pair(70,  Color(0xFF0062C0)),
)

@Composable
fun Zone1CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findZone1Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Animatables
    val buildingProgress = remember { BUILDINGS.map { Animatable(0f) } }
    val pathDotAlphas    = remember { List(10) { Animatable(0f) } }
    val starAlphas       = remember { List(12) { Animatable(0f) } }
    val cardAlpha        = remember { Animatable(0f) }
    val cardScale        = remember { Animatable(0.85f) }
    val glow             = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        // 1. Estrellas aparecen
        for (star in starAlphas) {
            launch { delay((Math.random() * 400).toLong()); star.animateTo(1f, tween(500)) }
        }
        delay(300)
        // 2. Edificios crecen escalonados
        buildingProgress.forEachIndexed { i, anim ->
            launch { delay(i * 140L); anim.animateTo(1f, tween(700)) }
        }
        delay(900)
        // 3. Luces del camino aparecen
        for (dot in pathDotAlphas) {
            dot.animateTo(1f, tween(120))
            delay(70)
        }
        delay(400)
        // 4. Tarjeta de Tom Atom aparece
        launch { cardAlpha.animateTo(1f, tween(400)) }
        cardScale.animateTo(1f, tween(500))
        // 5. Glow pulsante infinito
        while (true) {
            glow.animateTo(1f, tween(800))
            glow.animateTo(0.5f, tween(800))
        }
    }

    // Posiciones aleatorias (pero fijas) para las estrellas
    val starPositions = remember {
        List(12) { Pair((Math.random() * 0.9 + 0.05).toFloat(), (Math.random() * 0.4).toFloat()) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF000510), Color(0xFF000A1A), Color(0xFF001530)))
            )
    ) {

        // ── Estrellas ────────────────────────────────────────────────────────
        starAlphas.forEachIndexed { i, anim ->
            val (xFrac, yFrac) = starPositions[i]
            val size = if (i % 3 == 0) 4.dp else 2.dp
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = anim.value }
            ) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (xFrac * 100).toInt().dp,
                            y = (yFrac * 100).toInt().dp
                        )
                        .size(size)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                )
            }
        }

        // ── Edificios ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                buildingProgress.forEachIndexed { i, anim ->
                    val (maxHDp, color) = BUILDINGS[i]
                    val animH = (maxHDp * anim.value).dp
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(animH)
                            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0.5f))
                                )
                            )
                            .drawBehind {
                                // Ventanas iluminadas
                                val winW = size.width / 4f
                                val winH = 8.dp.toPx()
                                val gap  = 14.dp.toPx()
                                var yy = 8.dp.toPx()
                                while (yy + winH < size.height - 4.dp.toPx()) {
                                    drawRect(
                                        color = Color(0xFFFFD600).copy(alpha = 0.55f * anim.value),
                                        topLeft = androidx.compose.ui.geometry.Offset(winW * 0.6f, yy),
                                        size = androidx.compose.ui.geometry.Size(winW, winH)
                                    )
                                    drawRect(
                                        color = Color(0xFF00E5FF).copy(alpha = 0.40f * anim.value),
                                        topLeft = androidx.compose.ui.geometry.Offset(winW * 2.2f, yy),
                                        size = androidx.compose.ui.geometry.Size(winW, winH)
                                    )
                                    yy += gap
                                }
                            }
                    )
                }
            }
        }

        // ── Luces del camino ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pathDotAlphas.forEach { anim ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .drawBehind {
                            drawCircle(Color(0xFF00E5FF).copy(alpha = anim.value * 0.5f),
                                radius = size.minDimension / 2f + 4f)
                        }
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF).copy(alpha = anim.value))
                )
            }
        }

        // ── Tarjeta de Tom Atom ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
                .graphicsLayer { scaleX = cardScale.value; scaleY = cardScale.value; alpha = cardAlpha.value }
                .fillMaxWidth(0.78f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F).copy(alpha = 0.95f))
                .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // ⭐ y título
                Text("⭐  ⭐  ⭐", fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "¡ZONA 1 COMPLETADA!",
                    fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = Color(0xFF69FF47),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))

                // Tom Atom + quote
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.atom),
                        contentDescription = "Tom Atom",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "TOM ATOM", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF), letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "\"Con un buen algoritmo,\ntodo funciona mejor.\"",
                            fontSize = 15.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.92f),
                            lineHeight = 22.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Badge Zona 2
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF001830))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        "🔓  ZONA 2 DESBLOQUEADA",
                        fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFF00E5FF).copy(alpha = 0.8f), letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Botón continuar
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.30f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                            )
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = { tryAwaitRelease(); onContinue() })
                        }
                        .padding(horizontal = 36.dp, vertical = 12.dp)
                ) {
                    Text(
                        "¡Al Bosque!  →", fontSize = 14.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
