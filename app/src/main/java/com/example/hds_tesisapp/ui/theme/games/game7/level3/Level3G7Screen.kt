package com.example.hds_tesisapp.ui.theme.games.game7.level3

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
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
import com.example.hds_tesisapp.ui.theme.games.game7.*
import com.example.hds_tesisapp.ui.theme.games.game7.level1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val FLASK_GREEN = Color(0xFF00E676)

@Composable
fun Level3G7Screen(
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
    var done       by remember { mutableStateOf(false) }
    var failed     by remember { mutableStateOf(false) }
    var flash      by remember { mutableStateOf<Boolean?>(null) }
    var compoundA  by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()

    val puzzle   = remember(roundIndex) { buildL3G7Puzzle(roundIndex) }
    var current  by remember(roundIndex) { mutableIntStateOf(puzzle.initial) }
    var opsUsed  by remember(roundIndex) { mutableIntStateOf(0) }
    var usesLeft by remember(roundIndex) { mutableStateOf(puzzle.ops.map { it.maxUses }) }

    fun reset() { current = puzzle.initial; opsUsed = 0; usesLeft = puzzle.ops.map { it.maxUses } }

    fun onOp(idx: Int) {
        if (flash != null || done || failed) return
        val op = puzzle.ops[idx]
        if (usesLeft[idx] <= 0) return
        val divisor = when (op.label) { "÷2" -> 2; "÷3" -> 3; "÷4" -> 4; "÷5" -> 5; else -> 0 }
        if (divisor > 0 && current % divisor != 0) return
        current = op.apply(current)
        usesLeft = usesLeft.toMutableList().also { it[idx]-- }
        opsUsed++
        if (current == puzzle.target) {
            scope.launch {
                flash = true; delay(700); flash = null
                if (roundIndex + 1 >= TOTAL_ROUNDS_L3) {
                    compoundA = true
                    delay(1000)
                    done = true
                } else roundIndex++
            }
        } else if (opsUsed >= puzzle.maxOps) {
            scope.launch {
                flash = false; delay(700); flash = null
                lives--; if (lives <= 0) failed = true else reset()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.lab_flask_bg), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.40f)))

        flash?.let { ok ->
            Box(Modifier.fillMaxSize()
                .background((if (ok) Color(0xFF69FF47) else Color(0xFFFF5252)).copy(alpha = 0.22f))
                .zIndex(5f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G7MenuButton(onNavigateToMenu)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text("NIVEL 3 · El Frasco Verde", fontSize = 13.sp,
                        fontFamily = OrbitronFontFamily, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Crea el Compuesto A combinando operaciones", fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily, color = FLASK_GREEN.copy(alpha = 0.8f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Badge compuesto A
                    CompoundBadge("COMP. A", compoundA, FLASK_GREEN)
                    G7LivesRow(lives)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Personaje + historial de pasos
                Column(
                    modifier = Modifier.width(130.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(painterResource(R.drawable.lina_lab), "Lina",
                        Modifier.height(110.dp), contentScale = ContentScale.Fit)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FLASK_GREEN.copy(alpha = 0.10f))
                            .border(1.dp, FLASK_GREEN.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("Busca la secuencia\ncorrecta de\noperaciones\npara llegar\nal objetivo.",
                            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                            color = Color.White.copy(alpha = 0.75f), lineHeight = 15.sp,
                            textAlign = TextAlign.Center)
                    }
                }

                // Frasco central
                FlaskPanel(current, puzzle.target, opsUsed, puzzle.maxOps, FLASK_GREEN, Modifier.weight(1f))

                // Operaciones (4 botones)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    puzzle.ops.forEachIndexed { idx, op ->
                        OpButton(op.label, usesLeft[idx],
                            flash == null && !done && !failed && usesLeft[idx] > 0
                                    && opsUsed < puzzle.maxOps && current != puzzle.target,
                            FLASK_GREEN) { onOp(idx) }
                    }
                    ResetButton { reset() }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                G7RoundDots(roundIndex, TOTAL_ROUNDS_L3)
                Text("Frasco ${roundIndex + 1} / $TOTAL_ROUNDS_L3", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.5f))
            }
        }

        if (done)   G7DoneOverlay("¡Compuesto A creado! 🟢\nGuardado para la Sala Central.") { onLevelComplete() }
        if (failed) G7FailOverlay { roundIndex = 0; lives = 3; failed = false; compoundA = false }
    }
}

@Composable
internal fun CompoundBadge(label: String, filled: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (filled) color.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (filled) color.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            if (filled) "✅ $label" else "⬜ $label",
            fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (filled) color else Color.White.copy(alpha = 0.3f)
        )
    }
}
