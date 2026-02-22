package com.example.hds_tesisapp.ui.theme.personajes

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// import com.example.hds_tesisapp.Nav.Routes // Assuming Routes is available

@Composable
fun LinaScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background - Park Theme
        Image(
            painter = painterResource(id = R.drawable.lina_park_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
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
            
            Spacer(modifier = Modifier.width(100.dp)) // Same spacing as Max

            // Left Side: Description Card
            Card(
                modifier = Modifier
                    .weight(0.8f)
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
                        text = "LINA",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE91E63), // Pink/Magenta for Lina
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "La mente estratégica, capaz de descifrar patrones y dar pistas.",
                        fontSize = 18.sp,
                        color = Color(0xFF37474F), 
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Additional "Stats"
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CharacterStat("Inteligencia", "10/10")
                        CharacterStat("Pistas", "SI")
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Right Side: Character Image
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lina_character),
                    contentDescription = "Lina",
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .graphicsLayer(
                            scaleX = 2.0f, 
                            scaleY = 2.0f, 
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        )
                        .offset(y = 80.dp), // Push down to counter internal padding/floating
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomCenter // Crucial: Align content to bottom of the Image view
                )
            }
        }

        // Next Character Arrow (To Tom & Atom)
        IconButton(
            onClick = { navController.navigate(com.example.hds_tesisapp.Nav.Routes.TomAtomCharacter.route) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
                .size(64.dp)
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFFFFCA28), Color(0xFFFF6F00))), 
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Siguiente",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Previous Character Arrow (To Max)
        IconButton(
            onClick = { navController.navigate(com.example.hds_tesisapp.Nav.Routes.MaxCharacter.route) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .size(64.dp)
                .background(Color.White.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Anterior",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


