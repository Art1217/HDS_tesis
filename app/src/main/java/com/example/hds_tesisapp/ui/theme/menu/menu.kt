package com.example.hds_tesisapp.ui.theme.menu

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.launch

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MenuScreen(navController: NavController) {
    val context = LocalContext.current

    // Force Landscape Orientation
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Music State
    var isMuted by remember { mutableStateOf(false) }
    var volume by remember { mutableFloatStateOf(1.0f) }
    var showSettings by remember { mutableStateOf(false) }

    // MediaPlayer setup
    val mediaPlayer = remember {
        try {
            MediaPlayer.create(context, R.raw.adventure_music)?.apply {
                isLooping = true
            }
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(Unit) {
        if (mediaPlayer != null && !isMuted) {
            try {
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onDispose {
            mediaPlayer?.release()
        }
    }

    LaunchedEffect(isMuted, volume) {
        mediaPlayer?.let { mp ->
            try {
                if (isMuted) {
                    if (mp.isPlaying) mp.pause()
                } else {
                    mp.setVolume(volume, volume)
                    if (!mp.isPlaying) mp.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Entrance animations ---
    val scope = rememberCoroutineScope()
    val titleAlpha = remember { Animatable(0f) }
    val buttonsOffset = remember { Animatable(-80f) }
    val robotScale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scope.launch { titleAlpha.animateTo(1f, tween(600)) }
        scope.launch { buttonsOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
        scope.launch { robotScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.fondo_menu_tesis),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Settings Button (Top Right) — HUD holographic style
        HudSettingsButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .zIndex(10f)
        )

        // Main Content — Robot partially behind/overlapping buttons
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Buttons with hierarchy
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        translationX = buttonsOffset.value
                        alpha = (80f + buttonsOffset.value) / 80f  // fade-in as slides in
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ====== CTA PRINCIPAL: JUGAR ======
                HudMenuButton(
                    text = "Jugar",
                    icon = Icons.Default.PlayArrow,
                    accentColor = Color(0xFF00E5FF),
                    buttonHeight = 80.dp,
                    iconSize = 38.dp,
                    fontSize = 22,
                    modifier = Modifier.fillMaxWidth(0.82f)
                ) {
                    navController.navigate(Routes.Story.route)
                }
                Spacer(modifier = Modifier.height(30.dp))
                // ====== BOTÓN SECUNDARIO: NIVELES ======
                HudMenuButton(
                    text = "Niveles",
                    icon = Icons.Default.List,
                    accentColor = Color(0xFF69FF47),
                    buttonHeight = 60.dp,
                    iconSize = 28.dp,
                    fontSize = 18,
                    modifier = Modifier.fillMaxWidth(0.68f)
                ) {
                    navController.navigate(Routes.Levels.route)
                }
                Spacer(modifier = Modifier.height(30.dp))
                // ====== BOTÓN SECUNDARIO: PERSONAJES ======
                HudMenuButton(
                    text = "Personajes",
                    icon = Icons.Default.Person,
                    accentColor = Color(0xFFE040FB),
                    buttonHeight = 60.dp,
                    iconSize = 28.dp,
                    fontSize = 18,
                    modifier = Modifier.fillMaxWidth(0.68f)
                ) {
                    navController.navigate(Routes.MaxCharacter.route)
                }
            }

            // Right Side: Character — overlaps slightly into buttons area
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .offset(x = (-20).dp)  // shift left so robot overlaps with button area
                    .graphicsLayer {
                        scaleX = robotScale.value
                        scaleY = robotScale.value
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.robots),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.90f)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Settings Dialog
        if (showSettings) {
            SettingsDialog(
                isMuted = isMuted,
                volume = volume,
                onMutedChange = { isMuted = it },
                onVolumeChange = { volume = it },
                onDismiss = { showSettings = false }
            )
        }
    }
}

// ============================================================
//  HudSettingsButton — Circular holographic HUD settings button
// ============================================================
@Composable
fun HudSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF00E5FF)
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
        label = "settingsScale"
    )

    // Pulsing glow animation
    val glowAlpha = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                glowAlpha.animateTo(1f, tween(1000))
                glowAlpha.animateTo(0.5f, tween(1000))
            }
        }
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .scale(scale)
            .drawBehind {
                val r = size.minDimension / 2f
                val cx = size.width / 2f
                val cy = size.height / 2f

                // Outer bloom glow (multiple rings)
                for (i in 4 downTo 1) {
                    drawCircle(
                        color = accentColor.copy(alpha = glowAlpha.value * 0.10f * (5 - i)),
                        radius = r + i * 9f,
                        center = Offset(cx, cy),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = i * 9f * 2)
                    )
                }

                // Dark navy fill
                drawCircle(
                    color = Color(0xFF000F1F).copy(alpha = if (isPressed) 0.90f else 0.68f),
                    radius = r,
                    center = Offset(cx, cy)
                )

                // Subtle top-edge highlight
                drawArc(
                    color = Color.White.copy(alpha = 0.15f),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )

                // Outer neon border — soft pass
                drawCircle(
                    color = accentColor.copy(alpha = glowAlpha.value * 0.5f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
                // Outer neon border — sharp pass
                drawCircle(
                    color = accentColor.copy(alpha = glowAlpha.value),
                    radius = r,
                    center = Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                // Small corner tick marks (top, right, bottom, left)
                val tickLen = 6f
                val tickOffset = r - 2f
                val angles = listOf(0f, 90f, 180f, 270f)
                for (angleDeg in angles) {
                    val rad = Math.toRadians(angleDeg.toDouble())
                    val sx = cx + (tickOffset * Math.cos(rad)).toFloat()
                    val sy = cy + (tickOffset * Math.sin(rad)).toFloat()
                    val ex = cx + ((tickOffset + tickLen) * Math.cos(rad)).toFloat()
                    val ey = cy + ((tickOffset + tickLen) * Math.sin(rad)).toFloat()
                    drawLine(accentColor, Offset(sx, sy), Offset(ex, ey), 2.5f)
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
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Configuración",
            tint = accentColor,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    shadowElevation = 0f
                    // Subtle glow via alpha pulse on the icon itself
                    alpha = 0.85f + glowAlpha.value * 0.15f
                }
        )
    }
}

// ============================================================
//  HudMenuButton — Holographic HUD / sci-fi style button
// ============================================================
@Composable
fun HudMenuButton(
    text: String,
    icon: ImageVector? = null,
    accentColor: Color = Color(0xFF00E5FF),   // neon cyan by default
    buttonHeight: Dp = 70.dp,
    iconSize: Dp = 32.dp,
    fontSize: Int = 20,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "hudButtonScale"
    )

    // Pulsing glow animation for the border
    val glowAlpha = remember { Animatable(0.4f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                glowAlpha.animateTo(1f, tween(900))
                glowAlpha.animateTo(0.4f, tween(900))
            }
        }
    }

    // Cut-corner size
    val cutPx = with(androidx.compose.ui.platform.LocalDensity.current) { 14.dp.toPx() }

    Box(
        modifier = modifier
            .height(buttonHeight)
            .scale(scale)
            .drawBehind {
                val w = size.width
                val h = size.height
                val c = cutPx

                // ── Outer glow bloom (4 passes, much stronger) ──
                for (i in 5 downTo 1) {
                    val blurPad = i * 8f
                    val glowPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(c + blurPad, 0f - blurPad)
                        lineTo(w - blurPad, 0f - blurPad)
                        lineTo(w + blurPad, c + blurPad)
                        lineTo(w + blurPad, h - c - blurPad)
                        lineTo(w - c - blurPad, h + blurPad)
                        lineTo(blurPad, h + blurPad)
                        lineTo(0f - blurPad, h - c - blurPad)
                        lineTo(0f - blurPad, c + blurPad)
                        close()
                    }
                    drawPath(
                        path = glowPath,
                        color = accentColor.copy(alpha = glowAlpha.value * 0.12f * (6 - i)),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = blurPad * 2)
                    )
                }

                // ── Glass fill: dark navy, higher opacity for visibility ──
                val fillPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h - c)
                    lineTo(w - c, h)
                    lineTo(0f, h)
                    lineTo(0f, c)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000F1F).copy(alpha = if (isPressed) 0.85f else 0.68f),
                            Color(0xFF001630).copy(alpha = if (isPressed) 0.75f else 0.55f)
                        )
                    )
                )

                // ── Top-edge inner light strip (depth effect) ──
                drawLine(
                    color = Color.White.copy(alpha = 0.18f),
                    start = Offset(c, 1f),
                    end = Offset(w, 1f),
                    strokeWidth = 2f
                )

                // ── Scanlines texture (more visible) ──
                val lineSpacing = 5f
                var y = lineSpacing
                while (y < h) {
                    drawLine(
                        color = accentColor.copy(alpha = 0.07f),
                        start = Offset(c, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += lineSpacing
                }

                // ── Neon border — double pass for thickness + glow ──
                val borderPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c, 0f)
                    lineTo(w, 0f)
                    lineTo(w, h - c)
                    lineTo(w - c, h)
                    lineTo(0f, h)
                    lineTo(0f, c)
                    close()
                }
                // Soft outer pass
                drawPath(
                    path = borderPath,
                    color = accentColor.copy(alpha = glowAlpha.value * 0.55f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
                // Sharp inner pass
                drawPath(
                    path = borderPath,
                    color = accentColor.copy(alpha = glowAlpha.value),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                )

                // ── Corner accent marks — bigger & brighter ──
                val markLen = 16f
                val markOff = 3f
                // top-left
                drawLine(accentColor, Offset(markOff, c + markOff), Offset(markOff, c + markLen + markOff), 3f)
                drawLine(accentColor, Offset(markOff, c + markOff), Offset(markLen + markOff, c + markOff), 3f)
                // top-right
                drawLine(accentColor, Offset(w - markOff, markOff), Offset(w - markLen - markOff, markOff), 3f)
                drawLine(accentColor, Offset(w - markOff, markOff), Offset(w - markOff, markLen + markOff), 3f)
                // bottom-left
                drawLine(accentColor, Offset(markOff, h - markOff), Offset(markLen + markOff, h - markOff), 3f)
                drawLine(accentColor, Offset(markOff, h - markOff), Offset(markOff, h - markLen - markOff), 3f)
                // bottom-right
                drawLine(accentColor, Offset(w - c - markOff, h - markOff), Offset(w - c - markLen - markOff, h - markOff), 3f)
                drawLine(accentColor, Offset(w - c - markOff, h - markOff), Offset(w - c - markOff, h - markLen - markOff), 3f)
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text.uppercase(),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OrbitronFontFamily,
                color = Color.White,
                letterSpacing = 2.sp,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OrbitronFontFamily,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = accentColor,
                        offset = Offset(0f, 0f),
                        blurRadius = 18f
                    )
                )
            )
        }
    }
}

