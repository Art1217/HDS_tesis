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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Datos de cada slide ──────────────────────────────────────────────────────

private data class TutorialSlide(
    val title: String,
    val message: String,
    val highlightWord: String = "",
    val emoji: String,
    val useAtom: Boolean,        // true = Atom, false = Bit
    val accentColor: Color,
    val commandIcons: List<String> = emptyList()  // iconos extra (slide 3)
)

private val SLIDES = listOf(
    TutorialSlide(
        title        = "¿Qué es un Algoritmo?",
        message      = "¡Hola! Soy Atom, tu guía robótico 🤖\n\nUn algoritmo es una lista de pasos ordenados para resolver un problema. ¡Es como una receta de cocina! Sigues los pasos en orden y obtienes el resultado.",
        highlightWord = "algoritmo",
        emoji        = "📋",
        useAtom      = true,
        accentColor  = Color(0xFF00E5FF)
    ),
    TutorialSlide(
        title        = "¡El Orden Importa!",
        message      = "Si haces los pasos en el orden equivocado, ¡todo sale mal! 😅\n\nImagina ponerte primero los zapatos y LUEGO los calcetines... ¡qué desastre!\n\nUn buen algoritmo siempre tiene el orden correcto.",
        highlightWord = "orden correcto",
        emoji        = "🔢",
        useAtom      = true,
        accentColor  = Color(0xFFFFD600)
    ),
    TutorialSlide(
        title        = "Bit Necesita tus Instrucciones",
        message      = "Bit es mi amigo robot. Él no puede moverse solo — necesita que TÚ le des instrucciones exactas.\n\nTú eres su programador. ¡Usa estos comandos para guiarlo!",
        highlightWord = "TÚ",
        emoji        = "🤖",
        useAtom      = false,
        accentColor  = Color(0xFF69FF47),
        commandIcons = listOf("⬆", "⬇", "↰", "↱")
    ),
    TutorialSlide(
        title        = "¡Crea tu Primer Algoritmo!",
        message      = "En cada nivel crearás un algoritmo para llevar a Bit a su casita 🏠.\n\nSi los pasos están bien ordenados, ¡Bit llegará a casa! Si no... ¡Glitch lo confundirá! 😈\n\n¡Tú puedes lograrlo!",
        highlightWord = "¡Tú puedes lograrlo!",
        emoji        = "🏆",
        useAtom      = true,
        accentColor  = Color(0xFF69FF47)
    )
)

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun Context.findTutorialActivity(): Activity? = when (this) {
    is Activity     -> this
    is ContextWrapper -> baseContext.findTutorialActivity()
    else            -> null
}

// ─── Pantalla principal ───────────────────────────────────────────────────────

@Composable
fun AlgorithmTutorialScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context.findTutorialActivity() }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var currentSlide by remember { mutableIntStateOf(0) }
    val totalSlides = SLIDES.size
    val slide       = SLIDES[currentSlide]

    fun goToNext() {
        if (currentSlide < totalSlides - 1) {
            currentSlide++
        } else {
            navController.navigate(Routes.Level1.route) {
                popUpTo(Routes.AlgorithmTutorial.route) { inclusive = true }
            }
        }
    }

    fun skip() {
        navController.navigate(Routes.Level1.route) {
            popUpTo(Routes.AlgorithmTutorial.route) { inclusive = true }
        }
    }

    // Animaciones por slide
    val charAlpha  = remember(currentSlide) { Animatable(0f) }
    val charOffset = remember(currentSlide) { Animatable(-60f) }
    val cardAlpha  = remember(currentSlide) { Animatable(0f) }
    val cardOffset = remember(currentSlide) { Animatable(40f) }
    val glowAnim   = remember { Animatable(0.5f) }

    LaunchedEffect(currentSlide) {
        launch { charAlpha.animateTo(1f, tween(420)) }
        launch { charOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) }
        launch { cardAlpha.animateTo(1f, tween(380)) }
        launch { cardOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            glowAnim.animateTo(1f, tween(900))
            glowAnim.animateTo(0.5f, tween(900))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001530), Color(0xFF000A1A)))
            )
    ) {
        // Grid digital de fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    for (i in 0..12) drawLine(Color(0xFF00E5FF).copy(alpha = 0.04f), Offset(0f, h * i / 12f), Offset(w, h * i / 12f), 1f)
                    for (i in 0..20) drawLine(Color(0xFF00E5FF).copy(alpha = 0.03f), Offset(w * i / 20f, 0f), Offset(w * i / 20f, h), 1f)
                }
        )

        // Botón "Saltar" — esquina superior derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { tryAwaitRelease(); skip() })
                }
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                "Saltar  ›",
                fontSize = 11.sp,
                fontFamily = OrbitronFontFamily,
                color = Color.White.copy(alpha = 0.45f)
            )
        }

        // Layout principal: izquierda = personaje, derecha = diálogo
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Lado izquierdo: personaje ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha       = charAlpha.value
                        translationX = charOffset.value
                    },
                contentAlignment = Alignment.Center
            ) {
                CharacterPanel(slide = slide, glowAnim = glowAnim.value)
            }

            // ── Lado derecho: burbuja de diálogo ──────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha       = cardAlpha.value
                        translationY = cardOffset.value
                    },
                contentAlignment = Alignment.Center
            ) {
                DialogCard(
                    slide         = slide,
                    currentSlide  = currentSlide,
                    totalSlides   = totalSlides,
                    glowAnim      = glowAnim.value,
                    onNext        = { goToNext() }
                )
            }
        }

        // Badge "ZONA 1 · TUTORIAL" superior izquierda
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF00E5FF).copy(alpha = 0.12f))
                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                "ZONA 1  ·  ¿QUÉ ES UN ALGORITMO?",
                fontSize = 9.sp,
                fontFamily = OrbitronFontFamily,
                color = Color(0xFF00E5FF).copy(alpha = 0.75f),
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ─── Panel del personaje ─────────────────────────────────────────────────────

