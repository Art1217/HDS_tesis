package com.example.hds_tesisapp.ui.theme.games.game4.level3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game4.PatternSymbol
import com.example.hds_tesisapp.ui.theme.games.game4.buildStoneRound
import com.example.hds_tesisapp.ui.theme.games.game4.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun Context.findL3G4Activity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findL3G4Activity()
    else              -> null
}

private val AMBER  = Color(0xFFFFB300)
private val DARK_A = Color(0xFF3E2000)

@Composable
fun Level3G4Screen(onLevelComplete: () -> Unit, onNavigateToMenu: () -> Unit) {
    val context  = LocalContext.current
    val activity = remember { context.findL3G4Activity() }
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

    val round = remember(roundIndex) { buildStoneRound(roundIndex) }
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
        Image(painterResource(R.drawable.stone_path_bg), null,
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
                ZoneBadge("ZONA 4  ·  NIVEL 3", AMBER)
                LivesRow(lives)
            }

            Spacer(Modifier.height(8.dp))

            // Pattern label badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AMBER.copy(0.18f))
                    .border(1.5.dp, AMBER.copy(0.70f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text("PATRÓN: ${round.patternLabel}", fontSize = 12.sp,
                    fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                    color = AMBER)
            }

            Spacer(Modifier.height(8.dp))

            Text("COMPLETA LA SECUENCIA", fontSize = 13.sp,
                fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.80f), letterSpacing = 1.5.sp)

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                round.sequence.forEach { sym -> PatternCell(sym) }
                QuestionCell(AMBER)
            }

            Spacer(Modifier.height(18.dp))

            // Distractor warning
            Text("⚠️ ¡Uno de los botones es un distractor!", fontSize = 10.sp,
                fontFamily = Baloo2FontFamily, color = Color(0xFFFF7043).copy(0.85f))

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                round.options.forEach { opt ->
                    OptionBtn(opt, AMBER, DARK_A) { onPick(opt) }
                }
            }

            Spacer(Modifier.weight(1f))
            RoundDots(roundIndex, TOTAL, AMBER)
        }

        if (levelDone) DoneOverlay(AMBER) { onLevelComplete() }
        if (gameOver)  FailOverlay(AMBER) {
            roundIndex = 0; lives = 3; flash = null; gameOver = false
        }
    }
}
