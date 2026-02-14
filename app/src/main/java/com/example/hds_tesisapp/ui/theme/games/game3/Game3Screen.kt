package com.example.hds_tesisapp.ui.theme.games.game3

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Helper function for Activity (Shared code usually, but duplicated here for standalone file)
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

// =========================================================================================
// CONFIGURACIÓN DE RECURSOS (Game 3)
// =========================================================================================
object Game3Config {
    val BackgroundColor = Color(0xFFE1F5FE) // Light Blue (Sky/Lab?)
    val TomColor = Color(0xFF5C6BC0) // Indigo (Placeholder for Tom Atom)
    
    // Items
    val SmallItemColor = Color(0xFF8BC34A) // Light Green
    val MediumItemColor = Color(0xFFFFCA28) // Amber
    val LargeItemColor = Color(0xFFEF5350) // Red
    
    // Slots
    val SlotColor = Color.White.copy(alpha = 0.5f)
    val SlotBorderColor = Color.Gray
}

data class SizeItem(
    val id: Int,
    val name: String,
    val sizeType: SizeType,
    val color: Color,
    val initialOffset: Offset,
    val sizeDp: androidx.compose.ui.unit.Dp
)

enum class SizeType {
    SMALL, MEDIUM, LARGE
}

@Composable
fun Game3Screen() {
    val context = LocalContext.current
    
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Game3Config.BackgroundColor)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val density = LocalDensity.current
        
        // Calculate dynamic positions for "Aligned at Top"
        val initialItems = remember(screenWidth, density) {
            with(density) {
                // Place items in the upper section (approx 1/4 down)
                val y = 90.dp.toPx() 
                val spacing = screenWidth.toPx() / 4
                listOf(
                    SizeItem(1, "Pequeño", SizeType.SMALL, Game3Config.SmallItemColor, Offset(spacing * 1 - 30.dp.toPx(), y), 60.dp),
                    SizeItem(2, "Mediano", SizeType.MEDIUM, Game3Config.MediumItemColor, Offset(spacing * 2 - 45.dp.toPx(), y), 90.dp),
                    SizeItem(3, "Grande", SizeType.LARGE, Game3Config.LargeItemColor, Offset(spacing * 3 - 60.dp.toPx(), y), 120.dp)
                )
            }
        }
        
        // Update currentItems when initialItems changes (e.g. screen resize)
        var currentItems by remember(initialItems) { mutableStateOf(initialItems) }
        var placedItems by remember { mutableStateOf(mapOf<SizeType, SizeItem>()) }
        
        // Slot Areas
        var smallSlotBounds by remember { mutableStateOf<Rect?>(null) }
        var mediumSlotBounds by remember { mutableStateOf<Rect?>(null) }
        var largeSlotBounds by remember { mutableStateOf<Rect?>(null) }

        // 1. TITLE & INSTRUCTION
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ORDENA POR TAMAÑO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF01579B)
            )
            Text(
                text = "(De Menor a Mayor)",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0277BD)
            )
        }
        
        // 2. TOM ATOM (Character Placeholder)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
                .size(160.dp)
                .background(Game3Config.TomColor, CircleShape)
                .border(4.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("Tom Atom", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // 3. DROP SLOTS (Bottom Center)
        val commonSlotSize = 130.dp
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align to bottom
                .padding(bottom = 50.dp, start = 120.dp) // Lift up slightly, offset for char
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom 
        ) {
            // Small Slot (Position 1)
            OrderSlot(
                label = "",
                size = commonSlotSize, 
                onGloballyPositioned = { smallSlotBounds = it },
                placedItem = placedItems[SizeType.SMALL]
            )
            
            // Medium Slot (Position 2)
            OrderSlot(
                label = "",
                size = commonSlotSize,
                onGloballyPositioned = { mediumSlotBounds = it },
                placedItem = placedItems[SizeType.MEDIUM]
            )
            
            // Large Slot (Position 3)
            OrderSlot(
                label = "",
                size = commonSlotSize,
                onGloballyPositioned = { largeSlotBounds = it },
                placedItem = placedItems[SizeType.LARGE]
            )
        }
        
        // 4. DRAGGABLE ITEMS (Unplaced)
        currentItems.forEach { item ->  
             if (!placedItems.containsValue(item)) {
                 DraggableSizeItem(
                    item = item,
                    initialOffset = item.initialOffset,
                    onDrop = { position ->
                        val isSmallDrop = smallSlotBounds?.contains(position) == true
                        val isMediumDrop = mediumSlotBounds?.contains(position) == true
                        val isLargeDrop = largeSlotBounds?.contains(position) == true
                        
                        var placed = false
                        if (isSmallDrop && item.sizeType == SizeType.SMALL) {
                            placed = true
                        } else if (isMediumDrop && item.sizeType == SizeType.MEDIUM) {
                             placed = true
                        } else if (isLargeDrop && item.sizeType == SizeType.LARGE) {
                             placed = true
                        }
                        
                        if (placed) {
                            placedItems = placedItems + (item.sizeType to item)
                            Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Intenta de nuevo", Toast.LENGTH_SHORT).show()
                        }
                    }
                 )
             }
        }
        
        // 5. WIN MESSAGE
        if (placedItems.size == 3) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡EXCELENTE!",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Green
                    )
                     Text(
                        text = "Has ordenado todo",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSlot(
    label: String,
    size: androidx.compose.ui.unit.Dp,
    onGloballyPositioned: (Rect) -> Unit,
    placedItem: SizeItem? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Game3Config.SlotColor, RoundedCornerShape(12.dp))
                .border(2.dp, Game3Config.SlotBorderColor, RoundedCornerShape(12.dp))
                .onGloballyPositioned { coords ->
                     onGloballyPositioned(coords.boundsInWindow())
                },
            contentAlignment = Alignment.Center
        ) {
            if (placedItem != null) {
                // Render placed item non-draggable
                Box(
                    modifier = Modifier
                        .size(placedItem.sizeDp) // Render actual item size
                        .background(placedItem.color, RoundedCornerShape(8.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(placedItem.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Text(label, color = Color.Gray)
            }
        }
        // Simplified visual: Just a box
    }
}

@Composable
fun DraggableSizeItem(
    item: SizeItem,
    initialOffset: Offset,
    onDrop: (Offset) -> Unit
) {
    var offsetX by remember { mutableStateOf(initialOffset.x) }
    var offsetY by remember { mutableStateOf(initialOffset.y) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(item.sizeDp)
            .background(item.color, RoundedCornerShape(8.dp))
            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
            .onGloballyPositioned { coordinates ->
                 globalPosition = coordinates.positionInWindow()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val dropCenter = globalPosition + Offset(size.width / 2f, size.height / 2f)
                        onDrop(dropCenter)
                        // Snap back? We generally leave it or it gets consumed if placed
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
