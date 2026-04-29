package com.example.hds_tesisapp.ui.theme.games.game2

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily
import kotlinx.coroutines.delay

// ── Background ──────────────────────────────────────────────────────────────

@Composable
fun ForestBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.forest_bg),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

// ── Timer bar ───────────────────────────────────────────────────────────────

@Composable
fun G2TimerBar(
    secondsTotal: Int,
    secondsLeft: Int,
    modifier: Modifier = Modifier
) {
    val fraction = (secondsLeft.toFloat() / secondsTotal).coerceIn(0f, 1f)
    val color = when {
        fraction > 0.5f  -> Color(0xFF69FF47)
        fraction > 0.25f -> Color(0xFFFFD600)
        else             -> Color(0xFFFF1744)
    }
    Column(modifier = modifier) {
        Text(
            "⏱ $secondsLeft s",
            fontSize = 13.sp, fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold, color = color
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

// ── Character speech bubble ──────────────────────────────────────────────────

@Composable
fun G2CharacterBubble(
    @DrawableRes characterRes: Int,
    characterName: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    }

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .fillMaxWidth(0.82f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0A1A0A).copy(alpha = 0.95f))
            .border(2.dp, Color(0xFF69FF47).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(characterRes),
                    contentDescription = characterName,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        characterName.uppercase(),
                        fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Color(0xFF69FF47),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        message,
                        fontSize = 13.sp, fontFamily = Baloo2FontFamily,
                        color = Color.White.copy(alpha = 0.93f),
                        lineHeight = 20.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF388E3C))))
                    .border(1.dp, Color(0xFF69FF47).copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 28.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "¡Entendido!", fontSize = 13.sp, fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold, color = Color.White
                )
            }
        }
    }
}

// ── Draggable item chip ──────────────────────────────────────────────────────

@Composable
fun G2ItemChip(
    item: DragItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFFFFD600) else Color(0xFF69FF47).copy(alpha = 0.4f)
    val bgColor     = if (isSelected) Color(0xFF2A2200) else Color(0xFF0A1A0A)
    val scale       = if (isSelected) 1.1f else 1f

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = 0.92f))
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (item.drawableRes != null) {
                Image(
                    painter = painterResource(item.drawableRes!!),
                    contentDescription = item.label,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(item.emoji, fontSize = 22.sp)
            }
            Text(
                item.label, fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center
            )
        }
    }
}

// ── Tree drop zone ───────────────────────────────────────────────────────────

@Composable
fun G2TreeZone(
    tree: TreeType,
    placedItems: List<DragItem>,
    isHighlighted: Boolean,
    onZoneTap: () -> Unit,
    modifier: Modifier = Modifier,
    treeHeight: Dp = 130.dp
) {
    val pulse = rememberInfiniteTransition(label = "zone_${tree.name}")
    val glow by pulse.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "glow"
    )
    val borderColor = if (isHighlighted)
        Color(0xFFFFD600).copy(alpha = glow)
    else
        Color(0xFF69FF47).copy(alpha = 0.25f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF001A00).copy(alpha = 0.75f))
            .border(if (isHighlighted) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onZoneTap
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(tree.drawableRes),
            contentDescription = tree.label,
            modifier = Modifier.height(treeHeight).fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(4.dp))
        Text(
            tree.fruit.emoji + " " + tree.label,
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        if (placedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isHighlighted) "← toca aquí" else "Selecciona fruta",
                    fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = if (isHighlighted) Color(0xFFFFD600).copy(alpha = 0.8f)
                            else Color.White.copy(alpha = 0.35f)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF002200).copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                placedItems.take(6).forEach { item ->
                    if (item.drawableRes != null) {
                        Image(
                            painter = painterResource(item.drawableRes!!),
                            contentDescription = item.label,
                            modifier = Modifier.size(20.dp).padding(horizontal = 1.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(item.emoji, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 2.dp))
                    }
                }
                if (placedItems.size > 6) {
                    Text("+${placedItems.size - 6}", fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// ── Habitat drop zone ────────────────────────────────────────────────────────

@Composable
fun G2HabitatZone(
    habitat: HabitatType,
    placedItems: List<DragItem>,
    isHighlighted: Boolean,
    onZoneTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = rememberInfiniteTransition(label = "hab_${habitat.name}")
    val glow by pulse.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "glow"
    )
    val borderColor = if (isHighlighted)
        Color(0xFFFFD600).copy(alpha = glow)
    else
        Color(0xFF00C8FF).copy(alpha = 0.25f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF001525).copy(alpha = 0.82f))
            .border(if (isHighlighted) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onZoneTap
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(habitat.icon, fontSize = 32.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            habitat.label,
            fontSize = 10.sp, fontFamily = Baloo2FontFamily,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        if (placedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isHighlighted) "← toca aquí" else "Selecciona animal",
                    fontSize = 9.sp, fontFamily = Baloo2FontFamily,
                    color = if (isHighlighted) Color(0xFFFFD600).copy(alpha = 0.8f)
                            else Color.White.copy(alpha = 0.35f)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF001830).copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                placedItems.take(6).forEach { item ->
                    if (item.drawableRes != null) {
                        Image(
                            painter = painterResource(item.drawableRes!!),
                            contentDescription = item.label,
                            modifier = Modifier.size(20.dp).padding(horizontal = 1.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(item.emoji, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 2.dp))
                    }
                }
            }
        }
    }
}

