package com.example.hds_tesisapp.ui.theme.games.game4.level1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.hds_tesisapp.ui.theme.games.game4.PatternSymbol
import com.example.hds_tesisapp.ui.theme.games.game4.buildABRound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findL1G4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findL1G4Activity()
    else              -> null
}

private val LIME   = Color(0xFF8BC34A)
private val LIME_D = Color(0xFF33691E)

@Composable
fun Level1G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL1G4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val TOTAL = 5
    var roundIndex   by remember { mutableIntStateOf(0) }
    var lives        by remember { mutableIntStateOf(3) }
    var flash        by remember { mutableStateOf<Boolean?>(null) }
    var levelDone    by remember { mutableStateOf(false) }
    var gameOver     by remember { mutableStateOf(false) }

    val round = remember(roundIndex) { buildABRound(roundIndex) }
    val scope = rememberCoroutineScope()

    fun onPick(sym: PatternSymbol) {
        if (flash != null || levelDone || gameOver) return
        val ok = sym.id == round.answer.id
        flash = ok
        scope.launch {
            delay(700)
            if (ok) {
                if (roundIndex + 1 >= TOTAL) levelDone = true
                else roundIndex++
            } else {
                lives--
                if (lives <= 0) gameOver = true
            }
            flash = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.valley_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.52f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background(
                if (ok) Color(0xFF69FF47).copy(0.22f) else Color(0xFFFF5252).copy(0.22f)))
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuBtn(onClick = onNavigateToMenu)
                ZoneBadge("ZONA 4  ·  NIVEL 1", LIME)
                LivesRow(lives)
            }

            Spacer(Modifier.height(10.dp))

            Text("COMPLETA EL PATRÓN", fontSize = 14.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                color = LIME, letterSpacing = 2.sp)

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                round.sequence.forEach { sym -> PatternCell(sym) }
                QuestionCell(LIME)
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                round.options.forEach { opt ->
                    OptionBtn(opt, LIME, LIME_D) { onPick(opt) }
                }
            }

            Spacer(Modifier.weight(1f))

            RoundDots(roundIndex, TOTAL, LIME)
        }

        if (levelDone) DoneOverlay(LIME) { onLevelComplete() }
        if (gameOver)  FailOverlay(LIME) {
            roundIndex = 0; lives = 3; flash = null; gameOver = false
        }
    }
}

// ── Shared composables ────────────────────────────────────────────────────────

@Composable
internal fun MenuBtn(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(0.10f))
            .border(1.dp, Color.White.copy(0.22f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); onClick() }) },
        contentAlignment = Alignment.Center
    ) { Text("≡", fontSize = 18.sp, color = Color.White.copy(0.80f)) }
}

@Composable
internal fun ZoneBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(0.12f))
            .border(1.dp, color.copy(0.45f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            color = color.copy(0.85f), letterSpacing = 1.5.sp)
    }
}

@Composable
internal fun LivesRow(lives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 18.sp) }
    }
}

@Composable
internal fun PatternCell(sym: PatternSymbol) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(sym.color.copy(0.18f))
            .border(2.dp, sym.color.copy(0.70f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) { Text(sym.emoji, fontSize = 24.sp) }
}

@Composable
internal fun QuestionCell(accent: Color) {
    val pulse = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        while (true) { pulse.animateTo(1f, tween(600)); pulse.animateTo(0.4f, tween(600)) }
    }
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.12f))
            .border(2.dp, accent.copy(pulse.value), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("?", fontSize = 26.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold, color = accent.copy(pulse.value))
    }
}

@Composable
internal fun OptionBtn(
    sym: PatternSymbol, accent: Color, darkAccent: Color, onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) 0.88f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh), label = "opt"
    )
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .size(width = 90.dp, height = 72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(sym.color.copy(0.30f), darkAccent.copy(0.60f))))
            .border(2.dp, sym.color.copy(0.80f), RoundedCornerShape(14.dp))
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    pressed = true; tryAwaitRelease(); pressed = false; onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(sym.emoji, fontSize = 28.sp)
            Text(sym.label, fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(0.70f))
        }
    }
}

@Composable
internal fun RoundDots(current: Int, total: Int, accent: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            val a by animateFloatAsState(if (i <= current) 1f else 0.25f, tween(300), label = "rd$i")
            Box(
                modifier = Modifier
                    .size(if (i == current) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(accent.copy(a))
            )
        }
    }
}

@Composable
internal fun DoneOverlay(accent: Color, onContinue: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.70f))
            .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); onContinue() }) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0A1A00))
                .border(2.dp, accent, RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⭐  ⭐  ⭐", fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text("¡NIVEL COMPLETADO!", fontSize = 18.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                color = accent, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text("Toca para continuar", fontSize = 11.sp,
                fontFamily = Baloo2FontFamily, color = Color.White.copy(0.55f))
        }
    }
}

@Composable
internal fun FailOverlay(accent: Color, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A0000))
                .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💔", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text("¡SIN VIDAS!", fontSize = 18.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF5252))
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF33691E), Color(0xFF558B2F))))
                    .border(2.dp, accent, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); onRetry() }) }
                    .padding(horizontal = 28.dp, vertical = 10.dp)
            ) {
                Text("Reintentar  ↺", fontSize = 13.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                    color = Color.White)
            }
        }
    }
}
