package com.example.hds_tesisapp.ui.theme.games.game6.level2

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.hds_tesisapp.ui.theme.games.game6.buildL2G6Round
import com.example.hds_tesisapp.ui.theme.games.game6.level1.CodeBlock
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6DoneOverlay
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6FailOverlay
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6LivesRow
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6MenuButton
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6RoundDots
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6_AMBER
import com.example.hds_tesisapp.ui.theme.games.game6.level1.G6_ORANGE
import com.example.hds_tesisapp.ui.theme.games.game6.level1.LoopOptionButton
import com.example.hds_tesisapp.ui.theme.games.game6.level1.TaskCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 4

@Composable
fun Level2G6Screen(
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

    val round = remember(roundIndex) { buildL2G6Round(roundIndex) }

    fun onTap(option: Int) {
        if (flash != null || done || failed) return
        val correct = option == round.correct
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
            painter = painterResource(R.drawable.factory_interface_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))

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
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6MenuButton(onNavigateToMenu)
                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 2 · Bits Dañados", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Bucle de reparación · ¿Cuántas veces repetir?", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = G6_AMBER.copy(alpha = 0.8f))
                }
                G6LivesRow(lives)
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(G6_ORANGE.copy(alpha = 0.12f))
                    .border(1.dp, G6_AMBER.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text("Cada bit dañado necesita una reparación. ¿Cuántas veces ejecutar la función?",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = G6_AMBER.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: damaged bits visual
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.width(160.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(round.correct.coerceAtMost(5)) {
                            Image(
                                painter = painterResource(R.drawable.bit_triste),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    TaskCard(round.taskTitle, round.taskDesc, Color(0xFFFF5252))
                }

                // Center: code block
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("¿Cuántas veces?", fontSize = 10.sp,
                        fontFamily = Baloo2FontFamily, color = G6_AMBER.copy(alpha = 0.7f))
                    CodeBlock(round.codeLines)
                }

                // Right: options
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Elige:", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = G6_AMBER.copy(alpha = 0.7f))
                    round.options.forEach { opt ->
                        LoopOptionButton(
                            value   = opt,
                            color   = G6_ORANGE,
                            enabled = flash == null && !done && !failed,
                            onClick = { onTap(opt) }
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G6RoundDots(roundIndex, TOTAL_ROUNDS)
                Text("Ronda ${roundIndex + 1} / $TOTAL_ROUNDS", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G6DoneOverlay { onLevelComplete() }
        if (failed) G6FailOverlay { roundIndex = 0; lives = 3; failed = false }
    }
}
