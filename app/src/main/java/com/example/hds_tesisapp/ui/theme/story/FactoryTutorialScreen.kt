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

private data class FSlide(
    val title: String,
    val body: String,
    val extra: FExtra = FExtra.NONE
)

private enum class FExtra {
    NONE,
    LOOP_COMPARE,   // slide 1: sin bucle vs con bucle
    CODE_DEMO,      // slide 2: ejemplo de repetir(N)
    NESTED_DEMO,    // slide 3: bucle anidado
    TIP_LIST        // slide 4: consejos
}

private val FSLIDES = listOf(
    FSlide(
        title = "¿Qué es un Bucle?",
        body  = "Un bucle es una instrucción que se repite\nvarias veces. En lugar de escribir lo mismo\nmuchas veces, usamos REPETIR N VECES.",
        extra = FExtra.LOOP_COMPARE
    ),
    FSlide(
        title = "REPETIR N VECES",
        body  = "Si hay 3 tornillos que apretar, puedes\nescribir la acción 3 veces o usar un bucle.\n¡El bucle es más eficiente!",
        extra = FExtra.CODE_DEMO
    ),
    FSlide(
        title = "Bucles Anidados",
        body  = "Un bucle dentro de otro multiplica acciones.\n2 estantes × 3 cajas = 6 cajas en total.\n¡Así funciona la fábrica!",
        extra = FExtra.NESTED_DEMO
    ),
    FSlide(
        title = "¡Tú eres el Ingeniero!",
        body  = "Calcula cuántas veces se repite cada acción.\nLee el código con atención y elige el número.\n¡La fábrica depende de ti!",
        extra = FExtra.TIP_LIST
    )
)

private val FACTORY_ORANGE = Color(0xFFFF6D00)
private val FACTORY_AMBER  = Color(0xFFFFB300)
private val BG_TOP = Color(0xFF1A0A00)
private val BG_BOT = Color(0xFF3D1500)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun FactoryTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context as? Activity }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var slideIndex by remember { mutableIntStateOf(0) }
    val slide = FSLIDES[slideIndex]
    val scope = rememberCoroutineScope()

    val charY    = remember { Animatable(60f) }
    val charAlpha = remember { Animatable(0f) }
    val glow     = remember { Animatable(0.4f) }

    LaunchedEffect(slideIndex) {
        charY.snapTo(60f); charAlpha.snapTo(0f)
        launch { charY.animateTo(0f, tween(500)) }
        launch { charAlpha.animateTo(1f, tween(400)) }
        launch {
            while (true) {
                glow.animateTo(1f, tween(900))
                glow.animateTo(0.4f, tween(900))
            }
        }
    }

    fun advance() {
        if (slideIndex < FSLIDES.lastIndex) slideIndex++
        else navController.navigate(Routes.Level1G6.route) {
            popUpTo(Routes.FactoryTutorial.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOT)))
            .pointerInput(slideIndex) { detectTapGestures { advance() } }
    ) {
        // Floating gear decorations
        repeat(6) { i ->
            val xFrac = (i * 0.18f + 0.05f).coerceIn(0f, 0.95f)
            val yFrac = (i * 0.15f + 0.05f).coerceIn(0f, 0.95f)
            val col = if (i % 2 == 0) FACTORY_ORANGE.copy(alpha = 0.10f)
                      else FACTORY_AMBER.copy(alpha = 0.08f)
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
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .drawBehind {
                            drawCircle(
                                FACTORY_ORANGE.copy(alpha = glow.value * 0.35f),
                                radius = size.minDimension / 2f + 18f
                            )
                        }
                        .clip(CircleShape)
                        .background(FACTORY_ORANGE.copy(alpha = 0.15f))
                        .border(2.dp, FACTORY_AMBER.copy(alpha = glow.value * 0.8f + 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.bit_maestro),
                        contentDescription = "Bit Maestro",
                        modifier = Modifier.fillMaxSize(0.85f),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    "Bit Maestro",
                    fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = FACTORY_AMBER
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FSLIDES.indices.forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == slideIndex) 9.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == slideIndex) FACTORY_AMBER else FACTORY_AMBER.copy(alpha = 0.3f)
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
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.5.dp, FACTORY_AMBER.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FACTORY_ORANGE.copy(alpha = 0.22f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        "ZONA 6 · TUTORIAL",
                        fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = FACTORY_AMBER, letterSpacing = 1.sp
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

                when (slide.extra) {
                    FExtra.LOOP_COMPARE  -> FLoopCompare()
                    FExtra.CODE_DEMO     -> FCodeDemo()
                    FExtra.NESTED_DEMO   -> FNestedDemo()
                    FExtra.TIP_LIST      -> FTipList()
                    FExtra.NONE          -> {}
                }

                Spacer(Modifier.weight(1f))

                Text(
                    if (slideIndex < FSLIDES.lastIndex) "Toca para continuar ›" else "¡A la fábrica! →",
                    fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    color = FACTORY_AMBER.copy(alpha = 0.7f), textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Skip button
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FACTORY_ORANGE.copy(alpha = 0.15f))
                    .border(1.dp, FACTORY_AMBER.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            navController.navigate(Routes.Level1G6.route) {
                                popUpTo(Routes.FactoryTutorial.route) { inclusive = true }
                            }
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SKIP", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = FACTORY_AMBER.copy(alpha = 0.7f),
                    letterSpacing = 1.sp, textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Extras ───────────────────────────────────────────────────────────────────

/** Círculo con número para representar "REPETIR N" sin código */
@Composable
private fun LoopBadge(n: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.22f))
            .border(1.5.dp, color.copy(alpha = 0.7f), CircleShape)
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(n, fontSize = 12.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@Composable
private fun FLoopCompare() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // Sin bucle — lista repetida de acciones
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF44336).copy(alpha = 0.10f))
                .border(1.dp, Color(0xFFF44336).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("❌ Sin bucle", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
            repeat(3) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("🔩", fontSize = 13.sp)
                    Text("apretar", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(0.75f))
                }
            }
        }
        // Con bucle — REPETIR N VECES visual
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50).copy(alpha = 0.10f))
                .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("✅ Con bucle", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = Color(0xFF69FF47))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("REPETIR", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                    color = FACTORY_AMBER)
                LoopBadge("3", FACTORY_AMBER)
                Text("VECES", fontSize = 8.sp, fontFamily = OrbitronFontFamily,
                    color = FACTORY_AMBER)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("  🔩", fontSize = 13.sp)
                Text("apretar", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.75f))
            }
        }
    }
}

