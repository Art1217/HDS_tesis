package com.example.hds_tesisapp.ui.theme.games.game8

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun Context.findGame8Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame8Activity()
    else -> null
}

// ─── Data ─────────────────────────────────────────────────────────────────────

private data class CharacterInfo(
    val name: String,
    val emoji: String,
    val description: String,
    val drawableRes: Int,
    val gradientColors: List<Color>,
    val borderColor: Color
)

private data class Scenario(
    val situationEmoji: String,
    val situationTitle: String,
    val situationDescription: String,
    val correctCharacterIndex: Int,  // index into the characters list
    val feedbackCorrect: String,
    val feedbackWrong: String,
    val bgGradient: List<Color>
)

private val characters = listOf(
    CharacterInfo(
        name = "Max",
        emoji = "💪",
        description = "Fuerza y vuelo",
        drawableRes = R.drawable.max_character,
        gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)),
        borderColor = Color(0xFF90CAF9)
    ),
    CharacterInfo(
        name = "Lina",
        emoji = "🧠",
        description = "Mente estratégica",
        drawableRes = R.drawable.lina_character,
        gradientColors = listOf(Color(0xFFAB47BC), Color(0xFF6A1B9A)),
        borderColor = Color(0xFFCE93D8)
    ),
    CharacterInfo(
        name = "Tom",
        emoji = "🔧",
        description = "Constructor",
        drawableRes = R.drawable.tom_atom_character,
        gradientColors = listOf(Color(0xFFFFA726), Color(0xFFEF6C00)),
        borderColor = Color(0xFFFFCC80)
    ),
    CharacterInfo(
        name = "Atom",
        emoji = "🤖",
        description = "Robot-guía",
        drawableRes = R.drawable.robots,
        gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
        borderColor = Color(0xFFA5D6A7)
    )
)

