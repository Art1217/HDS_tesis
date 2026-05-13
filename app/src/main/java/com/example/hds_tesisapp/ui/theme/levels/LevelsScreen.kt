package com.example.hds_tesisapp.ui.theme.levels

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.delay

private fun Context.findLevelsActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findLevelsActivity()
    else -> null
}

private data class ZoneData(
    val zoneNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val accentColor: Color,
    val bgGradient: List<Color>,
    val borderColor: Color,
    val levelRoutes: List<String>,
    val levelTitles: List<String>
)

private val ZONE1 = ZoneData(
    zoneNumber   = 1,
    title        = "El Bit Perdido",
    subtitle     = "Secuencias y algoritmos",
    icon         = "🤖",
    accentColor  = Color(0xFF00E5FF),
    bgGradient   = listOf(Color(0xFF000D1A), Color(0xFF001530), Color(0xFF002244)),
    borderColor  = Color(0xFF00E5FF),
    levelRoutes  = listOf(
        Routes.Level1.route,
        Routes.Level2.route,
        Routes.Level3.route,
        Routes.Level4.route,
        Routes.Level5.route,
    ),
    levelTitles  = listOf("El Regreso", "El Callejón", "Rutas Falsas", "Doble Obstáculo", "Mini-Glitch")
)

private val ZONE2 = ZoneData(
    zoneNumber   = 2,
    title        = "El Bosque de los Grupos",
    subtitle     = "Clasificación y organización",
    icon         = "🌳",
    accentColor  = Color(0xFF69FF47),
    bgGradient   = listOf(Color(0xFF001A00), Color(0xFF002800), Color(0xFF003A00)),
    borderColor  = Color(0xFF69FF47),
    levelRoutes  = listOf(
        Routes.Level1G2.route,
        Routes.Level2G2.route,
        Routes.Level3G2.route,
        Routes.Level4G2.route,
        Routes.Level5G2.route,
    ),
    levelTitles  = listOf("Frutas", "Hábitats", "Rotación", "Mezcla", "Mapache")
)

private val ZONE3 = ZoneData(
    zoneNumber   = 3,
    title        = "Las Cascadas en Secuencia",
    subtitle     = "Ordenamiento y Bubble Sort",
    icon         = "💧",
    accentColor  = Color(0xFF00B0FF),
    bgGradient   = listOf(Color(0xFF001828), Color(0xFF002840), Color(0xFF003050)),
    borderColor  = Color(0xFF00E5FF),
    levelRoutes  = listOf(
        Routes.Level1G3.route,
        Routes.Level2G3.route,
        Routes.Level3G3.route,
        Routes.Level4G3.route,
        Routes.Level5G3.route,
    ),
    levelTitles  = listOf("El Puente", "Las Piedras", "El Jardín", "La Cascada", "Mini Jefe")
)

private val ZONE4 = ZoneData(
    zoneNumber   = 4,
    title        = "El Valle de los Patrones",
    subtitle     = "Patrones y secuencias",
    icon         = "🔁",
    accentColor  = Color(0xFF8BC34A),
    bgGradient   = listOf(Color(0xFF0A1A00), Color(0xFF102800), Color(0xFF152E00)),
    borderColor  = Color(0xFF8BC34A),
    levelRoutes  = listOf(
        Routes.Level1G4.route,
        Routes.Level2G4.route,
        Routes.Level3G4.route,
        Routes.Level4G4.route,
        Routes.Level5G4.route,
    ),
    levelTitles  = listOf("Las Flores", "Luciérnagas", "Las Piedras", "El Puente", "Glitch")
)

private val ZONE5 = ZoneData(
    zoneNumber   = 5,
    title        = "La Ciudad de los Portales",
    subtitle     = "Condiciones y portales",
    icon         = "🌀",
    accentColor  = Color(0xFF9C27B0),
    bgGradient   = listOf(Color(0xFF0D0020), Color(0xFF1A0035), Color(0xFF260050)),
    borderColor  = Color(0xFFCE93D8),
    levelRoutes  = listOf(
        Routes.Level1G5.route,
        Routes.Level2G5.route,
        Routes.Level3G5.route,
        Routes.Level4G5.route,
        Routes.Level5G5.route,
    ),
    levelTitles  = listOf("El Portal Inicial", "Sí y No", "Portales Dobles", "Sala Múltiple", "Caóticos")
)

private const val MAX_PAGE = 3   // 0 = Z1+Z2  |  1 = Z3  |  2 = Z4  |  3 = Z5

