package com.example.hds_tesisapp.ui.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hds_tesisapp.R
import com.example.hds_tesisapp.ui.theme.Baloo2FontFamily
import com.example.hds_tesisapp.ui.theme.OrbitronFontFamily

val AuthAccentCyan = Color(0xFF00E5FF)
val AuthAccentMagenta = Color(0xFFE040FB)
val AuthErrorColor = Color(0xFFFF5252)

@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_menu_tesis),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF000814).copy(alpha = 0.80f),
                            Color(0xFF000814).copy(alpha = 0.94f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun AuthTitle(text: String, accentColor: Color = AuthAccentCyan) {
    Text(
        text = text.uppercase(),
        color = Color.White,
        letterSpacing = 2.sp,
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            shadow = Shadow(color = accentColor, offset = Offset.Zero, blurRadius = 22f)
        )
    )
}

@Composable
fun AuthGlassPanel(
    accentColor: Color = AuthAccentCyan,
    content: @Composable ColumnScope.() -> Unit
) {
    val glowAlpha = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        while (true) {
            glowAlpha.animateTo(1f, tween(1200))
            glowAlpha.animateTo(0.4f, tween(1200))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                for (i in 3 downTo 1) {
                    drawRoundRect(
                        color = accentColor.copy(alpha = glowAlpha.value * 0.08f * (4 - i)),
                        cornerRadius = CornerRadius(22.dp.toPx()),
                        style = Stroke(width = i * 12f)
                    )
                }
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF000D1A).copy(alpha = 0.92f),
                        Color(0xFF001428).copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(accentColor.copy(alpha = 0.85f), accentColor.copy(alpha = 0.35f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun HudTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    accentColor: Color = AuthAccentCyan,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                label.uppercase(),
                fontFamily = OrbitronFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        },
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = null, tint = accentColor)
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions.Default
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = Baloo2FontFamily,
            color = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = accentColor.copy(alpha = 0.35f),
            focusedLabelColor = accentColor,
            unfocusedLabelColor = Color.White.copy(alpha = 0.55f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.85f),
            cursorColor = accentColor,
            focusedContainerColor = Color(0xFF001828).copy(alpha = 0.55f),
            unfocusedContainerColor = Color(0xFF001020).copy(alpha = 0.4f)
        )
    )
}

@Composable
fun HudErrorMessage(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AuthErrorColor.copy(alpha = 0.12f))
            .border(1.dp, AuthErrorColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text,
            color = AuthErrorColor,
            fontFamily = Baloo2FontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
