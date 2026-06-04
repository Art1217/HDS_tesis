package com.example.hds_tesisapp.ui.theme.games.game6

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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

private val ORANGE = Color(0xFFFF6D00)
private val AMBER  = Color(0xFFFFB300)
private val BG_TOP = Color(0xFF1A0A00)
private val BG_BOT = Color(0xFF3D1500)

@Composable
fun Zone6CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val glow = remember { Animatable(0.4f) }
    val charY = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        charY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
        while (true) { glow.animateTo(1f, tween(1000)); glow.animateTo(0.4f, tween(1000)) }
    }

    val emojis = listOf("⚙️", "🔧", "🤖", "⚡", "🏭", "🔩")
    val floatAnims = emojis.mapIndexed { i, _ ->
        val a = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(i * 200L)
            while (true) {
                a.animateTo(1f, tween(1300 + i * 120, easing = EaseInOut))
                a.animateTo(0f, tween(1300 + i * 120, easing = EaseInOut))
            }
        }
        a
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.factory_boss_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(BG_TOP.copy(alpha = 0.80f), BG_BOT.copy(alpha = 0.88f)))
        ))

        emojis.forEachIndexed { i, emoji ->
            val xOff = (i * 125f + 50f)
            val yOff = 35f + floatAnims[i].value * 28f + i * 52f
            Text(emoji, fontSize = 24.sp, modifier = Modifier.offset(xOff.dp, yOff.dp))
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Bit Maestro character
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .drawBehind {
                        drawCircle(ORANGE.copy(alpha = glow.value * 0.5f),
                            radius = size.minDimension / 2f + 22f)
                    }
                    .clip(CircleShape)
                    .background(ORANGE.copy(alpha = 0.15f))
                    .border(2.dp, AMBER.copy(alpha = glow.value * 0.9f + 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bit_maestro),
                    contentDescription = "Bit Maestro",
                    modifier = Modifier.fillMaxSize(0.85f),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ORANGE.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 6 COMPLETA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = AMBER, letterSpacing = 1.sp)
                }

                Text(
                    "¡La Fábrica de Bits\nvuelve a funcionar!",
                    fontSize = 26.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 32.sp
                )

                Text(
                    "Dominaste los bucles y venciste\nal Glitch Maestro. Bit Maestro\nagradece tu ayuda.",
                    fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.78f), lineHeight = 19.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("FOR", "WHILE", "Anidado", "Maestro").forEach { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ORANGE.copy(alpha = 0.20f))
                                .border(1.dp, AMBER.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(badge, fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = AMBER)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(ORANGE, Color(0xFFBF360C))))
                        .border(1.5.dp, AMBER.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .pointerInput(Unit) { detectTapGestures { onContinue() } }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("Volver al Menú →", fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
