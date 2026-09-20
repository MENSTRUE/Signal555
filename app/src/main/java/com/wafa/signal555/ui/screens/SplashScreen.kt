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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF171A20), Color(0xFF0F1115), Color(0xFF090A0C))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val base = size.height * 0.72f
            val widths = listOf(.08f, .11f, .07f, .15f, .09f, .12f, .08f)
            var x = 0f
            widths.forEachIndexed { index, fraction ->
                val w = size.width * fraction
                val h = size.height * (0.10f + (index % 4) * 0.035f)
                drawRect(
                    color = Color(0xFF20242A),
                    topLeft = Offset(x, base - h),
                    size = Size(w, h)
                )
                x += w + 7f
            }
            drawLine(
                color = SignalRed.copy(alpha = .8f),
                start = Offset(size.width * .63f, size.height * .42f),
                end = Offset(size.width * .63f, size.height * .72f),
                strokeWidth = 2f
            )
            drawCircle(
                color = Color.Transparent,
                radius = 16f,
                center = Offset(size.width * .63f, size.height * .54f),
                style = Stroke(width = 2f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("A", color = Color.White.copy(alpha = .65f), fontSize = 10.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "CLEARER\nTOMORROW",
                    color = Color.White.copy(alpha = .55f),
                    fontSize = 9.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 2.sp
                )
            }

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "555",
                        color = SignalRed,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        " Assist",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "AI Context Assistant",
                    color = Color.White.copy(alpha = .78f),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(34.dp))
                Text(
                    "Technology that helps\nyou decide.",
                    color = Color.White.copy(alpha = .9f),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
                Spacer(Modifier.height(26.dp))
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SignalRed)
                ) {
                    Text("Mulai  →", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Informasi yang lebih jelas untuk langkah yang lebih baik.",
                    color = Color.White.copy(alpha = .42f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
