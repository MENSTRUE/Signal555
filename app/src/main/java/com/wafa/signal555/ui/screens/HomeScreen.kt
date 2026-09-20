package com.wafa.signal555.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafa.signal555.ui.components.BottomDestination
import com.wafa.signal555.ui.components.QuickAssistCard
import com.wafa.signal555.ui.components.SectionHeader
import com.wafa.signal555.ui.components.SignalBottomBar
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalBorder
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun HomeScreen(
    onScreenshot: () -> Unit,
    onCamera: () -> Unit,
    onDocument: () -> Unit,
    onAsk: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    Scaffold(
        containerColor = SignalSurface,
        bottomBar = {
            SignalBottomBar(
                selected = BottomDestination.Home,
                onHome = {},
                onCheck = onScreenshot,
                onAsk = onAsk,
                onHistory = onHistory,
                onProfile = onProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = SignalBlack)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFF31343A), CircleShape)
                        .clickable(onClick = onProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Text("W", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(28.dp))
            Text("Selamat malam,", color = SignalBlack, fontSize = 18.sp)
            Text("Wafa.", color = SignalBlack, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onAsk),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Security, null, tint = SignalMuted, modifier = Modifier.size(20.dp))
                    Text(
                        "Tanya apa saja...",
                        color = SignalMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 10.dp).weight(1f)
                    )
                    Icon(Icons.Outlined.Mic, null, tint = SignalBlack, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(26.dp))
            SectionHeader("Quick Assist")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAssistCard(
                    icon = Icons.Outlined.PhotoCamera,
                    title = "Screenshot Check",
                    subtitle = "Periksa pesan atau gambar mencurigakan",
                    onClick = onScreenshot,
                    modifier = Modifier.weight(1f)
                )
                QuickAssistCard(
                    icon = Icons.Outlined.CameraAlt,
                    title = "Camera Assist",
                    subtitle = "Pahami objek yang ada di depanmu",
                    onClick = onCamera,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAssistCard(
                    icon = Icons.Outlined.Link,
                    title = "Link Check",
                    subtitle = "Cek sebelum kamu membuka tautan",
                    onClick = onScreenshot,
                    modifier = Modifier.weight(1f)
                )
                QuickAssistCard(
                    icon = Icons.Outlined.Description,
                    title = "Document AI",
                    subtitle = "Ringkas dan pahami dokumen",
                    onClick = onDocument,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))
            SectionHeader("Bantuan Hari Ini", "Lihat semua", onHistory)
            Spacer(Modifier.height(10.dp))
            HelpRow(Icons.Outlined.Security, "Waspadai pesan penipuan", "Modus terbaru yang perlu kamu tahu")
            HelpRow(Icons.Outlined.Description, "Cek keaslian dokumen", "Pastikan dokumen valid")
            HelpRow(Icons.Outlined.Lightbulb, "Tips aman berinternet", "Langkah sederhana, dampak besar")
            Spacer(Modifier.height(18.dp))
            Text(
                "555 / STANDING BY",
                color = SignalMuted,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HelpRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(SignalRed.copy(alpha = .08f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = SignalRed, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SignalBlack)
            Text(subtitle, fontSize = 11.sp, color = SignalMuted)
        }
    }
}
