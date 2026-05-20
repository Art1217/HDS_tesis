package com.example.hds_tesisapp.ui.theme.games.game6.level4

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
import com.example.hds_tesisapp.ui.theme.games.game6.*
import com.example.hds_tesisapp.ui.theme.games.game6.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TOTAL_ROUNDS = 4
private val TEAL = Color(0xFF00BFA5)

@Composable
fun Level4G6Screen(
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

    val round = remember(roundIndex) { buildL4G6Round(roundIndex) }

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
            painter = painterResource(R.drawable.factory_warehouse_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)))

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
                    Text("NIVEL 4 · Almacén de Cajas", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Bucles anidados · multiplica filas × columnas", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = TEAL.copy(alpha = 0.8f))
                }
                G6LivesRow(lives)
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TEAL.copy(alpha = 0.10f))
                    .border(1.dp, TEAL.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text("Hay dos bucles. El de afuera repite filas y el de adentro repite cajas. ¿Cuántas cajas en total?",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = TEAL.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: visual boxes
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.width(160.dp)
                ) {
                    WarehouseVisual(round.correct)
                    TaskCard(round.taskTitle, round.taskDesc, TEAL)
                }

                // Center: nested code block
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Bucle anidado:", fontSize = 10.sp,
                        fontFamily = Baloo2FontFamily, color = TEAL.copy(alpha = 0.7f))
                    NestedCodeBlock(round.codeLines)
                }

                // Right: options
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Total =", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = TEAL.copy(alpha = 0.7f))
                    round.options.forEach { opt ->
                        LoopOptionButton(
                            value   = opt,
                            color   = TEAL,
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

@Composable
private fun WarehouseVisual(total: Int) {
    val rows = when {
        total <= 6  -> 2
        total <= 12 -> 3
        else        -> 3
    }
    val cols = (total / rows).coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(rows.coerceAtMost(3)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(cols.coerceAtMost(5)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(TEAL.copy(alpha = 0.35f))
                            .border(1.dp, TEAL.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun NestedCodeBlock(lines: List<String>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, TEAL.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        lines.forEach { line ->
            val color = when {
                line.startsWith("//") -> Color(0xFF69FF47).copy(alpha = 0.75f)
                line.trimStart().startsWith("repetir") -> G6_AMBER
                line.trimStart().startsWith("}") -> if (line.contains("//")) G6_AMBER else TEAL
                line.trimStart().startsWith("  repetir") -> Color(0xFF40C4FF)
                line.trimStart().startsWith("  }") -> Color(0xFF40C4FF)
                else -> Color.White.copy(alpha = 0.85f)
            }
            Text(line, fontSize = 11.sp, fontFamily = Baloo2FontFamily, color = color)
        }
    }
}
