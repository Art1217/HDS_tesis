package com.example.hds_tesisapp.ui.theme.games.game9

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
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun Context.findGame9Activity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findGame9Activity()
    else -> null
}

// ─── Data ─────────────────────────────────────────────────────────────────────

private enum class SymbolType {
    CIRCLE, TRIANGLE, SQUARE, CROSS
}

// Each row definition: what kind of symbol, how many columns, which column index is the error
private data class RowDef(
    val symbolType: SymbolType,
    val columns: Int,
    val errorIndex: Int,  // which cell is the "bug"
    val color: Color
)

private val rows = listOf(
    RowDef(SymbolType.CIRCLE,   5, 0, Color(0xFFE040FB)),  // row 1: circles, error at index 0 (half circle)
    RowDef(SymbolType.TRIANGLE, 5, 1, Color(0xFF00E5FF)),  // row 2: triangles, error at index 1 (inverted)
    RowDef(SymbolType.SQUARE,   5, 0, Color(0xFF76FF03)),  // row 3: squares, error at index 0 (has dot inside)
    RowDef(SymbolType.CROSS,    4, 2, Color(0xFFFFFF00)),  // row 4: crosses, error at index 2 (is X instead of +)
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun Game9Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = remember { context.findGame9Activity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val scope = rememberCoroutineScope()

    // Track selected cell per row (-1 = not selected)
    val selectedCells = remember { mutableStateListOf(-1, -1, -1, -1) }
    var hasChecked by remember { mutableStateOf(false) }
    var allCorrect by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    // Calculate correctness
    val correctCount = rows.indices.count { selectedCells[it] == rows[it].errorIndex }

    fun onCheck() {
        hasChecked = true
        allCorrect = correctCount == rows.size
        showResult = true
        if (allCorrect) {
            scope.launch {
                delay(2500)
                gameComplete = true
            }
        }
    }

    fun onRetry() {
        for (i in selectedCells.indices) selectedCells[i] = -1
        hasChecked = false
        allCorrect = false
        showResult = false
    }

    // Entrance animation
    val screenAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        screenAlpha.animateTo(1f, tween(500))
    }

    // Background gradient — dark cave/tech theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A0033), Color(0xFF2D1B69), Color(0xFF1A0033))
                )
            )
    ) {
        // Win overlay
        if (gameComplete) {
            GameCompleteOverlay9(onContinue = onLevelComplete)
            return@Box
        }

        // ── Title ──
        Text(
            text = "🔍 ENCUENTRA EL ERROR",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = Baloo2FontFamily,
            color = Color(0xFFE040FB),
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .graphicsLayer { alpha = screenAlpha.value }
        )

        // ── Subtitle ──
        Text(
            text = "Selecciona el símbolo diferente en cada fila",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp)
                .graphicsLayer { alpha = screenAlpha.value }
        )

        // ── Main content: Robot + Grid ──
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
                .graphicsLayer { alpha = screenAlpha.value },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Robot / mascot area
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Atom robot placeholder
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF37474F), Color(0xFF263238))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 42.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Atom",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Baloo2FontFamily,
                        color = Color(0xFF00E5FF)
                    )
                    // Speech bubble with hint
                    if (!hasChecked) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .background(
                                    Color(0xFF263238),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "¡Busca el\ndiferente!",
                                fontSize = 11.sp,
                                fontFamily = Baloo2FontFamily,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // CENTER: Symbol grid
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rows.forEachIndexed { rowIndex, rowDef ->
                    SymbolRow(
                        rowDef = rowDef,
                        rowIndex = rowIndex,
                        selectedCell = selectedCells[rowIndex],
                        hasChecked = hasChecked,
                        onCellSelected = { cellIndex ->
                            if (!hasChecked) {
                                selectedCells[rowIndex] = if (selectedCells[rowIndex] == cellIndex) -1 else cellIndex
                            }
                        }
                    )
                    if (rowIndex < rows.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // RIGHT: Check / Retry buttons + score
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Selected count indicator
                    val selectedCount = selectedCells.count { it >= 0 }
                    Text(
                        text = "$selectedCount/${rows.size}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = Baloo2FontFamily,
                        color = if (selectedCount == rows.size) Color(0xFF69F0AE) else Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "seleccionados",
                        fontSize = 10.sp,
                        fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!hasChecked) {
                        // Check button
                        val canCheck = selectedCount == rows.size
                        ActionButton9(
                            text = "VERIFICAR",
                            enabled = canCheck,
                            colors = if (canCheck) listOf(Color(0xFF00C853), Color(0xFF00897B))
                            else listOf(Color(0xFF424242), Color(0xFF303030)),
                            borderColor = if (canCheck) Color(0xFF69F0AE) else Color(0xFF616161)
                        ) {
                            if (canCheck) onCheck()
                        }
                    } else {
                        // Result display
                        Text(
                            text = if (allCorrect) "✅" else "❌",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "$correctCount/${rows.size}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Baloo2FontFamily,
                            color = if (allCorrect) Color(0xFF69F0AE) else Color(0xFFEF5350)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!allCorrect) {
                            ActionButton9(
                                text = "REINTENTAR",
                                enabled = true,
                                colors = listOf(Color(0xFFFF6F00), Color(0xFFE65100)),
                                borderColor = Color(0xFFFFCC80)
                            ) { onRetry() }
                        }
                    }
                }
            }
        }

        // ── Feedback toast ──
        AnimatedVisibility(
            visible = showResult,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        ) {
            val msg = if (allCorrect) "¡Perfecto! Encontraste todos los errores 🎉"
            else "Encontraste $correctCount de ${rows.size}. ¡Inténtalo de nuevo!"
            val bg = if (allCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
            val brd = if (allCorrect) Color(0xFF69F0AE) else Color(0xFFEF9A9A)

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(bg, RoundedCornerShape(12.dp))
                    .border(2.dp, brd, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = msg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Baloo2FontFamily,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Symbol Row ───────────────────────────────────────────────────────────────

@Composable
private fun SymbolRow(
    rowDef: RowDef,
    rowIndex: Int,
    selectedCell: Int,
    hasChecked: Boolean,
    onCellSelected: (Int) -> Unit
) {
    // Staggered entrance
    val rowScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(rowIndex * 120L + 100L)
        rowScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.graphicsLayer {
            scaleX = rowScale.value
            scaleY = rowScale.value
        }
    ) {
        for (col in 0 until rowDef.columns) {
            val isError = col == rowDef.errorIndex
            val isSelected = selectedCell == col

            // Determine border color based on state
            val borderColor = when {
                hasChecked && isSelected && isError -> Color(0xFF69F0AE)  // correct pick
                hasChecked && isSelected && !isError -> Color(0xFFEF5350) // wrong pick
                hasChecked && !isSelected && isError -> Color(0xFFFFEB3B) // missed error
                isSelected -> Color(0xFFE040FB) // selected (not checked yet)
                else -> Color(0xFF424242)
            }
            val borderWidth = if (isSelected || (hasChecked && isError)) 3.dp else 1.5.dp

            SymbolCell(
                symbolType = rowDef.symbolType,
                isError = isError,
                color = rowDef.color,
                isSelected = isSelected,
                borderColor = borderColor,
                borderWidth = borderWidth,
                hasChecked = hasChecked,
                onClick = { onCellSelected(col) }
            )
        }
    }
}

// ─── Symbol Cell ──────────────────────────────────────────────────────────────

@Composable
private fun SymbolCell(
    symbolType: SymbolType,
    isError: Boolean,
    color: Color,
    isSelected: Boolean,
    borderColor: Color,
    borderWidth: androidx.compose.ui.unit.Dp,
    hasChecked: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "cellPress"
    )

    val cellSize = 52.dp

    Box(
        modifier = Modifier
            .size(cellSize)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFF2A1450) else Color(0xFF1A0A30),
                RoundedCornerShape(12.dp)
            )
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .drawBehind {
                // Subtle inner glow when selected
                if (isSelected && !hasChecked) {
                    drawRoundRect(
                        color = color.copy(alpha = 0.15f),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                }
            }
            .pointerInput(hasChecked) {
                if (!hasChecked) {
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
        // Draw the symbol using Canvas
        Canvas(
            modifier = Modifier.size(30.dp)
        ) {
            when (symbolType) {
                SymbolType.CIRCLE -> {
                    if (isError) {
                        // Error: draw an incomplete circle (C shape)
                        drawArc(
                            color = color,
                            startAngle = 40f,
                            sweepAngle = 280f,
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                            topLeft = Offset(2f, 2f),
                            size = Size(size.width - 4f, size.height - 4f)
                        )
                    } else {
                        // Normal: full circle
                        drawCircle(
                            color = color,
                            radius = size.minDimension / 2 - 3f,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
                SymbolType.TRIANGLE -> {
                    if (isError) {
                        // Error: inverted triangle (pointing down)
                        drawInvertedTriangle(color, 3.dp.toPx())
                    } else {
                        // Normal: triangle pointing up
                        drawTriangle(color, 3.dp.toPx())
                    }
                }
                SymbolType.SQUARE -> {
                    if (isError) {
                        // Error: square with a dot inside
                        drawRoundRect(
                            color = color,
                            style = Stroke(width = 3.dp.toPx()),
                            cornerRadius = CornerRadius(3f),
                            topLeft = Offset(3f, 3f),
                            size = Size(size.width - 6f, size.height - 6f)
                        )
                        drawCircle(
                            color = color,
                            radius = 4f,
                            center = center
                        )
                    } else {
                        // Normal: plain square
                        drawRoundRect(
                            color = color,
                            style = Stroke(width = 3.dp.toPx()),
                            cornerRadius = CornerRadius(3f),
                            topLeft = Offset(3f, 3f),
                            size = Size(size.width - 6f, size.height - 6f)
                        )
                    }
                }
                SymbolType.CROSS -> {
                    if (isError) {
                        // Error: X shape (rotated 45°)
                        rotate(45f) {
                            drawCrossShape(color, 3.dp.toPx())
                        }
                    } else {
                        // Normal: + shape
                        drawCrossShape(color, 3.dp.toPx())
                    }
                }
            }
        }
    }
}

// ─── Drawing helpers ──────────────────────────────────────────────────────────

private fun DrawScope.drawTriangle(color: Color, strokeWidth: Float) {
    val path = Path().apply {
        moveTo(size.width / 2, 2f)
        lineTo(size.width - 2f, size.height - 2f)
        lineTo(2f, size.height - 2f)
        close()
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

private fun DrawScope.drawInvertedTriangle(color: Color, strokeWidth: Float) {
    val path = Path().apply {
        moveTo(size.width / 2, size.height - 2f)
        lineTo(size.width - 2f, 2f)
        lineTo(2f, 2f)
        close()
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

private fun DrawScope.drawCrossShape(color: Color, strokeWidth: Float) {
    val cx = size.width / 2
    val cy = size.height / 2
    val arm = size.minDimension / 2 - 3f
    // Vertical bar
    drawLine(color, Offset(cx, cy - arm), Offset(cx, cy + arm), strokeWidth, StrokeCap.Round)
    // Horizontal bar
    drawLine(color, Offset(cx - arm, cy), Offset(cx + arm, cy), strokeWidth, StrokeCap.Round)
}

// ─── Action Button ────────────────────────────────────────────────────────────

@Composable
private fun ActionButton9(
    text: String,
    enabled: Boolean,
    colors: List<Color>,
    borderColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "btnPress"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(colors), RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                        startY = 0f, endY = size.height * 0.45f
                    )
                )
            }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                }
            }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = Baloo2FontFamily,
            color = Color.White
        )
    }
}

// ─── Game Complete Overlay ─────────────────────────────────────────────────────

@Composable
private fun GameCompleteOverlay9(onContinue: () -> Unit) {
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
            .background(Color.Black.copy(alpha = 0.75f)),
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
                    Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF6A1B9A))),
                    RoundedCornerShape(24.dp)
                )
                .border(3.dp, Color(0xFFE040FB), RoundedCornerShape(24.dp))
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🐛✅", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡BUGS ELIMINADOS!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Baloo2FontFamily,
                color = Color(0xFFE040FB),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Encontraste todos los errores",
                fontSize = 15.sp,
                fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text("⭐⭐⭐", fontSize = 24.sp, modifier = Modifier.padding(vertical = 6.dp))

            Spacer(modifier = Modifier.height(14.dp))

            ActionButton9(
                text = "CONTINUAR →",
                enabled = true,
                colors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00)),
                borderColor = Color(0xFFFFE082)
            ) { onContinue() }
        }
    }
}
