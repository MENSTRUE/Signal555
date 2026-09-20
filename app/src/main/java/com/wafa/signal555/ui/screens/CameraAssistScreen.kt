package com.wafa.signal555.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.theme.SignalRed

@Composable
fun CameraAssistScreen(onBack: () -> Unit, onAsk: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF121417), Color(0xFF25292E), Color(0xFF0D0F12))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width * .53f
            val top = size.height * .34f
            val laptopW = size.width * .56f
            val laptopH = size.height * .22f
            drawRoundRect(
                color = Color(0xFF262B31),
                topLeft = Offset(centerX - laptopW / 2, top),
                size = Size(laptopW, laptopH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f)
            )
            drawRoundRect(
                color = Color(0xFF0D1014),
                topLeft = Offset(centerX - laptopW / 2 + 14f, top + 14f),
                size = Size(laptopW - 28f, laptopH - 32f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
            )
            drawLine(
                color = Color(0xFF8B9097),
                start = Offset(centerX - laptopW * .58f, top + laptopH + 20f),
                end = Offset(centerX + laptopW * .58f, top + laptopH + 20f),
                strokeWidth = 16f
            )

            val left = size.width * .15f
            val right = size.width * .88f
            val boxTop = size.height * .24f
            val boxBottom = size.height * .64f
            val corner = 42f
            val sw = 4f
            val scanColor = Color.White
            drawLine(scanColor, Offset(left, boxTop), Offset(left + corner, boxTop), sw)
            drawLine(scanColor, Offset(left, boxTop), Offset(left, boxTop + corner), sw)
            drawLine(scanColor, Offset(right, boxTop), Offset(right - corner, boxTop), sw)
            drawLine(scanColor, Offset(right, boxTop), Offset(right, boxTop + corner), sw)
            drawLine(scanColor, Offset(left, boxBottom), Offset(left + corner, boxBottom), sw)
            drawLine(scanColor, Offset(left, boxBottom), Offset(left, boxBottom - corner), sw)
            drawLine(scanColor, Offset(right, boxBottom), Offset(right - corner, boxBottom), sw)
            drawLine(scanColor, Offset(right, boxBottom), Offset(right, boxBottom - corner), sw)

            drawRect(
                color = SignalRed.copy(alpha = .75f),
                topLeft = Offset(left + 18f, size.height * .47f),
                size = Size(right - left - 36f, 2f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                }
                Text(
                    "Camera Assist",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Outlined.FlashOn, contentDescription = null, tint = Color.White)
            }

            Spacer(Modifier.weight(1f))

            Card(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD14171B))
            ) {
                Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(38.dp).background(Color.White.copy(alpha = .08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Info, null, tint = Color.White)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Laptop terdeteksi", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Electronic device", color = Color.White.copy(alpha = .58f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CameraAction("Identifikasi", Icons.Outlined.Info, Modifier.weight(1f), onAsk)
                CameraAction("Jelaskan", Icons.Outlined.QuestionAnswer, Modifier.weight(1f), onAsk)
                CameraAction("Troubleshoot", Icons.Outlined.Build, Modifier.weight(1f), onAsk)
            }

            Spacer(Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, CircleShape)
                        .padding(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CameraAction(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22262B)),
        shape = RoundedCornerShape(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
        Text("  $text", fontSize = 10.sp)
    }
}
