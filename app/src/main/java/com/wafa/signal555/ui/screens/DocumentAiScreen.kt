package com.wafa.signal555.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.PrimaryButton
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun DocumentAiScreen(onBack: () -> Unit, onNextAction: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(SignalSurface)) {
        AppTopBar(
            title = "Document AI",
            onBack = onBack,
            trailing = { Icon(Icons.Outlined.MoreVert, contentDescription = null) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(58.dp).background(SignalRed.copy(alpha = .09f), RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Description, null, tint = SignalRed, modifier = Modifier.size(30.dp))
                    }
                    Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                        Text("Laporan_Keuangan_2024.pdf", fontWeight = FontWeight.Bold, color = SignalBlack)
                        Text("2,4 MB · 24 halaman", color = SignalMuted, fontSize = 11.sp)
                    }
                    Text("PDF", color = SignalRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DocumentTool(Icons.Outlined.Summarize, "Ringkas", true, Modifier.weight(1f))
                DocumentTool(Icons.Outlined.Search, "Cari Info", false, Modifier.weight(1f))
                DocumentTool(Icons.Outlined.CheckBox, "Jadikan Tugas", false, Modifier.weight(1f))
            }

            Spacer(Modifier.height(22.dp))
            Text("Ringkasan Dokumen", fontWeight = FontWeight.Bold, color = SignalBlack, fontSize = 16.sp)
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    "Dokumen ini membahas laporan keuangan perusahaan untuk tahun 2024, mencakup pertumbuhan pendapatan, efisiensi biaya operasional, dan rencana strategis untuk tahun berikutnya. Bagian utama menyoroti arus kas, ekspansi pasar, serta kebutuhan transformasi digital.",
                    modifier = Modifier.padding(18.dp),
                    color = SignalBlack,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "AI dapat membantu menemukan informasi penting, tetapi selalu periksa kembali dokumen sumber sebelum mengambil keputusan penting.",
                color = SignalMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Buat langkah selanjutnya", onNextAction)
        }
    }
}

@Composable
private fun DocumentTool(icon: ImageVector, text: String, active: Boolean, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (active) SignalRed else Color.White),
        border = if (active) null else androidx.compose.foundation.BorderStroke(1.dp, SignalBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = if (active) Color.White else SignalBlack, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(5.dp))
            Text(text, fontSize = 10.sp, color = if (active) Color.White else SignalBlack, fontWeight = FontWeight.SemiBold)
        }
    }
}
