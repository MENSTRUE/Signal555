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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
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
import com.wafa.signal555.ui.theme.SignalBlack
import com.wafa.signal555.ui.theme.SignalMuted
import com.wafa.signal555.ui.theme.SignalRed
import com.wafa.signal555.ui.theme.SignalSurface

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(SignalSurface)) {
        AppTopBar(title = "Profile", onBack = onBack)
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).background(SignalBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("W", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text("Wafa", color = SignalBlack, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("555-0001", color = SignalMuted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(28.dp))
            ProfileItem(Icons.Outlined.DataUsage, "AI Context", "Riwayat konteks dan preferensi")
            ProfileItem(Icons.Outlined.Palette, "Appearance", "Tema dan tampilan aplikasi")
            ProfileItem(Icons.Outlined.Language, "Language", "Bahasa Indonesia / English")
            ProfileItem(Icons.Outlined.Lock, "Data & Privacy", "Kelola data lokal dan izin")
            ProfileItem(Icons.Outlined.Info, "About", "555 Assist v1.0.0")
        }
    }
}

@Composable
private fun ProfileItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(SignalRed.copy(alpha = .08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SignalRed, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(title, color = SignalBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = SignalMuted, fontSize = 11.sp)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = SignalMuted)
    }
}
