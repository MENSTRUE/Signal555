package com.wafa.signal555.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.PrimaryButton
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun NextActionScreen(onBack: () -> Unit, onDone: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(SignalSurface)) {
        AppTopBar(
            title = "Next Action",
            onBack = onBack,
            trailing = { Icon(Icons.Outlined.List, contentDescription = null) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(92.dp).background(SignalRed.copy(alpha = .08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(54.dp).background(SignalRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Analisis selesai", fontWeight = FontWeight.Black, fontSize = 23.sp, color = SignalBlack)
            Text(
                "Berikut langkah yang dapat Anda lakukan.",
                color = SignalMuted,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(28.dp))
            ActionStep(1, "Verifikasi pengirim", "Pastikan identitas pengirim melalui kanal resmi yang sudah Anda kenal.")
            ActionStep(2, "Jangan klik tautan", "Hindari membuka tautan hingga alamat dan pengirim telah terverifikasi.")
            ActionStep(3, "Simpan bukti", "Simpan screenshot atau informasi terkait jika perlu dilaporkan.")
            Spacer(Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F1F2))
            ) {
                Text(
                    "Tetap waspada. Keputusan akhir tetap berada di tangan Anda.",
                    modifier = Modifier.padding(16.dp),
                    color = SignalMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Selesai", onDone)
        }
    }
}

@Composable
private fun ActionStep(number: Int, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(34.dp).background(SignalRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = SignalMuted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
