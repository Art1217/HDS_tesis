package com.example.hds_tesisapp.ui.theme.games.game2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.Rect
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.DisposableEffect

// ... existing imports ...

// Helper function
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}



// CONFIGURACIÓN DE RECURSOS (CAMBIA LOS COLORES POR TUS IMÁGENES AQUÍ)
// =========================================================================================
// Cuando tengas las imágenes, usa: override val imageRes: Int? = R.drawable.tu_imagen
// y actualiza los Composables para usar Image(painterResource(id = ...)) si imageRes != null
object Game2Config {
    val BackgroundColor = Color(0xFFFFF3E0) // Color Crema/Cocina
    
    // Lina
    val LinaColor = Color(0xFF795548) // Marrón
    
    // Bowl (Container)
    val BowlColor = Color(0xFF2196F3) // Azul
    val BowlBorderColor = Color(0xFF0D47A1) // Azul Oscuro
    
    // Bins
    val FruitBinColor = Color(0xFFEF5350) // Rojo Claro
    val VegBinColor = Color(0xFF66BB6A) // Verde Claro
}

// Data class for draggable items
data class GameItem(
    val id: Int,
    val name: String,
    val type: ItemType,
    val color: Color,
    val initialOffset: Offset = Offset.Zero // Para dispersarlos en el bowl
)

enum class ItemType {
    FRUIT, VEGETABLE
}

