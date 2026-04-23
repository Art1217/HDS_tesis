package com.example.hds_tesisapp.ui.theme.games.level1

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily


// ============================================================
//  Comandos disponibles del Nivel 1
// ============================================================
enum class GameCommand(
    val label: String,
    val icon: String,
    val accentColor: Color
) {
    AVANZAR         ("Avanzar",      "▲", Color(0xFF00E5FF)),
    GIRAR_IZQUIERDA ("Girar izq.",   "◄", Color(0xFFFFD600)),
    GIRAR_DERECHA   ("Girar der.",   "►", Color(0xFFFF7043)),
}

val AVAILABLE_COMMANDS = listOf(
    GameCommand.AVANZAR,
    GameCommand.GIRAR_IZQUIERDA,
    GameCommand.GIRAR_DERECHA,
)


// ============================================================
//  Panel de comandos completo
// ============================================================
@Composable
fun Level1CommandPanel(
    slots: List<GameCommand?>,
    hintSlotIndex: Int,       // índice del slot a destacar (-1 = ninguno)
    hintsActive: Boolean,
    canExecute: Boolean,
    isExecuting: Boolean,
    onCommandTap: (GameCommand) -> Unit,
    onReset: () -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "PROGRAMA",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = Color.Cyan,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (!hintsActive) "Escucha a Atom primero"
                   else "Toca un comando para agregarlo al paso indicado",
            fontSize = 11.sp,
            fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Slots de pasos ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            slots.forEachIndexed { index, command ->
                CommandSlot(
                    stepNumber = index + 1,
                    command = command,
                    isHinted = hintsActive && hintSlotIndex == index
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── Separador ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF00E5FF).copy(alpha = 0.20f))
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Comandos disponibles ──
        Text(
            text = "COMANDOS",
            fontSize = 11.sp,
            fontFamily = OrbitronFontFamily,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AVAILABLE_COMMANDS.forEach { cmd ->
                CommandCard(
                    command = cmd,
                    enabled = hintsActive && !isExecuting,
                    onTap = { onCommandTap(cmd) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Botones de acción ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón reset
            ActionButton(
                text = "↺  Reset",
                accentColor = Color(0xFFFF5252),
                enabled = slots.any { it != null } && !isExecuting,
                onClick = onReset
            )
            // Botón ejecutar
            ActionButton(
                text = "▶  Ejecutar",
                accentColor = Color(0xFF69FF47),
                enabled = canExecute && !isExecuting,
                onClick = onExecute
            )
        }
    }
}


// ============================================================
//  Slot individual de paso
// ============================================================
@Composable
private fun CommandSlot(
    stepNumber: Int,
    command: GameCommand?,
    isHinted: Boolean
) {
    val hintAlpha = remember { Animatable(0.25f) }
    LaunchedEffect(isHinted) {
        if (isHinted) {
            while (true) {
                hintAlpha.animateTo(1f, tween(480))
                hintAlpha.animateTo(0.2f, tween(480))
            }
        } else {
            hintAlpha.snapTo(0.25f)
        }
    }

    val borderColor = when {
        command != null -> command.accentColor.copy(alpha = 0.9f)
        isHinted        -> Color(0xFFFFD600).copy(alpha = hintAlpha.value)
        else            -> Color.White.copy(alpha = 0.18f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Indicador de paso
        Text(
            text = "Paso $stepNumber",
            fontSize = 9.sp,
            fontFamily = OrbitronFontFamily,
            color = if (isHinted) Color(0xFFFFD600).copy(alpha = hintAlpha.value)
                    else Color.White.copy(alpha = 0.4f)
        )
        // Flecha guía
        if (isHinted) {
            Text(
                text = "▼",
                fontSize = 14.sp,
                color = Color(0xFFFFD600).copy(alpha = hintAlpha.value),
                fontWeight = FontWeight.Bold
            )
        } else {
            Spacer(modifier = Modifier.height(18.dp))
        }

        Box(
            modifier = Modifier
                .size(88.dp, 68.dp)
                .drawBehind {
                    if (isHinted) {
                        drawRoundRect(
                            color = Color(0xFFFFD600).copy(alpha = hintAlpha.value * 0.25f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f)
                        )
                    }
                }
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (command != null)
                        Brush.radialGradient(
                            listOf(command.accentColor.copy(alpha = 0.18f), Color(0xFF050A14))
                        )
                    else
                        Brush.radialGradient(listOf(Color(0xFF0D1B2A), Color(0xFF050A14)))
                )
                .border(2.dp, borderColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (command != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = command.icon,
                        fontSize = 22.sp,
                        color = command.accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = command.label,
                        fontSize = 9.sp,
                        fontFamily = Baloo2FontFamily,
                        color = command.accentColor.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "___",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}


// ============================================================
//  Tarjeta de comando (paleta)
// ============================================================
@Composable
private fun CommandCard(
    command: GameCommand,
    enabled: Boolean,
    onTap: () -> Unit
) {
    val pressAlpha = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .size(82.dp, 70.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        command.accentColor.copy(alpha = if (enabled) 0.22f else 0.07f),
                        Color(0xFF050A14)
                    )
                )
            )
            .border(
                2.dp,
                command.accentColor.copy(alpha = if (enabled) 0.85f else 0.25f),
                RoundedCornerShape(14.dp)
            )
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(onPress = {
                        pressAlpha.animateTo(0.6f, tween(80))
                        tryAwaitRelease()
                        pressAlpha.animateTo(1f, tween(120))
                        onTap()
                    })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = command.icon,
                fontSize = 26.sp,
                color = command.accentColor.copy(alpha = if (enabled) 1f else 0.35f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = command.label,
                fontSize = 9.sp,
                fontFamily = Baloo2FontFamily,
                color = command.accentColor.copy(alpha = if (enabled) 0.85f else 0.3f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}


// ============================================================
//  Botón de acción genérico (Execute / Reset)
// ============================================================
@Composable
private fun ActionButton(
    text: String,
    accentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val pressAlpha = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(50.dp)
            .drawBehind {
                if (enabled) {
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.25f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                    )
                }
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    if (enabled)
                        listOf(accentColor.copy(alpha = 0.20f), Color(0xFF050A14))
                    else
                        listOf(Color(0xFF0A0F1F), Color(0xFF050A14))
                )
            )
            .border(
                2.dp,
                accentColor.copy(alpha = if (enabled) 0.80f else 0.20f),
                RoundedCornerShape(14.dp)
            )
            .graphicsLayer { alpha = pressAlpha.value }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(onPress = {
                        pressAlpha.animateTo(0.6f, tween(80))
                        tryAwaitRelease()
                        pressAlpha.animateTo(1f, tween(120))
                        onClick()
                    })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = OrbitronFontFamily,
            color = accentColor.copy(alpha = if (enabled) 1f else 0.30f)
        )
    }
}
