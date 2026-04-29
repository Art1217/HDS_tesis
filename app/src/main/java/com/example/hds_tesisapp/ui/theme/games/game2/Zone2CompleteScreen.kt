package com.example.hds_tesisapp.ui.theme.games.game2

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

private fun Context.findZone2Activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findZone2Activity()
    else -> null
}

@Composable
fun Zone2CompleteScreen(onContinue: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findZone2Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val cardAlpha  = remember { Animatable(0f) }
    val cardScale  = remember { Animatable(0.8f) }
    val glow       = remember { Animatable(0.5f) }
    val treeAlphas = remember { List(4) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        treeAlphas.forEachIndexed { i, anim ->
            launch { delay(i * 200L); anim.animateTo(1f, tween(600)) }
        }
        delay(900)
        launch { cardAlpha.animateTo(1f, tween(400)) }
        cardScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) {
            glow.animateTo(1f, tween(800))
            glow.animateTo(0.5f, tween(800))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Forest background
        Image(
            painter = painterResource(R.drawable.forest_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        // Animated trees at bottom
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val treeRes = listOf(R.drawable.tree_manzana, R.drawable.tree_naranja, R.drawable.tree_limon, R.drawable.tree_manzana)
            treeRes.forEachIndexed { i, res ->
                Image(
                    painter = painterResource(res),
                    contentDescription = null,
                    modifier = Modifier
                        .height(120.dp)
                        .width(90.dp)
                        .graphicsLayer { alpha = treeAlphas[i].value },
                    contentScale = ContentScale.Fit
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
                .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐  ⭐  ⭐", fontSize = 26.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "¡ZONA 2 COMPLETADA!",
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = Color(0xFF69FF47),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.lina),
                        contentDescription = "Lina",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "LINA", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold, color = Color(0xFF69FF47),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "\"Clasificar bien es el primer paso\npara entender el mundo.\"",
                            fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF001830))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        "🔓  ZONA 3 DESBLOQUEADA",
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        color = Color(0xFF00E5FF).copy(alpha = 0.85f), letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.3f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                            )
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))))
                        .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(14.dp))
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
