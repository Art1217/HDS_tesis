package com.example.hds_tesisapp.ui.theme.games.game1

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


// ===========================================================
// DROP TARGET REGISTRY
// ===========================================================
object DropTargetRegistry {
    private val targets = mutableMapOf<String, DropTarget>()

    fun registerTarget(name: String, area: Rect?, onDrop: (String) -> Unit) {
        targets[name] = DropTarget(area, onDrop)
    }

    fun handleDrop(position: Offset, value: String) {
        targets.forEach { (_, target) ->
            if (target.area?.contains(position) == true) {
                target.onDrop(value)
            }
        }
    }

    data class DropTarget(val area: Rect?, val onDrop: (String) -> Unit)
}


// ===========================================================
// DROP TARGET CARD (con highlight de hint)
// ===========================================================
@Composable
fun DropTargetCard(
    placeholder: String,
    droppedValue: String?,
    isHinted: Boolean = false,
    onDrop: (String) -> Unit
) {
    var area by remember { mutableStateOf<Rect?>(null) }

    val hintAlpha = remember { Animatable(0.3f) }
    LaunchedEffect(isHinted) {
        if (isHinted) {
            while (true) {
                hintAlpha.animateTo(1f, tween(500))
                hintAlpha.animateTo(0.2f, tween(500))
            }
        } else {
            hintAlpha.snapTo(0.3f)
        }
    }

    val borderColor = if (isHinted)
        Color(0xFFFFD600).copy(alpha = hintAlpha.value)
    else
        Color.Cyan

    val borderWidth = if (isHinted) 3.dp else 2.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Flecha indicadora arriba del slot activo
        if (isHinted) {
            Text(
                text = "▼",
                color = Color(0xFFFFD600).copy(alpha = hintAlpha.value),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Spacer(modifier = Modifier.height(22.dp))
        }

        Box(
            modifier = Modifier
                .size(120.dp, 80.dp)
                .drawBehind {
                    if (isHinted) {
                        // Glow amarillo alrededor del slot activo
                        drawRoundRect(
                            color = Color(0xFFFFD600).copy(alpha = hintAlpha.value * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f)
                        )
                    }
                }
                .background(Color(0xFF0A0F1F), RoundedCornerShape(16.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
                .onGloballyPositioned { layout ->
                    val rect = layout.boundsInWindow()
                    area = Rect(rect.left, rect.top, rect.right, rect.bottom)
                    DropTargetRegistry.registerTarget(placeholder, area!!, onDrop)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = droppedValue ?: placeholder,
                color = if (droppedValue != null) Color(0xFF69FF47) else Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                fontFamily = Baloo2FontFamily,
                fontWeight = if (droppedValue != null) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}


fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}


// ===========================================================
// PANTALLA DE JUEGO (NIVEL 1)
// ===========================================================
@Composable
fun GameScreen(onLevelComplete: () -> Unit) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Estados de los slots (subidos al nivel de GameScreen)
    var step1 by remember { mutableStateOf<String?>(null) }
    var step2 by remember { mutableStateOf<String?>(null) }
    var step3 by remember { mutableStateOf<String?>(null) }

    // Control de overlays
    var showTutorialDialog by remember { mutableStateOf(true) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showVictory by remember { mutableStateOf(false) }
    var showErrorHint by remember { mutableStateOf(false) }

    // Slot activo para el hint (1=slot1, 2=slot2, 3=slot3, 0=todos llenos)
    val hintStep = when {
        step1 == null -> 1
        step2 == null -> 2
        step3 == null -> 3
        else -> 0
    }
    // Solo mostramos hints si el tutorial fue cerrado
    val hintsActive = !showTutorialDialog && !showVictory

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Fondo ──
        Image(
            painter = painterResource(id = R.drawable.fondogame1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ── Layout principal ──
        Row(modifier = Modifier.fillMaxSize()) {

            // Personaje izquierda
            Box(
                modifier = Modifier
                    .weight(0.50f)
                    .wrapContentHeight(unbounded = true),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pers_g1),
                    contentDescription = "Personaje",
                    modifier = Modifier
                        .height(1100.dp)
                        .offset(y = 80.dp)
                        .padding(top = 15.dp)
                )
            }

            // UI derecha
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "PASOS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily,
                        color = Color.Cyan,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instrucción dinámica
                    Text(
                        text = when {
                            hintStep == 1 && hintsActive -> "Arrastra el número al primer paso"
                            hintStep == 2 && hintsActive -> "¡Bien! Ahora el segundo paso"
                            hintStep == 3 && hintsActive -> "¡Casi! Solo falta el último"
                            hintStep == 0 && hintsActive -> "¡Listo! Presiona COMPROBAR"
                            else -> "Ordena los pasos correctamente"
                        },
                        fontSize = 12.sp,
                        fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slots con hints
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DropTargetCard(
                            placeholder = "Entrar a la torre",
                            droppedValue = step1,
                            isHinted = hintsActive && hintStep == 1
                        ) { step1 = it }

                        DropTargetCard(
                            placeholder = "Abrir la puerta",
                            droppedValue = step2,
                            isHinted = hintsActive && hintStep == 2
                        ) { step2 = it }

                        DropTargetCard(
                            placeholder = "Resolver el código",
                            droppedValue = step3,
                            isHinted = hintsActive && hintStep == 3
                        ) { step3 = it }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DraggableNumber("1")
                        DraggableNumber("2")
                        DraggableNumber("3")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FuturisticButton(
                        onClick = {
                            if (step1 == "3" && step2 == "2" && step3 == "1") {
                                showVictory = true
                            } else {
                                // En modo tutorial: en vez de toast, Atom aparece con ayuda
                                showErrorHint = true
                            }
                        }
                    )
                }
            }
        }

        // ── Botón de ayuda [?] ──
        if (!showTutorialDialog && !showVictory) {
            HelpButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .zIndex(5f)
            ) { showHelpDialog = true }
        }

        // ── Dialog tutorial (inicial) ──
        if (showTutorialDialog) {
            AtomDialog(
                message = "¡Oh no! El ataque de Glitch desordenó a los Bits.\n\nPara que este Bit pueda llegar a su casa, necesitamos darle una secuencia de pasos exacta.\n\n¡Ayúdame a ordenarlos!",
                onDismiss = { showTutorialDialog = false }
            )
        }

        // ── Dialog de ayuda [?] ──
        if (showHelpDialog) {
            AtomDialog(
                message = "Coloca los 3 bloques en orden para guiar al Bit a su casa.\n\nRecuerda: primero debe Resolver el código, luego Abrir la puerta, y finalmente Entrar a la torre.",
                onDismiss = { showHelpDialog = false }
            )
        }

        // ── Dialog de error (respuesta incorrecta) ──
        if (showErrorHint) {
            AtomDialog(
                message = "¡Casi! Revisa el orden de los pasos.\n\nPiensa: ¿qué se hace primero para entrar a un edificio?\n\n¡Tú puedes!",
                onDismiss = {
                    showErrorHint = false
                    // Limpiar slots para que reintente
                    step1 = null
                    step2 = null
                    step3 = null
                }
            )
        }

        // ── Pantalla de victoria ──
        if (showVictory) {
            VictoryOverlay(onNext = onLevelComplete)
        }
    }
}


// ===========================================================
// DIALOG DE ATOM (tutorial / ayuda)
// ===========================================================
@Composable
fun AtomDialog(message: String, onDismiss: () -> Unit) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(250)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    val scope = rememberCoroutineScope()

    fun dismiss() {
        scope.launch {
            launch { alpha.animateTo(0f, tween(200)) }
            scale.animateTo(0.8f, tween(200))
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .zIndex(10f)
            .pointerInput(Unit) { /* Bloquea toques al fondo */ },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.78f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen de Atom
                Image(
                    painter = painterResource(id = R.drawable.atom),
                    contentDescription = "Atom",
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Nombre del personaje
                    Text(
                        text = "ATOM",
                        fontSize = 14.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mensaje
                    Text(
                        text = message,
                        fontSize = 15.sp,
                        fontFamily = Baloo2FontFamily,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón Entendido
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00838F), Color(0xFF00BCD4))
                                )
                            )
                            .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = {
                                    tryAwaitRelease()
                                    dismiss()
                                })
                            }
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "¡Entendido!",
                            fontSize = 14.sp,
                            fontFamily = Baloo2FontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