private val scenarios = listOf(
    Scenario(
        situationEmoji = "🏗️",
        situationTitle = "¡El puente está roto!",
        situationDescription = "Un puente se ha derrumbado y los animales no pueden cruzar el río. ¿Quién puede construir uno nuevo?",
        correctCharacterIndex = 2,  // Tom
        feedbackCorrect = "¡Correcto! Tom puede construir un puente nuevo con sus herramientas 🔧",
        feedbackWrong = "Hmm... necesitamos a alguien que sepa construir cosas",
        bgGradient = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
    ),
    Scenario(
        situationEmoji = "🪨",
        situationTitle = "¡Una roca gigante bloquea el camino!",
        situationDescription = "Una enorme roca cayó y bloquea la entrada de la cueva. ¿Quién puede moverla?",
        correctCharacterIndex = 0,  // Max
        feedbackCorrect = "¡Exacto! Max usa su súper fuerza para mover la roca 💪",
        feedbackWrong = "Necesitamos a alguien con mucha fuerza...",
        bgGradient = listOf(Color(0xFF37474F), Color(0xFF455A64), Color(0xFF546E7A))
    ),
    Scenario(
        situationEmoji = "🔐",
        situationTitle = "¡Un código secreto!",
        situationDescription = "La puerta tiene una cerradura con un patrón misterioso. ¿Quién puede descifrar el código?",
        correctCharacterIndex = 1,  // Lina
        feedbackCorrect = "¡Bien hecho! Lina descifra el patrón con su mente brillante 🧠",
        feedbackWrong = "Este acertijo necesita a alguien muy inteligente...",
        bgGradient = listOf(Color(0xFF4A148C), Color(0xFF6A1B9A), Color(0xFF7B1FA2))
    ),
    Scenario(
        situationEmoji = "❓",
        situationTitle = "¡No entiendo este problema!",
        situationDescription = "El niño se trabó con un ejercicio de matemáticas y no sabe cómo resolverlo. ¿Quién puede explicarle?",
        correctCharacterIndex = 3,  // Atom
        feedbackCorrect = "¡Perfecto! Atom le explica paso a paso el concepto 🤖",
        feedbackWrong = "Necesitamos a alguien que sepa explicar bien...",
        bgGradient = listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2))
    )
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun Game8Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findGame8Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()

    // Game state
    var currentScenarioIndex by remember { mutableIntStateOf(0) }
    var selectedCharacterIndex by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }

    val scenario = scenarios[currentScenarioIndex]

    // Entrance animation
    val screenAlpha = remember { Animatable(0f) }
    LaunchedEffect(currentScenarioIndex) {
        screenAlpha.snapTo(0f)
        screenAlpha.animateTo(1f, tween(400))
    }

    // Handle answer
    fun onCharacterSelected(index: Int) {
        if (showFeedback) return
        selectedCharacterIndex = index
        isCorrect = index == scenario.correctCharacterIndex
        showFeedback = true
        if (isCorrect == true) score++

        scope.launch {
            delay(2200)
            if (currentScenarioIndex < scenarios.size - 1) {
                currentScenarioIndex++
                selectedCharacterIndex = null
                isCorrect = null
                showFeedback = false
            } else {
                gameComplete = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(scenario.bgGradient))
    ) {
        // ── Win overlay ──
        if (gameComplete) {
            GameCompleteOverlay(score = score, total = scenarios.size, onContinue = onLevelComplete)
            return@Box
        }

        // ── Progress bar (top) ──
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 80.dp, end = 80.dp)
                .fillMaxWidth()
                .graphicsLayer { alpha = screenAlpha.value },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            scenarios.forEachIndexed { idx, _ ->
                val color = when {
                    idx < currentScenarioIndex -> Color(0xFF69F0AE)
                    idx == currentScenarioIndex -> Color(0xFFFFEB3B)
                    else -> Color.White.copy(alpha = 0.3f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .padding(horizontal = 3.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${currentScenarioIndex + 1}/${scenarios.size}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // ── Main content ──
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
                .graphicsLayer { alpha = screenAlpha.value },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Situation card
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                SituationCard(scenario = scenario, showFeedback = showFeedback, isCorrect = isCorrect)
            }

            // RIGHT: Character selection
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Quién debe ayudar?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 2x2 grid of characters
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterCard(
                        character = characters[0],
                        index = 0,
                        selectedIndex = selectedCharacterIndex,
                        correctIndex = scenario.correctCharacterIndex,
                        showFeedback = showFeedback
                    ) { onCharacterSelected(0) }

                    CharacterCard(
                        character = characters[1],
                        index = 1,
                        selectedIndex = selectedCharacterIndex,
                        correctIndex = scenario.correctCharacterIndex,
                        showFeedback = showFeedback
                    ) { onCharacterSelected(1) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterCard(
                        character = characters[2],
                        index = 2,
                        selectedIndex = selectedCharacterIndex,
                        correctIndex = scenario.correctCharacterIndex,
                        showFeedback = showFeedback
                    ) { onCharacterSelected(2) }

                    CharacterCard(
                        character = characters[3],
                        index = 3,
                        selectedIndex = selectedCharacterIndex,
                        correctIndex = scenario.correctCharacterIndex,
                        showFeedback = showFeedback
                    ) { onCharacterSelected(3) }
                }
            }
        }

        // ── Feedback toast ──
        AnimatedVisibility(
            visible = showFeedback,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            val feedbackText = if (isCorrect == true) scenario.feedbackCorrect else scenario.feedbackWrong
            val feedbackColor = if (isCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)
            val feedbackBorder = if (isCorrect == true) Color(0xFF69F0AE) else Color(0xFFEF9A9A)

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(feedbackColor, RoundedCornerShape(16.dp))
                    .border(2.dp, feedbackBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feedbackText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Situation Card ───────────────────────────────────────────────────────────

@Composable
private fun SituationCard(
    scenario: Scenario,
    showFeedback: Boolean,
    isCorrect: Boolean?
) {
    val cardScale = remember { Animatable(0.8f) }
    LaunchedEffect(scenario) {
        cardScale.snapTo(0.8f)
        cardScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = cardScale.value
                scaleY = cardScale.value
            }
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF263238).copy(alpha = 0.95f), Color(0xFF37474F).copy(alpha = 0.95f))
                ),
                RoundedCornerShape(20.dp)
            )
            .border(2.dp, Color(0xFFFFEB3B).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Situation emoji (large)
            Text(
                text = scenario.situationEmoji,
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Title
            Text(
                text = scenario.situationTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Baloo2FontFamily,
                color = Color(0xFFFFEB3B),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Description
            Text(
                text = scenario.situationDescription,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ─── Character Card ───────────────────────────────────────────────────────────

@Composable
private fun CharacterCard(
    character: CharacterInfo,
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int,
    showFeedback: Boolean,
    onClick: () -> Unit
) {
    // Press animation
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "charPress"
    )

    // Determine border/glow based on feedback state
    val borderColor = when {
        showFeedback && index == correctIndex -> Color(0xFF69F0AE)
        showFeedback && index == selectedIndex && index != correctIndex -> Color(0xFFEF5350)
        else -> character.borderColor.copy(alpha = 0.7f)
    }
    val borderWidth = when {
        showFeedback && (index == correctIndex || index == selectedIndex) -> 3.dp
        else -> 2.dp
    }
    val dimAlpha = when {
        showFeedback && index != correctIndex && index != selectedIndex -> 0.4f
        else -> 1f
    }

    // Staggered entrance
    val nodeScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 80L + 150L)
        nodeScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = nodeScale.value * pressScale
                scaleY = nodeScale.value * pressScale
                alpha = dimAlpha
            }
    ) {
        // Character avatar circle (claymorphism)
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = if (isPressed) 4.dp else 8.dp,
                    shape = CircleShape,
                    ambientColor = character.gradientColors[1].copy(alpha = 0.4f),
                    spotColor = character.gradientColors[1].copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(Brush.verticalGradient(character.gradientColors))
                .border(borderWidth, borderColor, CircleShape)
                .drawBehind {
                    // Top inner glow
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                            startY = 0f, endY = size.height * 0.4f
                        )
                    )
                    // Bottom shadow
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f)),
                            startY = size.height * 0.65f, endY = size.height
                        )
                    )
                    // Press glow
                    if (isPressed) {
                        drawCircle(
                            color = character.borderColor.copy(alpha = 0.4f),
                            radius = size.width * 0.5f
                        )
                    }
                    // Correct/wrong glow rings
                    if (showFeedback && index == correctIndex) {
                        drawCircle(
                            color = Color(0xFF69F0AE).copy(alpha = 0.3f),
                            radius = size.width * 0.55f
                        )
                    }
                    if (showFeedback && index == selectedIndex && index != correctIndex) {
                        drawCircle(
                            color = Color(0xFFEF5350).copy(alpha = 0.3f),
                            radius = size.width * 0.55f
                        )
                    }
                }
                .pointerInput(showFeedback) {
                    if (!showFeedback) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                                onClick()
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Character image
            Image(
                painter = painterResource(id = character.drawableRes),
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }

        // name label
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = character.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Baloo2FontFamily,
            color = Color.White
        )
        Text(
            text = character.emoji,
            fontSize = 12.sp
        )
    }
}

