package com.example.hds_tesisapp.ui.theme.games.game4.level2

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game4.PatternSymbol
import com.example.hds_tesisapp.ui.theme.games.game4.buildABCRound
import com.example.hds_tesisapp.ui.theme.games.game4.level1.DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game4.level1.FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game4.level1.LivesRow
import com.example.hds_tesisapp.ui.theme.games.game4.level1.MenuBtn
import com.example.hds_tesisapp.ui.theme.games.game4.level1.OptionBtn
import com.example.hds_tesisapp.ui.theme.games.game4.level1.PatternCell
import com.example.hds_tesisapp.ui.theme.games.game4.level1.QuestionCell
import com.example.hds_tesisapp.ui.theme.games.game4.level1.RoundDots
import com.example.hds_tesisapp.ui.theme.games.game4.level1.ZoneBadge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findL2G4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findL2G4Activity()
    else              -> null
}

private val CYAN  = Color(0xFF00B0FF)
private val DARK  = Color(0xFF00263A)

@Composable
fun Level2G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL2G4Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val TOTAL = 5
    var roundIndex by remember { mutableIntStateOf(0) }
    var lives      by remember { mutableIntStateOf(3) }
    var flash      by remember { mutableStateOf<Boolean?>(null) }
    var levelDone  by remember { mutableStateOf(false) }
    var gameOver   by remember { mutableStateOf(false) }

    val round = remember(roundIndex) { buildABCRound(roundIndex) }
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
        Image(painterResource(R.drawable.firefly_night_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.55f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize().background(
                if (ok) Color(0xFF69FF47).copy(0.22f) else Color(0xFFFF5252).copy(0.22f)))
        }

        // Firefly sparkles
        FireflySparkles()

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
                ZoneBadge("ZONA 4  ·  NIVEL 2", CYAN)
                LivesRow(lives)
            }

            Spacer(Modifier.height(10.dp))

            Text("COMPLETA EL PATRÓN ABC", fontSize = 13.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                color = CYAN, letterSpacing = 2.sp)

            Spacer(Modifier.height(6.dp))
            Text("¿Qué sigue en el ciclo de 3?", fontSize = 10.sp,
                fontFamily = com.example.hds_tesisapp.ui.theme.Baloo2FontFamily,
                color = Color.White.copy(0.55f))

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                round.sequence.forEach { sym -> PatternCell(sym) }
                QuestionCell(CYAN)
            }

            Spacer(Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                round.options.forEach { opt ->
                    OptionBtn(opt, CYAN, DARK) { onPick(opt) }
                }
            }

            Spacer(Modifier.weight(1f))
            RoundDots(roundIndex, TOTAL, CYAN)
        }

        if (levelDone) DoneOverlay(CYAN) { onLevelComplete() }
        if (gameOver)  FailOverlay(CYAN) {
            roundIndex = 0; lives = 3; flash = null; gameOver = false
        }
    }
}

@Composable
private fun FireflySparkles() {
    val positions = remember {
        List(12) { Pair((Math.random()).toFloat(), (Math.random()).toFloat()) }
    }
    val inf = rememberInfiniteTransition(label = "ff")
    positions.forEachIndexed { i, (xf, yf) ->
        val alpha by inf.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(800 + i * 130, delayMillis = i * 200), RepeatMode.Reverse
            ), label = "ff$i"
        )
        val color = listOf(
            Color(0xFF40C4FF), Color(0xFF69FF47), Color(0xFFFFD600)
        )[i % 3]
        Box(
            modifier = Modifier
                .offset(x = (xf * 720).toInt().dp, y = (yf * 380).toInt().dp)
                .size(5.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(color.copy(alpha = alpha * 0.9f))
        )
    }
}
