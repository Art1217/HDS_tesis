package com.example.hds_tesisapp.ui.theme.games.game1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findTransitionActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findTransitionActivity()
    else -> null
}

private val TRANSITION_MESSAGES = mapOf(
    2 to "¡Increíble, el Bit llegó a casa!\n\nNunca olvidará el camino. " +
         "Pero ahora las calles del barrio norte están más complicadas " +
         "y hay un bloque de ERROR acechando en la esquina.\n\n" +
         "Un buen programador anticipa los obstáculos antes de escribir el código.",
    3 to "¡Superaste el callejón!\n\nEl Bit aprendió a no tomar caminos cortos. " +
         "La Ciudad Desordenada tiene rutas que parecen buenas pero llevan al caos.\n\n" +
         "¡Ojo con los caminos falsos del siguiente nivel!",
    4 to "¡Ya dominaste 3 niveles!\n\nGlitch puso obstáculos dobles y rutas falsas " +
         "para confundirte. Esta vez tendrás un comando extra en tu kit.\n\n" +
         "Piensa bien cada paso... y recuerda que a veces conviene retroceder.",
    5 to "¡Solo falta un desafío!\n\nMini-Glitch, agente del caos, ha tomado el control " +
         "del último paso de la Zona 1. Es más astuto que cualquier obstáculo que hayas visto.\n\n" +
         "Crea el algoritmo perfecto y prepárate... él intentará sabotearlo.",
)

@Composable
fun Game1TransitionScreen(toLevel: Int, onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findTransitionActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val dotCount  = 5
    val dotAlphas = remember { List(dotCount) { Animatable(0f) } }
    val cardScale = remember { Animatable(0.8f) }
    val cardAlpha = remember { Animatable(0f) }
    val glow      = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        for (dot in dotAlphas) {
            dot.animateTo(1f, tween(200))
            delay(120)
        }
        delay(200)
        launch { cardAlpha.animateTo(1f, tween(300)) }
        cardScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) {
            glow.animateTo(1f, tween(800))
            glow.animateTo(0.5f, tween(800))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001530), Color(0xFF000A1A)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated path dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dotAlphas.forEachIndexed { index, anim ->
                    val isLast = index == dotAlphas.size - 1
                    if (isLast) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .drawBehind {
                                    drawCircle(
                                        Color(0xFF69FF47).copy(alpha = anim.value * 0.4f),
                                        radius = size.minDimension / 2f + 6f
                                    )
                                }
                                .clip(CircleShape)
                                .background(Color(0xFF1B5E20).copy(alpha = anim.value))
                                .border(2.dp, Color(0xFF69FF47).copy(alpha = anim.value), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏠", fontSize = 13.sp,
                                modifier = Modifier.graphicsLayer { alpha = anim.value })
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .drawBehind {
                                    drawCircle(
                                        Color(0xFF00E5FF).copy(alpha = anim.value * 0.3f),
                                        radius = size.minDimension / 2f + 4f
                                    )
                                }
                                .clip(CircleShape)
                                .background(Color(0xFF00E5FF).copy(alpha = anim.value * 0.8f))
                        )
                        Text(
                            "›", fontSize = 16.sp,
                            color = Color(0xFF00E5FF).copy(alpha = anim.value * 0.6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Main card
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = cardScale.value; scaleY = cardScale.value; alpha = cardAlpha.value
                    }
                    .fillMaxWidth(0.72f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF020E1F))
                    .border(2.dp, Color(0xFF00E5FF).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "⭐  ¡NIVEL SUPERADO!  ⭐", fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold, fontFamily = OrbitronFontFamily,
                        color = Color(0xFF69FF47), textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        TRANSITION_MESSAGES[toLevel] ?: "¡Sigue adelante, el Bit te necesita!",
                        fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Preparando: NIVEL $toLevel",
                        fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFF00E5FF).copy(alpha = 0.7f), letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(24.dp))
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
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF388E3C)))
                            )
                            .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = { tryAwaitRelease(); onContinue() })
                            }
                            .padding(horizontal = 36.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "¡Al Nivel $toLevel!  →", fontSize = 15.sp,
                            fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
