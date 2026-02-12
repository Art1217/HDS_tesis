package com.example.hds_tesisapp.ui.theme.personajes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hds_tesisapp.R
// import com.example.hds_tesisapp.Nav.Routes

@Composable
fun TomAtomScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background - Workshop Theme
        // Background - Workshop Theme
        Image(
            painter = painterResource(id = R.drawable.fondo_atom_),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Top-Left Button (Back to Menu, acts specifically as "Exit")
        IconButton(
            onClick = { 
                // Navigate back to Menu
                navController.popBackStack(com.example.hds_tesisapp.Nav.Routes.Menu.route, inclusive = false)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Home, // Changed to Home icon
                contentDescription = "Volver al Menú",
                tint = Color.Black
            )
        }

        // Main Content Row
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Spacer(modifier = Modifier.width(100.dp)) 

            // Left Side: Description Card
            Card(
                modifier = Modifier
                    .weight(0.9f) // Slightly wider for more text
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOM & ATOM",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFB8C00), // Orange/Engineering color
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tom: constructor que puede arreglar máquinas, crear puentes, ordenar piezas.",
                        fontSize = 16.sp,
                        color = Color(0xFF37474F), 
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Atom: robot-guía que explica conceptos y ayuda cuando el niño se traba.",
                        fontSize = 16.sp,
                        color = Color(0xFF37474F), 
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Additional "Stats"
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CharacterStat("Construcción", "10/10")
                        CharacterStat("Guía", "SI")
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Right Side: Character Image
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tom_atom_character),
                    contentDescription = "Tom & Atom",
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .graphicsLayer(
                            scaleX = 1.35f, 
                            scaleY = 1.35f, 
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        )
                        .offset(y = 60.dp), // Push down to ground them
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomCenter
                )
            }
        }

        // Left Arrow (Previous Character: Lina)
        IconButton(
            onClick = { navController.navigate(com.example.hds_tesisapp.Nav.Routes.LinaCharacter.route) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .size(64.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Color(0xFFFFCA28), Color(0xFFFF6F00))), 
                    CircleShape
                ) 
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack, 
                contentDescription = "Anterior",
                tint = Color.White, // Changed to White
                modifier = Modifier.size(32.dp)
            )
        }

        /* 
        // Right Arrow (Next Character: ??? - Loop to Max or disable)
        IconButton(
            onClick = { navController.navigate(Routes.MaxCharacter.route) },
            modifier = Modifier.align(Alignment.CenterEnd)...
        ) 
        */
    }
}