@Composable
fun LevelsScreen(navController: NavController) {
    val context  = LocalContext.current
    val activity = remember { context.findLevelsActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF000510), Color(0xFF000A18), Color(0xFF001020)))
            )
    ) {
        StarField()

        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(10f)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SELECCIONA TU NIVEL",
                fontSize = 18.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                "Elige una zona para comenzar",
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.45f)
            )
        }

        // ← arrow (previous page)
        if (currentPage > 0) {
            PageArrowButton(
                label     = "‹",
                modifier  = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp),
                onClick   = { currentPage-- }
            )
        }

        // → arrow (next page)
        if (currentPage < MAX_PAGE) {
            PageArrowButton(
                label     = "›",
                modifier  = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                onClick   = { currentPage++ }
            )
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(MAX_PAGE + 1) { page ->
                val dotColor by animateColorAsState(
                    targetValue = if (page == currentPage) Color.White.copy(0.85f) else Color.White.copy(0.22f),
                    animationSpec = tween(300),
                    label = "dot$page"
                )
                Box(
                    modifier = Modifier
                        .size(if (page == currentPage) 9.dp else 6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }

        // Page content with slide animation
        val arrowPadding = 48.dp
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState > initialState)
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                else
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            },
            label = "pageContent",
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 52.dp,
                    bottom = 24.dp,
                    start = arrowPadding,
                    end = arrowPadding
                )
        ) { page ->
            when (page) {
                0 -> Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ZonePanel(
                        zone        = ZONE1,
                        startDelay  = 0,
                        onNavigate  = { route -> navController.navigate(route) },
                        modifier    = Modifier.weight(1f).fillMaxHeight()
                    )
                    ZonePanel(
                        zone        = ZONE2,
                        startDelay  = 150,
                        onNavigate  = { route -> navController.navigate(route) },
                        modifier    = Modifier.weight(1f).fillMaxHeight()
                    )
                }
                1 -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ZonePanel(
                        zone        = ZONE3,
                        startDelay  = 0,
                        onNavigate  = { route -> navController.navigate(route) },
                        modifier    = Modifier.fillMaxWidth(0.52f).fillMaxHeight()
                    )
                }
                2 -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ZonePanel(
                        zone        = ZONE4,
                        startDelay  = 0,
                        onNavigate  = { route -> navController.navigate(route) },
                        modifier    = Modifier.fillMaxWidth(0.52f).fillMaxHeight()
                    )
                }
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ZonePanel(
                        zone        = ZONE5,
                        startDelay  = 0,
                        onNavigate  = { route -> navController.navigate(route) },
                        modifier    = Modifier.fillMaxWidth(0.52f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

// ── Botón flecha de página ────────────────────────────────────────────────────

@Composable
private fun PageArrowButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val pulse = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        while (true) { pulse.animateTo(1f, tween(700)); pulse.animateTo(0.5f, tween(700)) }
    }
    Box(
        modifier = modifier
            .size(40.dp)
            .drawBehind {
                drawCircle(Color(0xFF00B0FF).copy(alpha = pulse.value * 0.25f), radius = size.minDimension / 2f + 8f)
            }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.5.dp, Color(0xFF00B0FF).copy(alpha = pulse.value * 0.7f + 0.2f), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { tryAwaitRelease(); onClick() })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.80f)
        )
    }
}

// ── Estrellas de fondo ────────────────────────────────────────────────────────

@Composable
private fun StarField() {
    val positions = remember {
        List(40) { Pair((Math.random()).toFloat(), (Math.random()).toFloat()) }
    }
    val pulse = rememberInfiniteTransition(label = "stars")
    val alpha by pulse.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "starAlpha"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        positions.forEachIndexed { i, (xFrac, yFrac) ->
            val size = if (i % 4 == 0) 3.dp else if (i % 3 == 0) 2.dp else 1.5.dp
            val thisAlpha = if (i % 2 == 0) alpha else 1f - alpha * 0.4f
            Box(
                modifier = Modifier
                    .offset(
                        x = (xFrac * 800).toInt().dp,
                        y = (yFrac * 500).toInt().dp
                    )
                    .size(size)
                    .background(Color.White.copy(alpha = thisAlpha * 0.6f), CircleShape)
            )
        }
    }
}

// ── Panel de zona ─────────────────────────────────────────────────────────────

