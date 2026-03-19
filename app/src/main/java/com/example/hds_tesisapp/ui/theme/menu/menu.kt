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
            painter = painterResource(id = R.drawable.background_landscape),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Settings Button (Top Right) — glass-style
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .zIndex(10f)
                .size(52.dp)
                .shadow(6.dp, CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color(0xFFE8E8E8).copy(alpha = 0.85f)
                        )
                    ),
                    CircleShape
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = Color(0xFF5D4037),
                modifier = Modifier.fillMaxSize()
            )
        }

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
                // ====== CTA PRINCIPAL: JUGAR (más grande, más vívido) ======
                ClayMenuButton(
                    text = "Jugar",
                    icon = Icons.Default.PlayArrow,
                    gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
                    glowColor = Color(0xFF81C784),
                    borderColor = Color(0xFFA5D6A7),
                    shadowColor = Color(0xFF1B5E20),
                    buttonHeight = 80.dp,
                    iconSize = 38.dp,
                    fontSize = 30,
                    modifier = Modifier.fillMaxWidth(0.82f)
                ) {
                    navController.navigate(Routes.Game.route)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ====== BOTÓN SECUNDARIO: NIVELES ======
                ClayMenuButton(
                    text = "Niveles",
                    icon = Icons.Default.List,
                    gradientColors = listOf(Color(0xFFFFA726), Color(0xFFEF6C00)),
                    glowColor = Color(0xFFFFCC80),
                    borderColor = Color(0xFFFFE0B2),
                    shadowColor = Color(0xFFE65100),
                    buttonHeight = 60.dp,
                    iconSize = 28.dp,
                    fontSize = 22,
                    modifier = Modifier.fillMaxWidth(0.68f)
                ) {
                    navController.navigate(Routes.Levels.route)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ====== BOTÓN SECUNDARIO: PERSONAJES ======
                ClayMenuButton(
                    text = "Personajes",
                    icon = Icons.Default.Person,
                    gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)),
                    glowColor = Color(0xFF90CAF9),
                    borderColor = Color(0xFFBBDEFB),
                    shadowColor = Color(0xFF0D47A1),
                    buttonHeight = 60.dp,
                    iconSize = 28.dp,
                    fontSize = 22,
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
//  ClayMenuButton — Claymorphism / Skeuomorphic button
// ============================================================
@Composable
fun ClayMenuButton(
    text: String,
    icon: ImageVector? = null,
    gradientColors: List<Color>,
    glowColor: Color,
    borderColor: Color,
    shadowColor: Color,
    buttonHeight: Dp = 70.dp,
    iconSize: Dp = 32.dp,
    fontSize: Int = 26,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Press animation state
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .height(buttonHeight)
            .scale(scale)
            // Outer deep shadow
            .shadow(
                elevation = if (isPressed) 4.dp else 10.dp,
                shape = shape,
                ambientColor = shadowColor.copy(alpha = 0.4f),
                spotColor = shadowColor.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(Brush.verticalGradient(gradientColors))
            // Colored border matching palette (not white)
            .border(2.dp, borderColor.copy(alpha = 0.7f), shape)
            // Inner glow overlay (top-light effect)
            .drawBehind {
                // Top inner glow — simulates light coming from above
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.45f
                    )
                )
                // Bottom inner shadow — adds depth
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f)
                        ),
                        startY = size.height * 0.6f,
                        endY = size.height
                    )
                )
                // Press glow effect
                if (isPressed) {
                    drawCircle(
                        color = glowColor.copy(alpha = 0.3f),
                        radius = size.width * 0.5f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text.uppercase(),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Baloo2FontFamily,
                color = Color.White,
                letterSpacing = 1.5.sp,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Baloo2FontFamily,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.45f),
                        offset = Offset(2f, 3f),
                        blurRadius = 5f
                    )
                )
            )
        }
    }
}

// ============================================================
//  Settings Dialog — visual upgrade
// ============================================================
@Composable
fun SettingsDialog(
    isMuted: Boolean,
    volume: Float,
    onMutedChange: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .border(3.dp, Color(0xFF8D6E63), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CONFIGURACIÓN",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Baloo2FontFamily,
                    color = Color(0xFF5D4037),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(28.dp))

                // Mute Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val icon = if (isMuted) Icons.Default.Close else Icons.Default.Check
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isMuted) Color(0xFFE53935) else Color(0xFF43A047),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Música de Fondo",
                        color = Color(0xFF5D4037),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Baloo2FontFamily,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = !isMuted,
                        onCheckedChange = { onMutedChange(!it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF43A047),
                            checkedTrackColor = Color(0xFFA5D6A7),
                            uncheckedThumbColor = Color(0xFFE53935),
                            uncheckedTrackColor = Color(0xFFEF9A9A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Volume Slider
                Text(
                    "Volumen Maestro",
                    color = Color(0xFF5D4037),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Baloo2FontFamily,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 6.dp)
                )
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    enabled = !isMuted,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF8F00),
                        activeTrackColor = Color(0xFFFFB300),
                        inactiveTrackColor = Color(0xFFFFECB3)
                    ),
                    modifier = Modifier.height(20.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                ClayMenuButton(
                    text = "Cerrar",
                    gradientColors = listOf(Color(0xFFEF5350), Color(0xFFB71C1C)),
                    glowColor = Color(0xFFEF9A9A),
                    borderColor = Color(0xFFFFCDD2),
                    shadowColor = Color(0xFF7F0000),
                    icon = Icons.Default.Close,
                    buttonHeight = 50.dp,
                    iconSize = 24.dp,
                    fontSize = 20,
                    modifier = Modifier.width(200.dp)
                ) {
                    onDismiss()
                }
            }
        }
    }
}
