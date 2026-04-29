package com.example.hds_tesisapp.ui.theme.story

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

// ─── Modelos ──────────────────────────────────────────────────────────────────

private data class ClsSlide(
    val title: String,
    val message: String,
    val highlightWord: String = "",
    val emoji: String,
    val accentColor: Color,
    val extraType: ExtraType = ExtraType.NONE
)

private enum class ExtraType {
    NONE,
    PROPERTIES_TABLE,   // Slide 1: tabla dato → propiedad
    RULES_TABLE,        // Slide 2: reglas SI → ENTONCES
    BUG_TABLE,          // Slide 3: bug vs correcto
    STEPS_LIST          // Slide 4: pasos numerados
}

private val GREEN_FOREST = Color(0xFF2E7D32)
private val GREEN_LIME   = Color(0xFF69FF47)
private val AMBER        = Color(0xFFFFD600)
private val RED_BUG      = Color(0xFFFF5252)

private val SLIDES = listOf(
    ClsSlide(
        title         = "Los Datos Tienen Propiedades",
        message       = "En computación, cada objeto tiene propiedades que la computadora puede leer.\n\nUna 🍎 tiene la propiedad TIPO = MANZANA. Una 🍊 tiene TIPO = NARANJA.\n\n¡La computadora no «ve» las frutas — lee sus propiedades!",
        highlightWord = "propiedades",
        emoji         = "🏷️",
        accentColor   = GREEN_LIME,
        extraType     = ExtraType.PROPERTIES_TABLE
    ),
    ClsSlide(
        title         = "Reglas SI → ENTONCES",
        message       = "¿Recuerdas los algoritmos de la Zona 1? Clasificar también es un algoritmo, pero con condiciones.\n\nLa computadora pregunta: ¿cuál es la propiedad? Y aplica la regla que corresponde. ¡Así decide a qué grupo va cada dato!",
        highlightWord = "SI → ENTONCES",
        emoji         = "⚡",
        accentColor   = AMBER,
        extraType     = ExtraType.RULES_TABLE
    ),
    ClsSlide(
        title         = "Un Error = Bug 🐛",
        message       = "Si la computadora aplica la regla equivocada, ocurre un error de clasificación. ¡Se llama bug!\n\nEn el juego, si pones una fruta en el árbol incorrecto, el bosque se pondrá rojo 🔴. Un buen programador aplica la regla correcta siempre.",
        highlightWord = "bug",
        emoji         = "🐛",
        accentColor   = RED_BUG,
        extraType     = ExtraType.BUG_TABLE
    ),
    ClsSlide(
        title         = "¡Tú Eres el Motor de Clasificación!",
        message       = "Ahora tú aplicarás las reglas como una computadora. ¡Clasifica todas las frutas sin bugs y el bosque quedará ordenado!",
        highlightWord = "¡Tú Eres el Motor de Clasificación!",
        emoji         = "🏆",
        accentColor   = GREEN_LIME,
        extraType     = ExtraType.STEPS_LIST
    )
)

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun Context.findClsTutorialActivity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findClsTutorialActivity()
    else              -> null
}

// ─── Pantalla ─────────────────────────────────────────────────────────────────

