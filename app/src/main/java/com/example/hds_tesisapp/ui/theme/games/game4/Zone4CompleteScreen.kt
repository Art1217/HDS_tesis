package com.example.hds_tesisapp.ui.theme.games.game4

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

private val LIME   = Color(0xFF8BC34A)
private val LIME_D = Color(0xFF33691E)
private val GOLD   = Color(0xFFFFD600)

private fun Context.findZ4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findZ4Activity()
    else              -> null
}

@Composable
fun Zone4CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findZ4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val cardAlpha   = remember { Animatable(0f) }
    val cardScale   = remember { Animatable(0.8f) }
    val glow        = remember { Animatable(0.5f) }
    val starAlphas  = remember { List(5) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        starAlphas.forEachIndexed { i, anim ->
            launch { delay(i * 160L); anim.animateTo(1f, tween(400)) }
        }
        delay(900)
        launch { cardAlpha.animateTo(1f, tween(400)) }
        cardScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) { glow.animateTo(1f, tween(800)); glow.animateTo(0.5f, tween(800)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.valley_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.50f)))

        // Floating pattern symbols
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            listOf("🌸", "🔵", "🔴", "🟢", "✨").forEachIndexed { i, e ->
                Text(e, fontSize = 28.sp,
                    modifier = Modifier.graphicsLayer { alpha = starAlphas[i].value })
            }
        }

        // Main card
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-10).dp)
                .graphicsLayer { scaleX = cardScale.value; scaleY = cardScale.value; alpha = cardAlpha.value }
                .fillMaxWidth(0.78f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F).copy(0.96f))
                .border(2.dp, LIME.copy(glow.value), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐  ⭐  ⭐  ⭐  ⭐", fontSize = 22.sp)
                Spacer(Modifier.height(8.dp))
                Text("¡ZONA 4 COMPLETADA!", fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, fontFamily = OrbitronFontFamily,
                    color = LIME, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.lina_character),
                        contentDescription = "Lina",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("LINA", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = LIME, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "\"¡Lo lograste! Los patrones ya no\ntienen secretos para ti.\"",
                            fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(0.90f),
                            lineHeight = 20.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1A00))
                        .border(1.dp, LIME.copy(0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅  Patrones aprendidos:", fontSize = 10.sp,
                            fontFamily = OrbitronFontFamily, color = LIME.copy(0.85f))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "AB  ·  ABC  ·  AAB / ABB  ·  Distractores  ·  Detección de errores",
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
                                color = LIME.copy(glow.value * 0.30f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                            )
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(LIME_D, Color(0xFF558B2F))))
                        .border(2.dp, LIME.copy(glow.value), RoundedCornerShape(14.dp))
                        .clickable(onClick = onContinue)
                        .padding(horizontal = 32.dp, vertical = 10.dp)
                ) {
                    Text("Volver al Menú  →", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                        color = Color.White)
                }
            }
        }
    }
}
