package com.example.hds_tesisapp.ui.theme.games.game5.level2

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import com.example.hds_tesisapp.ui.theme.games.game5.Portal
import com.example.hds_tesisapp.ui.theme.games.game5.buildLevel2Round
import com.example.hds_tesisapp.ui.theme.games.game5.level1.ConditionCard
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5LivesRow
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5MenuButton
import com.example.hds_tesisapp.ui.theme.games.game5.level1.G5RoundDots
import com.example.hds_tesisapp.ui.theme.games.game5.level1.NumberCard
import com.example.hds_tesisapp.ui.theme.games.game5.level1.PortalButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 5
private val CYAN = Color(0xFF00B0FF)

@Composable
fun Level2G5Screen(
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

    val round = remember(roundIndex) { buildLevel2Round(roundIndex) }

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
            painter = painterResource(R.drawable.portal_yesno_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

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
            // Header
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G5MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 2 · Portales del Sí y No", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("IF / ELSE · par o impar", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = CYAN.copy(alpha = 0.8f))
                }
                G5LivesRow(lives)
            }

            // Instrucción
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CYAN.copy(alpha = 0.12f))
                    .border(1.dp, CYAN.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text("Decide si el número es par o impar y toca el portal correcto.",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = CYAN.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

            // Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NumberCard(round.number, CYAN)
                    // Par/impar hint chip
                    val esPar = round.number % 2 == 0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background((if (esPar) Color(0xFF4CAF50) else Color(0xFFF44336)).copy(alpha = 0.18f))
                            .border(1.dp, (if (esPar) Color(0xFF4CAF50) else Color(0xFFF44336)).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            if (esPar) "✅ Es PAR (÷2 exacto)" else "❌ Es IMPAR (no ÷2)",
                            fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    ConditionCard(round.conditionLines)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    round.portals.forEach { p ->
                        PortalButton(p, flash == null && !done && !failed) { onTap(p) }
                    }
                }
            }

            // Footer
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