@Composable
fun Game2Screen(onLevelComplete: () -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Forzar orientación horizontal SOLO AQUÍ
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }
    
    // Game State
    // Mark items with unique IDs to track them
    val initialItems = remember {
        listOf(
            GameItem(1, "Manzana", ItemType.FRUIT, Color.Red, Offset(20f, 20f)),
            GameItem(2, "Zanahoria", ItemType.VEGETABLE, Color(0xFFFF9800), Offset(-30f, 10f)),
            GameItem(3, "Plátano", ItemType.FRUIT, Color.Yellow, Offset(50f, -20f)),
            GameItem(4, "Lechuga", ItemType.VEGETABLE, Color.Green, Offset(-10f, 40f)),
            GameItem(5, "Uva", ItemType.FRUIT, Color.Magenta, Offset(0f, 0f))
        )
    }
    
    // Items placed in bins
    var fruitBinItems by remember { mutableStateOf(listOf<GameItem>()) }
    var vegBinItems by remember { mutableStateOf(listOf<GameItem>()) }
    
    // Derived: Items still on the table are those NOT in any bin
    // This allows robust resetting: just clear bins, and items "reappear".
    val currentItems = remember(initialItems, fruitBinItems, vegBinItems) {
        val placedIds = fruitBinItems.map { it.id } + vegBinItems.map { it.id }
        initialItems.filter { it.id !in placedIds }
    }

    var score by remember { mutableStateOf(0) }
    var isGameWon by remember { mutableStateOf(false) }
    
    // Drop Zones
    var fruitZoneBounds by remember { mutableStateOf<Rect?>(null) }
    var vegetableZoneBounds by remember { mutableStateOf<Rect?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Game2Config.BackgroundColor)
    ) {
        
        // 1. HEADER (Score & Title)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CLASIFICACIÓN",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE65100),
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }

        // 2. MAIN SCENE LAYOUT
        
        // --- LINA (Left Character) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
                .size(150.dp) 
                .background(Game2Config.LinaColor, CircleShape)
                .border(4.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("Lina", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // --- BOWL (Center) ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
                .size(350.dp, 180.dp)
                .background(Game2Config.BowlColor, RoundedCornerShape(50))
                .border(6.dp, Game2Config.BowlBorderColor, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text("BOWL", color = Color.White.copy(alpha=0.5f), fontSize=24.sp)
        }

        // --- BINS (Bottom Center/Right) ---
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 150.dp)
                .fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Fruit Bin
            ClassificationBin(
                name = "FRUTAS",
                color = Game2Config.FruitBinColor,
                borderColor = Color.Red,
                onGloballyPositioned = { fruitZoneBounds = it },
                items = fruitBinItems // Pass items to render
            )

            // Vegetable Bin
            ClassificationBin(
                name = "VERDURAS",
                color = Game2Config.VegBinColor,
                borderColor = Color.Green,
                onGloballyPositioned = { vegetableZoneBounds = it },
                items = vegBinItems
            )
        }

        // 3. DRAGGABLE ITEMS LAYER
        // Removed outer fillMaxSize Box to prevent overlaying the Check Button
        Box(
             modifier = Modifier
                 .align(Alignment.Center)
                 .offset(y = (-80).dp)
                 // .zIndex(1f) // Optional: explicit layer
        ) {
             currentItems.forEach { item ->
                // Use key composable to track state by ID
                key(item.id) {
                    DraggableGameItem(
                        key = item.id,
                        item = item,
                        initialOffset = item.initialOffset,
                        onDrop = { position ->
                            val isFruitDrop = fruitZoneBounds?.contains(position) == true
                            val isVegDrop = vegetableZoneBounds?.contains(position) == true
                            
                            if (isFruitDrop) {
                                fruitBinItems = fruitBinItems + item
                            } else if (isVegDrop) {
                                vegBinItems = vegBinItems + item
                            } else {
                                Toast.makeText(context, "Colócalo en una caja", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
        
        // CHECK BUTTON
        androidx.compose.material3.Button(
            onClick = {
                // Validation Logic
                val allFruitsCorrect = fruitBinItems.all { it.type == ItemType.FRUIT }
                val allVegsCorrect = vegBinItems.all { it.type == ItemType.VEGETABLE }
                
                // Also check counts to ensure we didn't just place 0 items?
                val placedCount = fruitBinItems.size + vegBinItems.size
                
                if (placedCount == initialItems.size && allFruitsCorrect && allVegsCorrect) {
                     score = 100
                     isGameWon = true
                } else {
                    Toast.makeText(context, "Incorrecto. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    
                    // Reset Logic: Clear bins! Items will automatically reappear in currentItems via derived state.
                    fruitBinItems = emptyList()
                    vegBinItems = emptyList()
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding() // Ensure not under notch/status bar
                .padding(16.dp)
                .zIndex(10f) // Ensure button is always on top
        ) {
            Text("COMPROBAR")
        }
        
        // WIN MESSAGE
        if (isGameWon) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onLevelComplete() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡GANASTE!",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Yellow
                    )
                    Text(
                        text = "Toca para continuar",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ClassificationBin(
    name: String,
    color: Color,
    borderColor: Color,
    onGloballyPositioned: (Rect) -> Unit,
    items: List<GameItem>
) {
    Box(
        modifier = Modifier
            .size(140.dp, 100.dp)
            .background(color, RoundedCornerShape(10.dp))
            .border(4.dp, borderColor, RoundedCornerShape(10.dp))
            .onGloballyPositioned { layoutCoordinates ->
                onGloballyPositioned(layoutCoordinates.boundsInWindow())
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$name (${items.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            // Render Tiny Icons of items inside
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.take(5).forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .background(item.color, CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun DraggableGameItem(
    key: Int,
    item: GameItem,
    initialOffset: Offset,
    onDrop: (Offset) -> Unit
) {
    // State for drag offset with key to reset
    var offsetX by remember(key) { mutableStateOf(initialOffset.x) }
    var offsetY by remember(key) { mutableStateOf(initialOffset.y) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Reset position logic: If the item receives a new ID or is reset, we might want to snap back.
    // However, in this simple version, if drag fails, we just leave it where dropped or snap back.
    // Adding snap-back logic:
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(70.dp) // Tamaño del item
            .background(item.color, RoundedCornerShape(8.dp))
            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
            .onGloballyPositioned { coordinates ->
                 globalPosition = coordinates.positionInWindow()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val dropCenter = globalPosition + Offset(size.width / 2f, size.height / 2f)
                        onDrop(dropCenter)
                        
                        // Optional: Snap back if not consumed (checked via parent list update)
                        // For now we leave it there or let the parent recomposition remove it.
                        // If we wanted snap back visual, we'd need a Callback "onInvalidDrop".
                        // Assuming simple "Reset to initial" on failure for UX:
                        // offsetX = initialOffset.x
                        // offsetY = initialOffset.y
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Placeholder Icon/Text
        Text(
            text = item.name.take(3).uppercase(), // "MAN", "ZAN"...
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