@Composable
private fun ZonePanel(
    zone: ZoneData,
    startDelay: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.88f) }
    LaunchedEffect(Unit) {
        delay(startDelay.toLong())
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    val glowPulse = rememberInfiniteTransition(label = "glow${zone.zoneNumber}")
    val glow by glowPulse.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "glowVal"
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .drawBehind {
                // outer glow
                drawRoundRect(
                    color = zone.accentColor.copy(alpha = glow * 0.25f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
                    style = Stroke(width = 24f)
                )
            }
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(zone.bgGradient))
            .border(1.5.dp, zone.borderColor.copy(alpha = glow + 0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            ZoneHeader(zone = zone)

            Spacer(Modifier.height(10.dp))

            // Divisor
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                zone.accentColor.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(Modifier.height(10.dp))

            // Level path
            LevelPath(
                zone       = zone,
                startDelay = startDelay,
                onNavigate = onNavigate,
                modifier   = Modifier.weight(1f).fillMaxWidth()
            )
        }
    }
}

// ── Cabecera de zona ──────────────────────────────────────────────────────────

@Composable
private fun ZoneHeader(zone: ZoneData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Zone badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(zone.accentColor.copy(alpha = 0.15f))
                .border(1.dp, zone.accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(zone.icon, fontSize = 24.sp)
        }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(zone.accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        "ZONA ${zone.zoneNumber}",
                        fontSize = 9.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = zone.accentColor,
                        letterSpacing = 1.sp
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                zone.title,
                fontSize = 14.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color.White
            )
            Text(
                zone.subtitle,
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

// ── Camino de niveles ─────────────────────────────────────────────────────────

@Composable
private fun LevelPath(
    zone: ZoneData,
    startDelay: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 5 nodes: 3 on top row, 2 on bottom row
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top row: niveles 1, 2, 3
            LevelRow(
                indices    = listOf(0, 1, 2),
                zone       = zone,
                startDelay = startDelay,
                onNavigate = onNavigate
            )
            // Bottom row: niveles 4, 5 (centrados)
            LevelRow(
                indices    = listOf(3, 4),
                zone       = zone,
                startDelay = startDelay,
                onNavigate = onNavigate,
                centered   = true
            )
        }
    }
}

@Composable
private fun LevelRow(
    indices: List<Int>,
    zone: ZoneData,
    startDelay: Int,
    onNavigate: (String) -> Unit,
    centered: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (centered)
            Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
        else
            Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        indices.forEachIndexed { rowPos, levelIndex ->
            LevelNode(
                levelNumber = levelIndex + 1,
                title       = zone.levelTitles[levelIndex],
                accentColor = zone.accentColor,
                bgGradient  = zone.bgGradient,
                nodeBorder  = zone.borderColor,
                animDelay   = startDelay + levelIndex * 100,
                onClick     = { onNavigate(zone.levelRoutes[levelIndex]) }
            )
            // Flecha conectora entre nodos (excepto el último de la fila)
            if (rowPos < indices.size - 1) {
                Text(
                    "›", fontSize = 18.sp,
                    color = zone.accentColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Nodo de nivel ─────────────────────────────────────────────────────────────

@Composable
private fun LevelNode(
    levelNumber: Int,
    title: String,
    accentColor: Color,
    bgGradient: List<Color>,
    nodeBorder: Color,
    animDelay: Int,
    nodeSize: Dp = 56.dp,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(animDelay.toLong())
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    var pressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.87f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "press"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    ) {
        Box(
            modifier = Modifier
                .size(nodeSize)
                .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
                .drawBehind {
                    // glow ring
                    drawCircle(
                        color = accentColor.copy(alpha = if (pressed) 0.5f else 0.2f),
                        radius = size.minDimension / 2f + 6f
                    )
                }
                .shadow(if (pressed) 2.dp else 8.dp, CircleShape,
                    spotColor = accentColor.copy(alpha = 0.4f),
                    ambientColor = accentColor.copy(alpha = 0.2f))
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.35f),
                            bgGradient.last().copy(alpha = 0.8f)
                        )
                    )
                )
                .border(2.dp, nodeBorder.copy(alpha = if (pressed) 1f else 0.7f), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            tryAwaitRelease()
                            pressed = false
                            onClick()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner shine
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                            startY = 0f, endY = nodeSize.value * 0.5f
                        )
                    )
            )
            Text(
                text = "$levelNumber",
                fontSize = 20.sp, fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.ExtraBold, color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(4.dp))

        // Level title below node
        Text(
            text = title,
            fontSize = 8.sp, fontFamily = Baloo2FontFamily,
            color = accentColor.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
