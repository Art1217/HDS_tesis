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

private data class PtrSlide(
    val title: String,
    val message: String,
    val highlightWord: String = "",
    val emoji: String,
    val accentColor: Color,
    val extraType: PtrExtra = PtrExtra.NONE
)

private enum class PtrExtra { NONE, AB_TABLE, PATTERN_TYPES, DISTRACTOR_TABLE, STEPS_LIST }

private val LIME   = Color(0xFF8BC34A)
private val GOLD   = Color(0xFFFFD600)
private val NEON_R = Color(0xFFFF5252)
private val CYAN_V = Color(0xFF00E5FF)

private val PTR_SLIDES = listOf(
    PtrSlide(
        title         = "Los Patrones Están en Todo",
        message       = "Un patrón es una secuencia que se repite de manera predecible.\n\nEn computación, los algoritmos buscan patrones para hacer predicciones. ¡Si reconoces el patrón, puedes predecir el siguiente dato!",
        highlightWord = "se repite",
        emoji         = "🔁",
        accentColor   = LIME,
        extraType     = PtrExtra.AB_TABLE
    ),
    PtrSlide(
        title         = "AB, ABC y Grupos",
        message       = "Los patrones tienen nombres según cuántos elementos distintos usan:\n\n• AB: alterna entre 2 elementos\n• ABC: cicla entre 3 elementos\n• AAB / ABB: un elemento se repite dentro del ciclo\n\n¡Entre más larga la clave, más difícil el patrón!",
        highlightWord = "AB, ABC",
        emoji         = "🔢",
        accentColor   = GOLD,
        extraType     = PtrExtra.PATTERN_TYPES
    ),
    PtrSlide(
        title         = "¡Cuidado con Distractores!",
        message       = "Un distractor es un elemento que NO pertenece al patrón. Su trabajo es confundirte.\n\nUn buen detector de patrones pregunta: ¿sigue el ciclo o es un impostor?\n\n¡No caigas en la trampa!",
        highlightWord = "distractor",
        emoji         = "🚨",
        accentColor   = NEON_R,
        extraType     = PtrExtra.DISTRACTOR_TABLE
    ),
    PtrSlide(
        title         = "¡Tú Eres el Detector de Patrones!",
        message       = "Identifica la secuencia, predice el siguiente elemento y expulsa los distractores en El Valle de los Patrones. ¡Lina cuenta contigo!",
        highlightWord = "Detector de Patrones",
        emoji         = "🏆",
        accentColor   = LIME,
        extraType     = PtrExtra.STEPS_LIST
    )
)

private fun Context.findPtrActivity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findPtrActivity()
    else              -> null
}

@Composable
fun PatternTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context.findPtrActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var current by remember { mutableIntStateOf(0) }
    val slide   = PTR_SLIDES[current]

    fun goNext() {
        if (current < PTR_SLIDES.lastIndex) current++
        else navController.navigate(Routes.Level1G4.route) {
            popUpTo(Routes.PatternTutorial.route) { inclusive = true }
        }
    }
    fun skip() = navController.navigate(Routes.Level1G4.route) {
        popUpTo(Routes.PatternTutorial.route) { inclusive = true }
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
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1A00), Color(0xFF102800), Color(0xFF0A1A00))))
    ) {
        Box(modifier = Modifier.fillMaxSize().drawBehind {
            val (w, h) = size.width to size.height
            for (i in 0..12) drawLine(LIME.copy(alpha = 0.04f), Offset(0f, h * i / 12f), Offset(w, h * i / 12f), 1f)
            for (i in 0..20) drawLine(LIME.copy(alpha = 0.03f), Offset(w * i / 20f, 0f), Offset(w * i / 20f, h), 1f)
        })

        Box(
            modifier = Modifier
                .align(Alignment.TopStart).padding(16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LIME.copy(alpha = 0.10f))
                .border(1.dp, LIME.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("ZONA 4  ·  PATRONES", fontSize = 9.sp,
                fontFamily = OrbitronFontFamily, color = LIME.copy(alpha = 0.75f), letterSpacing = 1.5.sp)
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
            ) { PtrLinaPanel(slide = slide, glow = glow.value) }

            Box(
                modifier = Modifier
                    .weight(1.2f).fillMaxHeight()
                    .graphicsLayer { alpha = cardAlpha.value; translationY = cardSlide.value },
                contentAlignment = Alignment.Center
            ) {
                PtrDialogCard(
                    slide  = slide,
                    index  = current,
                    total  = PTR_SLIDES.size,
                    glow   = glow.value,
                    onNext = { goNext() }
                )
            }
        }
    }
}