// ===========================================================
// PANTALLA DE VICTORIA
// ===========================================================
@Composable
fun VictoryOverlay(onNext: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val starScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(300)) }
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        starScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow))
    }

    // Pulso del brillo
    val glow = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        while (true) {
            glow.animateTo(1f, tween(700))
            glow.animateTo(0.6f, tween(700))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value }
                .fillMaxWidth(0.60f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF001830), Color(0xFF002040))
                    )
                )
                .border(2.5.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(28.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Estrellas
                Text(
                    text = "⭐  ⭐  ⭐",
                    fontSize = 36.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = starScale.value
                        scaleY = starScale.value
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡NIVEL COMPLETADO!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily,
                    color = Color(0xFF69FF47),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¡El Bit llegó a casa!\nAprendiste a crear un algoritmo.",
                    fontSize = 14.sp,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Botón Siguiente
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.4f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 22f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                            )
                        )
                        .border(
                            2.dp,
                            Color(0xFF69FF47).copy(alpha = glow.value),
                            RoundedCornerShape(16.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                tryAwaitRelease()
                                onNext()
                            })
                        }
                        .padding(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Siguiente Nivel  →",
                        fontSize = 16.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


// ===========================================================
// BOTÓN DE AYUDA [?]
// ===========================================================
@Composable
fun HelpButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val glow = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) {
            glow.animateTo(1f, tween(900))
            glow.animateTo(0.5f, tween(900))
        }
    }

    Box(
        modifier = modifier
            .size(46.dp)
            .drawBehind {
                drawCircle(
                    color = Color(0xFFFFD600).copy(alpha = glow.value * 0.25f),
                    radius = size.minDimension / 2f + 8f
                )
            }
            .clip(CircleShape)
            .background(Color(0xFF0A0F1F))
            .border(2.dp, Color(0xFFFFD600).copy(alpha = glow.value), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    tryAwaitRelease()
                    onClick()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = Color(0xFFFFD600)
        )
    }
}


// ===========================================================
// NÚMERO ARRASTRABLE
// ===========================================================
@Composable
fun DraggableNumber(number: String) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .size(70.dp)
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInWindow()
            }
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .background(
                Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0097A7))),
                RoundedCornerShape(20.dp)
            )
            .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        val dropCenter = globalPosition + offset +
                                Offset(density.run { 35.dp.toPx() }, density.run { 35.dp.toPx() })
                        DropTargetRegistry.handleDrop(dropCenter, number)
                        offset = Offset.Zero
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            number,
            color = Color.Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = OrbitronFontFamily
        )
    }
}
