package com.example.hds_tesisapp.ui.theme.levels

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

private data class LevelInfo(
    val number: Int,
    val route: String,
    val colors: List<Color>,
    val glowColor: Color,
    val borderColor: Color
)

private val levels = listOf(
    LevelInfo(1, Routes.Game.route,  listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)), Color(0xFFA5D6A7), Color(0xFFC8E6C9)),
    LevelInfo(2, Routes.Game2.route, listOf(Color(0xFF81C784), Color(0xFF388E3C)), Color(0xFFC8E6C9), Color(0xFFDCEDC8)),
    LevelInfo(3, Routes.Game3.route, listOf(Color(0xFFFFA726), Color(0xFFEF6C00)), Color(0xFFFFCC80), Color(0xFFFFE0B2)),
    LevelInfo(4, Routes.Game4.route, listOf(Color(0xFFFFB74D), Color(0xFFF57C00)), Color(0xFFFFE0B2), Color(0xFFFFF3E0)),
    LevelInfo(5, Routes.Game5.route, listOf(Color(0xFFEF5350), Color(0xFFC62828)), Color(0xFFEF9A9A), Color(0xFFFFCDD2)),
    LevelInfo(6, Routes.Game6.route, listOf(Color(0xFFEC407A), Color(0xFFAD1457)), Color(0xFFF48FB1), Color(0xFFF8BBD0)),
    LevelInfo(7, Routes.Game7.route, listOf(Color(0xFFAB47BC), Color(0xFF6A1B9A)), Color(0xFFCE93D8), Color(0xFFE1BEE7)),
)

// Shared math: compute node pixel positions given container dimensions
private fun computeNodePositions(
    containerWidthPx: Float,
    containerHeightPx: Float,
    nodeCount: Int
): List<Offset> {
    val marginPx = 80f
    val usableWidth = containerWidthPx - marginPx * 2
    val spacing = usableWidth / (nodeCount - 1).coerceAtLeast(1)
    val centerY = containerHeightPx / 2f
    val amplitude = containerHeightPx * 0.22f

    return List(nodeCount) { i ->
        val x = marginPx + i * spacing
        val y = if (i % 2 == 0) centerY - amplitude else centerY + amplitude
        Offset(x, y)
    }
}

@Composable
fun LevelsScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB),
                        Color(0xFF90EE90),
                        Color(0xFF228B22)
                    )
                )
            )
    ) {
        // ── Back button ──
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(10f)
                .size(48.dp)
                .shadow(6.dp, CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color(0xFFE8E8E8).copy(alpha = 0.85f)
                        )
                    ),
                    CircleShape
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color(0xFF5D4037),
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Title ──
        Text(
            text = "NIVELES",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = Baloo2FontFamily,
            color = Color.White,
            letterSpacing = 3.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp)
                .graphicsLayer { alpha = contentAlpha.value }
                .drawBehind {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f),
                        radius = size.width * 0.6f,
                        center = Offset(size.width / 2, size.height / 2 + 4f)
                    )
                }
        )

        // ── Level map area ──
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .graphicsLayer { alpha = contentAlpha.value }
        ) {
            val density = LocalDensity.current
            val containerWidthPx = with(density) { maxWidth.toPx() }
            val containerHeightPx = with(density) { maxHeight.toPx() }
            val nodeCount = levels.size
            val positions = remember(containerWidthPx, containerHeightPx) {
                computeNodePositions(containerWidthPx, containerHeightPx, nodeCount)
            }

            // ── Canvas: zigzag path ──
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (positions.size >= 2) {
                    val path = Path().apply {
                        moveTo(positions[0].x, positions[0].y)
                        for (i in 1 until positions.size) {
                            val prev = positions[i - 1]
                            val curr = positions[i]
                            val cx = (prev.x + curr.x) / 2f
                            cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }
                    }

                    // Shadow layer
                    drawPath(
                        path = path,
                        color = Color(0xFF5D4037).copy(alpha = 0.3f),
                        style = Stroke(width = 20f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Main dashed road
                    drawPath(
                        path = path,
                        color = Color(0xFFD7CCC8),
                        style = Stroke(
                            width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(28f, 16f), 0f)
                        )
                    )
                    // Center highlight dots
                    drawPath(
                        path = path,
                        color = Color(0xFFEFEBE9),
                        style = Stroke(
                            width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 22f), 8f)
                        )
                    )
                }
            }

            // ── Level nodes ──
            val nodeSizeDp = 62.dp
            val nodeSizePx = with(density) { nodeSizeDp.toPx() }

            levels.forEachIndexed { index, level ->
                val pos = positions[index]
                LevelNode(
                    level = level,
                    index = index,
                    nodeSizeDp = nodeSizeDp,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (pos.x - nodeSizePx / 2).roundToInt(),
                                (pos.y - nodeSizePx / 2).roundToInt()
                            )
                        }
                ) {
                    navController.navigate(level.route)
                }
            }
        }
    }
}

// ============================================================
//  Level Node — claymorphism circle with number
// ============================================================
@Composable
private fun LevelNode(
    level: LevelInfo,
    index: Int,
    nodeSizeDp: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Staggered entrance
    val nodeScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 100L + 200L)
        nodeScale.animateTo(
            1f,
            spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
        )
    }

    // Press feedback
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "nodePress"
    )

    Box(
        modifier = modifier
            .size(nodeSizeDp)
            .graphicsLayer {
                scaleX = nodeScale.value * pressScale
                scaleY = nodeScale.value * pressScale
            }
            .shadow(
                elevation = if (isPressed) 4.dp else 10.dp,
                shape = CircleShape,
                ambientColor = level.colors[1].copy(alpha = 0.4f),
                spotColor = level.colors[1].copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .background(Brush.verticalGradient(level.colors))
            .border(2.5.dp, level.borderColor.copy(alpha = 0.8f), CircleShape)
            .drawBehind {
                // Top inner glow
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                        startY = 0f, endY = size.height * 0.4f
                    )
                )
                // Bottom inner shadow
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f)),
                        startY = size.height * 0.65f, endY = size.height
                    )
                )
                if (isPressed) {
                    drawCircle(color = level.glowColor.copy(alpha = 0.4f), radius = size.width * 0.5f)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Level number
        Text(
            text = "${level.number}",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = Baloo2FontFamily,
            color = Color.White,
            modifier = Modifier.drawBehind {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = 18f,
                    center = Offset(size.width / 2 + 2f, size.height / 2 + 2f)
                )
            }
        )
    }
}