// ─── Game Complete Overlay ─────────────────────────────────────────────────────

@Composable
private fun GameCompleteOverlay(
    score: Int,
    total: Int,
    onContinue: () -> Unit
) {
    val overlayAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch { overlayAlpha.animateTo(1f, tween(400)) }
        scope.launch {
            delay(200)
            contentScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = overlayAlpha.value }
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = contentScale.value
                    scaleY = contentScale.value
                }
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .border(3.dp, Color(0xFF69F0AE), RoundedCornerShape(24.dp))
                .padding(horizontal = 40.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌟",
                fontSize = 42.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡NIVEL COMPLETO!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Baloo2FontFamily,
                color = Color(0xFFFFEB3B),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Acertaste $score de $total",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Baloo2FontFamily,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Stars based on score
            val stars = when {
                score == total -> "⭐⭐⭐"
                score >= total - 1 -> "⭐⭐"
                score >= 1 -> "⭐"
                else -> ""
            }
            if (stars.isNotEmpty()) {
                Text(
                    text = stars,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
            var btnPressed by remember { mutableStateOf(false) }
            val btnScale by animateFloatAsState(
                targetValue = if (btnPressed) 0.92f else 1f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                label = "btnScale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = btnScale
                        scaleY = btnScale
                    }
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .border(2.dp, Color(0xFFFFE082), RoundedCornerShape(16.dp))
                    .drawBehind {
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                                startY = 0f, endY = size.height * 0.45f
                            )
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                btnPressed = true
                                tryAwaitRelease()
                                btnPressed = false
                                onContinue()
                            }
                        )
                    }
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CONTINUAR →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White
                )
            }
        }
    }
}
