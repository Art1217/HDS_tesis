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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

private data class SortSlide(
    val title: String,
    val message: String,
    val highlightWord: String = "",
    val emoji: String,
    val accentColor: Color,
    val extraType: SortExtraType = SortExtraType.NONE
)

private enum class SortExtraType {
    NONE,
    COMPARISON_TABLE,
    BUBBLE_STEPS,
    PASS_VISUAL,
    STEPS_LIST
}

private val WATER_BLUE  = Color(0xFF00B0FF)
private val CYAN_GLOW   = Color(0xFF00E5FF)
private val AMBER_SORT  = Color(0xFFFFD600)
private val GREEN_OK    = Color(0xFF69FF47)

private val SORT_SLIDES = listOf(
    SortSlide(
        title         = "¿Qué es Ordenar?",
        message       = "Ordenar significa organizar elementos según una regla: de menor a mayor, de más corto a más alto...\n\nLas computadoras ordenan datos millones de veces al día — para mostrar resultados de búsqueda, rankings, listas de contactos...",
        highlightWord = "Ordenar",
        emoji         = "📊",
        accentColor   = WATER_BLUE,
        extraType     = SortExtraType.COMPARISON_TABLE
    ),
    SortSlide(
        title         = "Comparar de a Pares",
        message       = "El truco más simple: compara dos elementos a la vez.\n\nSi A > B → están en el orden equivocado → ¡intercámbialo!\nSi A ≤ B → están bien → no hagas nada.\n\nRepite esto para cada par y el dato más grande irá \"subiendo\" como una burbuja.",
        highlightWord = "Comparar",
        emoji         = "⚖️",
        accentColor   = AMBER_SORT,
        extraType     = SortExtraType.BUBBLE_STEPS
    ),
    SortSlide(
        title         = "El Algoritmo Burbuja",
        message       = "Bubble Sort hace pasadas completas sobre la lista:\n\nEn cada pasada, el elemento más grande \"burbujea\" hasta su posición final.\n\nDespués de N-1 pasadas, toda la lista queda ordenada.",
        highlightWord = "Bubble Sort",
        emoji         = "🫧",
        accentColor   = CYAN_GLOW,
        extraType     = SortExtraType.PASS_VISUAL
    ),
    SortSlide(
        title         = "¡Tú Eres el Algoritmo!",
        message       = "En la Cascada, las gotas de agua están desordenadas.\n\nUsarás la Piedra Comparadora para comparar cada par de gotas y decidir si deben intercambiarse.\n\n¡Ordena todas las gotas para que fluyan en armonía!",
        highlightWord = "¡Tú Eres el Algoritmo!",
        emoji         = "💧",
        accentColor   = GREEN_OK,
        extraType     = SortExtraType.STEPS_LIST
    )
)

private fun Context.findSortTutActivity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findSortTutActivity()
    else              -> null
}

@Composable
fun SortingTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context.findSortTutActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var current by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    val slide   = SORT_SLIDES[current]

    fun goNext() {
        if (current < SORT_SLIDES.lastIndex) current++
        else navController.navigate(Routes.Level1G3.route) {
            popUpTo(Routes.SortingTutorial.route) { inclusive = true }
        }
    }
    fun skip() = navController.navigate(Routes.Level1G3.route) {
        popUpTo(Routes.SortingTutorial.route) { inclusive = true }
    }

    val charAlpha = remember(current) { Animatable(0f) }
    val charSlide = remember(current) { Animatable(-60f) }
    val cardAlpha = remember(current) { Animatable(0f) }
    val cardSlide = remember(current) { Animatable(40f) }
    val glow      = remember { Animatable(0.5f) }

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
                Brush.verticalGradient(listOf(Color(0xFF000D1A), Color(0xFF001828), Color(0xFF000D1A)))
            )
    ) {
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            val (w, h) = size.width to size.height
            for (i in 0..12) drawLine(WATER_BLUE.copy(alpha = 0.04f), Offset(0f, h * i / 12f), Offset(w, h * i / 12f), 1f)
            for (i in 0..20) drawLine(WATER_BLUE.copy(alpha = 0.03f), Offset(w * i / 20f, 0f), Offset(w * i / 20f, h), 1f)
        })

        Box(
            modifier = Modifier
                .align(Alignment.TopStart).padding(16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(WATER_BLUE.copy(alpha = 0.10f))
                .border(1.dp, WATER_BLUE.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("ZONA 3  ·  ORDENAMIENTO", fontSize = 9.sp,
                fontFamily = OrbitronFontFamily, color = WATER_BLUE.copy(alpha = 0.75f), letterSpacing = 1.5.sp)
        }

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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.85f).fillMaxHeight()
                    .graphicsLayer { alpha = charAlpha.value; translationX = charSlide.value },
                contentAlignment = Alignment.Center
            ) { TomPanel(slide = slide, glow = glow.value) }

            Box(
                modifier = Modifier
                    .weight(1.2f).fillMaxHeight()
                    .graphicsLayer { alpha = cardAlpha.value; translationY = cardSlide.value },
                contentAlignment = Alignment.Center
            ) { SortDialogCard(slide = slide, index = current, total = SORT_SLIDES.size, glow = glow.value, onNext = { goNext() }) }
        }
    }
}

