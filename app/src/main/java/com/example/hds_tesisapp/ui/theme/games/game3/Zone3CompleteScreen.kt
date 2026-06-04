package com.example.hds_tesisapp.ui.theme.games.game3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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

private fun Context.findZone3Activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findZone3Activity()
    else -> null
}

@Composable
fun Zone3CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findZone3Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val cardAlpha  = remember { Animatable(0f) }
    val cardScale  = remember { Animatable(0.8f) }
    val glow       = remember { Animatable(0.5f) }
    val dropAlphas = remember { List(4) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        dropAlphas.forEachIndexed { i, anim ->
            launch { delay(i * 180L); anim.animateTo(1f, tween(500)) }
        }
        delay(800)
        launch { cardAlpha.animateTo(1f, tween(400)) }
        cardScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) {
            glow.animateTo(1f, tween(800))
            glow.animateTo(0.5f, tween(800))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.waterfall_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))

        // Animated water drops at bottom
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val dropEmojis = listOf("💧", "💧", "💧", "💧")
            dropEmojis.forEachIndexed { i, emoji ->
                Text(
                    emoji, fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { alpha = dropAlphas[i].value }
                )
            }
        }

        // Card
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-16).dp)
                .graphicsLayer { scaleX = cardScale.value; scaleY = cardScale.value; alpha = cardAlpha.value }
                .fillMaxWidth(0.75f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F).copy(alpha = 0.96f))
                .border(2.dp, Color(0xFF00E5FF).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐  ⭐  ⭐", fontSize = 26.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "¡ZONA 3 COMPLETADA!",
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = Color(0xFF00E5FF),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.tom_y_atom),
                        contentDescription = "Tom Atom",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "TOM ATOM", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "\"Ordenar es la base de todo.\nBubble Sort, el primero de muchos.\"",
                            fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // What was learned
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF001828))
                        .border(1.dp, Color(0xFF00B0FF).copy(0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅  Ordenamiento aprendido:", fontSize = 10.sp,
                            fontFamily = OrbitronFontFamily, color = Color(0xFF00E5FF).copy(0.85f))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Comparación por pares  ·  Bubble Sort  ·  Pasadas sucesivas",
                            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(0.75f), textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF00E5FF).copy(alpha = glow.value * 0.3f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                            )
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B))))
                        .border(2.dp, Color(0xFF00E5FF).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                        .clickable(onClick = onContinue)
                        .padding(horizontal = 32.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Volver al Menú  →", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
