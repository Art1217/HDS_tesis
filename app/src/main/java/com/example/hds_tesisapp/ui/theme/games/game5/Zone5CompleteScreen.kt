package com.example.hds_tesisapp.ui.theme.games.game5

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

private val PURPLE    = Color(0xFF9C27B0)
private val PURPLE_LT = Color(0xFFCE93D8)
private val BG_TOP    = Color(0xFF0D0020)
private val BG_BOT    = Color(0xFF260050)

@Composable
fun Zone5CompleteScreen(onContinue: () -> Unit) {
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

    val emojis = listOf("🔵", "🟢", "🔴", "🟣", "⚡", "🌀")
    val floatAnims = emojis.mapIndexed { i, _ ->
        val a = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(i * 220L)
            while (true) {
                a.animateTo(1f, tween(1400 + i * 130, easing = EaseInOut))
                a.animateTo(0f, tween(1400 + i * 130, easing = EaseInOut))
            }
        }
        a
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.portal_room_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(BG_TOP.copy(alpha = 0.75f), BG_BOT.copy(alpha = 0.85f)))
        ))

        // Floating portal emojis
        emojis.forEachIndexed { i, emoji ->
            val xOff = (i * 130f + 60f)
            val yOff = 40f + floatAnims[i].value * 30f + i * 55f
            Text(
                emoji, fontSize = 26.sp,
                modifier = Modifier.offset(xOff.dp, yOff.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Lina character
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .drawBehind {
                        drawCircle(PURPLE.copy(alpha = glow.value * 0.5f), radius = size.minDimension / 2f + 22f)
                    }
                    .clip(CircleShape)
                    .background(PURPLE.copy(alpha = 0.18f))
                    .border(2.dp, PURPLE_LT.copy(alpha = glow.value * 0.9f + 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.lina_character),
                    contentDescription = "Lina",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Text content
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PURPLE.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 5 COMPLETA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = PURPLE_LT, letterSpacing = 1.sp)
                }

                Text(
                    "¡La Ciudad de los\nPortales está libre!",
                    fontSize = 26.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 32.sp
                )

                Text(
                    "Dominaste las condiciones y\nvenciste al Glitch. Los portales\nvuelven a funcionar.",
                    fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.78f), lineHeight = 19.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("IF", "ELSE", "AND", "Anidado").forEach { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PURPLE.copy(alpha = 0.2f))
                                .border(1.dp, PURPLE_LT.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(badge, fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold, color = PURPLE_LT)
                        }
                    }
                }

                // Continue button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(PURPLE, Color(0xFF6A1B9A))))
                        .border(1.5.dp, PURPLE_LT.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
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