@Composable
fun ClassificationTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context.findClsTutorialActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var current by remember { mutableIntStateOf(0) }
    val slide   = SLIDES[current]

    fun goNext() {
        if (current < SLIDES.lastIndex) current++
        else navController.navigate(Routes.Level1G2.route) {
            popUpTo(Routes.ClassificationTutorial.route) { inclusive = true }
        }
    }
    fun skip() = navController.navigate(Routes.Level1G2.route) {
        popUpTo(Routes.ClassificationTutorial.route) { inclusive = true }
    }

    // Per-slide animations
    val charAlpha  = remember(current) { Animatable(0f) }
    val charSlide  = remember(current) { Animatable(-60f) }
    val cardAlpha  = remember(current) { Animatable(0f) }
    val cardSlide  = remember(current) { Animatable(40f) }
    val glow       = remember { Animatable(0.5f) }

    LaunchedEffect(current) {
        launch { charAlpha.animateTo(1f, tween(420)) }
        launch { charSlide.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) }
        launch { cardAlpha.animateTo(1f, tween(380)) }
        launch { cardSlide.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) }
    }
    LaunchedEffect(Unit) {
        while (true) { glow.animateTo(1f, tween(900)); glow.animateTo(0.5f, tween(900)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF001A0A), Color(0xFF002D12), Color(0xFF001A0A)))
            )
    ) {
        // Grid verde de fondo
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            val (w, h) = size.width to size.height
            for (i in 0..12) drawLine(Color(0xFF4CAF50).copy(alpha = 0.04f), Offset(0f, h * i / 12f), Offset(w, h * i / 12f), 1f)
            for (i in 0..20) drawLine(Color(0xFF4CAF50).copy(alpha = 0.03f), Offset(w * i / 20f, 0f), Offset(w * i / 20f, h), 1f)
        })

        // Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart).padding(16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GREEN_LIME.copy(alpha = 0.10f))
                .border(1.dp, GREEN_LIME.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("ZONA 2  ·  CLASIFICACIÓN", fontSize = 9.sp,
                fontFamily = OrbitronFontFamily, color = GREEN_LIME.copy(alpha = 0.75f), letterSpacing = 1.5.sp)
        }

        // Botón Saltar
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd).padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.07f))
                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); skip() }) }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text("Saltar  ›", fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(alpha = 0.40f))
        }

        // Layout principal
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Lina — izquierda
            Box(
                modifier = Modifier
                    .weight(0.85f).fillMaxHeight()
                    .graphicsLayer { alpha = charAlpha.value; translationX = charSlide.value },
                contentAlignment = Alignment.Center
            ) { LinaPanel(slide = slide, glow = glow.value) }

            // Diálogo — derecha
            Box(
                modifier = Modifier
                    .weight(1.2f).fillMaxHeight()
                    .graphicsLayer { alpha = cardAlpha.value; translationY = cardSlide.value },
                contentAlignment = Alignment.Center
            ) { ClsDialogCard(slide = slide, index = current, total = SLIDES.size, glow = glow.value, onNext = { goNext() }) }
        }
    }
}

// ─── Panel de Lina ────────────────────────────────────────────────────────────

@Composable
private fun LinaPanel(slide: ClsSlide, glow: Float) {
    val float = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) { float.animateTo(10f, tween(1200)); float.animateTo(-10f, tween(1200)) }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Emoji badge
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(slide.accentColor.copy(alpha = 0.15f))
                .border(2.dp, slide.accentColor.copy(alpha = 0.60f), CircleShape)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) { Text(slide.emoji, fontSize = 22.sp) }

        Spacer(Modifier.height(12.dp))

        // Imagen Lina flotando
        Box(
            modifier = Modifier
                .offset(y = float.value.dp)
                .size(200.dp)
                .drawBehind {
                    drawCircle(slide.accentColor.copy(alpha = glow * 0.18f),
                        radius = size.minDimension / 2f + 22f)
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

        Spacer(Modifier.height(10.dp))

        // Nombre
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(slide.accentColor.copy(alpha = 0.12f))
                .border(1.dp, slide.accentColor.copy(alpha = 0.50f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Text("LINA", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = slide.accentColor, letterSpacing = 3.sp)
        }
    }
}

// ─── Card de diálogo ──────────────────────────────────────────────────────────

@Composable
private fun ClsDialogCard(
    slide: ClsSlide,
    index: Int,
    total: Int,
    glow: Float,
    onNext: () -> Unit
) {
    val isLast = index == total - 1

    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {

        // Indicador de progreso (puntos)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 14.dp)) {
            repeat(total) { i ->
                val a by animateFloatAsState(if (i == index) 1f else 0.28f, tween(300), label = "dot$i")
                Box(modifier = Modifier
                    .size(if (i == index) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(slide.accentColor.copy(alpha = a)))
            }
        }

        // Card principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = slide.accentColor.copy(alpha = glow * 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f)
                    )
                }
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, slide.accentColor.copy(alpha = glow * 0.85f), RoundedCornerShape(24.dp))
                .padding(28.dp)
        ) {
            Column {
                // Título
                Text(slide.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = slide.accentColor)

                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(2.dp)
                    .background(Brush.horizontalGradient(listOf(slide.accentColor.copy(0.8f), Color.Transparent))))

                Spacer(Modifier.height(14.dp))

                // Mensaje con palabra resaltada
                val annotated = buildAnnotatedString {
                    val msg = slide.message; val kw = slide.highlightWord
                    if (kw.isNotEmpty() && msg.contains(kw)) {
                        val s = msg.indexOf(kw)
                        append(msg.substring(0, s))
                        withStyle(SpanStyle(color = slide.accentColor, fontWeight = FontWeight.ExtraBold)) { append(kw) }
                        append(msg.substring(s + kw.length))
                    } else append(msg)
                }
                Text(annotated, fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.90f), lineHeight = 21.sp)

                Spacer(Modifier.height(14.dp))

                // Panel extra según tipo
                when (slide.extraType) {
                    ExtraType.PROPERTIES_TABLE -> PropertiesTable()
                    ExtraType.RULES_TABLE      -> RulesTable()
                    ExtraType.BUG_TABLE        -> BugTable()
                    ExtraType.STEPS_LIST       -> StepsList()
                    ExtraType.NONE             -> Unit
                }

                Spacer(Modifier.height(20.dp))

                // Fila inferior: contador + botón
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1} / $total", fontSize = 10.sp,
                        fontFamily = OrbitronFontFamily, color = Color.White.copy(0.28f))

                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = slide.accentColor.copy(alpha = glow * 0.28f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                                )
                            }
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isLast) listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                                    else listOf(slide.accentColor.copy(0.20f), Color(0xFF050A14))
                                )
                            )
                            .border(2.dp, slide.accentColor.copy(glow), RoundedCornerShape(14.dp))
                            .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); onNext() }) }
                            .padding(horizontal = 22.dp, vertical = 11.dp)
                    ) {
                        Text(if (isLast) "¡Al Bosque!  →" else "Siguiente  →",
                            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = OrbitronFontFamily, color = Color.White)
                    }
                }
            }
        }

        // Tip inferior
        Spacer(Modifier.height(10.dp))
        Text(
            text = when (index) {
                0 -> "💡 En bases de datos, cada fila es un dato y cada columna es una propiedad"
                1 -> "💡 Las condiciones SI→ENTONCES se llaman sentencias condicionales en programación"
                2 -> "💡 Los programadores prueban sus reglas para encontrar y eliminar bugs"
                3 -> "💡 Los motores de búsqueda como Google clasifican millones de datos por segundo"
                else -> ""
            },
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(0.36f), lineHeight = 14.sp
        )
    }
}

