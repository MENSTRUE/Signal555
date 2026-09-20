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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.AppTopBar
import com.wafa.signal555.ui.components.BottomDestination
import com.wafa.signal555.ui.components.SignalBottomBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun HistoryScreen(
    onHome: () -> Unit,
    onCheck: () -> Unit,
    onAsk: () -> Unit,
    onProfile: () -> Unit
) {
    Scaffold(
        containerColor = SignalSurface,
        bottomBar = {
            SignalBottomBar(
                selected = BottomDestination.History,
                onHome = onHome,
                onCheck = onCheck,
                onAsk = onAsk,
                onHistory = {},
                onProfile = onProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SignalSurface)
        ) {
            AppTopBar(
                title = "History",
                trailing = {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = SignalRed)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text("Recent checks", fontSize = 23.sp, fontWeight = FontWeight.Black, color = SignalBlack)
                Text("Riwayat bantuan dan pemeriksaan terakhir.", color = SignalMuted, fontSize = 12.sp)
                Spacer(Modifier.height(20.dp))
                HistoryRow(Icons.Outlined.PhotoCamera, "WhatsApp screenshot", "Perlu perhatian", "20:14")
                HistoryRow(Icons.Outlined.Link, "promo-hadiah.xyz", "Risiko tinggi", "Kemarin")
                HistoryRow(Icons.Outlined.Description, "Laporan_Keuangan_2024.pdf", "Diringkas", "2 hari lalu")
            }
        }
    }
}

@Composable
private fun HistoryRow(icon: ImageVector, title: String, status: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(SignalRed.copy(alpha = .08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SignalRed, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = SignalBlack, fontSize = 13.sp)
            Text(status, color = SignalMuted, fontSize = 11.sp)
        }
        Text(time, color = SignalMuted, fontSize = 10.sp)
    }
}