@Composable
private fun FCodeDemo() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.30f))
            .border(1.dp, FACTORY_AMBER.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Encabezado REPETIR N VECES
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("REPETIR", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = FACTORY_AMBER)
            LoopBadge("3", FACTORY_AMBER)
            Text("VECES  →", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = FACTORY_AMBER)
        }
        // Resultado visual: 3 tornillos
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FACTORY_ORANGE.copy(0.15f))
                        .border(1.dp, FACTORY_ORANGE.copy(0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) { Text("🔩", fontSize = 18.sp) }
            }
            Text("✅", fontSize = 18.sp)
        }
    }
}

@Composable
private fun FNestedDemo() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.30f))
            .border(1.dp, FACTORY_AMBER.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Fórmula visual: 2 × 3 = 6
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            LoopBadge("2", FACTORY_AMBER)
            Text("estantes  ×", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = FACTORY_AMBER)
            LoopBadge("3", Color(0xFF40C4FF))
            Text("cajas  =", fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color(0xFF40C4FF))
            Text("6 📦", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFF69FF47))
        }
        // Cuadrícula 2 filas × 3 cajas
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF40C4FF).copy(0.10f))
                                .border(1.dp, Color(0xFF40C4FF).copy(0.35f), RoundedCornerShape(4.dp))
                                .size(26.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("📦", fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FTipList() {
    val tips = listOf(
        "1️⃣  Lee cuántas veces dice repetir(N)",
        "2️⃣  Si hay dos bucles, multiplica N × M",
        "3️⃣  Cuenta bien antes de tocar",
        "4️⃣  ¡La fábrica confía en ti!"
    )
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        tips.forEach { tip ->
            Text(tip, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.85f))
        }
    }
}
