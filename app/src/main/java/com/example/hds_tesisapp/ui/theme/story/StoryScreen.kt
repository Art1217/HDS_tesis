package com.example.hds_tesisapp.ui.theme.story

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import kotlinx.coroutines.launch

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// Orden de páginas: imagen drawable → audio raw
// Para agregar tu audio: reemplaza res/raw/story_page_N.mp3 con tu narración
private val storyPages = listOf(
    Pair(R.drawable.m1_0,   R.raw.story_page_1),
    Pair(R.drawable.m1_0_5, R.raw.story_page_2),
    Pair(R.drawable.m1_1_1, R.raw.story_page_3),
    Pair(R.drawable.m1_2,   R.raw.story_page_4),
    Pair(R.drawable.m1_3,   R.raw.story_page_5),
    Pair(R.drawable.m1_4,   R.raw.story_page_6),
    Pair(R.drawable.m1_5,   R.raw.story_page_7),
    Pair(R.drawable.m1_6,   R.raw.story_page_8),
    Pair(R.drawable.m1_7,   R.raw.story_page_9),
)

@Composable
fun StoryScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var currentPage by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val pageAlpha = remember { Animatable(1f) }
    val pageTranslationX = remember { Animatable(0f) }
    val pageRotationY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun playAudio(page: Int) {
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
        try {
            MediaPlayer.create(context, storyPages[page].second)?.let { mp ->
                mp.start()
                mediaPlayer = mp
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(currentPage) {
        playAudio(currentPage)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.apply { if (isPlaying) stop(); release() }
        }
    }

    fun goNext() {
        if (isAnimating) return
        isAnimating = true
        scope.launch {
            // Detener audio actual
            mediaPlayer?.apply { if (isPlaying) stop(); release() }
            mediaPlayer = null

            // Salida: desliza y dobla hacia la izquierda (efecto hoja de libro)
            launch { pageAlpha.animateTo(0f, tween(280)) }
            launch { pageRotationY.animateTo(-20f, tween(280)) }
            pageTranslationX.animateTo(-500f, tween(280))

            if (currentPage < storyPages.size - 1) {
                currentPage++
                // Posicionar nueva página al lado derecho
                pageTranslationX.snapTo(500f)
                pageRotationY.snapTo(20f)

                // Entrada: desliza desde la derecha con efecto libro
                launch { pageAlpha.animateTo(1f, tween(320)) }
                launch { pageRotationY.animateTo(0f, tween(320)) }
                pageTranslationX.animateTo(
                    0f,
                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                )
            } else {
                // Última página terminada → al juego
                navController.navigate(Routes.Game.route) {
                    popUpTo(Routes.Story.route) { inclusive = true }
                }
            }
            isAnimating = false
        }
    }

    // Rebote continuo de la flecha "siguiente"
    val arrowBounce = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            arrowBounce.animateTo(10f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            arrowBounce.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imagen de la página actual
        Image(
            painter = painterResource(id = storyPages[currentPage].first),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = pageAlpha.value
                    translationX = pageTranslationX.value
                    rotationY = pageRotationY.value
                    cameraDistance = 12f * density
                },
            contentScale = ContentScale.Fit
        )

        // Botón siguiente — esquina derecha con rebote
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-14).dp + arrowBounce.value.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.50f))
                .border(2.dp, Color.White.copy(alpha = 0.80f), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        tryAwaitRelease()
                        goNext()
                    })
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Siguiente página",
                tint = Color.White,
                modifier = Modifier
                    .size(34.dp)
                    .padding(start = 4.dp)
            )
        }
    }
}