@Composable
private fun CharacterPanel(slide: TutorialSlide, glowAnim: Float) {
    val floatAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            floatAnim.animateTo(10f, tween(1200))
            floatAnim.animateTo(-10f, tween(1200))
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Emoji decorativo sobre el personaje
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(slide.accentColor.copy(alpha = 0.15f))
                .border(2.dp, slide.accentColor.copy(alpha = 0.6f), CircleShape)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(slide.emoji, fontSize = 22.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Imagen del personaje con glow
        Box(
            modifier = Modifier
                .offset(y = floatAnim.value.dp)
                .size(200.dp)
                .drawBehind {
                    drawCircle(
                        slide.accentColor.copy(alpha = glowAnim * 0.20f),
                        radius = size.minDimension / 2f + 20f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    if (slide.useAtom) R.drawable.tom_atom_character
                    else R.drawable.bit_tutorial
                ),
                contentDescription = if (slide.useAtom) "Atom" else "Bit",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Nombre del personaje
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(slide.accentColor.copy(alpha = 0.12f))
                .border(1.dp, slide.accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (slide.useAtom) "ATOM" else "BIT",
                fontSize = 12.sp,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                color = slide.accentColor,
                letterSpacing = 3.sp
            )
        }

        // Íconos de comandos (solo slide 3 - Bit)
        if (slide.commandIcons.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slide.commandIcons.forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF69FF47).copy(alpha = 0.12f))
                            .border(1.5.dp, Color(0xFF69FF47).copy(alpha = 0.7f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// ─── Burbuja de diálogo ───────────────────────────────────────────────────────

@Composable
private fun DialogCard(
    slide: TutorialSlide,
    currentSlide: Int,
    totalSlides: Int,
    glowAnim: Float,
    onNext: () -> Unit
) {
    val isLast = currentSlide == totalSlides - 1

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        // Indicador de progreso (puntos)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            repeat(totalSlides) { index ->
                val isActive = index == currentSlide
                val dotAlpha by animateFloatAsState(if (isActive) 1f else 0.30f, tween(300), label = "dot")
                Box(
                    modifier = Modifier
                        .size(if (isActive) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(slide.accentColor.copy(alpha = dotAlpha))
                )
            }
        }

        // Card principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = slide.accentColor.copy(alpha = glowAnim * 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f)
                    )
                }
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, slide.accentColor.copy(alpha = glowAnim * 0.85f), RoundedCornerShape(24.dp))
                .padding(28.dp)
        ) {
            Column {
                // Título del slide
                Text(
                    text = slide.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    color = slide.accentColor,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.height(4.dp))

                // Separador decorativo
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(slide.accentColor.copy(alpha = 0.8f), Color.Transparent)
                            )
                        )
                )

                Spacer(Modifier.height(16.dp))

                // Mensaje principal con palabra resaltada
                val annotated = buildAnnotatedString {
                    val msg  = slide.message
                    val kw   = slide.highlightWord
                    if (kw.isNotEmpty() && msg.contains(kw)) {
                        val start = msg.indexOf(kw)
                        append(msg.substring(0, start))
                        withStyle(
                            SpanStyle(
                                color      = slide.accentColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) { append(kw) }
                        append(msg.substring(start + kw.length))
                    } else {
                        append(msg)
                    }
                }

                Text(
                    text       = annotated,
                    fontSize   = 14.sp,
                    fontFamily = Baloo2FontFamily,
                    color      = Color.White.copy(alpha = 0.90f),
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(28.dp))

                // Numeración del slide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${currentSlide + 1} / $totalSlides",
                        fontSize = 10.sp,
                        fontFamily = OrbitronFontFamily,
                        color = Color.White.copy(alpha = 0.30f)
                    )

                    // Botón Siguiente / A jugar
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = slide.accentColor.copy(alpha = glowAnim * 0.28f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                                )
                            }
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isLast)
                                        listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                                    else
                                        listOf(
                                            slide.accentColor.copy(alpha = 0.22f),
                                            Color(0xFF050A14)
                                        )
                                )
                            )
                            .border(
                                2.dp,
                                slide.accentColor.copy(alpha = glowAnim),
                                RoundedCornerShape(14.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = { tryAwaitRelease(); onNext() })
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = if (isLast) "¡A jugar!  →" else "Siguiente  →",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = OrbitronFontFamily,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Tip decorativo inferior
        Spacer(Modifier.height(12.dp))
        Text(
            text = when (currentSlide) {
                0 -> "💡 Un algoritmo puede describirse en palabras, diagramas o código"
                1 -> "💡 Los computadores siguen instrucciones exactamente como se las damos"
                2 -> "💡 ¡Los programadores son los que dan instrucciones a las computadoras!"
                3 -> "💡 Cada nivel te hará pensar como un verdadero programador"
                else -> ""
            },
            fontSize = 10.sp,
            fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.38f),
            textAlign = TextAlign.Start,
            lineHeight = 15.sp
        )
    }
}