// ============================================================
//  Settings Dialog — HUD sci-fi style
// ============================================================
@Composable
fun SettingsDialog(
    isMuted: Boolean,
    volume: Float,
    onMutedChange: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = Color(0xFF00E5FF)
    val glowAlpha = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        while (true) {
            glowAlpha.animateTo(1f, tween(1100))
            glowAlpha.animateTo(0.4f, tween(1100))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Outer bloom glow
                    for (i in 3 downTo 1) {
                        drawRoundRect(
                            color = accent.copy(alpha = glowAlpha.value * 0.08f * (4 - i)),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = i * 14f)
                        )
                    }
                }
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF000D1A), Color(0xFF001428), Color(0xFF000D1A))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.9f), accent.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ── Título ──
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = null,
                        tint = accent, modifier = Modifier.size(22.dp))
                    Text(
                        "CONFIGURACIÓN",
                        fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = OrbitronFontFamily, color = accent,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(1.dp)
                    .background(accent.copy(alpha = 0.25f)))
                Spacer(Modifier.height(18.dp))

                // ── Toggle música ──
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF001828))
                        .border(1.5.dp,
                            if (!isMuted) Color(0xFF69FF47).copy(alpha = 0.6f)
                            else Color(0xFFFF5252).copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val musicIcon = if (!isMuted) Icons.Default.Check else Icons.Default.Close
                    Icon(
                        imageVector = musicIcon,
                        contentDescription = null,
                        tint = if (!isMuted) Color(0xFF69FF47) else Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Música de Fondo",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = !isMuted,
                        onCheckedChange = { onMutedChange(!it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = Color(0xFF69FF47),
                            checkedTrackColor  = Color(0xFF69FF47).copy(alpha = 0.35f),
                            uncheckedThumbColor = Color(0xFFFF5252),
                            uncheckedTrackColor = Color(0xFFFF5252).copy(alpha = 0.25f)
                        )
                    )
                }

                Spacer(Modifier.height(18.dp))

                // ── Slider volumen ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "🔊  VOLUMEN",
                        color = accent.copy(alpha = 0.85f),
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                    Text(
                        "${(volume * 100).toInt()}%",
                        color = accent,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFontFamily
                    )
                }
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    enabled = !isMuted,
                    colors = SliderDefaults.colors(
                        thumbColor        = accent,
                        activeTrackColor  = accent,
                        inactiveTrackColor = accent.copy(alpha = 0.18f),
                        disabledThumbColor       = Color.White.copy(alpha = 0.25f),
                        disabledActiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )

                Spacer(Modifier.height(20.dp))

                // ── Botón cerrar ──
                HudMenuButton(
                    text = "Cerrar",
                    icon = Icons.Default.Close,
                    accentColor = Color(0xFFFF5252),
                    buttonHeight = 46.dp,
                    iconSize = 20.dp,
                    fontSize = 13,
                    modifier = Modifier.fillMaxWidth(0.65f)
                ) { onDismiss() }
            }
        }
    }
}
