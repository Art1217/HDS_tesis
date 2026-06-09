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
import androidx.compose.foundation.layout.height
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

// ─── Datos de slide ───────────────────────────────────────────────────────────

private data class PSlide(
    val title: String,
    val body: String,
    val extra: PExtra = PExtra.NONE
)

private enum class PExtra {
    NONE,
    IF_TABLE,       // slide 1: tabla si/entonces
    PORTAL_DEMO,    // slide 2: demo de portales
    GLITCH_WARN,    // slide 3: advertencia inversión
    TIP_LIST        // slide 4: lista de tips
}

private val SLIDES = listOf(
    PSlide(
        title = "¿Qué es una Condición?",
        body  = "Una condición es una pregunta que puede ser\nVERDADERA o FALSA. Según la respuesta,\nel programa toma una acción diferente.",
        extra = PExtra.IF_TABLE
    ),
    PSlide(
        title = "Los Portales de la Ciudad",
        body  = "Cada portal tiene una regla.\nAparece un número y debes decidir\na cuál portal entrar según la condición.",
        extra = PExtra.PORTAL_DEMO
    ),
    PSlide(
        title = "¿Cómo elegir el portal?",
        body  = "Lee la condición, evalúa el número\ny entra al portal que la condición indica.\n¡Así repararás los portales!",
        extra = PExtra.GLITCH_WARN
    ),
    PSlide(
        title = "¡Tú Eres el Guardián!",
        body  = "Evalúa la condición paso a paso.\nCada nivel tiene reglas más complejas.\n¡Concéntrate y elige el portal correcto!",
        extra = PExtra.TIP_LIST
    )
)

