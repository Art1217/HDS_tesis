package com.example.hds_tesisapp.ui.theme.games.level1

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch


// ============================================================
//  Dialog de Atom — usado para tutorial, ayuda y errores
// ============================================================
@Composable
fun Level1AtomDialog(message: String, onDismiss: () -> Unit) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(220)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    fun dismiss() {
        scope.launch {
            launch { alpha.animateTo(0f, tween(180)) }
            scale.animateTo(0.85f, tween(180))
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .zIndex(10f)
            .pointerInput(Unit) { /* bloquea toques al fondo */ },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.76f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen de Atom
                Image(
                    painter = painterResource(id = R.drawable.atom),
                    contentDescription = "Atom",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ATOM",
                        fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        fontFamily = Baloo2FontFamily,
                        color = Color.White,
                        lineHeight = 21.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF00838F), Color(0xFF00BCD4)))
                            )
                            .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = { tryAwaitRelease(); dismiss() })
                            }
                            .padding(horizontal = 22.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "¡Entendido!",
                            fontSize = 14.sp,
                            fontFamily = Baloo2FontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
//  Pantalla de victoria
// ============================================================
@Composable
fun Level1VictoryOverlay(onNext: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val starScale = remember { Animatable(0f) }
    val glow = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(300)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        starScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow))
        while (true) {
            glow.animateTo(1f, tween(700))
            glow.animateTo(0.6f, tween(700))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.58f)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF001830), Color(0xFF002040))))
                .border(2.5.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(28.dp))
                .padding(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "⭐  ⭐  ⭐",
                    fontSize = 38.sp,
                    modifier = Modifier.graphicsLayer { scaleX = starScale.value; scaleY = starScale.value }
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "¡NIVEL COMPLETADO!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    color = Color(0xFF69FF47),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "¡El Bit llegó a casa!\nCreaste tu primer algoritmo.",
                    fontSize = 14.sp,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.35f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 22f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = { tryAwaitRelease(); onNext() })
                        }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Siguiente Nivel  →",
                        fontSize = 15.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


// ============================================================
//  Botón de ayuda [?]
// ============================================================
@Composable
fun Level1HelpButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) {
            glow.animateTo(1f, tween(900))
            glow.animateTo(0.5f, tween(900))
        }
    }
    Box(
        modifier = modifier
            .size(46.dp)
            .drawBehind {
                drawCircle(
                    color = Color(0xFFFFD600).copy(alpha = glow.value * 0.25f),
                    radius = size.minDimension / 2f + 8f
                )
            }
            .clip(CircleShape)
            .background(Color(0xFF0A0F1F))
            .border(2.dp, Color(0xFFFFD600).copy(alpha = glow.value), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { tryAwaitRelease(); onClick() })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = Color(0xFFFFD600)
        )
    }
}
