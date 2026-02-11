package com.example.hds_tesisapp.ui.theme.personajes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.example.hds_tesisapp.Nav.Routes

@Composable
fun MaxScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.max_city_rooftop_bg),
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
                .padding(top = 20.dp, bottom = 20.dp, end = 20.dp), // Removed horizontal padding here to control via Spacer
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Spacer(modifier = Modifier.width(100.dp)) // Move card further to the right

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
                        text = "MAX",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1565C0), 
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Usa su fuerza y vuelo para mover bloques, empujar objetos, proteger, cargar objetos.",
                        fontSize = 18.sp,
                        color = Color(0xFF37474F), 
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Additional "Stats" or details
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CharacterStat("Fuerza", "10/10")
                        CharacterStat("Vuelo", "SI")
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Right Side: Character Image
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight().padding(top = 50.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.max_character),
                    contentDescription = "Max",
                    modifier = Modifier
                        .fillMaxHeight(1f) // Use full height
                        .graphicsLayer(
                            scaleX = 1.8f, 
                            scaleY = 1.8f, 
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Next Character Arrow
        IconButton(
            onClick = { navController.navigate(Routes.LinaCharacter.route) },
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
    }
}

@Composable
fun CharacterStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
    }
}
