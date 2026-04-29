package com.example.hds_tesisapp.ui.theme.story

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import kotlinx.coroutines.delay

private fun Context.findActivityZone(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityZone()
    else -> null
}

@Composable
fun ZoneIntroScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context.findActivityZone() }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Animaciones de entrada
    val badgeAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.7f) }
    val descAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }
    val illustrationAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(200)
        badgeAlpha.animateTo(1f, tween(400))
        delay(150)
        titleAlpha.animateTo(1f, tween(500))
        titleScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        delay(200)
        illustrationAlpha.animateTo(1f, tween(500))
        delay(100)
        descAlpha.animateTo(1f, tween(400))
        delay(200)
        buttonAlpha.animateTo(1f, tween(400))
    }

    // Pulso del botón
    val buttonGlow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) {
            buttonGlow.animateTo(1f, tween(800))
            buttonGlow.animateTo(0.5f, tween(800))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF000A1A), Color(0xFF001830), Color(0xFF000A1A))
                )
            )
    ) {
        // Líneas decorativas de fondo (efecto ciudad digital)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    // Líneas horizontales tenues
                    for (i in 0..12) {
                        drawLine(
                            color = Color(0xFF00E5FF).copy(alpha = 0.04f),
                            start = Offset(0f, h * i / 12f),
                            end = Offset(w, h * i / 12f),
                            strokeWidth = 1f
                        )
                    }
                    // Líneas verticales tenues
                    for (i in 0..20) {
                        drawLine(
                            color = Color(0xFF00E5FF).copy(alpha = 0.03f),
                            start = Offset(w * i / 20f, 0f),
                            end = Offset(w * i / 20f, h),
                            strokeWidth = 1f
                        )
                    }
                }
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // === LADO IZQUIERDO: Texto y botón ===
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // Badge "ZONA 1"
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = badgeAlpha.value }
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ZONA 1  ·  ALGORITMOS",
                        fontSize = 12.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título principal
                Text(
                    text = "La Ciudad\nDesordenada",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    color = Color.White,
                    lineHeight = 44.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = titleAlpha.value
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Descripción
                Text(
                    text = "El ataque de Glitch ha sembrado el caos.\nCalles torcidas, semáforos rotos, puertas\nen lugares imposibles. ¡Los Bits necesitan tu ayuda!",
                    fontSize = 15.sp,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.80f),
                    lineHeight = 22.sp,
                    modifier = Modifier.graphicsLayer { alpha = descAlpha.value }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón "¡Comenzar!"
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = buttonAlpha.value }
                        .width(220.dp)
                        .height(56.dp)
                        .drawBehind {
                            // Glow exterior
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = buttonGlow.value * 0.3f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
                            )
                        )
                        .border(
                            2.dp,
                            Color(0xFF69FF47).copy(alpha = buttonGlow.value),
                            RoundedCornerShape(16.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                tryAwaitRelease()
                                navController.navigate(Routes.AlgorithmTutorial.route) {
                                    popUpTo(Routes.ZoneIntro.route) { inclusive = true }
                                }
                            })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¡COMENZAR!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(32.dp))

            // === LADO DERECHO: Video en bucle (crop para llenar sin barras negras) ===
            val mediaPlayerRef = remember { mutableStateOf<MediaPlayer?>(null) }
            DisposableEffect(Unit) {
                onDispose {
                    mediaPlayerRef.value?.apply {
                        if (isPlaying) stop()
                        release()
                    }
                    mediaPlayerRef.value = null
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight(0.85f)
                    .graphicsLayer { alpha = illustrationAlpha.value }
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        2.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFF00E5FF).copy(alpha = 0.5f), Color(0xFF00E5FF).copy(alpha = 0.1f))
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                AndroidView(
                    factory = { ctx ->
                        SurfaceView(ctx).apply {
                            holder.addCallback(object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    val mp = MediaPlayer().apply {
                                        setDataSource(
                                            ctx,
                                            Uri.parse("android.resource://${ctx.packageName}/${R.raw.la_ciudad_desordenada}")
                                        )
                                        setDisplay(holder)
                                        setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                                        isLooping = true
                                        prepareAsync()
                                        setOnPreparedListener { start() }
                                    }
                                    mediaPlayerRef.value = mp
                                }
                                override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, ht: Int) {}
                                override fun surfaceDestroyed(h: SurfaceHolder) {
                                    mediaPlayerRef.value?.apply { if (isPlaying) stop(); release() }
                                    mediaPlayerRef.value = null
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
