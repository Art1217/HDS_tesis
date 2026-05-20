package com.example.hds_tesisapp.ui.theme.games.game5.level4

import android.app.Activity
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game5.*
import com.example.hds_tesisapp.ui.theme.games.game5.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 5
private val ORANGE = Color(0xFFFF6D00)

@Composable
fun Level4G5Screen(
    onLevelComplete: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var roundIndex by remember { mutableIntStateOf(0) }
    var lives      by remember { mutableIntStateOf(3) }
    var flash      by remember { mutableStateOf<Boolean?>(null) }
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()

    val round = remember(roundIndex) { buildLevel4Round(roundIndex) }

    fun onTap(portal: Portal) {
        if (flash != null || done || failed) return
        val correct = portal.id == round.correctTap.id
        scope.launch {
            flash = correct
            delay(600)
            flash = null
            if (correct) {
                if (roundIndex + 1 >= TOTAL_ROUNDS) done = true
                else roundIndex++
            } else {
                lives--
                if (lives <= 0) failed = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.portal_maze_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.38f)))

        flash?.let { ok ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                    .zIndex(5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 4 · Sala de Múltiples Portales", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold,
                        color = Color.White)
                    Text("if · else if · else", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = ORANGE.copy(alpha = 0.8f))
                }
                G5LivesRow(lives)
            }

            // ── Instrucción ──────────────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ORANGE.copy(alpha = 0.12f))
                    .border(1.dp, ORANGE.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    "Evalúa en qué rango está el número y entra al portal que indica la regla.",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = ORANGE.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Centro ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Izquierda: número + dónde cae + condición
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NumberCard(round.number, ORANGE)

                    // Muestra en qué rango cae el número
                    val n = round.number
                    val (rangeText, rangeColor) = when {
                        n < 3  -> "¿$n < 3? → Sí"  to Color(0xFF2196F3)
                        n <= 7 -> "¿3 ≤ $n ≤ 7? → Sí" to Color(0xFF4CAF50)
                        else   -> "¿$n > 7? → Sí"  to Color(0xFFF44336)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(rangeColor.copy(alpha = 0.15f))
                            .border(1.5.dp, rangeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(rangeText, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.95f))
                    }

                    ConditionCard(round.conditionLines)
                }

                // Derecha: 3 portales, el que es TRAMPA tiene badge ⚠️
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    round.portals.forEach { p ->
                        PortalButton(
                            portal  = p,
                            enabled = flash == null && !done && !failed,
                            onClick = { onTap(p) }
                        )
                    }
                }
            }

            // ── Footer ───────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G5DoneOverlay { onLevelComplete() }
        if (failed) G5FailOverlay { roundIndex = 0; lives = 3; failed = false }
    }
}
