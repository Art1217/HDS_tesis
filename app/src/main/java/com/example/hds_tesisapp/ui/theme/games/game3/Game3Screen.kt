package com.example.hds_tesisapp.ui.theme.games.game3

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.zIndex
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
fun Game3Screen(onLevelComplete: () -> Unit = {}) {
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
                // Place items in the upper section (Move down to 130dp to avoid Title overlap)
                val y = 130.dp.toPx() 
                val spacing = screenWidth.toPx() / 4
                listOf(
                    SizeItem(1, "Pequeño", SizeType.SMALL, Game3Config.SmallItemColor, Offset(spacing * 1 - 30.dp.toPx(), y), 60.dp),
                    SizeItem(2, "Mediano", SizeType.MEDIUM, Game3Config.MediumItemColor, Offset(spacing * 2 - 45.dp.toPx(), y), 90.dp),
                    SizeItem(3, "Grande", SizeType.LARGE, Game3Config.LargeItemColor, Offset(spacing * 3 - 60.dp.toPx(), y), 120.dp)
                )
            }
        }
        
        // Map of Slot Index (0,1,2) -> Placed Item
        var placedItems by remember { mutableStateOf(mapOf<Int, SizeItem>()) }
        var isGameWon by remember { mutableStateOf(false) }
        
        // Derived State: Items that are NOT placed
        val unplacedItems = remember(initialItems, placedItems) {
            initialItems.filter { item -> 
                placedItems.values.none { it.id == item.id }
            }
        }

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
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 120.dp)
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom 
        ) {
            // Slot 0 (Small)
            OrderSlot(
                label = "",
                size = commonSlotSize, 
                onGloballyPositioned = { smallSlotBounds = it },
                placedItem = placedItems[0],
                onClick = { placedItems = placedItems - 0 }
            )
            
            // Slot 1 (Medium)
            OrderSlot(
                label = "",
                size = commonSlotSize,
                onGloballyPositioned = { mediumSlotBounds = it },
                placedItem = placedItems[1],
                 onClick = { placedItems = placedItems - 1 }
            )
            
            // Slot 2 (Large)
            OrderSlot(
                label = "",
                size = commonSlotSize,
                onGloballyPositioned = { largeSlotBounds = it },
                placedItem = placedItems[2],
                 onClick = { placedItems = placedItems - 2 }
            )
        }
        
        // 4. DRAGGABLE ITEMS (Unplaced)
        // Explicit key composable to track items by ID
        unplacedItems.forEach { item ->
            key(item.id) {
                 DraggableSizeItem(
                    key = item.id,
                    item = item,
                    initialOffset = item.initialOffset,
                    onDrop = { position ->
                        val isSlot0Drop = smallSlotBounds?.contains(position) == true
                        val isSlot1Drop = mediumSlotBounds?.contains(position) == true
                        val isSlot2Drop = largeSlotBounds?.contains(position) == true
                        
                        var targetSlot = -1
                        if (isSlot0Drop) targetSlot = 0
                        else if (isSlot1Drop) targetSlot = 1
                        else if (isSlot2Drop) targetSlot = 2
                        
                        if (targetSlot != -1) {
                            // If we drop on an occupied slot, we swap/replace.
                            // The occupied item goes back to unplaced (because it's removed from placedItems).
                            placedItems = placedItems + (targetSlot to item)
                        } else {
                           // If dropped nowhere, it just snaps back (DraggableSizeItem handles visual snap backs mainly by recomposition usage)
                           Toast.makeText(context, "Colócalo en un espacio", Toast.LENGTH_SHORT).show()
                        }
                    }
                 )
            }
        }
        
        // CHECK BUTTON
        androidx.compose.material3.Button(
            onClick = {
                // Validate
                val item0 = placedItems[0]
                val item1 = placedItems[1]
                val item2 = placedItems[2]
                
                val correct0 = item0?.sizeType == SizeType.SMALL
                val correct1 = item1?.sizeType == SizeType.MEDIUM
                val correct2 = item2?.sizeType == SizeType.LARGE
                
                if (correct0 && correct1 && correct2) {
                    isGameWon = true
                } else {
                     Toast.makeText(context, "Incorrecto. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
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

        // 5. WIN MESSAGE
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
    placedItem: SizeItem? = null,
    onClick: () -> Unit = {}
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
                }
                .clickable { onClick() }, // Click to remove
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
    key: Int,
    item: SizeItem,
    initialOffset: Offset,
    onDrop: (Offset) -> Unit
) {
    // key is used by parent to identify unique items, but we specifically need it for State.
    // Actually, 'key' passed to the composable function doesn't automatically restart 'remember'.
    // Use 'key(item.id)' in the call site or pass modifier.
    // We already passed 'key' in arguments, let's use it for the 'remember' keys if we want.
    
    var offsetX by remember(key) { mutableStateOf(initialOffset.x) }
    var offsetY by remember(key) { mutableStateOf(initialOffset.y) }
    // ...
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
