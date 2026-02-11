package com.example.hds_tesisapp.ui.theme.menu

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hds_tesisapp.Nav.Routes
import com.example.hds_tesisapp.R

@Composable
fun MenuScreen(navController: NavController) {
    val context = LocalContext.current
    
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

        // Settings Button (Top Right)
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(64.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFE0E0E0))
                    ), 
                    RoundedCornerShape(16.dp)
                )
                .border(2.dp, Color(0xFFBDBDBD), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = Color(0xFF5D4037),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Main Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Buttons
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameMenuButton(
                    text = "Jugar", 
                    gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)), // Bright Green -> Dark Green
                    icon = Icons.Default.PlayArrow
                ) {
                    navController.navigate(Routes.Game.route)
                }
                Spacer(modifier = Modifier.height(20.dp))
                GameMenuButton(
                    text = "Niveles", 
                    gradientColors = listOf(Color(0xFFFFCA28), Color(0xFFFF6F00)), // Amber -> Orange
                    icon = Icons.Default.List
                ) {
                    // Navigate to Levels
                }
                Spacer(modifier = Modifier.height(20.dp))
                GameMenuButton(
                    text = "Personajes", 
                    gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)), // Blue -> Dark Blue
                    icon = Icons.Default.Person
                ) {
                    navController.navigate(Routes.MaxCharacter.route)
                }
            }

            // Right Side: Character Image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                 Image(
                    painter = painterResource(id = R.drawable.robots),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .padding(bottom = 15.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Settings Dialog
        if (showSettings) {
            Dialog(onDismissRequest = { showSettings = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Wider
                        .padding(16.dp)
                        .border(4.dp, Color(0xFF8D6E63), RoundedCornerShape(24.dp)), // Wood-like border
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CONFIGURACIÓN",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF5D4037),
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // Mute Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFFFF), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFD7CCC8), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            val icon = if (isMuted) Icons.Default.Close else Icons.Default.Check 
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isMuted) Color.Red else Color.Green,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Música de Fondo", 
                                color = Color(0xFF5D4037), 
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = !isMuted,
                                onCheckedChange = { isMuted = !it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF43A047),
                                    checkedTrackColor = Color(0xFFA5D6A7),
                                    uncheckedThumbColor = Color(0xFFE53935),
                                    uncheckedTrackColor = Color(0xFFEF9A9A)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Volume Slider
                        Text(
                            "Volumen Maestro", 
                            color = Color(0xFF5D4037), 
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 8.dp)
                        )
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..1f,
                            enabled = !isMuted,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFF8F00),
                                activeTrackColor = Color(0xFFFFB300),
                                inactiveTrackColor = Color(0xFFFFECB3)
                            ),
                            modifier = Modifier.height(20.dp)
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                        
                        GameMenuButton(
                            text = "Cerrar",
                            gradientColors = listOf(Color(0xFFE53935), Color(0xFFB71C1C)), // Red gradient
                            icon = Icons.Default.Close,
                            modifier = Modifier.width(200.dp).height(55.dp) // Smaller than main buttons
                        ) {
                            showSettings = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameMenuButton(
    text: String, 
    gradientColors: List<Color>, 
    icon: ImageVector? = null,
    modifier: Modifier = Modifier.fillMaxWidth(0.75f).height(75.dp),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp), // Remove default padding to use Box
        border = BorderStroke(3.dp, Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors)),
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
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = text.uppercase(), 
                    fontSize = 26.sp, 
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}