private val PURPLE    = Color(0xFF9C27B0)
private val PURPLE_LT = Color(0xFFCE93D8)
private val BG_TOP    = Color(0xFF0D0020)
private val BG_BOT    = Color(0xFF260050)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PortalTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var slideIndex by remember { mutableIntStateOf(0) }
    val slide = SLIDES[slideIndex]
    val scope = rememberCoroutineScope()

    val charY     = remember { Animatable(60f) }
    val charAlpha = remember { Animatable(0f) }
    val glow      = remember { Animatable(0.4f) }
    val floatAnim = remember { Animatable(0f) }

    LaunchedEffect(slideIndex) {
        charY.snapTo(60f); charAlpha.snapTo(0f)
        launch { charY.animateTo(0f,    tween(500)) }
        launch { charAlpha.animateTo(1f, tween(400)) }
        launch {
            while (true) {
                glow.animateTo(1f,    tween(900))
                glow.animateTo(0.4f, tween(900))
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            floatAnim.animateTo(9f,  tween(1300))
            floatAnim.animateTo(-9f, tween(1300))
        }
    }

    fun advance() {
        if (slideIndex < SLIDES.lastIndex) slideIndex++
        else navController.navigate(Routes.Level1G5.route) {
            popUpTo(Routes.PortalTutorial.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(slideIndex) { detectTapGestures { advance() } }
    ) {
        // floating portal orbs decoration
        repeat(6) { i ->
            val xFrac = (i * 0.18f + 0.05f).coerceIn(0f, 0.95f)
            val yFrac = (i * 0.15f + 0.05f).coerceIn(0f, 0.95f)
            val col   = if (i % 2 == 0) PURPLE.copy(alpha = 0.15f) else PURPLE_LT.copy(alpha = 0.10f)
            Box(
                modifier = Modifier
                    .offset((xFrac * 800).dp, (yFrac * 450).dp)
                    .size(if (i % 3 == 0) 10.dp else 6.dp)
                    .clip(CircleShape)
                    .background(col)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Character panel ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .width(160.dp)
                    .offset(y = charY.value.dp)
                    .drawBehind { /* entrance alpha applied via graphicsLayer below */ },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Emoji badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PURPLE.copy(alpha = 0.15f))
                        .border(2.dp, PURPLE_LT.copy(alpha = 0.6f), CircleShape)
                        .size(44.dp),
                    contentAlignment = Alignment.Center
                ) { Text("🌀", fontSize = 20.sp) }

                Spacer(Modifier.height(4.dp))

                // Imagen de Lina flotando con glow
                Box(
                    modifier = Modifier
                        .offset(y = floatAnim.value.dp)
                        .size(148.dp)
                        .drawBehind {
                            drawCircle(
                                PURPLE.copy(alpha = glow.value * 0.28f),
                                radius = size.minDimension / 2f + 22f
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.lina_character),
                        contentDescription = "Lina",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Badge de nombre
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PURPLE.copy(alpha = 0.12f))
                        .border(1.dp, PURPLE_LT.copy(alpha = 0.50f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        "LINA", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = PURPLE_LT, letterSpacing = 3.sp
                    )
                }

                // Slide dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SLIDES.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == slideIndex) 9.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == slideIndex) PURPLE_LT else PURPLE_LT.copy(alpha = 0.3f)
                                )
                        )
                    }
                }
            }

            // ── Content panel ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.5.dp, PURPLE_LT.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Zone badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PURPLE.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        "ZONA 5 · TUTORIAL",
                        fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = PURPLE_LT, letterSpacing = 1.sp
                    )
                }

                Text(
                    slide.title,
                    fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = Color.White
                )

                Text(
                    slide.body,
                    fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f), lineHeight = 20.sp
                )

                // Extra panel
                when (slide.extra) {
                    PExtra.IF_TABLE    -> PIfTable()
                    PExtra.PORTAL_DEMO -> PPortalDemo()
                    PExtra.GLITCH_WARN -> PGlitchWarn()
                    PExtra.TIP_LIST    -> PTipList()
                    PExtra.NONE        -> {}
                }

                Spacer(Modifier.weight(1f))

                // Next hint
                Text(
                    if (slideIndex < SLIDES.lastIndex) "Toca para continuar ›" else "¡A jugar! →",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = PURPLE_LT.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Skip button
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PURPLE.copy(alpha = 0.18f))
                    .border(1.dp, PURPLE_LT.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            navController.navigate(Routes.Level1G5.route) {
                                popUpTo(Routes.PortalTutorial.route) { inclusive = true }
                            }
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SKIP", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = PURPLE_LT.copy(alpha = 0.7f),
                    letterSpacing = 1.sp, textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Extras ───────────────────────────────────────────────────────────────────

@Composable
private fun PIfTable() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("CONDICIÓN", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = PURPLE_LT, fontWeight = FontWeight.Bold)
            Text("7 > 5", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White)
            Text("3 > 5", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("RESULTADO", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = PURPLE_LT, fontWeight = FontWeight.Bold)
            Text("✅ VERDADERO", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color(0xFF69FF47))
            Text("❌ FALSO", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color(0xFFFF5252))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("ACCIÓN", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = PURPLE_LT, fontWeight = FontWeight.Bold)
            Text("→ Hacer A", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color(0xFF40C4FF))
            Text("→ Hacer B", fontSize = 12.sp, fontFamily = Baloo2FontFamily, color = Color(0xFFFFD600))
        }
    }
}

@Composable
private fun PPortalDemo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        PortalChip("🔵", "Azul",  Color(0xFF2196F3), "Si número > 5")
        Text("·", fontSize = 18.sp, color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterVertically))
        PortalChip("🔴", "Rojo",  Color(0xFFF44336), "Si no")
    }
}

@Composable
private fun PortalChip(emoji: String, name: String, color: Color, rule: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(emoji, fontSize = 26.sp)
        Text(name, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = color)
        Text(rule, fontSize = 9.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.65f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun PGlitchWarn() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF44336).copy(alpha = 0.12f))
            .border(1.5.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("⚡", fontSize = 20.sp)
            Text("REGLA DEL GLITCH", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
        }
        Text(
            "La condición te dice qué portal usar.\nEvalúa bien el número y entra al correcto.",
            fontSize = 12.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.9f), lineHeight = 18.sp
        )
    }
}

@Composable
private fun PTipList() {
    val tips = listOf(
        "1️⃣  Lee la condición con calma",
        "2️⃣  Evalúa el número contra la regla",
        "3️⃣  Identifica el portal que corresponde",
        "4️⃣  ¡Toca ese portal!"
    )
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        tips.forEach { tip ->
            Text(tip, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f))
        }
    }
}
