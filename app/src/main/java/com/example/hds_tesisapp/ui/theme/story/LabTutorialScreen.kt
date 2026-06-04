package com.example.hds_tesisapp.ui.theme.story

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

private data class LSlide(val title: String, val body: String, val extra: LExtra = LExtra.NONE)
private enum class LExtra { NONE, VAR_DEMO, CODE_DEMO, STEP_DEMO, TIP_LIST }

private val LSLIDES = listOf(
    LSlide("¿Qué es una Variable?",
        "Una variable es un contenedor que guarda un valor.\nEse valor puede CAMBIAR si le aplicamos\nuna operación matemática.",
        LExtra.VAR_DEMO),
    LSlide("Operaciones sobre Variables",
        "Cada operación modifica el valor actual.\n+1 suma, −1 resta, ×2 multiplica, ÷3 divide.\n¡El orden de las operaciones importa!",
        LExtra.CODE_DEMO),
    LSlide("Llegar al Valor Exacto",
        "El objetivo es aplicar operaciones para llegar\nexactamente al valor que necesitas.\nCada opción tiene un límite de usos.",
        LExtra.STEP_DEMO),
    LSlide("¡Tú eres el Científico!",
        "Observa el valor actual y el objetivo.\nPlanea las operaciones antes de tocarlas.\n¡No malgastes usos!",
        LExtra.TIP_LIST)
)

private val LAB_CYAN   = Color(0xFF00E5FF)
private val LAB_TEAL   = Color(0xFF00BFA5)
private val BG_TOP     = Color(0xFF001020)
private val BG_BOT     = Color(0xFF002040)

@Composable
fun LabTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var slideIndex by remember { mutableIntStateOf(0) }
    val slide = LSLIDES[slideIndex]
    val scope = rememberCoroutineScope()
    val glow  = remember { Animatable(0.4f) }

    LaunchedEffect(slideIndex) {
        launch {
            while (true) { glow.animateTo(1f, tween(900)); glow.animateTo(0.4f, tween(900)) }
        }
    }

    fun advance() {
        if (slideIndex < LSLIDES.lastIndex) slideIndex++
        else navController.navigate(Routes.Level1G7.route) {
            popUpTo(Routes.LabTutorial.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(slideIndex) { detectTapGestures { advance() } }
    ) {
        // Partículas decorativas
        repeat(7) { i ->
            val xFrac = (i * 0.15f + 0.04f).coerceIn(0f, 0.94f)
            val yFrac = (i * 0.13f + 0.06f).coerceIn(0f, 0.94f)
            Box(
                modifier = Modifier
                    .offset((xFrac * 800).dp, (yFrac * 450).dp)
                    .size(if (i % 3 == 0) 10.dp else 5.dp)
                    .clip(CircleShape)
                    .background(if (i % 2 == 0) LAB_CYAN.copy(alpha = 0.12f) else LAB_TEAL.copy(alpha = 0.08f))
            )
        }

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Panel del personaje ──────────────────────────────────────────
            Column(
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .drawBehind {
                            drawCircle(LAB_CYAN.copy(alpha = glow.value * 0.35f),
                                radius = size.minDimension / 2f + 18f)
                        }
                        .clip(CircleShape)
                        .background(LAB_CYAN.copy(alpha = 0.10f))
                        .border(2.dp, LAB_TEAL.copy(alpha = glow.value * 0.8f + 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.lina_lab),
                        contentDescription = "Lina",
                        modifier = Modifier.fillMaxSize(0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
                Text("Lina", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = LAB_TEAL)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LSLIDES.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == slideIndex) 9.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (i == slideIndex) LAB_CYAN else LAB_CYAN.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            // ── Panel de contenido ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, LAB_TEAL.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LAB_CYAN.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("ZONA 7 · TUTORIAL", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = LAB_CYAN, letterSpacing = 1.sp)
                }
                Text(slide.title, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(slide.body, fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 20.sp)
                when (slide.extra) {
                    LExtra.VAR_DEMO  -> LVarDemo()
                    LExtra.CODE_DEMO -> LCodeDemo()
                    LExtra.STEP_DEMO -> LStepDemo()
                    LExtra.TIP_LIST  -> LTipList()
                    LExtra.NONE      -> {}
                }
                Spacer(Modifier.weight(1f))
                Text(
                    if (slideIndex < LSLIDES.lastIndex) "Toca para continuar ›" else "¡Al laboratorio! →",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = LAB_TEAL.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Skip
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LAB_CYAN.copy(alpha = 0.12f))
                    .border(1.dp, LAB_TEAL.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            navController.navigate(Routes.Level1G7.route) {
                                popUpTo(Routes.LabTutorial.route) { inclusive = true }
                            }
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("SKIP", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = LAB_TEAL.copy(alpha = 0.7f),
                    letterSpacing = 1.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

// ─── Extras ───────────────────────────────────────────────────────────────────

private val LAB_CYAN_L = Color(0xFF00E5FF)
private val LAB_TEAL_L = Color(0xFF00BFA5)

@Composable
private fun LVarDemo() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        VarBox("energia", "5", LAB_CYAN_L)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("+ 1", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFF69FF47))
            Text("→", fontSize = 20.sp, color = Color.White.copy(alpha = 0.6f))
        }
        VarBox("energia", "6", LAB_TEAL_L)
    }
}

@Composable
private fun VarBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 9.sp, fontFamily = OrbitronFontFamily,
            color = color, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 24.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}

@Composable
private fun LCodeDemo() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.dp, LAB_CYAN_L.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text("var energia = 5", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = LAB_CYAN_L)
        Text("energia = energia + 1  // → 6", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.85f))
        Text("energia = energia × 2  // → 12", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White.copy(alpha = 0.85f))
        Text("// ¡Meta: 12! ✅", fontSize = 11.sp, fontFamily = Baloo2FontFamily, color = Color(0xFF69FF47).copy(alpha = 0.8f))
    }
}

@Composable
private fun LStepDemo() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("5" to null, "+1" to Color(0xFF69FF47), "6" to null, "×2" to Color(0xFFFFB300), "12 ✅" to Color(0xFF00E5FF)).forEach { (txt, col) ->
            if (col != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(col.copy(alpha = 0.18f))
                        .border(1.dp, col.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text(txt, fontSize = 12.sp, fontFamily = OrbitronFontFamily, color = col, fontWeight = FontWeight.Bold) }
            } else {
                Text(txt, fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun LTipList() {
    val tips = listOf(
        "1️⃣  Lee bien el valor actual y el objetivo",
        "2️⃣  Revisa cuántos usos le quedan a cada operación",
        "3️⃣  Piensa el orden antes de tocar",
        "4️⃣  Usa RESET si te equivocas"
    )
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        tips.forEach { tip ->
            Text(tip, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f))
        }
    }
}
