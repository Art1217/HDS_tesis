package com.example.hds_tesisapp.ui.theme.games.game1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt




// ===========================================================
// DROP TARGET REGISTRY (Detecta dónde cae un número arrastrado)
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

    data class DropTarget(
        val area: Rect?,
        val onDrop: (String) -> Unit
    )
}


// ================================================
// COMPONENTE: TARJETA DONDE SE SUELTA EL NÚMERO
// ================================================
@Composable
fun DropTargetCard(
    placeholder: String,
    droppedValue: String?,
    onDrop: (String) -> Unit
) {
    var area by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .size(120.dp, 80.dp)
            .background(Color(0xFF0A0F1F), RoundedCornerShape(16.dp))
            .border(2.dp, Color.Cyan, RoundedCornerShape(16.dp))
            .onGloballyPositioned { layout ->
                val rect = layout.boundsInWindow()
                area = Rect(rect.left, rect.top, rect.right, rect.bottom)
                DropTargetRegistry.registerTarget(placeholder, area!!, onDrop)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = droppedValue ?: placeholder,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}



fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}


// ================================================
// PANTALLA DE JUEGO (FINAL COMPLETA)
// ================================================
@Composable
fun GameScreen(onLevelComplete: () -> Unit) {

    // Forzar orientación horizontal SOLO AQUÍ
    val context = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {}
    }

    // FONDO CON IMAGEN
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // ===== IMAGEN DE FONDO =====
        Image(
            painter = painterResource(id = R.drawable.fondogame1), // <-- tu imagen de fondo
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // ===== CONTENIDO PRINCIPAL
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {

            // ==== PERSONAJE ====
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
            // ==== UI DERECHA ====
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
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Cyan,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // === ESTADOS DE LAS TARJETAS ===
                    var step1 by remember { mutableStateOf<String?>(null) }
                    var step2 by remember { mutableStateOf<String?>(null) }
                    var step3 by remember { mutableStateOf<String?>(null) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DropTargetCard("Entrar a la torre", step1) { step1 = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        DropTargetCard("Abrir la puerta", step2) { step2 = it }
                        Spacer(modifier = Modifier.height(10.dp))
                        DropTargetCard("Resolver el código", step3) { step3 = it }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DraggableNumber("1")
                        DraggableNumber("2")
                        DraggableNumber("3")
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    FuturisticButton(
                        onClick = {
                            if (step1 == "3" && step2 == "2" && step3 == "1") {
                                Toast.makeText(context, "¡Correcto! Has resuelto los pasos.", Toast.LENGTH_SHORT).show()
                                onLevelComplete()
                            } else {
                                Toast.makeText(context, "Incorrecto. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}


// ================================================
// COMPONENTE: NÚMERO ARRASTRABLE
// ================================================
@Composable
fun DraggableNumber(
    number: String
) {
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
            .background(Color.Cyan, RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    },
                    onDragEnd = {
                        // Calculate center of the dragged item
                        val dropCenter = globalPosition + offset + Offset(density.run { 35.dp.toPx() }, density.run { 35.dp.toPx() })
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
            fontWeight = FontWeight.Bold
        )
    }
}