@Composable
private fun TomPanel(slide: SortSlide, glow: Float) {
    val float = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) { float.animateTo(10f, tween(1200)); float.animateTo(-10f, tween(1200)) }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(slide.accentColor.copy(alpha = 0.15f))
                .border(2.dp, slide.accentColor.copy(alpha = 0.60f), CircleShape)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) { Text(slide.emoji, fontSize = 22.sp) }

        Spacer(Modifier.height(12.dp))

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
                painter = painterResource(R.drawable.tom_y_atom),
                contentDescription = "Tom Atom",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(slide.accentColor.copy(alpha = 0.12f))
                .border(1.dp, slide.accentColor.copy(alpha = 0.50f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Text("TOM ATOM", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = slide.accentColor, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun SortDialogCard(
    slide: SortSlide,
    index: Int,
    total: Int,
    glow: Float,
    onNext: () -> Unit
) {
    val isLast = index == total - 1

    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 14.dp)) {
            repeat(total) { i ->
                val a by animateFloatAsState(if (i == index) 1f else 0.28f, tween(300), label = "dot$i")
                Box(modifier = Modifier
                    .size(if (i == index) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(slide.accentColor.copy(alpha = a)))
            }
        }

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
                Text(slide.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = slide.accentColor)

                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(2.dp)
                    .background(Brush.horizontalGradient(listOf(slide.accentColor.copy(0.8f), Color.Transparent))))

                Spacer(Modifier.height(14.dp))

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

                when (slide.extraType) {
                    SortExtraType.COMPARISON_TABLE -> SortComparisonTable()
                    SortExtraType.BUBBLE_STEPS     -> BubbleStepsPanel()
                    SortExtraType.PASS_VISUAL      -> PassVisualPanel()
                    SortExtraType.STEPS_LIST       -> SortStepsList()
                    SortExtraType.NONE             -> Unit
                }

                Spacer(Modifier.height(20.dp))

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
                        Text(if (isLast) "¡A la Cascada!  →" else "Siguiente  →",
                            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = OrbitronFontFamily, color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = when (index) {
                0 -> "💡 Google ordena millones de resultados en menos de un segundo usando algoritmos de sorting"
                1 -> "💡 Bubble Sort compara A y B: si A > B, los intercambia — como burbujas que suben"
                2 -> "💡 Bubble Sort tiene O(n²) comparaciones en el peor caso — hay algoritmos más rápidos, pero es el más fácil de entender"
                3 -> "💡 Cuando lo domines, aprenderás QuickSort y MergeSort — ¡mucho más rápidos!"
                else -> ""
            },
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(0.36f), lineHeight = 14.sp
        )
    }
}

@Composable
private fun SortComparisonTable() {
    val rows = listOf(
        Triple(3, 7, false),
        Triple(9, 2, true),
        Triple(5, 5, false),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF001828))
            .border(1.dp, WATER_BLUE.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("A", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("vs", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("B", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("¿Swap?", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(WATER_BLUE.copy(0.20f)))
        rows.forEach { (a, b, swap) ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("$a", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = WATER_BLUE, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(if (a > b) ">" else if (a < b) "<" else "=", fontSize = 14.sp,
                    fontFamily = OrbitronFontFamily, color = Color.White.copy(0.7f),
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("$b", fontSize = 16.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = WATER_BLUE, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(if (swap) "✅ Sí" else "❌ No", fontSize = 12.sp,
                    fontFamily = Baloo2FontFamily, fontWeight = FontWeight.Bold,
                    color = if (swap) Color(0xFFFF9800) else GREEN_OK,
                    modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun BubbleStepsPanel() {
    val steps = listOf(
        Triple("1", "Compara A y B (par adyacente)", "⚖️"),
        Triple("2", "Si A > B → intercambia (swap)", "🔄"),
        Triple("3", "Avanza al siguiente par", "→"),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF100F00))
            .border(1.dp, AMBER_SORT.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        steps.forEach { (num, text, icon) ->
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                        .background(AMBER_SORT.copy(0.18f))
                        .border(1.5.dp, AMBER_SORT.copy(0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(num, fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = AMBER_SORT) }
                Text(icon, fontSize = 14.sp)
                Text(text, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.88f), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PassVisualPanel() {
    // Show a mini Bubble Sort pass visualization
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF001828))
            .border(1.dp, CYAN_GLOW.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Pasada 1: el mayor burbujea al final", fontSize = 10.sp,
            fontFamily = OrbitronFontFamily, color = CYAN_GLOW.copy(0.7f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            listOf("3","1","4","2","5").forEachIndexed { i, v ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (i == 4) CYAN_GLOW.copy(0.25f) else Color(0xFF001828))
                        .border(1.dp, if (i == 4) CYAN_GLOW else Color.White.copy(0.2f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) { Text(v, fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    color = if (i == 4) CYAN_GLOW else Color.White.copy(0.8f),
                    fontWeight = FontWeight.Bold) }
            }
        }
        Text("✅ 5 llega a su posición final", fontSize = 10.sp,
            fontFamily = Baloo2FontFamily, color = Color.White.copy(0.6f))
        Text("Repite hasta que todo esté en orden.", fontSize = 10.sp,
            fontFamily = Baloo2FontFamily, color = Color.White.copy(0.5f))
    }
}

@Composable
private fun SortStepsList() {
    val steps = listOf(
        Triple("1", "Lee los valores de dos gotas (A y B)", "💧"),
        Triple("2", "Decide: ¿necesitan intercambiarse?", "🤔"),
        Triple("3", "Presiona el botón correcto en la Piedra", "🪨"),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1F0A))
            .border(1.dp, GREEN_OK.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEach { (num, text, icon) ->
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(26.dp).clip(CircleShape)
                        .background(GREEN_OK.copy(0.18f))
                        .border(1.5.dp, GREEN_OK.copy(0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(num, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.ExtraBold, color = GREEN_OK) }
                Text(icon, fontSize = 16.sp)
                Text(text, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.88f), modifier = Modifier.weight(1f))
            }
        }
    }
}