// ─── Paneles extra ────────────────────────────────────────────────────────────

@Composable
private fun PropertiesTable() {
    val rows = listOf(
        Triple("🍎", "MANZANA", GREEN_LIME),
        Triple("🍊", "NARANJA", Color(0xFFFF9800)),
        Triple("🍋", "LIMÓN",   AMBER),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1F0A))
            .border(1.dp, GREEN_LIME.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("DATO", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1f))
            Text("PROPIEDAD  (tipo =)", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(2f))
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(GREEN_LIME.copy(0.20f)))
        rows.forEach { (emoji, label, color) ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text("tipo = $label", fontSize = 11.sp, fontFamily = Baloo2FontFamily,
                    fontWeight = FontWeight.Bold, color = color, modifier = Modifier.weight(2f))
            }
        }
    }
}

@Composable
private fun RulesTable() {
    val rules = listOf(
        Triple("SI 🍎 tipo = MANZANA", "🌳 Manzano", GREEN_LIME),
        Triple("SI 🍊 tipo = NARANJA",  "🌳 Naranjo", Color(0xFFFF9800)),
        Triple("SI 🍋 tipo = LIMÓN",    "🌳 Limonero", AMBER),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF100F00))
            .border(1.dp, AMBER.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        rules.forEach { (condition, result, color) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(condition, fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                    color = color, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.6f))
                Text("→  ENTONCES ir a $result", fontSize = 10.sp,
                    fontFamily = Baloo2FontFamily, color = Color.White.copy(0.80f),
                    modifier = Modifier.weight(1.4f))
            }
        }
    }
}

@Composable
private fun BugTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A0505))
            .border(1.dp, RED_BUG.copy(0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Bug
            Column(modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("❌  BUG", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = RED_BUG, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RED_BUG.copy(0.12f))
                    .border(1.dp, RED_BUG.copy(0.50f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("🍎 → 🌳 Naranjo", fontSize = 11.sp,
                        fontFamily = Baloo2FontFamily, color = RED_BUG,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
            // Correcto
            Column(modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅  CORRECTO", fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    color = GREEN_LIME, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GREEN_LIME.copy(0.10f))
                    .border(1.dp, GREEN_LIME.copy(0.50f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text("🍎 → 🌳 Manzano", fontSize = 11.sp,
                        fontFamily = Baloo2FontFamily, color = GREEN_LIME,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun StepsList() {
    val steps = listOf(
        Triple("1", "Toca una fruta para leer su propiedad", "🍎"),
        Triple("2", "Aplica la regla: ¿A qué árbol pertenece?", "⚡"),
        Triple("3", "Toca el árbol correcto para clasificarla", "🌳"),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1F0A))
            .border(1.dp, GREEN_LIME.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEach { (num, text, icon) ->
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(GREEN_LIME.copy(0.18f))
                        .border(1.5.dp, GREEN_LIME.copy(0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(num, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.ExtraBold, color = GREEN_LIME)
                }
                Text(icon, fontSize = 16.sp)
                Text(text, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.88f), modifier = Modifier.weight(1f))
            }
        }
    }
}