// ── Error flash overlay ──────────────────────────────────────────────────────

@Composable
fun G2ErrorFlash(visible: Boolean) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF1744).copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3A0000).copy(alpha = 0.92f))
                .border(2.dp, Color(0xFFFF1744), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                "❌ Eso no va ahí",
                fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                color = Color.White, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Victory overlay ──────────────────────────────────────────────────────────

@Composable
fun G2VictoryOverlay(levelNumber: Int, onNext: () -> Unit) {
    val scale = remember { Animatable(0.7f) }
    val glow  = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        while (true) {
            glow.animateTo(1f, tween(700))
            glow.animateTo(0.5f, tween(700))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF020E1F))
                .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(24.dp))
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐  ⭐  ⭐", fontSize = 26.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "¡NIVEL $levelNumber\nCOMPLETADO!",
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = OrbitronFontFamily, color = Color(0xFF69FF47),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF69FF47).copy(alpha = glow.value * 0.3f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f)
                            )
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF388E3C))))
                        .border(2.dp, Color(0xFF69FF47).copy(alpha = glow.value), RoundedCornerShape(14.dp))
                        .clickable(onClick = onNext)
                        .padding(horizontal = 32.dp, vertical = 12.dp)
                ) {
                    Text(
                        "¡Continuar!  →", fontSize = 14.sp,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
            }
        }
    }
}

// ── Item pool row ─────────────────────────────────────────────────────────────

@Composable
fun G2ItemPool(
    items: List<DragItem>,
    selectedItem: DragItem?,
    onItemTap: (DragItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xFF000A00).copy(alpha = 0.88f))
            .border(
                width = 1.dp,
                color = Color(0xFF69FF47).copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (selectedItem != null) "✅ Seleccionado: ${selectedItem.emoji} ${selectedItem.label}  →  toca un árbol"
                else "Toca una fruta o animal para seleccionar",
                fontSize = 10.sp, fontFamily = Baloo2FontFamily,
                color = if (selectedItem != null) Color(0xFFFFD600) else Color.White.copy(alpha = 0.5f)
            )
            Text(
                "${items.size} restantes",
                fontSize = 10.sp, fontFamily = OrbitronFontFamily,
                color = Color(0xFF69FF47).copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(6.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "¡Todo clasificado! 🎉",
                    fontSize = 14.sp, fontFamily = Baloo2FontFamily,
                    color = Color(0xFF69FF47), fontWeight = FontWeight.Bold
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    G2ItemChip(
                        item       = item,
                        isSelected = selectedItem?.id == item.id,
                        onClick    = { onItemTap(item) }
                    )
                }
            }
        }
    }
}