@Composable
private fun PtrLinaPanel(slide: PtrSlide, glow: Float) {
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
                painter = painterResource(R.drawable.lina_character),
                contentDescription = "Lina",
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
            Text("LINA", fontSize = 12.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold, color = slide.accentColor, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun PtrDialogCard(
    slide: PtrSlide, index: Int, total: Int, glow: Float, onNext: () -> Unit
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
                    PtrExtra.AB_TABLE        -> PtrAbTable()
                    PtrExtra.PATTERN_TYPES   -> PtrPatternTypes()
                    PtrExtra.DISTRACTOR_TABLE -> PtrDistractorTable()
                    PtrExtra.STEPS_LIST      -> PtrStepsList()
                    PtrExtra.NONE            -> Unit
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
                                    if (isLast) listOf(Color(0xFF33691E), Color(0xFF558B2F))
                                    else listOf(slide.accentColor.copy(0.20f), Color(0xFF050A14))
                                )
                            )
                            .border(2.dp, slide.accentColor.copy(glow), RoundedCornerShape(14.dp))
                            .pointerInput(Unit) { detectTapGestures(onPress = { tryAwaitRelease(); onNext() }) }
                            .padding(horizontal = 22.dp, vertical = 11.dp)
                    ) {
                        Text(if (isLast) "¡Al Valle!  →" else "Siguiente  →",
                            fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = OrbitronFontFamily, color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = when (index) {
                0 -> "💡 Los algoritmos de compresión como ZIP buscan patrones repetidos en los datos"
                1 -> "💡 El ADN es un patrón de 4 letras (A, T, C, G) que define a cada ser vivo"
                2 -> "💡 Los antivirus detectan virus buscando patrones maliciosos en los programas"
                3 -> "💡 El reconocimiento de voz convierte patrones de sonido en texto"
                else -> ""
            },
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(0.36f), lineHeight = 14.sp
        )
    }
}

@Composable
private fun PtrAbTable() {
    val rows = listOf(
        Triple("AB",  "🌸 🌼 🌸 🌼 🌸 🌼", LIME),
        Triple("ABC", "🔵 🟢 🟡 🔵 🟢 🟡", CYAN_V),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1F0A))
            .border(1.dp, LIME.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("TIPO", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(0.6f))
            Text("SECUENCIA", fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                color = Color.White.copy(0.45f), modifier = Modifier.weight(1.4f))
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LIME.copy(0.20f)))
        rows.forEach { (tipo, seq, color) ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(tipo, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = color, modifier = Modifier.weight(0.6f))
                Text(seq, fontSize = 14.sp, modifier = Modifier.weight(1.4f))
            }
        }
    }
}

@Composable
private fun PtrPatternTypes() {
    val types = listOf(
        Triple("AAB", "🔴 🔴 🔵 🔴 🔴 🔵", GOLD),
        Triple("ABB", "🔴 🔵 🔵 🔴 🔵 🔵", Color(0xFFFF9800)),
        Triple("AABB","🔴 🔴 🔵 🔵 🔴 🔴", Color(0xFFFF7043)),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF100F00))
            .border(1.dp, GOLD.copy(0.30f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { (name, seq, color) ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(0.18f))
                        .border(1.dp, color.copy(0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(name, fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = color)
                }
                Spacer(Modifier.width(10.dp))
                Text(seq, fontSize = 14.sp, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PtrDistractorTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A0505))
            .border(1.dp, NEON_R.copy(0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅ PATRÓN", fontSize = 9.sp, fontFamily = OrbitronFontFamily, color = LIME)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LIME.copy(0.10f))
                        .border(1.dp, LIME.copy(0.50f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) { Text("🟢 🔵 🟢 🔵 ?", fontSize = 12.sp, textAlign = TextAlign.Center) }
                Spacer(Modifier.height(4.dp))
                Text("Respuesta: 🟢", fontSize = 10.sp, color = LIME, fontFamily = Baloo2FontFamily)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚨 DISTRACTOR", fontSize = 9.sp, fontFamily = OrbitronFontFamily, color = NEON_R)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NEON_R.copy(0.10f))
                        .border(1.dp, NEON_R.copy(0.50f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) { Text("🔴 impostor", fontSize = 12.sp, color = NEON_R, textAlign = TextAlign.Center) }
                Spacer(Modifier.height(4.dp))
                Text("¡No pertenece!", fontSize = 10.sp, color = NEON_R, fontFamily = Baloo2FontFamily)
            }
        }
    }
}

@Composable
private fun PtrStepsList() {
    val steps = listOf(
        Triple("1", "Observa la secuencia completa antes de responder", "👁️"),
        Triple("2", "Identifica el ciclo: ¿cada cuántos se repite?", "🔁"),
        Triple("3", "Descarta distractores: si no sigue el ciclo, ¡es trampa!", "🚫"),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1F0A))
            .border(1.dp, LIME.copy(0.30f), RoundedCornerShape(12.dp))
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
                        .background(LIME.copy(0.18f))
                        .border(1.5.dp, LIME.copy(0.70f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(num, fontSize = 11.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.ExtraBold, color = LIME)
                }
                Text(icon, fontSize = 16.sp)
                Text(text, fontSize = 12.sp, fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(0.88f), modifier = Modifier.weight(1f))
            }
        }
    }
}
