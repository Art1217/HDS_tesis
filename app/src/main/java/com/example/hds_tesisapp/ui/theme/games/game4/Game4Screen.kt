package com.example.hds_tesisapp.ui.theme.games.game4

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.geometry.Rect
import kotlin.math.roundToInt

// Helper function
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

data class NumberItem(
    val id: Int,
    val number: Int,
    val initialOffset: Offset = Offset.Zero
)

@Composable
fun Game4Screen(onLevelComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Force Landscape
    val activity = remember { context.findActivity() }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {}
    }

    // Config
    val boxSize = 80.dp
    val optionBoxSize = 70.dp
    
    // State
    // Valid options: 22, 20, 16
    val initialOptions = remember {
        listOf(
            NumberItem(1, 22),
            NumberItem(2, 20),
            NumberItem(3, 16) // Correct Answer
        )
    }

    // Placed item in the "?" slot
    var placedItem by remember { mutableStateOf<NumberItem?>(null) }
    
    // Win State
    var isGameWon by remember { mutableStateOf(false) }

    // Drop Zone Bounds
    var dropZoneBounds by remember { mutableStateOf<Rect?>(null) }
    
    // Derived state for draggable options (filter out placed one)
    val currentOptions = remember(initialOptions, placedItem) {
        initialOptions.filter { it.id != placedItem?.id }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Light Blue
    ) {
        // TITLE
        Text(
            text = "LA SECUENCIA",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0277BD), // Dark Blue
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        // ----------------- SEQUENCE AREA (Left/Center) -----------------
        // 2 -> 4 -> 8 -> ?
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-80).dp), // Shift left to make room for options
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Static Boxes
            SequenceBox(text = "2")
            Arrow()
            SequenceBox(text = "4")
            Arrow()
            SequenceBox(text = "8")
            Arrow()
            
            // Target Box (?)
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)) // Dashed border ideal but solid ok
                    .onGloballyPositioned { dropZoneBounds = it.boundsInWindow() }
                    .clickable { 
                        // Click to remove placed item
                        placedItem = null
                    },
                contentAlignment = Alignment.Center
            ) {
                if (placedItem != null) {
                    // Show placed number
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .background(Color(0xFF2196F3), RoundedCornerShape(4.dp)), // Blue fill
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = placedItem!!.number.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "?",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }

        // ----------------- OPTIONS AREA (Right) -----------------
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Render placeholders for layout, but actual draggables overlay this
            // We can just put a column of "slots" or just float the draggables.
            // Let's float them.
        }
        
        // ----------------- DRAGGABLE LAYOUT -----------------
        // Options are placed absolutely on the right side.
        // We calculate positions.
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
             currentOptions.forEachIndexed { index, item ->
                 // Calculate initial position for "Right Side Column"
                 // Screen width approx 800-900dp. 
                 // Let's align them roughly.
                 // We will use a Box in the main layout to anchor them? 
                 // Or just hardcode offsets relative to center/end.
                 
                 // Better approach: Layout them in a Column on the right, but wrapper logic.
                 // Actually, simpler to just hardcode offsets for this fixed layout.
                 
                 val baseX = 600.dp // Approximate
                 val baseY = 100.dp + (index * 90).dp
                 
                 // We need screen width to be responsive?
                 // Let's use Alignment.CenterEnd + offset.
                 
                 key(item.id) {
                     DraggableNumber(
                        key = item.id,
                        item = item,
                        initialOffset = Offset.Zero, // Logic handled below
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 50.dp)
                            .offset(y = (-100).dp + (index * 100).dp), // Vertical stack
                        onDrop = { position ->
                            val isDrop = dropZoneBounds?.contains(position) == true
                            if (isDrop) {
                                placedItem = item
                            } else {
                                // Snap back (recomposition handles it by not updating placedItem)
                            }
                        }
                     )
                 }
             }
        }

        // ----------------- CHARACTER LINA (Bottom Left) -----------------
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
             // Placeholder for Lina image
             Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .background(Color(0xFF8D6E63), CircleShape) // Brown hair color
                     .border(2.dp, Color.Black, CircleShape)
             )
             Text("Lina", color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        // ----------------- CHECK BUTTON -----------------
        Button(
            onClick = {
                if (placedItem?.number == 16) {
                    isGameWon = true
                } else {
                    Toast.makeText(context, "Incorrecto. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    placedItem = null // Reset
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding()
                .padding(16.dp)
                .zIndex(10f) // Keep zIndex fix
        ) {
            Text("COMPROBAR")
        }
        
        // ----------------- WIN MESSAGE -----------------
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
                        text = "¡CORRECTO!",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Green
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
fun SequenceBox(text: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color(0xFFE0F7FA), RoundedCornerShape(8.dp)) // Lighter cyan
            .border(2.dp, Color(0xFF0097A7), RoundedCornerShape(8.dp)), // Cyan border
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF006064)
        )
    }
}

@Composable
fun Arrow() {
    Text(
        text = "→",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun DraggableNumber(
    key: Int,
    item: NumberItem,
    initialOffset: Offset,
    modifier: Modifier = Modifier,
    onDrop: (Offset) -> Unit
) {
    var offsetX by remember(key) { mutableStateOf(initialOffset.x) }
    var offsetY by remember(key) { mutableStateOf(initialOffset.y) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(80.dp, 60.dp) // Ellipse-like
            .background(Color(0xFF2196F3), RoundedCornerShape(50)) // Blue pill
            .border(2.dp, Color(0xFFFFEB3B), RoundedCornerShape(50)) // Yellow border
            .onGloballyPositioned { globalPosition = it.positionInWindow() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val dropCenter = globalPosition + Offset(size.width / 2f, size.height / 2f)
                        onDrop(dropCenter)
                        // Reset if drag ends (recomposition will handle effective "snap back" if not placed)
                        offsetX = 0f
                        offsetY = 0f
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
        Text(
            text = item.number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}
