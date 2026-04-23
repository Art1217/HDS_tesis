package com.example.hds_tesisapp.ui.theme.games.level1

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily


// ============================================================
//  Dirección del Bit
// ============================================================
enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun turnLeft(): Direction = when (this) {
        NORTH -> WEST
        EAST  -> NORTH
        SOUTH -> EAST
        WEST  -> SOUTH
    }

    fun turnRight(): Direction = when (this) {
        NORTH -> EAST
        EAST  -> SOUTH
        SOUTH -> WEST
        WEST  -> NORTH
    }

    fun move(row: Int, col: Int): Pair<Int, Int> = when (this) {
        NORTH -> Pair(row - 1, col)
        EAST  -> Pair(row, col + 1)
        SOUTH -> Pair(row + 1, col)
        WEST  -> Pair(row, col - 1)
    }

    // "▲" apunta hacia NORTH (0°), rotamos según dirección
    fun arrowDegrees(): Float = when (this) {
        NORTH ->   0f
        EAST  ->  90f
        SOUTH -> 180f
        WEST  -> 270f
    }
}


// ============================================================
//  Mapa del nivel 1
//  Grilla 3×3 — coordenadas (row, col)
//
//   (0,0)  (0,1)🏠  (0,2)
//   (1,0)🤖 (1,1)·  (1,2)
//   (2,0)  (2,1)   (2,2)
//
//  Camino correcto:
//    Start (1,0) → [Avanzar] → (1,1)
//              → [Girar izquierda] → NORTH
//              → [Avanzar] → (0,1) = Casa
// ============================================================
val LEVEL1_PATH_CELLS  = setOf(Pair(1, 0), Pair(1, 1), Pair(0, 1))
val LEVEL1_HOUSE_CELL  = Pair(0, 1)
val LEVEL1_START_CELL  = Pair(1, 0)
const val LEVEL1_GRID_ROWS = 3
const val LEVEL1_GRID_COLS = 3

private val CELL_SIZE: Dp = 90.dp
private val BIT_SIZE: Dp  = 66.dp


// ============================================================
//  Tablero principal
// ============================================================
@Composable
fun Level1GameBoard(
    bitRow: Int,
    bitCol: Int,
    bitDirection: Direction,
    modifier: Modifier = Modifier
) {
    val targetX = CELL_SIZE * bitCol + (CELL_SIZE - BIT_SIZE) / 2
    val targetY = CELL_SIZE * bitRow + (CELL_SIZE - BIT_SIZE) / 2

    val bitOffsetX by animateDpAsState(targetValue = targetX, animationSpec = tween(620), label = "bx")
    val bitOffsetY by animateDpAsState(targetValue = targetY, animationSpec = tween(620), label = "by")
    val arrowAngle by animateFloatAsState(targetValue = bitDirection.arrowDegrees(), animationSpec = tween(280), label = "ba")

    Box(
        modifier = modifier
            .size(CELL_SIZE * LEVEL1_GRID_COLS, CELL_SIZE * LEVEL1_GRID_ROWS)
    ) {
        // ── Celdas de la grilla ──
        Column {
            repeat(LEVEL1_GRID_ROWS) { row ->
                Row {
                    repeat(LEVEL1_GRID_COLS) { col ->
                        GridCell(
                            row = row,
                            col = col,
                            isPath  = Pair(row, col) in LEVEL1_PATH_CELLS,
                            isHouse = Pair(row, col) == LEVEL1_HOUSE_CELL
                        )
                    }
                }
            }
        }

        // ── Bit (personaje NPC animado sobre la grilla) ──
        BitCharacter(
            modifier = Modifier
                .size(BIT_SIZE)
                .offset(x = bitOffsetX, y = bitOffsetY),
            arrowAngle = arrowAngle
        )
    }
}


// ============================================================
//  Celda individual de la grilla
// ============================================================
@Composable
private fun GridCell(row: Int, col: Int, isPath: Boolean, isHouse: Boolean) {
    val bgColor = when {
        isHouse -> Color(0xFF0D2B0D)
        isPath  -> Color(0xFF0C1A2E)
        else    -> Color(0xFF05080F)
    }
    val borderColor = when {
        isHouse -> Color(0xFF69FF47).copy(alpha = 0.7f)
        isPath  -> Color(0xFF00E5FF).copy(alpha = 0.25f)
        else    -> Color(0xFF111827).copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .size(CELL_SIZE)
            .background(bgColor)
            .border(1.dp, borderColor),
        contentAlignment = Alignment.Center
    ) {
        when {
            isHouse -> HousePlaceholder()
            isPath  -> PathDot()
        }
    }
}


// ============================================================
//  Placeholder de la casa (reemplazar con imagen bit_house.png)
// ============================================================
@Composable
private fun HousePlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF69FF47).copy(alpha = 0.15f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
                    )
                }
                .clip(RoundedCornerShape(10.dp))
                .border(1.5.dp, Color(0xFF69FF47).copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: reemplazar con Image(painterResource(R.drawable.bit_house))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏠", fontSize = 20.sp)
                Text(
                    text = "CASA",
                    fontSize = 7.sp,
                    fontFamily = OrbitronFontFamily,
                    color = Color(0xFF69FF47),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ============================================================
//  Punto indicador del camino
// ============================================================
@Composable
private fun PathDot() {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(Color(0xFF00E5FF).copy(alpha = 0.35f), RoundedCornerShape(50))
    )
}


// ============================================================
//  Personaje Bit
//  Placeholder: caja cyan con flecha de dirección
//  TODO: reemplazar interior con Image(painterResource(R.drawable.bit_character))
// ============================================================
@Composable
private fun BitCharacter(modifier: Modifier = Modifier, arrowAngle: Float) {
    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.18f),
                    radius = size.minDimension / 2f + 10f
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Flecha de dirección sobre el Bit (rota animada)
        Image(
            painter = painterResource(R.drawable.bit_triste),
            contentDescription = "Bit",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = arrowAngle },
            contentScale = ContentScale.Fit
        )
    }
}
